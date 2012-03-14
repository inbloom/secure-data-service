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

import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.BatchJob;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordEntity;
import org.slc.sli.ingestion.NeutralRecordFileReader;
import org.slc.sli.ingestion.Translator;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.StagingMongoTemplate;
import org.slc.sli.ingestion.handler.EntityPersistHandler;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.measurement.ExtractBatchJobIdToContext;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.transformation.SmooksEdFi2SLITransformer;
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

    private EntityPersistHandler entityPersistHandler;

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

        try {
            long startTime = System.currentTimeMillis();

            BatchJob job = exchange.getIn().getBody(BatchJob.class);

            this.exchange = exchange;

            // Indicate Camel processing
            LOG.info("processing persistence: {}", job);

            neutralRecordMongoAccess = TransformationFactory.getNeutralRecordMongoAccess();
            neutralRecordMongoAccess.getRecordRepository().setTemplate(new StagingMongoTemplate(neutralRecordMongoAccess.getRecordRepository().getTemplate().getDatabasePrefix(), job.getId(), neutralRecordMongoAccess.getRecordRepository().getTemplate().getNeutralRecordMappingConverter()));

            transformedCollections = getTransformedCollections();

            for (IngestionFileEntry fe : job.getFiles()) {

                ErrorReport errorReportForFile = null;
                try {
                    errorReportForFile = processIngestionStream(fe, getTransformedCollections(), new HashSet<String>());

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
     * @param arrayList2
     * @param arrayList
     * @throws IOException
     */
    public ErrorReport processIngestionStream(IngestionFileEntry ingestionFileEntry, ArrayList<String> transformedCollections, Set<String> processedStagedCollections) throws IOException {

        return processIngestionStream(ingestionFileEntry.getNeutralRecordFile(), ingestionFileEntry.getFileName(), transformedCollections, processedStagedCollections);
    }

    /**
     * Consumes the SLI Neutral records file, parses, and persists the SLI Ingestion instances.
     * Validation errors will go to an error file that corresponds with the file passed in.
     *
     * @param neutralRecordsFile
     * @throws IOException
     */
    public ErrorReport processIngestionStream(File neutralRecordsFile) throws IOException {

        return processIngestionStream(neutralRecordsFile, neutralRecordsFile.getName(), new ArrayList<String>(), new HashSet<String>());
    }

    private ErrorReport processIngestionStream(File neutralRecordsFile, String originalInputFileName, ArrayList<String> transformedCollections, Set<String> processedStagedCollections)
            throws IOException {

        long recordNumber = 0;

        ch.qos.logback.classic.Logger errorLogger = createErrorLoggerForFile(originalInputFileName);
        ErrorReport recordLevelErrorsInFile = new LoggingErrorReport(errorLogger);

        NeutralRecordFileReader nrFileReader = null;
        try {
            nrFileReader = new NeutralRecordFileReader(neutralRecordsFile);

            while (nrFileReader.hasNext()) {

                recordNumber++;

                NeutralRecord neutralRecord = nrFileReader.next();

                if (!transformedCollections.contains(neutralRecord.getRecordType())) {
                    //this doesn't exist in collection, persist

                    LOG.debug("processing " + neutralRecord);

                    // map NeutralRecord to Entity
                    NeutralRecordEntity neutralRecordEntity = Translator.mapToEntity(neutralRecord, recordNumber);
                    entityPersistHandler.handle(neutralRecordEntity, new ProxyErrorReport(recordLevelErrorsInFile));

                } else {
                    //process collection of the entities from db
                    LOG.debug("processing staged collection: " + neutralRecord.getRecordType());

                    if (!processedStagedCollections.contains(neutralRecord.getRecordType())) {
                        //collection wasn't processed yet

                        processedStagedCollections.add(neutralRecord.getRecordType());

                        Iterable<NeutralRecord> neutralRecordData = neutralRecordMongoAccess.getRecordRepository().findAll(neutralRecord.getRecordType() + "_transformed");

                        for (NeutralRecord nr : neutralRecordData) {
                            nr.setRecordType(neutralRecord.getRecordType());
                            List<? extends Entity> result = transformer.handle(nr);
                            NeutralRecordEntity neutralRecordEntity = (NeutralRecordEntity) result.get(0);
                            entityPersistHandler.handle(neutralRecordEntity, new ProxyErrorReport(recordLevelErrorsInFile));
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

            if (exchange != null) {
                LOG.info("Setting records.processed value on exchange header");

                long processedSoFar = 0;
                Long processed = exchange.getProperty("records.processed", Long.class);

                if (processed != null) {
                    processedSoFar = processed.longValue();
                }

                exchange.setProperty("records.processed", Long.valueOf(processedSoFar + recordNumber));
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
}
