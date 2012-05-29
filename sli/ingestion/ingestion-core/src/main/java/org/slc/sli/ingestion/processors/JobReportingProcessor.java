package org.slc.sli.ingestion.processors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.landingzone.LandingZone;
import org.slc.sli.ingestion.landingzone.LocalFileSystemLandingZone;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.util.LogUtil;

/**
 * Writes out a job report and any errors/warnings associated with the job.
 *
 * @author bsuzuki
 *
 */
@Component
public class JobReportingProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.JOB_REPORTING_PROCESSOR;

    public static final String JOB_STAGE_RESOURCE_ID = "job";

    private static final Logger LOG = LoggerFactory.getLogger(JobReportingProcessor.class);

    private static final int ERRORS_RESULT_LIMIT = 100;

    @Value("${sli.ingestion.staging.clearOnCompletion}")
    private String clearOnCompletion;

    @Value("${sli.ingestion.topic.command}")
    private String commandTopicUri;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Override
    public void process(Exchange exchange) {

        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        if (workNote == null || workNote.getBatchJobId() == null) {
            missingBatchJobIdError(exchange);
        } else {
            processJobReporting(workNote);
        }

        try {
            ProducerTemplate template = new DefaultProducerTemplate(exchange.getContext());
            template.start();
            template.sendBody(this.commandTopicUri, "flushStats|" + workNote.getBatchJobId());
            template.stop();
        } catch (Exception e) {
            LOG.error("Error sending `that's all folks` message to the orchestra", e);
        }
    }

    private void processJobReporting(WorkNote workNote) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob job = null;
        try {

            populateJobFromStageCollection(batchJobId);

            job = batchJobDAO.findBatchJobById(batchJobId);

            boolean hasErrors = writeErrorAndWarningReports(job);

            writeBatchJobReportFile(job, hasErrors);

        } catch (Exception e) {
            LogUtil.error(LOG, "Exception encountered in JobReportingProcessor. ", e);
        } finally {
            if ("true".equals(clearOnCompletion)) {
                neutralRecordMongoAccess.getRecordRepository().deleteCollectionsForJob(workNote.getBatchJobId());
                LOG.info("successfully deleted all staged collections for batch job: {}", workNote.getBatchJobId());
            } else if ("transformed".equals(clearOnCompletion)) {
                neutralRecordMongoAccess.getRecordRepository().deleteTransformedCollectionsForJob(
                        workNote.getBatchJobId());
                LOG.info("successfully deleted all TRANSFORMED staged collections for batch job: {}",
                        workNote.getBatchJobId());
            }
            if (job != null) {
                BatchJobUtils.completeStageAndJob(stage, job);
                batchJobDAO.saveBatchJob(job);
            }
        }
    }

    private void populateJobFromStageCollection(String jobId) {
        NewBatchJob job = batchJobDAO.findBatchJobById(jobId);

        List<Stage> stages = batchJobDAO.getBatchStagesStoredSeperatelly(jobId);
        Iterator<Stage> it = stages.iterator();
        Stage tempStage;

        while (it.hasNext()) {
            tempStage = it.next();

            job.addStage(tempStage);
        }

        batchJobDAO.saveBatchJob(job);
    }

    private void writeBatchJobReportFile(NewBatchJob job, boolean hasErrors) {

        PrintWriter jobReportWriter = null;
        FileLock lock = null;
        FileChannel channel = null;
        try {
            LandingZone landingZone = new LocalFileSystemLandingZone(new File(job.getTopLevelSourceId()));
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
            LOG.error("Unable to write report file for: {}", job.getId());
        } finally {
            cleanupWriterAndLocks(jobReportWriter, lock, channel);
        }
    }

    private void writeBatchJobProperties(NewBatchJob job, PrintWriter jobReportWriter) {
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
            Iterable<Error> errors = batchJobDAO.getBatchJobErrors(job.getId(), ERRORS_RESULT_LIMIT);
            LandingZone landingZone = new LocalFileSystemLandingZone(new File(job.getTopLevelSourceId()));
            for (Error error : errors) {
                String externalResourceId = error.getResourceId();

                PrintWriter errorWriter = null;
                if (FaultType.TYPE_ERROR.getName().equals(error.getSeverity())) {

                    hasErrors = true;
                    errorWriter = getErrorWriter("error", job.getId(), externalResourceId, resourceToErrorMap,
                            landingZone);

                    if (errorWriter != null) {
                        writeErrorLine(errorWriter, error.getErrorDetail());
                    } else {
                        LOG.error("Error: Unable to write to error file for: {} {}", job.getId(), externalResourceId);
                    }
                } else if (FaultType.TYPE_WARNING.getName().equals(error.getSeverity())) {

                    errorWriter = getErrorWriter("warn", job.getId(), externalResourceId, resourceToWarningMap,
                            landingZone);

                    if (errorWriter != null) {
                        writeWarningLine(errorWriter, error.getErrorDetail());
                    } else {
                        LOG.error("Error: Unable to write to warning file for: {} {}", job.getId(), externalResourceId);
                    }
                }
            }

        } catch (IOException e) {
            LOG.error("Unable to write error file for: {}", job.getId());
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

    private PrintWriter getErrorWriter(String type, String batchJobId, String externalResourceId,
            Map<String, PrintWriter> externalFileResourceToErrorMap, LandingZone landingZone) throws IOException {

        // writer for job and stage level (non-resource specific) errors and warnings
        if (externalResourceId == null) {
            externalResourceId = JOB_STAGE_RESOURCE_ID;
        }

        PrintWriter writer = externalFileResourceToErrorMap.get(externalResourceId);

        if (writer == null) {
            String errorFileName = null;

            if (JOB_STAGE_RESOURCE_ID.equals(externalResourceId)) {
                errorFileName = JOB_STAGE_RESOURCE_ID + "_" + type + "-" + batchJobId + ".log";
            } else {
                errorFileName = type + "." + externalResourceId + "-" + batchJobId + ".log";
            }
            writer = new PrintWriter(new FileWriter(landingZone.createFile(errorFileName)));
            externalFileResourceToErrorMap.put(externalResourceId, writer);
            return writer;
        }

        return writer;
    }

    private long writeBatchJobPersistenceMetrics(NewBatchJob job, PrintWriter jobReportWriter) {
        long totalProcessed = 0;

        // TODO group counts by externallyUploadedResourceId
        List<Metrics> metrics = job.getStageMetrics(BatchJobStageType.PERSISTENCE_PROCESSOR);
        Map<String, Metrics> combinedMetricsMap = new HashMap<String, Metrics>();

        for (Metrics m : metrics) {

            if (combinedMetricsMap.containsKey(m.getResourceId())) {
                // metrics exists, we should aggregate
                Metrics temp = new Metrics(m.getResourceId());

                temp.setResourceId(combinedMetricsMap.get(m.getResourceId()).getResourceId());
                temp.setRecordCount(combinedMetricsMap.get(m.getResourceId()).getRecordCount());
                temp.setErrorCount(combinedMetricsMap.get(m.getResourceId()).getErrorCount());

                temp.setErrorCount(temp.getErrorCount() + m.getErrorCount());
                temp.setRecordCount(temp.getRecordCount() + m.getRecordCount());

                combinedMetricsMap.put(m.getResourceId(), temp);

            } else {
                // adding metrics to the map
                combinedMetricsMap.put(m.getResourceId(),
                        new Metrics(m.getResourceId(), m.getRecordCount(), m.getErrorCount()));
            }

        }

        Collection<Metrics> combinedMetrics = combinedMetricsMap.values();

        for (Metrics metric : combinedMetrics) {
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

    private void doesntHavePersistenceMetrics(NewBatchJob job, PrintWriter jobReportWriter) {
        // write out 0 count metrics for the input files

        for (ResourceEntry resourceEntry : job.getResourceEntries()) {
            if (resourceEntry.getResourceFormat() != null
                    && resourceEntry.getResourceFormat().equalsIgnoreCase(FileFormat.EDFI_XML.getCode())) {
                logResourceMetric(resourceEntry, 0, 0, jobReportWriter);
            }
        }
    }

    private void logResourceMetric(ResourceEntry resourceEntry, long numProcessed, long numFailed,
            PrintWriter jobReportWriter) {
        String id = "[file] " + resourceEntry.getExternallyUploadedResourceId();
        writeInfoLine(jobReportWriter,
                id + " (" + resourceEntry.getResourceFormat() + "/" + resourceEntry.getResourceType() + ")");

        long numPassed = numProcessed - numFailed;

        writeInfoLine(jobReportWriter, id + " records considered: " + numProcessed);
        writeInfoLine(jobReportWriter, id + " records ingested successfully: " + numPassed);
        writeInfoLine(jobReportWriter, id + " records failed: " + numFailed);
    }

    private void missingBatchJobIdError(Exchange exchange) {
        exchange.getIn().setHeader("ErrorMessage", "No BatchJobId specified in exchange header.");
        exchange.getIn().setHeader("IngestionMessageType", MessageType.ERROR.name());
        LOG.error("No BatchJobId specified in " + this.getClass().getName() + " exchange message header.");
    }

    private void writeInfoLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "INFO", string);
        writeSecurityLog(LogLevelType.TYPE_INFO, string);
    }

    private void writeErrorLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "ERROR", string);
    }

    private void writeWarningLine(PrintWriter jobReportWriter, String string) {
        writeLine(jobReportWriter, "WARN", string);
    }

    private void writeLine(PrintWriter jobReportWriter, String type, String text) {
        jobReportWriter.write(type + "  " + text);
        jobReportWriter.println();
    }

    private void cleanupWriterAndLocks(PrintWriter jobReportWriter, FileLock lock, FileChannel channel) {
        if (jobReportWriter != null) {
            jobReportWriter.close();
        }
        if (lock != null && lock.isValid()) {
            try {
                lock.release();
            } catch (IOException e) {
                LogUtil.error(LOG, "unable to release FileLock.", e);
            }
        }
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                LogUtil.error(LOG, "unable to close FileChannel.", e);
            }
        }
    }

    private void writeSecurityLog(LogLevelType messageType, String message) {
        byte[] ipAddr = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();

            // Get IP Address
            ipAddr = addr.getAddress();

        } catch (UnknownHostException e) {
            LogUtil.error(LOG, "Error getting local host", e);
        }
        List<String> userRoles = Collections.emptyList();
        SecurityEvent event = new SecurityEvent("",  // Alpha MH (tenantId - written in 'message')
                "", // user
                "", // targetEdOrg
                "writeLine", // Alpha MH
                "Ingestion", // Alpha MH (appId)
                "", // origin
                ipAddr[0] + "." + ipAddr[1] + "." + ipAddr[2] + "." + ipAddr[3], // executedOn
                "", // Alpha MH (credential- N/A for ingestion)
                "", // userOrigin
                new Date(), // Alpha MH (timeStamp)
                ManagementFactory.getRuntimeMXBean().getName(), // processNameOrId
                this.getClass().getName(), // className
                messageType, // Alpha MH (logLevel)
                userRoles, message); // Alpha MH (logMessage)

        audit(event);
    }

    public void setCommandTopicUri(String commandTopicUri) {
        this.commandTopicUri = commandTopicUri;
    }
}
