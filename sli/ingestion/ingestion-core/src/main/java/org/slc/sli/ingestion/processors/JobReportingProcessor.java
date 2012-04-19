package org.slc.sli.ingestion.processors;

import java.util.List;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slc.sli.ingestion.BatchJobLogger;
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

    public JobReportingProcessor(LandingZone lz) {
        this.landingZone = lz;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        // get job from the batch job db
        String batchJobId = exchange.getIn().getHeader("BatchJobId", String.class);
        if (batchJobId == null) {
            exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
            exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
            LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
        }
        BatchJobDAO batchJobDAO = new BatchJobMongoDA();
        NewBatchJob job = batchJobDAO.findBatchJobById(batchJobId);

        Stage stage = new Stage();
        stage.setStageName(BatchJobStageType.JOB_REPORTING_PROCESSOR.getName());
        job.getStages().add(stage);
        stage.startStage();

        Logger jobLogger = BatchJobLogger.createLoggerForJob(job.getId(), landingZone);

        jobLogger.info("jobId: " + job.getId());

        // based on the PersistenceProcessor counts
        long totalProcessed = 0;

        // writes out persistence stage resource metrics
        // TODO group counts by externallyUploadedResourceId
        List<Metrics> metrics = job.getStageMetrics(BatchJobStageType.PERSISTENCE_PROCESSOR);
        if (metrics != null) {

            for (Metrics metric : metrics) {

                ResourceEntry resourceEntry = job.getResourceEntry(metric.getResourceId());
                if (resourceEntry == null) {
                    jobLogger.error("The resource referenced by metric by resourceId " + metric.getResourceId()
                            + " is not defined for this job");
                    continue;
                }

                logResourceMetric(jobLogger, resourceEntry, metric.getRecordCount(), metric.getErrorCount());

                totalProcessed += metric.getRecordCount();
            }

        } else {

            // write out 0 count metrics for the input files
            BatchJobMongoDA.logBatchStageError(batchJobId, BatchJobStageType.JOB_REPORTING_PROCESSOR,
                    FaultType.TYPE_WARNING.getName(), null, "There were no metrics for "
                            + BatchJobStageType.PERSISTENCE_PROCESSOR.getName() + ".");

            for (ResourceEntry resourceEntry : job.getResourceEntries()) {
                if (resourceEntry.getResourceFormat() != null
                        && resourceEntry.getResourceFormat().equalsIgnoreCase(FileFormat.EDFI_XML.getCode())) {
                    logResourceMetric(jobLogger, resourceEntry, 0, 0);
                }
            }
        }

        jobLogger.info("Processed " + totalProcessed + " records.");

        // write properties
        if (job.getBatchProperties() != null) {
            for (Entry<String, String> entry : job.getBatchProperties().entrySet()) {
                jobLogger.info("[configProperty] " + entry.getKey() + ": " + entry.getValue());
            }
        }


        boolean success = logBatchJobErrorsAndWarnings(job.getId(), batchJobDAO, jobLogger);


        if (success) {
            jobLogger.info("All records processed successfully.");
            job.setStatus(BatchJobStatusType.COMPLETED_SUCCESSFULLY.getName());
        } else {
            jobLogger.info("Not all records were processed completely due to errors.");
            job.setStatus(BatchJobStatusType.COMPLETED_WITH_ERRORS.getName());
        }

        // clean up after ourselves
        ((ch.qos.logback.classic.Logger) jobLogger).detachAndStopAllAppenders();

        stage.stopStage();
        batchJobDAO.saveBatchJob(job);
    }

    private boolean logBatchJobErrorsAndWarnings(String id, BatchJobDAO batchJobDAO, Logger jobLogger) {
        boolean hasErrors = false;

        BatchJobMongoDAStatus status = batchJobDAO.findBatchJobErrors(id);
        if (status != null && status.isSuccess()) {

            // TODO handle large numbers of errors
            @SuppressWarnings("unchecked")
            List<Error> errors = (List<Error>) status.getResult();
            for (Error error : errors) {
                if (FaultType.TYPE_ERROR.getName().equals(error.getSeverity())) {
                    hasErrors = false;
                    jobLogger.error(((error.getStageName() == null) ? "" : (error.getStageName())) + ","
                            + ((error.getResourceId() == null) ? "" : (error.getResourceId())) + ","
                            + ((error.getRecordIdentifier() == null) ? "" : (error.getRecordIdentifier())) + ","
                            + error.getErrorDetail());
                } else if (FaultType.TYPE_WARNING.getName().equals(error.getSeverity())) {
                    jobLogger.warn(((error.getStageName() == null) ? "" : (error.getStageName())) + ","
                            + ((error.getResourceId() == null) ? "" : (error.getResourceId())) + ","
                            + ((error.getRecordIdentifier() == null) ? "" : (error.getRecordIdentifier())) + ","
                            + error.getErrorDetail());
                }
            }

        }
        return hasErrors;
    }

    private void logResourceMetric(Logger jobLogger, ResourceEntry resourceEntry, long numProcessed, long numFailed) {
        String id = "[file] " + resourceEntry.getExternallyUploadedResourceId();
        jobLogger.info(id + " (" + resourceEntry.getResourceFormat() + "/" + resourceEntry.getResourceType() + ")");

        long numPassed = numProcessed - numFailed;

        jobLogger.info(id + " records considered: " + numProcessed);
        jobLogger.info(id + " records ingested successfully: " + numPassed);
        jobLogger.info(id + " records failed: " + numFailed);
    }

}
