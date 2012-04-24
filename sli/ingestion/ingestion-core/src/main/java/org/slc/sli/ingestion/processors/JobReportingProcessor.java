package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.JOB_REPORTING_PROCESSOR;

    // Logging
    private static final Logger LOG = LoggerFactory.getLogger(JobReportingProcessor.class);

    private LandingZone landingZone;

    private BatchJobDAO batchJobDAO;

    public JobReportingProcessor(LandingZone lz) {
        this.landingZone = lz;
        this.batchJobDAO = new BatchJobMongoDA();
    }

    @Override
    public void process(Exchange exchange) {

        // get job from the batch job db
        String batchJobId = getBatchJobId(exchange);

        if (batchJobId != null) {

            NewBatchJob job = batchJobDAO.findBatchJobById(batchJobId);

            try {

                Stage stage = startAndAddStageToJob(job);

                boolean hasErrors = writeErrorAndWarningReports(job);

                writeBatchJobReportFile(job, hasErrors);

                stage.stopStage();
                batchJobDAO.saveBatchJob(job);

            } catch (Exception e) {
                LOG.error("Exception encountered in JobReportingProcessor. ", e);
            } finally {
                deleteNeutralRecordFiles(job);
            }
        } else {
            missingBatchJobIdError(exchange);
        }
    }

    private void writeBatchJobReportFile(NewBatchJob job, boolean hasErrors) {

        PrintWriter jobReportWriter = null;
        FileLock lock = null;
        FileChannel channel = null;
        try {
            File file = landingZone.getLogFile(job.getId());
            FileOutputStream outputStream = new FileOutputStream(file);
            channel = outputStream.getChannel();
            lock = channel.lock();
            jobReportWriter = new PrintWriter(outputStream, true);

            writeInfoLine(jobReportWriter, "jobId: " + job.getId());

            long recordsProcessed = writeBatchJobPersistenceMetrics(job, jobReportWriter);

            writeBatchJobProperties(job, jobReportWriter);

            if (!hasErrors) {
                writeInfoLine(jobReportWriter, "All records processed successfully.");
                job.setStatus(BatchJobStatusType.COMPLETED_SUCCESSFULLY.getName());
            } else {
                writeInfoLine(jobReportWriter, "Not all records were processed completely due to errors.");
                job.setStatus(BatchJobStatusType.COMPLETED_WITH_ERRORS.getName());
            }

            writeInfoLine(jobReportWriter, "Processed " + recordsProcessed + " records.");

        } catch (IOException e) {
            LOG.error("Error: Unable to write report file for: {}", job.getId());
        } finally {
            cleanupWriterAndLocks(jobReportWriter, lock, channel);
        }
    }

    private static void writeBatchJobProperties(NewBatchJob job, PrintWriter jobReportWriter) {
        if (job.getBatchProperties() != null) {
            for (Entry<String, String> entry : job.getBatchProperties().entrySet()) {
                writeInfoLine(jobReportWriter, "[configProperty] " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private boolean writeErrorAndWarningReports(NewBatchJob job) {
        boolean hasErrors = false;

        Map<String, PrintWriter> resourceToErrorMap = new HashMap<String, PrintWriter>();
        Map<String, PrintWriter> resourceToWarningMap = new HashMap<String, PrintWriter>();

        try {
            BatchJobMongoDAStatus status = batchJobDAO.findBatchJobErrors(job.getId());
            if (status != null && status.isSuccess()) {

                // TODO handle large numbers of errors
                @SuppressWarnings("unchecked")
                List<Error> errors = (List<Error>) status.getResult();
                for (Error error : errors) {

                    String externalResourceId = getExternalResourceId(error.getResourceId(), job);

                    if (externalResourceId != null) {

                        PrintWriter externalResourceErrorWriter = null;
                        if (FaultType.TYPE_ERROR.getName().equals(error.getSeverity())) {

                            hasErrors = true;
                            externalResourceErrorWriter = getExternalResourceTypeWriter("error", job.getId(),
                                    externalResourceId, resourceToErrorMap);

                            if (externalResourceErrorWriter != null) {
                                writeErrorLine(externalResourceErrorWriter, error.getErrorDetail());
                            } else {
                                LOG.error("Error: Unable to write to error file for: {} {}", job.getId(),
                                        externalResourceId);
                            }
                        } else if (FaultType.TYPE_WARNING.getName().equals(error.getSeverity())) {

                            externalResourceErrorWriter = getExternalResourceTypeWriter("warn", job.getId(),
                                    externalResourceId, resourceToWarningMap);

                            if (externalResourceErrorWriter != null) {
                                writeWarningLine(externalResourceErrorWriter, error.getErrorDetail());
                            } else {
                                LOG.error("Error: Unable to write to warning file for: {} {}", job.getId(),
                                        externalResourceId);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Error: Unable to write error file for: {}", job.getId());
        } finally {
            for (PrintWriter writer : resourceToErrorMap.values()) {
                writer.close();
            }
            for (PrintWriter writer : resourceToWarningMap.values()) {
                writer.close();
            }
        }
        return hasErrors;
    }

    private PrintWriter getExternalResourceTypeWriter(String type, String batchJobId, String externalResourceId,
            Map<String, PrintWriter> externalFileResourceToErrorMap) throws IOException {

        PrintWriter writer = externalFileResourceToErrorMap.get(externalResourceId);

        if (writer == null) {
            String errorFileName = type + "." + externalResourceId + "." + System.currentTimeMillis() + ".log";
            writer = new PrintWriter(new FileWriter(landingZone.createFile(errorFileName)));
            externalFileResourceToErrorMap.put(externalResourceId, writer);
            return writer;
        }

        return writer;
    }

    private static String getExternalResourceId(String resourceId, NewBatchJob job) {
        if (resourceId != null) {
            ResourceEntry resourceEntry = job.getResourceEntry(resourceId);
            if (resourceEntry != null) {
                return resourceEntry.getExternallyUploadedResourceId();
            }
        }
        return null;
    }

    private static long writeBatchJobPersistenceMetrics(NewBatchJob job, PrintWriter jobReportWriter) {
        long totalProcessed = 0;

        // TODO group counts by externallyUploadedResourceId
        List<Metrics> metrics = job.getStageMetrics(BatchJobStageType.PERSISTENCE_PROCESSOR);
        for (Metrics metric : metrics) {

            ResourceEntry resourceEntry = job.getResourceEntry(metric.getResourceId());
            if (resourceEntry == null) {
                LOG.error("The resource referenced by metric by resourceId " + metric.getResourceId()
                        + " is not defined for this job");
                continue;
            }

            logResourceMetric(resourceEntry, metric.getRecordCount(), metric.getErrorCount(), jobReportWriter);

            totalProcessed += metric.getRecordCount();
        }

        if (metrics.size() == 0) {
            doesntHavePersistenceMetrics(job, jobReportWriter);
        }

        return totalProcessed;
    }

    private static void doesntHavePersistenceMetrics(NewBatchJob job, PrintWriter jobReportWriter) {
        // write out 0 count metrics for the input files
        BatchJobMongoDA.logBatchStageError(job.getId(), BATCH_JOB_STAGE, FaultType.TYPE_WARNING.getName(), null,
                "There were no metrics for " + BATCH_JOB_STAGE.getName() + ".");

        for (ResourceEntry resourceEntry : job.getResourceEntries()) {
            if (resourceEntry.getResourceFormat() != null
                    && resourceEntry.getResourceFormat().equalsIgnoreCase(FileFormat.EDFI_XML.getCode())) {
                logResourceMetric(resourceEntry, 0, 0, jobReportWriter);
            }
        }
    }

    private static void logResourceMetric(ResourceEntry resourceEntry, long numProcessed, long numFailed,
            PrintWriter jobReportWriter) {
        String id = "[file] " + resourceEntry.getExternallyUploadedResourceId();
        writeInfoLine(jobReportWriter,
                id + " (" + resourceEntry.getResourceFormat() + "/" + resourceEntry.getResourceType() + ")");

        long numPassed = numProcessed - numFailed;

        writeInfoLine(jobReportWriter, id + " records considered: " + numProcessed);
        writeInfoLine(jobReportWriter, id + " records ingested successfully: " + numPassed);
        writeInfoLine(jobReportWriter, id + " records failed: " + numFailed);
    }

    private static Stage startAndAddStageToJob(NewBatchJob newJob) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);
        newJob.getStages().add(stage);
        return stage;
    }

    // TODO move this into a dedicated cleanup processor routing stage run on error or normal completion
    private void deleteNeutralRecordFiles(NewBatchJob job) {
        for (ResourceEntry resourceEntry : job.getResourceEntries()) {
            if (resourceEntry.getResourceName() != null
                    && FileFormat.NEUTRALRECORD.getCode().equalsIgnoreCase(resourceEntry.getResourceFormat())) {
                File nrFile = new File(resourceEntry.getResourceName());
                if (!nrFile.delete()) {
                    LOG.warn("Failed to delete neutral record file " + resourceEntry.getResourceName());
                }
            }
        }
    }

    private String getBatchJobId(Exchange exchange) {
        return exchange.getIn().getHeader("BatchJobId", String.class);
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("Error:", "No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    private static void writeInfoLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "INFO", string);
    }

    private static void writeErrorLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "ERROR", string);
    }

    private static void writeWarningLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "WARN", string);
    }

    private static void writeLine(PrintWriter jobReportWriter, String type, String text) {
        jobReportWriter.write(type + "  " + text);
        jobReportWriter.println();
    }

    private static void cleanupWriterAndLocks(PrintWriter jobReportWriter, FileLock lock, FileChannel channel) {
        if (jobReportWriter != null) {
            jobReportWriter.close();
        }
        if (lock != null && lock.isValid()) {
            try {
                lock.release();
            } catch (IOException e) {
                LOG.error("unable to release FileLock.", e);
            }
        }
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                LOG.error("unable to close FileChannel.", e);
            }
        }
    }

    public BatchJobDAO getBatchJobDAO() {
        return batchJobDAO;
    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }

}
