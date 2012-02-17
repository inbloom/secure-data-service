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

import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.handler.EntityPersistHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.FileRecordParser;
import org.slc.sli.ingestion.util.NeutralRecordJsonStreamer;
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

        try {
            long startTime = System.currentTimeMillis();

            BatchJob job = exchange.getIn().getBody(BatchJob.class);

            this.exchange = exchange;

            // Indicate Camel processing
            LOG.info("processing persistence: {}", job);

            for (IngestionFileEntry fe : job.getFiles()) {

                ErrorReport errorReportForFile = null;
                try {
                    exchange.setProperty("records.processed", 0);

                    errorReportForFile = processIngestionStream(fe);

                } catch (IOException e) {
                    job.getFaultsReport().error("Internal error reading neutral representation of input file.", this);
                }

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
    public ErrorReport processIngestionStream(IngestionFileEntry ingestionFileEntry) throws IOException {

        return processIngestionStream(ingestionFileEntry.getNeutralRecordFile(), ingestionFileEntry.getFileName());
    }

    /**
     * Consumes the SLI Neutral records file, parses, and persists the SLI Ingestion instances.
     * Validation errors will go to an error file that corresponds with the file passed in.
     *
     * @param neutralRecordsFile
     * @throws IOException
     */
    public ErrorReport processIngestionStream(File neutralRecordsFile) throws IOException {

        return processIngestionStream(neutralRecordsFile, neutralRecordsFile.getName());
    }

    private ErrorReport processIngestionStream(File neutralRecordsFile, String originalInputFileName)
            throws IOException {

        int recordNumber = 0;

        ch.qos.logback.classic.Logger errorLogger = createErrorLoggerForFile(originalInputFileName);
        ErrorReport recordLevelErrorsInFile = new LoggingErrorReport(errorLogger);

        FileRecordParser<NeutralRecord> nrFileReader = new NeutralRecordJsonStreamer(neutralRecordsFile);

        NeutralRecord neutralRecord = nrFileReader.parseRecord();

        try {
            while (neutralRecord != null) {

                recordNumber++;
                LOG.debug("processing " + neutralRecord);

                // map NeutralRecord to Entity
                NeutralRecordEntity neutralRecordEntity = Translator.mapToEntity(neutralRecord, recordNumber);

                entityPersistHandler.handle(neutralRecordEntity, new ProxyErrorReport(recordLevelErrorsInFile));

                neutralRecord = nrFileReader.parseRecord();

            }

            if (exchange != null) {
                LOG.info("Setting records.processed value on exchange header");
                exchange.setProperty("records.processed", recordNumber);
            }

        } catch (Exception e) {
            recordLevelErrorsInFile.fatal("Fatal problem saving records to database.", PersistenceProcessor.class);
            LOG.error("Exception when attempting to ingest NeutralRecords in: " + neutralRecordsFile + "./n", e);
        } finally {
            nrFileReader.close();
            errorLogger.detachAndStopAllAppenders();
        }

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
