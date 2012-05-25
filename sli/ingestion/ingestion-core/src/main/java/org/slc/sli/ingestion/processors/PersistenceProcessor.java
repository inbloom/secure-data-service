package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.handler.AbstractIngestionHandler;
import org.slc.sli.ingestion.handler.NeutralRecordEntityPersistHandler;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.EdFi2SLITransformer;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.spring.MessageSourceHelper;
import org.slc.sli.ingestion.validation.DatabaseLoggingErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.ProxyErrorReport;

/**
 * Ingestion Persistence Processor.
 *
 * Specific Ingestion Persistence Processor which provides specific SLI Ingestion instance
 * persistence behavior.
 *
 */
@Component
public class PersistenceProcessor implements Processor, MessageSourceAware {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.PERSISTENCE_PROCESSOR;

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceProcessor.class);

    private Map<String, EdFi2SLITransformer> transformers;

    private EdFi2SLITransformer defaultEdFi2SLITransformer;
    // spring-loaded list of supported collections
    private Set<String> persistedCollections;

    private Map<String, ? extends AbstractIngestionHandler<SimpleEntity, Entity>> entityPersistHandlers;

    private AbstractIngestionHandler<SimpleEntity, Entity> defaultEntityPersistHandler;

    private NeutralRecordEntityPersistHandler obsoletePersistHandler;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Autowired
    private BatchJobDAO batchJobDAO;

    /** The names of Mongo collections of documents that have been transformed. */
    private Collection<String> transformedCollections;

    private MessageSource messageSource;

    /**
     * Camel Exchange process callback method
     *
     * @param exchange
     */
    @Override
    @ExtractBatchJobIdToContext
    @Profiled
    public void process(Exchange exchange) {

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {

            processPersistence(exchange, batchJobId);
        }
    }

    private void processPersistence(Exchange exchange, String batchJobId) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);
            LOG.info("processing persistence: {}", newJob);

            transformedCollections = getTransformedCollectionNames(newJob);

            for (ResourceEntry resource : newJob.getResourceEntries()) {
                if (FileFormat.NEUTRALRECORD.getCode().equalsIgnoreCase(resource.getResourceFormat())) {

                    processAndMeasureResource(resource, newJob, stage);
                } else {
                    LOG.warn(String.format("The resource %s is not a neutral record format.", resource.getResourceName()));
                }
            }

            exchange.getIn().setHeader("IngestionMessageType", MessageType.DONE.name());

        } catch (Exception exception) {
            handleProcessingExceptions(exception, exchange, batchJobId);
        } finally {
            if (newJob != null) {
                cleanupStagingDbForJob(newJob);

                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }

    private void processAndMeasureResource(ResourceEntry resource, NewBatchJob newJob, Stage stage) {
        Metrics metrics = Metrics.createAndStart(resource.getResourceId());
        stage.getMetrics().add(metrics);

        if (resource.getResourceName() != null) {
            try {

                processNeutralRecordsFile(new File(resource.getResourceName()), newJob, metrics);
            } catch (IOException e) {
                Error error = Error.createIngestionError(newJob.getId(), resource.getResourceId(),
                        BATCH_JOB_STAGE.getName(), null, null, null, FaultType.TYPE_ERROR.getName(), "Exception",
                        e.getMessage());
                batchJobDAO.saveError(error);
            }
        }
        metrics.stopMetric();
    }

    private void processNeutralRecordsFile(File neutralRecordsFile, Job job, Metrics metrics) throws IOException {

        long recordNumber = 0;
        long numFailed = 0;

        ErrorReport errorReportForNrFile = createDbErrorReport(job.getId(), neutralRecordsFile.getName());

        NeutralRecordFileReader nrFileReader = null;
        String fatalErrorMessage = MessageSourceHelper.getMessage(messageSource, "PERSISTPROC_FATAL_MSG1");
        try {
            Set<String> encounteredStgCollections = new HashSet<String>();

            nrFileReader = new NeutralRecordFileReader(neutralRecordsFile);
            while (nrFileReader.hasNext()) {

                recordNumber++;

                NeutralRecord neutralRecord = nrFileReader.next();

                fatalErrorMessage = MessageSourceHelper.getMessage(messageSource, "PERSISTPROC_FATAL_MSG1") + "\tEntity\t"
                        + neutralRecord.getRecordType() + "\n" + "\tIdentifier\t" + (String) neutralRecord.getLocalId()
                        + "\n";

                if (transformedCollections.contains(neutralRecord.getRecordType())) {

                    numFailed += processTransformableNeutralRecord(neutralRecord, job, encounteredStgCollections,
                            errorReportForNrFile);
                } else {
                    numFailed += processOldStyleNeutralRecord(neutralRecord, recordNumber, getTenantId(job),
                            errorReportForNrFile);
                }
            }
        } catch (Exception e) {
            errorReportForNrFile.fatal(fatalErrorMessage, PersistenceProcessor.class);
            LOG.error("Exception when attempting to ingest NeutralRecords in: " + neutralRecordsFile + ".\n");
        } finally {
            if (nrFileReader != null) {
                nrFileReader.close();
            }

            metrics.setRecordCount(recordNumber);
            metrics.setErrorCount(numFailed);
        }
    }

    private long processTransformableNeutralRecord(NeutralRecord neutralRecord, Job job,
            Set<String> encounteredStgCollections, ErrorReport errorReportForNrFile) {
        long numFailed = 0;

        // only proceed if we haven't proceesed this record type yet
        if (!encounteredStgCollections.contains(neutralRecord.getRecordType())) {
            LOG.debug("processing transformable neutral record: {}", neutralRecord.getRecordType());

            Iterable<NeutralRecord> stagedNeutralRecords = getStagedNeutralRecords(neutralRecord, job,
                    encounteredStgCollections);

            if (stagedNeutralRecords.iterator().hasNext()) {

                for (NeutralRecord stagedNeutralRecord : stagedNeutralRecords) {
                    stagedNeutralRecord.setSourceId(getTenantId(job));

                    // TODO: why is this necessary?
                    stagedNeutralRecord.setRecordType(neutralRecord.getRecordType());

                    ErrorReport errorReportForTransformer = new ProxyErrorReport(errorReportForNrFile);
                    EdFi2SLITransformer transformer = findTransformer(neutralRecord.getRecordType());
                    List<SimpleEntity> xformedEntities = transformer.handle(stagedNeutralRecord, errorReportForTransformer);

                    if (xformedEntities.isEmpty()) {
                        numFailed++;

                        errorReportForNrFile.error(MessageSourceHelper.getMessage(messageSource, "PERSISTPROC_ERR_MSG4", neutralRecord.getRecordType()), this);
                    }
                    for (SimpleEntity xformedEntity : xformedEntities) {

                        ErrorReport errorReportForNrEntity = new ProxyErrorReport(errorReportForNrFile);

                        AbstractIngestionHandler<SimpleEntity, Entity> entityPersistHandler = findHandler(xformedEntity.getType());
                        entityPersistHandler.handle(xformedEntity, errorReportForNrEntity);

                        if (errorReportForNrEntity.hasErrors()) {
                            numFailed++;
                        }
                    }
                }
            } else {
                // TODO: this isn't really a failure per record. revisit.
                numFailed++;
            }

        }
        return numFailed;
    }

    private AbstractIngestionHandler<SimpleEntity, Entity> findHandler(String type) {
        if (entityPersistHandlers.containsKey(type)) {
            return entityPersistHandlers.get(type);
        } else {
            return defaultEntityPersistHandler;
        }
    }

    private EdFi2SLITransformer findTransformer(String type) {
        if (transformers.containsKey(type)) {
            return transformers.get(type);
        } else {
            return defaultEdFi2SLITransformer;
        }
    }


    private long processOldStyleNeutralRecord(NeutralRecord neutralRecord, long recordNumber, String tenantId,
            ErrorReport errorReportForNrFile) {
        long numFailed = 0;

        // only persist if it's in the spring-loaded list of supported record types
        if (persistedCollections.contains(neutralRecord.getRecordType())) {
            LOG.debug("processing old-style neutral record");

            NeutralRecordEntity nrEntity = Translator.mapToEntity(neutralRecord, recordNumber);
            nrEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), tenantId);

            ErrorReport errorReportForNrEntity = new ProxyErrorReport(errorReportForNrFile);
            obsoletePersistHandler.handle(nrEntity, errorReportForNrEntity);

            if (errorReportForNrEntity.hasErrors()) {
                numFailed++;
            }
        }
        return numFailed;
    }

    private Iterable<NeutralRecord> getStagedNeutralRecords(NeutralRecord neutralRecord, Job job,
            Set<String> encounteredStgCollections) {

        Iterable<NeutralRecord> stagedNeutralRecords = Collections.emptyList();

        NeutralQuery neutralQuery = new NeutralQuery();
        neutralQuery.setLimit(0);

        stagedNeutralRecords = neutralRecordMongoAccess.getRecordRepository().findAllForJob(
                neutralRecord.getRecordType() + "_transformed", job.getId(), neutralQuery);

        encounteredStgCollections.add(neutralRecord.getRecordType());

        return stagedNeutralRecords;
    }

    /**
     * returns list of the names of collections that were created as a result transformation feature
     *
     * @param job
     *
     * @return transformedCollections
     */
    private Collection<String> getTransformedCollectionNames(Job job) {
        HashSet<String> collections = new HashSet<String>();

        Iterable<String> data = neutralRecordMongoAccess.getRecordRepository()
                .getCollectionFullNamesForJob(job.getId());
        Iterator<String> iter = data.iterator();

        while (iter.hasNext()) {
            String collectionName = iter.next();

            int indexOfTransformed = collectionName.indexOf("_transformed");
            if (indexOfTransformed != -1) {
                if (neutralRecordMongoAccess.getRecordRepository().count(collectionName, new NeutralQuery()) > 0) {
                    LOG.info("FOUND TRANSFORMED COLLECTION WITH MORE THEN 0 RECORD = " + collectionName);
                    collections.add(collectionName.substring(0, indexOfTransformed));
                }
            }
        }
        return collections;
    }

    private DatabaseLoggingErrorReport createDbErrorReport(String batchJobId, String resourceId) {
        DatabaseLoggingErrorReport dbErrorReport = new DatabaseLoggingErrorReport(batchJobId, BATCH_JOB_STAGE,
                resourceId, batchJobDAO);
        return dbErrorReport;
    }

    private void cleanupStagingDbForJob(Job job) {
        neutralRecordMongoAccess.getRecordRepository().deleteCollectionsForJob(job.getId());
    }

    private static String getTenantId(Job job) {
        // TODO this should be determined based on the sourceId
        String tenantId = job.getProperty("tenantId");
        if (tenantId == null) {
            tenantId = "SLI";
        }
        return tenantId;
    }

    private void handleNoBatchJobIdInExchange(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    private void handleProcessingExceptions(Exception exception, Exchange exchange, String batchJobId) {
        exchange.getIn().setHeader("ErrorMessage", exception.toString());
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Exception encountered in PersistenceProcessor. ");

        Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                FaultType.TYPE_ERROR.getName(), "Exception", exception.getMessage());
        batchJobDAO.saveError(error);
    }

    public void setEntityPersistHandlers(Map<String, ? extends AbstractIngestionHandler<SimpleEntity, Entity>> entityPersistHandlers) {
        this.entityPersistHandlers = entityPersistHandlers;
    }

    public NeutralRecordEntityPersistHandler getObsoletePersistHandler() {
        return obsoletePersistHandler;
    }

    public void setObsoletePersistHandler(NeutralRecordEntityPersistHandler obsoletePersistHandler) {
        this.obsoletePersistHandler = obsoletePersistHandler;
    }

    public Set<String> getPersistedCollections() {
        return persistedCollections;
    }

    public void setPersistedCollections(Set<String> persistedCollections) {
        this.persistedCollections = persistedCollections;
    }

    public void setTransformers(Map<String, EdFi2SLITransformer> transformers) {
        this.transformers = transformers;
    }

    public void setDefaultEdFi2SLITransformer(EdFi2SLITransformer defaultEdFi2SLITransformer) {
        this.defaultEdFi2SLITransformer = defaultEdFi2SLITransformer;
    }

    public void setDefaultEntityPersistHandler(AbstractIngestionHandler<SimpleEntity, Entity> defaultEntityPersistHandler) {
        this.defaultEntityPersistHandler = defaultEntityPersistHandler;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
