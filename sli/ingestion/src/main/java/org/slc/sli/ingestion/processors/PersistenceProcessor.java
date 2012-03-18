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
import ch.qos.logback.core.FileAppender;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slc.sli.domain.EntityMetadataKey;
import org.slc.sli.ingestion.BatchJob;
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
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.SimpleEntity;
import org.slc.sli.ingestion.transformation.SmooksEdFi2SLITransformer;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.slc.sli.ingestion.validation.LoggingErrorReport;
import org.slc.sli.ingestion.validation.ProxyErrorReport;
import org.slc.sli.util.performance.Profiled;

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
        
        // TODO get the batchjob from the state manager, define the stageName explicitly
        // Get the batch job ID from the exchange
//      String batchJobId = exchange.getIn().getBody(String.class);
//      BatchJobUtils.startStage(batchJobId, this.getClass().getName());
//      BatchJob batchJob = BatchJobUtils.getBatchJob(batchJobId);
        BatchJob job = BatchJobUtils.getBatchJobUsingStateManager(exchange);

        try {
            // TODO BatchJobUtils this won't be needed later
            long startTime = System.currentTimeMillis();

            // TODO this should be determined based on the sourceId
            String idNamespace = job.getProperty("idNamespace", "SLI");

            this.exchange = exchange;

            // Indicate Camel processing
            LOG.info("processing persistence: {}", job);

            // Create the database for this job.
            neutralRecordMongoAccess.changeMongoTemplate(job.getId());

            for (IngestionFileEntry fe : job.getFiles()) {

                // TODO BatchJobUtil.startMetric(job.getId(), this.getClass().getName(), fe.getFileName())

                ErrorReport errorReportForFile = null;
                try {
                    errorReportForFile = processIngestionStream(fe, idNamespace, getTransformedCollections(), new HashSet<String>());

                } catch (IOException e) {
                    job.getFaultsReport().error("Internal error reading neutral representation of input file.", this);
                }

                // TODO Add a recordCount variables to IngestionFileEntry for each file (i.e. original, neutral, ...) - see if things have changed with transform
                // TODO BatchJobUtil.stopMetric(job.getId(), this.getClass().getName(), fe.getFileName(), fe.getNeutralRecordCount(), fe.getFaultsReport.getFaults().size())

                // Inform user if there were any record-level errors encountered
                if (errorReportForFile != null && errorReportForFile.hasErrors()) {
                    job.getFaultsReport().error(
                            "Errors found for input file \"" + fe.getFileName() + "\". See \"error." + fe.getFileName()
                                    + "\" for details.", this);
                }

            }

            // Update Camel Exchange processor output result
            exchange.getIn().setBody(job);
            exchange.getIn().setHeader("IngestionMessageType", MessageType.DONE.name());

            long endTime = System.currentTimeMillis();

            BatchJobUtils.saveBatchJobUsingStateManager(job);
            // TODO When the interface firms up we should set the stage stopTimeStamp in job before saving to db, rather than after really
            BatchJobUtils.stopStage(job.getId(), this.getClass().getName());

            // Log statistics
            LOG.info("Persisted Ingestion files for batch job [{}] in {} ms", job, endTime - startTime);

        } catch (Exception exception) {
            exchange.getIn().setHeader("ErrorMessage", exception.toString());
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Exception:", exception);

        } finally {
            // Drop the database for this job.
            neutralRecordMongoAccess.dropDatabase();
        }
    }


    /**
     * Consumes the SLI Neutral records file contained by ingestionFileEntry, parses, and persists
     * the SLI Ingestion instances. Validation errors will go to an error file that corresponds with
     * the original input file for this IngestionFileEntry.
     *
     * @param ingestionFileEntry
     * @param idNamespace
     * @throws IOException
     */
    public ErrorReport processIngestionStream(IngestionFileEntry ingestionFileEntry, String idNamespace,
            ArrayList<String> transformedCollections, Set<String> processedStagedCollections) throws IOException {
        return processIngestionStream(ingestionFileEntry.getNeutralRecordFile(), ingestionFileEntry.getFileName(),
                idNamespace, transformedCollections, processedStagedCollections);
    }

    /**
     * Consumes the SLI Neutral records file, parses, and persists the SLI Ingestion instances.
     * Validation errors will go to an error file that corresponds with the file passed in.
     *
     * @param neutralRecordsFile
     * @param idNamespace
     * @throws IOException
     */
    public ErrorReport processIngestionStream(File neutralRecordsFile, String idNamespace) throws IOException {
        return processIngestionStream(neutralRecordsFile, neutralRecordsFile.getName(), idNamespace,
                new ArrayList<String>(), new HashSet<String>());
    }

    private ErrorReport processIngestionStream(File neutralRecordsFile, String originalInputFileName,
            String idNamespace, ArrayList<String> transformedCollections, Set<String> processedStagedCollections)
            throws IOException {

        long recordNumber = 0;
        long numFailed = 0;

        ch.qos.logback.classic.Logger errorLogger = createErrorLoggerForFile(originalInputFileName);
        ErrorReport recordLevelErrorsInFile = new LoggingErrorReport(errorLogger);

        NeutralRecordFileReader nrFileReader = null;
        try {
            nrFileReader = new NeutralRecordFileReader(neutralRecordsFile);

            while (nrFileReader.hasNext()) {

                recordNumber++;

                NeutralRecord neutralRecord = nrFileReader.next();

                if (!transformedCollections.contains(neutralRecord.getRecordType())) {
                    if (persistedCollections.contains(neutralRecord.getRecordType())) {
                        //this doesn't exist in collection, persist

                        LOG.debug("processing " + neutralRecord);

                        // map NeutralRecord to Entity
                        NeutralRecordEntity neutralRecordEntity = Translator.mapToEntity(neutralRecord, recordNumber);

                        neutralRecordEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), idNamespace);

                        ErrorReport errorReport = new ProxyErrorReport(recordLevelErrorsInFile);
                        obsoletePersistHandler.handle(neutralRecordEntity, new ProxyErrorReport(errorReport));

                        if (errorReport.hasErrors()) {
                            numFailed++;
                        }
                    }
                } else {
                    //process collection of the entities from db
                    LOG.debug("processing staged collection: " + neutralRecord.getRecordType());

                    if (!processedStagedCollections.contains(neutralRecord.getRecordType())) {
                        //collection wasn't processed yet

                        processedStagedCollections.add(neutralRecord.getRecordType());

                        Iterable<NeutralRecord> neutralRecordData = neutralRecordMongoAccess.getRecordRepository().findAll(neutralRecord.getRecordType() + "_transformed");

                        for (NeutralRecord nr : neutralRecordData) {
                            nr.setSourceId(idNamespace);
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
                    LOG.debug(errorLog.getName() + " is empty, deleting");
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

        Iterable<String> data = neutralRecordMongoAccess.getRecordRepository().getTemplate().getCollectionNames();
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

        FileAppender appender = new FileAppender();
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
