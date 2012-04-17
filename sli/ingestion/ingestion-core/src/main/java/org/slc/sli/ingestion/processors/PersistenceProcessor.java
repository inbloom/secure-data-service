package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

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
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.handler.EntityPersistHandler;
import org.slc.sli.ingestion.handler.NeutralRecordEntityPersistHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.SmooksEdFi2SLITransformer;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.LoggingFaultReport;
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

    private static final Logger LOG = LoggerFactory.getLogger(PersistenceProcessor.class);

    @Autowired
    SmooksEdFi2SLITransformer transformer;

    private Set<String> persistedCollections;

    private EntityPersistHandler entityPersistHandler;

    private NeutralRecordEntityPersistHandler obsoletePersistHandler;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    private Exchange exchange;

    @Autowired
    private LocalFileSystemLandingZone lz;

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
            exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
        }
        BatchJobDAO batchJobDAO = new BatchJobMongoDA();
        NewBatchJob newJob = batchJobDAO.findBatchJobById(batchJobId);
        Stage stage = new Stage();
        newJob.getStages().add(stage);
        stage.setStageName(BatchJobStageType.PERSISTENCE_PROCESSING.getName());
        stage.startStage();
        batchJobDAO.saveBatchJob(newJob);
        Job job = exchange.getIn().getBody(BatchJob.class);

        try {
            long startTime = System.currentTimeMillis();

            // TODO this should be determined based on the sourceId
            String tenantId = job.getProperty("tenantId", "SLI");

            this.exchange = exchange;

            // Indicate Camel processing
            LOG.info("processing persistence: {}", job);

            // Create the database for this job.
            neutralRecordMongoAccess.registerBatchId(job.getId());

            for (IngestionFileEntry fe : job.getFiles()) {
                String filename = fe.getFileName();
                Metrics metric =  new Metrics();
                metric.startMetric();
                metric.setResourceId(filename);
                if (stage.getMetrics() == null) {
                    List<Metrics> metricsList  = new ArrayList<Metrics>();
                    stage.setMetrics(metricsList);
                }
                stage.getMetrics().add(metric);
                batchJobDAO.saveBatchJob(newJob);

                ErrorReport errorReportForFile = null;
                try {
                    errorReportForFile = processIngestionStream(batchJobId, fe, tenantId, getTransformedCollections(), new HashSet<String>());

                } catch (IOException e) {
                    job.getFaultsReport().error("Internal error reading neutral representation of input file.", this);
                    BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.PERSISTENCE_PROCESSING, FaultType.TYPE_ERROR.getName(), "Exception", e.getMessage());
                }

                // Inform user if there were any record-level errors encountered
                if (errorReportForFile != null && errorReportForFile.hasErrors()) {
                    job.getFaultsReport().error(
                            "Errors found for input file \"" + fe.getFileName() + "\". See \"error." + fe.getFileName()
                                    + "\" for details.", this);

                    for (Fault fault : job.getFaultsReport().getFaults()) {
                        String faultMessage = fault.getMessage();
                        BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.PERSISTENCE_PROCESSING, FaultType.TYPE_ERROR.getName(), "Error", faultMessage);
                    }
                }

                String processedPropName = filename + ".records.processed";
                String failedPropName = filename + ".records.failed";
                long processedCount = (Long) exchange.getProperty(processedPropName);
                long failedCount = (Long) exchange.getProperty(failedPropName);
                metric.setRecordCount(processedCount);
                metric.setErrorCount(failedCount);
                metric.stopMetric();
                batchJobDAO.saveBatchJob(newJob);
            }

            // Update Camel Exchange processor output result
            exchange.getIn().setBody(job);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.DONE.name());

            long endTime = System.currentTimeMillis();

            // Log statistics
            LOG.info("Persisted Ingestion files for batch job [{}] in {} ms", job, endTime - startTime);
        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Exception:", exception);
            BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.PERSISTENCE_PROCESSING, FaultType.TYPE_ERROR.getName(), "Exception",  exception.getMessage());

        } finally {
            neutralRecordMongoAccess.cleanupGroupedCollections();
        }

        stage.stopStage();
        batchJobDAO.saveBatchJob(newJob);

    }


    /**
     * Consumes the SLI Neutral records file contained by ingestionFileEntry, parses, and persists
     * the SLI Ingestion instances. Validation errors will go to an error file that corresponds with
     * the original input file for this IngestionFileEntry.
     *
     * @param ingestionFileEntry
     * @param tenantId
     * @throws IOException
     */
    public ErrorReport processIngestionStream(String batchJobId, IngestionFileEntry ingestionFileEntry, String tenantId,
            ArrayList<String> transformedCollections, Set<String> processedStagedCollections) throws IOException {
        return processIngestionStream(batchJobId, ingestionFileEntry.getNeutralRecordFile(), ingestionFileEntry.getFileName(),
                tenantId, transformedCollections, processedStagedCollections);
    }

    /**
     * Consumes the SLI Neutral records file, parses, and persists the SLI Ingestion instances.
     * Validation errors will go to an error file that corresponds with the file passed in.
     *
     * @param neutralRecordsFile
     * @param tenantId
     * @throws IOException
     */
    public ErrorReport processIngestionStream(String batchJobId, File neutralRecordsFile, String tenantId) throws IOException {
        return processIngestionStream(batchJobId, neutralRecordsFile, neutralRecordsFile.getName(), tenantId,
                new ArrayList<String>(), new HashSet<String>());
    }

    private ErrorReport processIngestionStream(String batchJobId, File neutralRecordsFile, String originalInputFileName,
            String tenantId, ArrayList<String> transformedCollections, Set<String> processedStagedCollections)
            throws IOException {

        long recordNumber = 0;
        long numFailed = 0;

        ch.qos.logback.classic.Logger errorLogger = createErrorLoggerForFile(originalInputFileName);
        LoggingFaultReport recordLevelErrorsInFile = new LoggingFaultReport(errorLogger);

        NeutralRecordFileReader nrFileReader = null;
        try {
            nrFileReader = new NeutralRecordFileReader(neutralRecordsFile);

            while (nrFileReader.hasNext()) {

                recordNumber++;

                NeutralRecord neutralRecord = nrFileReader.next();

                if (!transformedCollections.contains(neutralRecord.getRecordType())) {
                    if (persistedCollections.contains(neutralRecord.getRecordType())) {
                        //this doesn't exist in collection, persist

                        LOG.debug("processing {}", neutralRecord);

                        // map NeutralRecord to Entity
                        NeutralRecordEntity neutralRecordEntity = Translator.mapToEntity(neutralRecord, recordNumber);

                        neutralRecordEntity.setMetaDataField(EntityMetadataKey.TENANT_ID.getKey(), tenantId);

                        ErrorReport errorReport = new ProxyErrorReport(recordLevelErrorsInFile);
                        obsoletePersistHandler.handle(neutralRecordEntity, new ProxyErrorReport(errorReport));

                        if (errorReport.hasErrors()) {
                            numFailed++;
                            for (Fault fault : recordLevelErrorsInFile.getFaults()) {
                                String faultMessage = fault.getMessage();
                                if (faultMessage != null) {
                                    faultMessage = faultMessage.replaceAll("\r|\n", " ");
                                }
                                String faultLevel  = fault.isError() ? FaultType.TYPE_ERROR.getName() : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";
                                BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.PERSISTENCE_PROCESSING, faultLevel, "Error", faultMessage);
                            }
                        }
                    }
                } else {
                    //process collection of the entities from db
                    LOG.debug("processing staged collection: {}", neutralRecord.getRecordType());
                    if (!processedStagedCollections.contains(neutralRecord.getRecordType())) {
                        //collection wasn't processed yet

                        Iterable<NeutralRecord> neutralRecordData = null;

                        if (neutralRecord.getRecordType().equals("studentTranscriptAssociation")) {
                            NeutralQuery neutralQuery = new NeutralQuery();
                            String studentAcademicRecordId = (String) neutralRecord.getAttributes().remove("studentAcademicRecordId");
                            neutralQuery.addCriteria(new NeutralCriteria("studentAcademicRecordId", "=", studentAcademicRecordId));
                            neutralRecordData = neutralRecordMongoAccess.getRecordRepository().findAll(neutralRecord.getRecordType() + "_transformed", neutralQuery);
                        } else {
                            processedStagedCollections.add(neutralRecord.getRecordType());
                            neutralRecordData = neutralRecordMongoAccess.getRecordRepository().findAll(neutralRecord.getRecordType() + "_transformed");
                        }

                        if (neutralRecordData != null) {
                            for (NeutralRecord nr : neutralRecordData) {
                                nr.setSourceId(tenantId);
                                nr.setRecordType(neutralRecord.getRecordType());
                                List<SimpleEntity> result = transformer.handle(nr, recordLevelErrorsInFile);
                                for (SimpleEntity entity : result) {
                                    ErrorReport errorReport = new ProxyErrorReport(recordLevelErrorsInFile);
                                    entityPersistHandler.handle(entity, errorReport);

                                    if (errorReport.hasErrors()) {
                                        numFailed++;
                                    }
                                }

                                if (recordLevelErrorsInFile.hasErrors()) {
                                    numFailed++;
                                }
                                for (Fault fault : recordLevelErrorsInFile.getFaults()) {
                                    String faultMessage = fault.getMessage();
                                    if (faultMessage != null) {
                                        faultMessage = faultMessage.replaceAll("\r|\n", " ");
                                    }
                                    String faultLevel  = fault.isError() ? FaultType.TYPE_ERROR.getName() : fault.isWarning() ? FaultType.TYPE_WARNING.getName() : "Unknown";
                                    BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.PERSISTENCE_PROCESSING, faultLevel, "Error", faultMessage);
                                }
                            }
                        } else {
                            numFailed++;
                        }
                    }
                }

            }
        } catch (Exception e) {
            recordLevelErrorsInFile.fatal("Fatal problem saving records to database.", PersistenceProcessor.class);
            LOG.error("Exception when attempting to ingest NeutralRecords in: " + neutralRecordsFile + ".\n", e);
        } finally {
            if (nrFileReader != null) {
                nrFileReader.close();
            }

            errorLogger.detachAndStopAllAppenders();

            // if error log exists and is 0L bytes, delete
            File errorLog = lz.getFile(errorLogger.getName());
            if (errorLog != null) {
                if (errorLog.length() == 0L) {
                    LOG.debug("{} is empty, deleting", errorLog.getName());
                    if (!errorLog.delete()) {
                        LOG.error(errorLog.getName() + " is empty but could not be deleted");
                    }
                }
            }

                // TODO store record counts in batch job db
            if (exchange != null) {
                LOG.info("Setting records.processed value on exchange header");

                long processedSoFar = 0;
                Long processed = exchange.getProperty("records.processed", Long.class);

                if (processed != null) {
                    processedSoFar = processed.longValue();
                }

                exchange.setProperty("records.processed", Long.valueOf(processedSoFar + recordNumber));
                exchange.setProperty(originalInputFileName + ".records.processed", recordNumber);

                // number of records processed successfully
                exchange.setProperty(originalInputFileName + ".records.passed", recordNumber - numFailed);

                // number of records not processed successfully
                exchange.setProperty(originalInputFileName + ".records.failed", numFailed);
            }
        }
        neutralRecordsFile.delete();

        return recordLevelErrorsInFile;
    }

    public void setEntityPersistHandler(EntityPersistHandler entityPersistHandler) {
        this.entityPersistHandler = entityPersistHandler;
    }

    /**
     * returns list of collections that were created as a result transformation feature
     *
     * @return transformedCollections
     */
    private ArrayList<String> getTransformedCollections() {

        ArrayList<String> collections = new ArrayList<String>();

        Iterable<String> data = neutralRecordMongoAccess.getRecordRepository().getCollectionNames();
        Iterator<String> iter = data.iterator();

        String collectionName;

        while (iter.hasNext()) {
            collectionName = iter.next();

            if (collectionName.endsWith("_transformed")) {
                collections.add(collectionName.substring(0, collectionName.length() - "_transformed".length()));
            }
        }

        return collections;
    }

    private ch.qos.logback.classic.Logger createErrorLoggerForFile(String fileName) throws IOException {

        final String loggerName = "error." + fileName + "." + System.currentTimeMillis() + ".log";

        File logFile = lz.createFile(loggerName);

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setContext(lc);
        patternLayout.setPattern("%msg%n");
        patternLayout.start();

        FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>();
        appender.setContext(lc);
        appender.setFile(logFile.getAbsolutePath()); // tricky if we're not localFS...
        appender.setLayout(patternLayout);
        appender.start();

        ch.qos.logback.classic.Logger logger = lc.getLogger(loggerName);
        logger.addAppender(appender);

        return logger;
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
