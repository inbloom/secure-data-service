package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.IOException;

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
import org.slc.sli.ingestion.handler.EntityPersistHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
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

    private EntityPersistHandler entityPersistHandler;

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

            for (IngestionFileEntry fe : job.getFiles()) {

                // TODO BatchJobUtil.startMetric(job.getId(), this.getClass().getName(), fe.getFileName())

                ErrorReport errorReportForFile = null;
                try {
                    errorReportForFile = processIngestionStream(fe, idNamespace);

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
        }
    }

    /**
     * Consumes the SLI Neutral records file contained by ingestionFileEntry, parses, and persists
     * the SLI Ingestion instances. Validation errors will go to an error file that corresponds with
     * the original input file for this IngestionFileEntry.
     *
     * @param ingestionFileEntry
     * @throws IOException
     */
    public ErrorReport processIngestionStream(IngestionFileEntry ingestionFileEntry, String idNamespace) throws IOException {

        return processIngestionStream(ingestionFileEntry.getNeutralRecordFile(), ingestionFileEntry.getFileName(), idNamespace);
    }

    /**
     * Consumes the SLI Neutral records file, parses, and persists the SLI Ingestion instances.
     * Validation errors will go to an error file that corresponds with the file passed in.
     *
     * @param neutralRecordsFile
     * @throws IOException
     */
    public ErrorReport processIngestionStream(File neutralRecordsFile, String idNamespace) throws IOException {

        return processIngestionStream(neutralRecordsFile, neutralRecordsFile.getName());
    }

    private ErrorReport processIngestionStream(File neutralRecordsFile, String originalInputFileName, String idNamespace)
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

                LOG.debug("processing " + neutralRecord);

                // map NeutralRecord to Entity
                NeutralRecordEntity neutralRecordEntity = Translator.mapToEntity(neutralRecord, recordNumber);
                ErrorReport errorReport = new ProxyErrorReport(recordLevelErrorsInFile);
                neutralRecordEntity.setMetaDataField(EntityMetadataKey.ID_NAMESPACE.getKey(), idNamespace);
                entityPersistHandler.handle(neutralRecordEntity, errorReport);
                if (errorReport.hasErrors()) {
                    numFailed++;
                }
            }
        } catch (Exception e) {
            recordLevelErrorsInFile.fatal("Fatal problem saving records to database.", PersistenceProcessor.class);
            LOG.error("Exception when attempting to ingest NeutralRecords in: " + neutralRecordsFile + "./n", e);
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

                // number of records considered
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

}
