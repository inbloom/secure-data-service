package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.performance.Profiled;
import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.handler.EntityPersistHandler;
import org.slc.sli.ingestion.handler.NeutralRecordEntityPersistHandler;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.SmooksEdFi2SLITransformer;
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
public class PersistenceProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.PERSISTENCE_PROCESSOR;

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceProcessor.class);

    @Autowired
    SmooksEdFi2SLITransformer transformer;

    // spring-loaded list of supported collections
    private Set<String> persistedCollections;

    private EntityPersistHandler entityPersistHandler;

    private NeutralRecordEntityPersistHandler obsoletePersistHandler;

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

        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            handleNoBatchJobIdInExchange(exchange);
        } else {

            Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

            NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);
            newJob.getStages().add(stage);

            try {
                LOG.info("processing persistence: {}", newJob);

                // sets jobid in thread local
                neutralRecordMongoAccess.registerBatchId(newJob.getId());

            // Ensure database indexing for this job. Tom Shewchuk 04/21/2012.

                for (ResourceEntry resource : newJob.getResourceEntries()) {

                    if (FileFormat.NEUTRALRECORD.getCode().equalsIgnoreCase(resource.getResourceFormat())) {

                        Metrics metrics = Metrics.createAndStart(resource.getResourceId());
                        stage.getMetrics().add(metrics);

                        if (resource.getResourceName() != null) {
                            try {

                                processNeutralRecordsFile(new File(resource.getResourceName()), getTenantId(newJob),
                                        batchJobId, metrics);
                            } catch (IOException e) {
                                Error error = Error.createIngestionError(batchJobId, BATCH_JOB_STAGE.getName(), resource.getResourceId(),
                                        null, null, null, FaultType.TYPE_ERROR.getName(), "Exception", e.getMessage());
                                batchJobDAO.saveError(error);
                            }
                        }
                        metrics.stopMetric();
                    }
                }
                exchange.getIn().setHeader("IngestionMessageType", MessageType.DONE.name());

            } catch (Exception exception) {
                handleProcessingExceptions(exception, exchange, batchJobId);
            } finally {
                stage.stopStage();
                batchJobDAO.saveBatchJob(newJob);

                cleanupStagingDbForJob();
            }
        }
    }

    private void processNeutralRecordsFile(File neutralRecordsFile, String tenantId, String batchJobId, Metrics metrics)
            throws IOException {

        long recordNumber = 0;
        long numFailed = 0;

        ErrorReport errorReportForNrFile = createDbErrorReport(batchJobId, neutralRecordsFile.getName());

        NeutralRecordFileReader nrFileReader = null;
        String fatalErrorMessage = "ERROR: Fatal problem saving records to database.\n";
        try {
            Set<String> encounteredStgCollections = new HashSet<String>();

            nrFileReader = new NeutralRecordFileReader(neutralRecordsFile);
            while (nrFileReader.hasNext()) {

                recordNumber++;

                NeutralRecord neutralRecord = nrFileReader.next();

                fatalErrorMessage = "ERROR: Fatal problem saving records to database: \n" + "\tEntity\t"
                        + neutralRecord.getRecordType() + "\n" + "\tIdentifier\t" + (String) neutralRecord.getLocalId()
                        + "\n";

                // TODO: don't need to construct this collection every time. construct once.
                if (getTransformedCollections().contains(neutralRecord.getRecordType())) {

                    numFailed += processTransformableNeutralRecord(neutralRecord, tenantId, encounteredStgCollections,
                            errorReportForNrFile);
                } else {

                    numFailed += processOldStyleNeutralRecord(neutralRecord, recordNumber, tenantId,
                            errorReportForNrFile);
                }
            }
        } catch (Exception e) {
            errorReportForNrFile.fatal(fatalErrorMessage, PersistenceProcessor.class);
            LOG.error("Exception when attempting to ingest NeutralRecords in: " + neutralRecordsFile + ".\n", e);
        } finally {
            if (nrFileReader != null) {
                nrFileReader.close();
            }

            metrics.setRecordCount(recordNumber);
            metrics.setErrorCount(numFailed);
        }
    }

    private long processTransformableNeutralRecord(NeutralRecord neutralRecord, String tenantId,
            Set<String> encounteredStgCollections, ErrorReport errorReportForNrFile) {
        long numFailed = 0;

        // only proceed if we haven't proceesed this record type yet
        if (!encounteredStgCollections.contains(neutralRecord.getRecordType())) {
            LOG.debug("processing transformable neutral record: {}", neutralRecord.getRecordType());

            Iterable<NeutralRecord> stagedNeutralRecords = getStagedNeutralRecords(neutralRecord,
                    encounteredStgCollections);

            if (stagedNeutralRecords.iterator().hasNext()) {

                for (NeutralRecord stagedNeutralRecord : stagedNeutralRecords) {
                    stagedNeutralRecord.setSourceId(tenantId);

                    // TODO: why is this necessary?
                    stagedNeutralRecord.setRecordType(neutralRecord.getRecordType());

                    List<SimpleEntity> xformedEntities = transformer.handle(stagedNeutralRecord, errorReportForNrFile);
                    for (SimpleEntity xformedEntity : xformedEntities) {

                        ErrorReport errorReportForNrEntity = new ProxyErrorReport(errorReportForNrFile);
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

    private long processOldStyleNeutralRecord(NeutralRecord neutralRecord, long recordNumber, String tenantId,
            ErrorReport errorReportForNrFile) {
        long numFailed = 0;

        // only persist if it's in the spring-loaded list of supported record types
        if (persistedCollections.contains(neutralRecord.getRecordType())) {
            LOG.debug("processing old-style neutral record: {}", neutralRecord);

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

    private Iterable<NeutralRecord> getStagedNeutralRecords(NeutralRecord neutralRecord,
            Set<String> encounteredStgCollections) {

        Iterable<NeutralRecord> stagedNeutralRecords = Collections.emptyList();

        if (neutralRecord.getRecordType().equals("studentTranscriptAssociation")) {

            NeutralQuery neutralQuery = new NeutralQuery();
            String studentAcademicRecordId = (String) neutralRecord.getAttributes().remove("studentAcademicRecordId");
            neutralQuery.addCriteria(new NeutralCriteria("studentAcademicRecordId", "=", studentAcademicRecordId));
            stagedNeutralRecords = neutralRecordMongoAccess.getRecordRepository().findAll(
                    neutralRecord.getRecordType() + "_transformed", neutralQuery);
        } else {

            stagedNeutralRecords = neutralRecordMongoAccess.getRecordRepository().findAll(
                    neutralRecord.getRecordType() + "_transformed");
            encounteredStgCollections.add(neutralRecord.getRecordType());
        }
        return stagedNeutralRecords;
    }

    /**
     * returns list of collections that were created as a result transformation feature
     *
     * @return transformedCollections
     */
    private List<String> getTransformedCollections() {
        List<String> collections = new ArrayList<String>();

        Iterable<String> data = neutralRecordMongoAccess.getRecordRepository().getCollectionNames();
        Iterator<String> iter = data.iterator();

        while (iter.hasNext()) {
            String collectionName = iter.next();

            if (collectionName.endsWith("_transformed")) {
                collections.add(collectionName.substring(0, collectionName.length() - "_transformed".length()));
            }
        }
        return collections;
    }

    private DatabaseLoggingErrorReport createDbErrorReport(String batchJobId, String resourceId) {
        DatabaseLoggingErrorReport dbErrorReport = new DatabaseLoggingErrorReport(batchJobId, BATCH_JOB_STAGE,
                resourceId, batchJobDAO);
        return dbErrorReport;
    }

    private void cleanupStagingDbForJob() {
        neutralRecordMongoAccess.cleanupGroupedCollections();
    }

    private static String getTenantId(NewBatchJob newJob) {
        // TODO this should be determined based on the sourceId
        String tenantId = newJob.getProperty("tenantId");
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

        Error error = Error.createIngestionError(batchJobId, BATCH_JOB_STAGE.getName(), null, null, null, null,
                FaultType.TYPE_ERROR.getName(), "Exception", exception.getMessage());
        batchJobDAO.saveError(error);
    }

    // TODO: currently only called by unit tests.... GET RID OF IT!!!
    public void processIngestionStream(String batchJobId, File neutralRecordsFile, String tenantId) throws IOException {
        processNeutralRecordsFile(neutralRecordsFile, tenantId, batchJobId, null);
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

}
