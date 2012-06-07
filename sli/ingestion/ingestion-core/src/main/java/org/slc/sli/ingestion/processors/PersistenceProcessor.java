package org.slc.sli.ingestion.processors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordReadConverter;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.handler.NeutralRecordEntityPersistHandler;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.EdFi2SLITransformer;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.DatabaseLoggingErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ProxyErrorReport;

/**
 * Ingestion Persistence Processor.
 *
 * Specific Ingestion Persistence Processor which provides specific SLI Ingestion instance
 * persistence behavior.
 * Persists data from Staged Database.
 *
 * @author ifaybyshev
 * @author dduran
 * @author shalka
 */
@Component
public class PersistenceProcessor implements Processor, MessageSourceAware {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.PERSISTENCE_PROCESSOR;

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceProcessor.class);

    private Map<String, EdFi2SLITransformer> transformers;

    private EdFi2SLITransformer defaultEdFi2SLITransformer;

    private Map<String, Set<String>> entityPersistTypeMap;

    private Map<String, ? extends AbstractIngestionHandler<SimpleEntity, Entity>> entityPersistHandlers;

    private AbstractIngestionHandler<SimpleEntity, Entity> defaultEntityPersistHandler;

    private NeutralRecordEntityPersistHandler obsoletePersistHandler;

    @Autowired
    private NeutralRecordReadConverter neutralRecordReadConverter;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Autowired
    private BatchJobDAO batchJobDAO;

    private MessageSource messageSource;

    /**
     * Camel Exchange process callback method
     *
     * @param exchange
     *            camel exchange.
     */
    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) {

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        if (workNote == null || workNote.getBatchJobId() == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {
            processPersistence(workNote, exchange);
        }
    }

    /**
     * Process the persistence of the entity specified by the work note.
     *
     * @param workNote
     *            specifies the entity to be persisted.
     * @param exchange
     *            camel exchange.
     */
    private void processPersistence(WorkNote workNote, Exchange exchange) {
        Stage stage = initializeStage(workNote);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);
            LOG.debug("processing persistence: {}", newJob);

            processWorkNote(workNote, newJob, stage);

        } catch (Exception exception) {
            handleProcessingExceptions(exception, exchange, batchJobId);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJobStageSeparatelly(batchJobId, stage);
            }
        }
    }

    /**
     * Initialize the current (persistence) stage.
     *
     * @param workNote
     *            specifies the entity to be persisted.
     * @return current (started) stage.
     */
    private Stage initializeStage(WorkNote workNote) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);
        stage.setProcessingInformation("stagedEntity="
                + workNote.getIngestionStagedEntity().getCollectionNameAsStaged() + ", rangeMin="
                + workNote.getRangeMinimum() + ", rangeMax=" + workNote.getRangeMaximum() + ", batchSize="
                + workNote.getBatchSize());
        return stage;
    }

    /**
     * Processes the work note by persisting the entity (with range) specified in the work note.
     *
     * @param workNote
     *            specifies the entity (and range) to be persisted.
     * @param job
     *            current batch job.
     * @param stage
     *            persistence stage.
     */
    private void processWorkNote(WorkNote workNote, Job job, Stage stage) {
        long recordNumber = 0;
        long numFailed = 0;
        boolean persistedFlag = false;

        String collectionNameAsStaged = workNote.getIngestionStagedEntity().getCollectionNameAsStaged();

        EntityPipelineType entityPipelineType = getEntityPipelineType(collectionNameAsStaged);
        String collectionToPersistFrom = getCollectionToPersistFrom(collectionNameAsStaged, entityPipelineType);

        LOG.info("PERSISTING DATA IN COLLECTION: {} (staged as: {})", collectionToPersistFrom, collectionNameAsStaged);

        Map<String, Metrics> perFileMetrics = new HashMap<String, Metrics>();
        ErrorReport errorReportForCollection = createDbErrorReport(job.getId(), collectionNameAsStaged);

        try {

            Map<Object, NeutralRecord> records = getCollectionFromDb(collectionToPersistFrom, job.getId(), workNote);

            for (Map.Entry<Object, NeutralRecord> neutralRecordEntry : records.entrySet()) {
                numFailed = 0;

                recordNumber++;
                persistedFlag = false;

                NeutralRecord neutralRecord = neutralRecordEntry.getValue();

                errorReportForCollection = createDbErrorReport(job.getId(), neutralRecord.getSourceFile());

                Metrics currentMetric = getOrCreateMetric(perFileMetrics, neutralRecord, workNote);

                // process NeutralRecord with old or new pipeline
                if (entityPipelineType == EntityPipelineType.OLD) {

                    numFailed += processOldStyleNeutralRecord(neutralRecord, recordNumber, getTenantId(job),
                            errorReportForCollection);
                    persistedFlag = true;

                } else if (entityPipelineType == EntityPipelineType.NEW_PLAIN
                        || entityPipelineType == EntityPipelineType.NEW_TRANSFORMED) {

                    numFailed += processTransformableNeutralRecord(neutralRecord, getTenantId(job),
                            errorReportForCollection);

                    persistedFlag = true;
                }

                if (persistedFlag) {
                    currentMetric.setRecordCount(currentMetric.getRecordCount() + 1);
                }

                currentMetric.setErrorCount(currentMetric.getErrorCount() + numFailed);
                perFileMetrics.put(currentMetric.getResourceId(), currentMetric);
            }

        } catch (Exception e) {
            String fatalErrorMessage = "ERROR: Fatal problem saving records to database: \n" + "\tEntity\t"
                    + collectionNameAsStaged + "\n";
            errorReportForCollection.fatal(fatalErrorMessage, PersistenceProcessor.class);
            LogUtil.error(LOG, "Exception when attempting to ingest NeutralRecords in: " + collectionNameAsStaged, e);
        } finally {

            Iterator<Metrics> it = perFileMetrics.values().iterator();
            while (it.hasNext()) {
                Metrics m = it.next();
                stage.getMetrics().add(m);
            }

        }
    }

    /**
     * Invoked if the neutral record is of type $$type$$_transformed (underwent transformation).
     *
     * @param neutralRecord
     *            transformed neutral record.
     * @param tenantId
     *            tenant of the neutral record.
     * @param errorReportForCollection
     *            error report.
     * @return number of records that failed to persist.
     */
    private long processTransformableNeutralRecord(NeutralRecord neutralRecord, String tenantId,
            ErrorReport errorReportForCollection) {
        long numFailed = 0;

        LOG.debug("processing transformable neutral record of type: {}", neutralRecord.getRecordType());

        // remove _transformed metadata from type. upcoming transformation is based on type.
        neutralRecord.setRecordType(neutralRecord.getRecordType().replaceFirst("_transformed", ""));

        // must set tenantId here, it is used by upcoming transformer.
        neutralRecord.setSourceId(tenantId);

        EdFi2SLITransformer transformer = findTransformer(neutralRecord.getRecordType());
        List<SimpleEntity> xformedEntities = transformer.handle(neutralRecord, errorReportForCollection);

        if (xformedEntities.isEmpty()) {
            numFailed++;
            errorReportForCollection.error(MessageSourceHelper.getMessage(messageSource, "PERSISTPROC_ERR_MSG4",
                    neutralRecord.getRecordType()), this);
        }

        for (SimpleEntity xformedEntity : xformedEntities) {
            ErrorReport errorReportForNrEntity = new ProxyErrorReport(errorReportForCollection);

            AbstractIngestionHandler<SimpleEntity, Entity> entityPersistentHandler = findHandler(xformedEntity
                    .getType());

            entityPersistentHandler.handle(xformedEntity, errorReportForNrEntity);

            if (errorReportForNrEntity.hasErrors()) {
                numFailed++;
            }
        }

        return numFailed;
    }

    /**
     * Invoked if the neutral record is of type $$type$$ (no transformation occurred).
     *
     * @param neutralRecord
     *            neutral record as it was initially ingested.
     * @param recordNumber
     *            count when neutral record was encountered in incoming interchange.
     * @param tenantId
     *            tenant of the neutral record.
     * @param errorReportForCollection
     *            error report.
     * @return
     */
    private long processOldStyleNeutralRecord(NeutralRecord neutralRecord, long recordNumber, String tenantId,
            ErrorReport errorReportForCollection) {
        long numFailed = 0;

        LOG.debug("persisting neutral record: {}", neutralRecord.getRecordType());

        NeutralRecordEntity nrEntity = Translator.mapToEntity(neutralRecord, recordNumber);
        nrEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), tenantId);

        ErrorReport errorReportForNrEntity = new ProxyErrorReport(errorReportForCollection);
        obsoletePersistHandler.handle(nrEntity, errorReportForNrEntity);

        if (errorReportForNrEntity.hasErrors()) {
            numFailed++;
        }

        return numFailed;
    }

    /**
     * Creates metrics for persistence of work note.
     *
     * @param perFileMetrics
     *            current metrics on a per file basis.
     * @param neutralRecord
     *            neutral record to be persisted.
     * @param workNote
     *            work note specifying entities to be persisted.
     * @return
     */
    private Metrics getOrCreateMetric(Map<String, Metrics> perFileMetrics, NeutralRecord neutralRecord,
            WorkNote workNote) {

        String sourceFile = neutralRecord.getSourceFile();
        if (sourceFile == null) {
            sourceFile = "unknown_" + workNote.getIngestionStagedEntity().getEdfiEntity() + "_file";
        }

        Metrics currentMetric = perFileMetrics.get(sourceFile);
        if (currentMetric == null) {
            // establish new metrics
            currentMetric = Metrics.newInstance(sourceFile);
        }
        return currentMetric;
    }

    /**
     * Performs a look up for ingestion handlers based on entity type. If the entity does not
     * specify a special transformer, then the default entity persist handler is returned.
     *
     * @param type
     *            neutral record entity type.
     * @return ingestion persistence handler.
     */
    private AbstractIngestionHandler<SimpleEntity, Entity> findHandler(String type) {
        if (entityPersistHandlers.containsKey(type)) {
            LOG.debug("Found special transformer for entity of type: {}", type);
            return entityPersistHandlers.get(type);
        } else {
            LOG.debug("Using default transformer for entity of type: {}", type);
            return defaultEntityPersistHandler;
        }
    }

    /**
     * Checks to see if there is a special ed-fi to sli transformer for the specified neutral
     * record entity type. If no special transformer is specified, then the default transformer is
     * used.
     *
     * @param type
     *            neutral record entity type.
     * @return ed-fi to sli transformer.
     */
    private EdFi2SLITransformer findTransformer(String type) {
        if (transformers.containsKey(type)) {
            return transformers.get(type);
        } else {
            return defaultEdFi2SLITransformer;
        }
    }

    /**
     * Creates an error report for the specified batch job id and resource id.
     *
     * @param batchJobId
     *            current batch job.
     * @param resourceId
     *            current resource id.
     * @return database logging error report.
     */
    private DatabaseLoggingErrorReport createDbErrorReport(String batchJobId, String resourceId) {
        DatabaseLoggingErrorReport dbErrorReport = new DatabaseLoggingErrorReport(batchJobId, BATCH_JOB_STAGE,
                resourceId, batchJobDAO);
        return dbErrorReport;
    }

    /**
     * Gets the tenant id of the current batch job.
     *
     * @param job
     *            current batch job.
     * @return tenant id.
     */
    private static String getTenantId(Job job) {
        // TODO this should be determined based on the sourceId
        String tenantId = job.getProperty("tenantId");
        if (tenantId == null) {
            tenantId = "SLI";
        }
        return tenantId;
    }

    private String getCollectionToPersistFrom(String collectionNameAsStaged, EntityPipelineType entityPipelineType) {
        String collectionToPersistFrom = collectionNameAsStaged;
        if (entityPipelineType == EntityPipelineType.NEW_TRANSFORMED) {
            collectionToPersistFrom = collectionNameAsStaged + "_transformed";
        }
        return collectionToPersistFrom;
    }

    private EntityPipelineType getEntityPipelineType(String collectionName) {
        EntityPipelineType entityPipelineType = EntityPipelineType.NONE;
        if (entityPersistTypeMap.get("oldPipelineEntities").contains(collectionName)) {
            entityPipelineType = EntityPipelineType.OLD;
        } else if (entityPersistTypeMap.get("newPipelinePlainEntities").contains(collectionName)) {
            entityPipelineType = EntityPipelineType.NEW_PLAIN;
        } else if (entityPersistTypeMap.get("newPipelineTransformedEntities").contains(collectionName)) {
            entityPipelineType = EntityPipelineType.NEW_TRANSFORMED;
        }
        return entityPipelineType;
    }

    /**
     * Handles the absence of a batch job id in the camel exchange.
     *
     * @param exchange
     *            camel exchange.
     */
    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    /**
     * Handles the existence of any processing exceptions in the exchange.
     *
     * @param exception
     *            processing exception in camel exchange.
     * @param exchange
     *            camel exchange.
     * @param batchJobId
     *            current batch job id.
     */
    private void handleProcessingExceptions(Exception exception, Exchange exchange, String batchJobId) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LogUtil.error(LOG, "Error persisting batch job " + batchJobId, exception);

        Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                FaultType.TYPE_ERROR.getName(), "Exception", exception.getMessage());
        batchJobDAO.saveError(error);
    }

    public void setEntityPersistHandlers(
            Map<String, ? extends AbstractIngestionHandler<SimpleEntity, Entity>> entityPersistHandlers) {
        this.entityPersistHandlers = entityPersistHandlers;
    }

    public NeutralRecordEntityPersistHandler getObsoletePersistHandler() {
        return obsoletePersistHandler;
    }

    public void setObsoletePersistHandler(NeutralRecordEntityPersistHandler obsoletePersistHandler) {
        this.obsoletePersistHandler = obsoletePersistHandler;
    }

    public Map<String, Set<String>> getEntityPersistTypeMap() {
        return entityPersistTypeMap;
    }

    public void setEntityPersistTypeMap(Map<String, Set<String>> entityPersistTypeMap) {
        this.entityPersistTypeMap = entityPersistTypeMap;
    }

    public void setTransformers(Map<String, EdFi2SLITransformer> transformers) {
        this.transformers = transformers;
    }

    public void setDefaultEdFi2SLITransformer(EdFi2SLITransformer defaultEdFi2SLITransformer) {
        this.defaultEdFi2SLITransformer = defaultEdFi2SLITransformer;
    }

    public void setDefaultEntityPersistHandler(
            AbstractIngestionHandler<SimpleEntity, Entity> defaultEntityPersistHandler) {
        this.defaultEntityPersistHandler = defaultEntityPersistHandler;
    }

    public NeutralRecordReadConverter getNeutralRecordReadConverter() {
        return neutralRecordReadConverter;
    }

    public void setNeutralRecordReadConverter(NeutralRecordReadConverter neutralRecordReadConverter) {
        this.neutralRecordReadConverter = neutralRecordReadConverter;
    }


    public Map<Object, NeutralRecord> getCollectionFromDb(String collectionName, String jobId, WorkNote workNote) {
        Query query = new Query();

        Iterable<NeutralRecord> data;

        Criteria limiter = Criteria.where("creationTime").gte(workNote.getRangeMinimum()).lte(workNote.getRangeMaximum());
        query.addCriteria(limiter);


        data = neutralRecordMongoAccess.getRecordRepository().findByQueryForJob(collectionName, query, jobId);

        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr;

        Iterator<NeutralRecord> neutralRecordIterator = data.iterator();
        while (neutralRecordIterator.hasNext()) {
            tempNr = neutralRecordIterator.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
        return collection;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private static enum EntityPipelineType {
        OLD, NEW_PLAIN, NEW_TRANSFORMED, NONE;
    }

}
