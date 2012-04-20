package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;
import org.slc.sli.ingestion.model.da.BatchJobMongoDAStatus;
import org.slc.sli.ingestion.queues.MessageType;

/**
 * Transforms body from ControlFile to ControlFileDescriptor type.
 *
 * @author bsuzuki
 *
 */
public class JobReportingProcessor implements Processor {

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(JobReportingProcessor.class);

    private LandingZone landingZone;

    private BatchJobDAO batchJobDAO;

    private boolean reportToLog = false;

    private static final String STR_TIMESTAMP_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS";
    private static final FastDateFormat FORMATTER = FastDateFormat.getInstance(STR_TIMESTAMP_FORMAT);

    public JobReportingProcessor(LandingZone lz) {
        this.landingZone = lz;
        this.batchJobDAO = new BatchJobMongoDA();
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        // get job from the batch job db
        String batchJobId = getBatchJobId(exchange);

        if (batchJobId != null) {

            NewBatchJob job = batchJobDAO.findBatchJobById(batchJobId);

            Stage stage = startAndGetStage(job);

            File logFile = landingZone.getLogFile(batchJobId);

            PrintWriter jobReportWriter = null;
            try {
                jobReportWriter = new PrintWriter(new FileWriter(landingZone.getLogFile(batchJobId)));
            } catch (IOException e) {
                LOG.error("Error:", "Unable to open job report file " + logFile.getCanonicalFile());
                reportToLog = true;
            }

            writeInfoLine(jobReportWriter, "jobId: " + job.getId());

            long totalProcessed =
                    logBatchJobPersistenceMetrics(job, jobReportWriter);

            logBatchJobProperties(job, jobReportWriter);

            boolean success =
                    logBatchJobErrorsAndWarnings(job.getId(), batchJobDAO, jobReportWriter);

            if (success) {
                writeInfoLine(jobReportWriter, "All records processed successfully.");
                job.setStatus(BatchJobStatusType.COMPLETED_SUCCESSFULLY.getName());
            } else {
                writeInfoLine(jobReportWriter, "Not all records were processed completely due to errors.");
                job.setStatus(BatchJobStatusType.COMPLETED_WITH_ERRORS.getName());
            }

            writeInfoLine(jobReportWriter, "Processed " + totalProcessed + " records.");

            // clean up after ourselves
            if (jobReportWriter != null) {
                jobReportWriter.close();
            }

            stage.stopStage();

            batchJobDAO.saveBatchJob(job);

        } else {
            missingBatchJobIdError(exchange);
        }

   }

    private void writeInfoLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "INFO", string);
    }

    private void writeErrorLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "ERROR", string);
    }

    private void writeWarningLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "WARN", string);
    }

    private void writeLine(PrintWriter jobReportWriter, String type, String text) {
        if (reportToLog) {
            LOG.info(text);
        } else {
            jobReportWriter.println(getCurrentTimeStamp() + " " + type + "  " + text);
        }
    }

    private void logBatchJobProperties(NewBatchJob job, PrintWriter jobReportWriter) {
        if (job.getBatchProperties() != null) {
            for (Entry<String, String> entry : job.getBatchProperties().entrySet()) {
                writeInfoLine(jobReportWriter, "[configProperty] " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private boolean logBatchJobErrorsAndWarnings(String id, BatchJobDAO batchJobDAO, PrintWriter jobReportWriter) {
        boolean hasErrors = false;

        BatchJobMongoDAStatus status = batchJobDAO.findBatchJobErrors(id);
        if (status != null && status.isSuccess()) {

            // TODO handle large numbers of errors
            @SuppressWarnings("unchecked")
            List<Error> errors = (List<Error>) status.getResult();
            for (Error error : errors) {
                if (FaultType.TYPE_ERROR.getName().equals(error.getSeverity())) {
                    hasErrors = false;
                    writeErrorLine(jobReportWriter,
                            ((error.getStageName() == null) ? "" : (error.getStageName())) + ","
                            + ((error.getResourceId() == null) ? "" : (error.getResourceId())) + ","
                            + ((error.getRecordIdentifier() == null) ? "" : (error.getRecordIdentifier())) + ","
                            + error.getErrorDetail());
                } else if (FaultType.TYPE_WARNING.getName().equals(error.getSeverity())) {
                    writeWarningLine(jobReportWriter,
                            ((error.getStageName() == null) ? "" : (error.getStageName())) + ","
                            + ((error.getResourceId() == null) ? "" : (error.getResourceId())) + ","
                            + ((error.getRecordIdentifier() == null) ? "" : (error.getRecordIdentifier())) + ","
                            + error.getErrorDetail());
                }
            }

        }
        return hasErrors;
    }

    private long logBatchJobPersistenceMetrics(NewBatchJob job, PrintWriter jobReportWriter) {
        long totalProcessed = 0;

        // TODO group counts by externallyUploadedResourceId
        List<Metrics> metrics = job.getStageMetrics(BatchJobStageType.PERSISTENCE_PROCESSOR);
        if (metrics != null) {

            for (Metrics metric : metrics) {

                ResourceEntry resourceEntry = job.getResourceEntry(metric.getResourceId());
                if (resourceEntry == null) {
                    LOG.error("The resource referenced by metric by resourceId " + metric.getResourceId()
                            + " is not defined for this job");
                    continue;
                }

                logResourceMetric(jobReportWriter, resourceEntry, metric.getRecordCount(), metric.getErrorCount());

                totalProcessed += metric.getRecordCount();
            }

        } else {

            // write out 0 count metrics for the input files
            BatchJobMongoDA.logBatchStageError(job.getId(), BatchJobStageType.JOB_REPORTING_PROCESSOR,
                    FaultType.TYPE_WARNING.getName(), null, "There were no metrics for "
                            + BatchJobStageType.PERSISTENCE_PROCESSOR.getName() + ".");

            for (ResourceEntry resourceEntry : job.getResourceEntries()) {
                if (resourceEntry.getResourceFormat() != null
                        && resourceEntry.getResourceFormat().equalsIgnoreCase(FileFormat.EDFI_XML.getCode())) {
                    logResourceMetric(jobReportWriter, resourceEntry, 0, 0);
                }
            }
        }

        return totalProcessed;
    }

    private void logResourceMetric(PrintWriter jobReportWriter, ResourceEntry resourceEntry, long numProcessed, long numFailed) {
        String id = "[file] " + resourceEntry.getExternallyUploadedResourceId();
        writeInfoLine(jobReportWriter, id + " (" + resourceEntry.getResourceFormat() + "/" + resourceEntry.getResourceType() + ")");

        long numPassed = numProcessed - numFailed;

        writeInfoLine(jobReportWriter, id + " records considered: " + numProcessed);
        writeInfoLine(jobReportWriter, id + " records ingested successfully: " + numPassed);
        writeInfoLine(jobReportWriter, id + " records failed: " + numFailed);
    }

    private Stage startAndGetStage(NewBatchJob newJob) {
        Stage stage = new Stage();
        newJob.getStages().add(stage);
        stage.setStageName(BatchJobStageType.JOB_REPORTING_PROCESSOR.getName());
        stage.startStage();
        return stage;
    }

    private String getBatchJobId(Exchange exchange) {
        return exchange.getIn().getHeader("BatchJobId", String.class);
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    public BatchJobDAO getBatchJobDAO() {
        return batchJobDAO;
    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }

    public static String getCurrentTimeStamp() {
        String timeStamp = FORMATTER.format(System.currentTimeMillis());
        return timeStamp;
    }

}
