package org.slc.sli.ingestion.processors;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordReadConverter;
import org.slc.sli.ingestion.handler.EntityPersistHandler;
import org.slc.sli.ingestion.handler.NeutralRecordEntityPersistHandler;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.SmooksEdFi2SLITransformer;
import org.slc.sli.ingestion.util.BatchJobUtils;
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
 */
@Component
public class StagedDataPersistenceProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.PERSISTENCE_PROCESSOR;

    private static final Logger LOG = LoggerFactory.getLogger(StagedDataPersistenceProcessor.class);

    @Autowired
    SmooksEdFi2SLITransformer transformer;

    // spring-loaded list of supported collections
    private Set<String> persistedCollections;

    private EntityPersistHandler entityPersistHandler;

    private NeutralRecordEntityPersistHandler obsoletePersistHandler;

    private NeutralRecordReadConverter neutralRecordReadConverter;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Autowired
    private BatchJobDAO batchJobDAO;

    /**
     * Camel Exchange process callback method
     *
     * @param exchange
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

    private void processPersistence(WorkNote workNote, Exchange exchange) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob newJob = null;
        try {
            newJob = batchJobDAO.findBatchJobById(batchJobId);
            LOG.info("processing persistence: {}", newJob);

            String collectionNameAsStaged = workNote.getIngestionStagedEntity().getCollectionNameAsStaged();

            String collectionToProcess = getStagedCollectionName(newJob, collectionNameAsStaged);

            LOG.info("PERSISTING DATA IN COLLECTION: {}", collectionToProcess);

            processAndMeasureResource(collectionNameAsStaged, collectionToProcess, newJob, stage);

            exchange.getIn().setHeader("IngestionMessageType", MessageType.DONE.name());

        } catch (Exception exception) {
            handleProcessingExceptions(exception, exchange, batchJobId);
        } finally {
            if (newJob != null) {
                BatchJobUtils.stopStageAndAddToJob(stage, newJob);
                batchJobDAO.saveBatchJob(newJob);
            }
        }
    }

    private void processAndMeasureResource(String collectionName, String transformedCollectionName, NewBatchJob newJob,
            Stage stage) {
        Metrics metrics = Metrics.createAndStart(newJob.getId() + "-" + collectionName);
        stage.getMetrics().add(metrics);

        processStagedCollection(collectionName, transformedCollectionName, newJob, metrics);

        metrics.stopMetric();
    }

    private void processStagedCollection(String collectionName, String transformedCollectionName, Job job,
            Metrics metrics) {

        long recordNumber = 0;
        long numFailed = 0;

        ErrorReport errorReportForCollection = createDbErrorReport(job.getId(), collectionName);

        String fatalErrorMessage = "ERROR: Fatal problem saving records to database.\n";
        try {

            BasicDBObject query = new BasicDBObject("batchJobId", job.getId());
            DBCursor cursor = getCollectionIterable(transformedCollectionName, job.getId());

            for (DBObject record : cursor) {

                recordNumber++;

                NeutralRecord neutralRecord = neutralRecordReadConverter.convert(record);

                fatalErrorMessage = "ERROR: Fatal problem saving records to database: \n" + "\tEntity\t"
                        + collectionName + "\n";

                if (!collectionName.equals(transformedCollectionName)) {
                    numFailed += processTransformableNeutralRecord(neutralRecord, getTenantId(job),
                            errorReportForCollection);
                } else {
                    numFailed += processOldStyleNeutralRecord(neutralRecord, recordNumber, getTenantId(job),
                            errorReportForCollection);
                }
            }

        } catch (Exception e) {
            errorReportForCollection.fatal(fatalErrorMessage, StagedDataPersistenceProcessor.class);
            LOG.error("Exception when attempting to ingest NeutralRecords in: " + collectionName + ".\n", e);
        } finally {

            metrics.setRecordCount(recordNumber);
            metrics.setErrorCount(numFailed);
        }
    }

    private long processTransformableNeutralRecord(NeutralRecord neutralRecord, String tenantId,
            ErrorReport errorReportForCollection) {
        long numFailed = 0;

        LOG.debug("processing transformable neutral record: {}", neutralRecord.getRecordType());

        // remove _transformed metadata from type. upcoming transformation is based on type.
        neutralRecord.setRecordType(neutralRecord.getRecordType().replaceFirst("_transformed", ""));

        // must set tenantId here, it is used by upcoming transformer.
        neutralRecord.setSourceId(tenantId);

        List<SimpleEntity> xformedEntities = transformer.handle(neutralRecord, errorReportForCollection);
        for (SimpleEntity xformedEntity : xformedEntities) {

            ErrorReport errorReportForNrEntity = new ProxyErrorReport(errorReportForCollection);

            if (xformedEntity.getType().equals("schoolSessionAssociation")) {

                persistSessionAndSchoolSessionAssociation(xformedEntity, errorReportForNrEntity);

            } else {

                entityPersistHandler.handle(xformedEntity, errorReportForNrEntity);

            }

            if (errorReportForNrEntity.hasErrors()) {
                numFailed++;
            }
        }

        return numFailed;
    }

    private void persistSessionAndSchoolSessionAssociation(SimpleEntity xformedEntity,
            ErrorReport errorReportForNrEntity) {
        SimpleEntity session = (SimpleEntity) xformedEntity.getBody().remove("session");

        Entity mongoSession = entityPersistHandler.handle(session, errorReportForNrEntity);
        xformedEntity.getBody().put("sessionId", mongoSession.getEntityId());

        entityPersistHandler.handle(xformedEntity, errorReportForNrEntity);

    }

    private long processOldStyleNeutralRecord(NeutralRecord neutralRecord, long recordNumber, String tenantId,
            ErrorReport errorReportForCollection) {
        long numFailed = 0;

        // only persist if it's in the spring-loaded list of supported record types
        if (persistedCollections.contains(neutralRecord.getRecordType())) {
            LOG.debug("processing old-style neutral record: {}", neutralRecord);

            NeutralRecordEntity nrEntity = Translator.mapToEntity(neutralRecord, recordNumber);
            nrEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), tenantId);

            ErrorReport errorReportForNrEntity = new ProxyErrorReport(errorReportForCollection);
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

        if (neutralRecord.getRecordType().equals("studentTranscriptAssociation")) {
            String studentAcademicRecordId = (String) neutralRecord.getAttributes().remove("studentAcademicRecordId");
            neutralQuery.addCriteria(new NeutralCriteria("studentAcademicRecordId", "=", studentAcademicRecordId));

            stagedNeutralRecords = neutralRecordMongoAccess.getRecordRepository().findAllForJob(
                    neutralRecord.getRecordType() + "_transformed", job.getId(), neutralQuery);
        } else if (neutralRecord.getRecordType().equals("session")) {
            stagedNeutralRecords = neutralRecordMongoAccess.getRecordRepository().findAllForJob("session", job.getId(),
                    neutralQuery);
            encounteredStgCollections.add("session");
        } else {

            stagedNeutralRecords = neutralRecordMongoAccess.getRecordRepository().findAllForJob(
                    neutralRecord.getRecordType() + "_transformed", job.getId(), neutralQuery);

            encounteredStgCollections.add(neutralRecord.getRecordType());
        }
        return stagedNeutralRecords;
    }

    /**
     * returns a name of a collection which we should use in data persistence
     *
     * @param job
     *
     * @return collectionName
     */
    private String getStagedCollectionName(Job job, String collectionName) {

        String collectionNameTransformed = collectionName + "_transformed";

        boolean collectionExists = neutralRecordMongoAccess.getRecordRepository().collectionExistsForJob(
                collectionNameTransformed, job.getId());

        if (collectionExists) {
            if (neutralRecordMongoAccess.getRecordRepository().countForJob(collectionNameTransformed,
                    new NeutralQuery(), job.getId()) > 0) {
                LOG.info("FOUND TRANSFORMED COLLECTION WITH MORE THAN 0 RECORD = " + collectionNameTransformed);
                return (collectionNameTransformed);
            }
        }
        return collectionName;
    }

    private DatabaseLoggingErrorReport createDbErrorReport(String batchJobId, String resourceId) {
        DatabaseLoggingErrorReport dbErrorReport = new DatabaseLoggingErrorReport(batchJobId, BATCH_JOB_STAGE,
                resourceId, batchJobDAO);
        return dbErrorReport;
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
        LOG.error("Exception:", exception);

        Error error = Error.createIngestionError(batchJobId, null, BATCH_JOB_STAGE.getName(), null, null, null,
                FaultType.TYPE_ERROR.getName(), "Exception", exception.getMessage());
        batchJobDAO.saveError(error);
    }

    public void setEntityPersistHandler(EntityPersistHandler entityPersistHandler) {
        this.entityPersistHandler = entityPersistHandler;
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

    public NeutralRecordReadConverter getNeutralRecordReadConverter() {
        return neutralRecordReadConverter;
    }

    public void setNeutralRecordReadConverter(NeutralRecordReadConverter neutralRecordReadConverter) {
        this.neutralRecordReadConverter = neutralRecordReadConverter;
    }

    protected DBCursor getCollectionIterable(String collectionName, String jobId) {
        DBCollection col = neutralRecordMongoAccess.getRecordRepository().getCollectionForJob(collectionName, jobId);

        DBCursor dbcursor = col.find();
        dbcursor.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
        dbcursor.batchSize(1000);

        return dbcursor;
    }
}
