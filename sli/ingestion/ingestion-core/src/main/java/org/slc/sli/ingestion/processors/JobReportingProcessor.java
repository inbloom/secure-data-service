/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.util.Arrays;
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
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.slc.sli.common.util.logging.LogLevelType;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.dal.TenantContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.BatchJobStatusType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
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

/**
 * Writes out a job report and any errors/warnings associated with the job.
 *
 * @author bsuzuki
 *
 */
@Component
public class JobReportingProcessor implements Processor {

    public static final BatchJobStageType BATCH_JOB_STAGE = BatchJobStageType.JOB_REPORTING_PROCESSOR;

    private static final String BATCH_JOB_STAGE_DESC = "Writes out a job report and any errors/warnings associated with the job";

    public static final String JOB_STAGE_RESOURCE_ID = "job";

    private static final Logger LOG = LoggerFactory.getLogger(JobReportingProcessor.class);

    private static final int ERRORS_RESULT_LIMIT = 100000;

    private static final List<String> ORCHESTRATION_STAGES_LIST = Arrays.asList("PersistenceProcessor",
            "TransformationProcessor", "WorkNoteSplitter");

    public static final String ORCHESTRATION_STAGES_NAME = "OrchestrationStages";

    public static final String ORCHESTRATION_STAGES_DESC = "Transforms and persists records to sli database";

    @Value("${sli.ingestion.staging.clearOnCompletion}")
    private String clearOnCompletion;

    @Value("${sli.ingestion.topic.command}")
    private String commandTopicUri;

    @Autowired
    private BatchJobDAO batchJobDAO;

    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Value("${sli.ingestion.errorsCountPerInterchange}")
    private int errorsCountPerInterchange;

    @Value("${sli.ingestion.warningsCountPerInterchange}")
    private int warningsCountPerInterchange;

    @Override
    public void process(Exchange exchange) {
        WorkNote workNote = exchange.getIn().getBody(WorkNote.class);

        if (workNote == null || workNote.getBatchJobId() == null) {
            missingBatchJobIdError(exchange);
        } else {
            processJobReporting(exchange, workNote);
        }
    }

    private void processJobReporting(Exchange exchange, WorkNote workNote) {
        Stage stage = Stage.createAndStartStage(BATCH_JOB_STAGE, BATCH_JOB_STAGE_DESC);

        String batchJobId = workNote.getBatchJobId();
        NewBatchJob job = null;
        try {
            populateJobBriefFromStageCollection(batchJobId);

            job = batchJobDAO.findBatchJobById(batchJobId);
            TenantContext.setTenantId(job.getTenantId());

            boolean hasErrors = writeErrorAndWarningReports(job);

            writeBatchJobReportFile(exchange, job, hasErrors);

        } catch (Exception e) {
            LOG.error("Exception encountered in JobReportingProcessor. ", e);
        } finally {

            performJobCleanup(exchange, workNote, stage, job);

        }
    }

    private void populateJobBriefFromStageCollection(String jobId) {
        NewBatchJob job = batchJobDAO.findBatchJobById(jobId);

        Map<String, Stage> stageBriefMap = new HashMap<String, Stage>();

        if (job != null) {
            List<Stage> stages = batchJobDAO.getBatchJobStages(jobId);
            Iterator<Stage> it = stages.iterator();

            while (it.hasNext()) {
                Stage stageChunk = it.next();

                // Account for special case of orchestration stages.
                String stageName = ORCHESTRATION_STAGES_NAME;
                String stageDesc = ORCHESTRATION_STAGES_DESC;
                if (!ORCHESTRATION_STAGES_LIST.contains(stageChunk.getStageName())) {
                    stageName = stageChunk.getStageName();
                    stageDesc = stageChunk.getStageDesc();
                }

                // Stage stageBrief = stageBriefMap.get(stageChunk.getStageName());
                Stage stageBrief = stageBriefMap.get(stageName);
                if (stageBrief != null) {
                    if (stageBrief.getStartTimestamp() != null
                            && stageBrief.getStartTimestamp().getTime() > stageChunk.getStartTimestamp().getTime()) {
                        stageBrief.setStartTimestamp(stageChunk.getStartTimestamp());
                    }
                    if (stageBrief.getStopTimestamp() != null
                            && stageBrief.getStopTimestamp().getTime() < stageChunk.getStopTimestamp().getTime()) {
                        stageBrief.setStopTimestamp(stageChunk.getStopTimestamp());
                    }
                    // stageBrief.setElapsedTime(stageBrief.getElapsedTime() +
                    // stageChunk.getElapsedTime());
                    stageBrief.setElapsedTime(stageBrief.getStopTimestamp().getTime()
                            - stageBrief.getStartTimestamp().getTime());

                } else {
                    // stageBrief = new Stage(stageChunk.getStageName(), stageChunk.getStageDesc(),
                    // stageChunk.getStatus(),
                    // stageChunk.getStartTimestamp(), stageChunk.getStopTimestamp(), null);
                    stageBrief = new Stage(stageName, stageDesc, stageChunk.getStatus(),
                            stageChunk.getStartTimestamp(), stageChunk.getStopTimestamp(), null);
                    stageBrief.setJobId(stageChunk.getJobId());
                    stageBrief.setElapsedTime(stageChunk.getElapsedTime());
                    stageBrief.setProcessingInformation("");

                    // stageBriefMap.put(stageChunk.getStageName(), stageBrief);
                    stageBriefMap.put(stageName, stageBrief);
                }
            }

            Iterator<Entry<String, Stage>> iter = stageBriefMap.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, Stage> temp = iter.next();
                job.addStage(temp.getValue());
            }

            batchJobDAO.saveBatchJob(job);
        } else {
            LOG.warn("Couldn't find job {}", jobId);
        }
    }

    private void writeBatchJobReportFile(Exchange exchange, NewBatchJob job, boolean hasErrors) {

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

            String purgeMessage = (String) exchange.getProperty("purge.complete");

            if (purgeMessage != null) {

                writeInfoLine(jobReportWriter, purgeMessage);

            }

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

        LandingZone landingZone = new LocalFileSystemLandingZone(new File(job.getTopLevelSourceId()));
        for (IngestionFileEntry file : job.getFiles()) {
            String externalResourceId = file.getFileName();
            hasErrors &= writeErrors(job, landingZone, externalResourceId, FaultType.TYPE_ERROR, "error");
            writeErrors(job, landingZone, externalResourceId, FaultType.TYPE_WARNING, "warn");
        }

        return hasErrors;
    }

    private boolean writeErrors(NewBatchJob job, LandingZone landingZone, String externalResourceId,
            FaultType severity, String fileType) {
        Iterable<Error> errors = batchJobDAO.getBatchJobErrors(job.getId(), externalResourceId, severity,
                errorsCountPerInterchange);
        if (errors.iterator().hasNext()) {
            PrintWriter errorWriter = null;
            try {
                errorWriter = getErrorWriter(fileType, job.getId(), externalResourceId, landingZone);
                if (errorWriter != null) {
                    for (Error error : errors) {
                        writeErrorLine(errorWriter, error.getErrorDetail());
                    }
                } else {
                    LOG.error("Unable to write to error file for: {} {}", job.getId(), externalResourceId);
                }
            } catch (IOException e) {
                LOG.error("Unable to write error file for: {}", job.getId(), e);
            } finally {
                if (errorWriter != null) {
                    errorWriter.close();
                }
            }
            return true;
        }
        return false;
    }

    private PrintWriter getErrorWriter(String type, String batchJobId, String externalResourceId,
            LandingZone landingZone) throws IOException {

        // writer for job and stage level (non-resource specific) errors and warnings
        if (externalResourceId == null) {
            externalResourceId = JOB_STAGE_RESOURCE_ID;
        }

        String errorFileName = null;

        if (JOB_STAGE_RESOURCE_ID.equals(externalResourceId)) {
            errorFileName = JOB_STAGE_RESOURCE_ID + "_" + type + "-" + batchJobId + ".log";
        } else {
            errorFileName = type + "." + externalResourceId + "-" + batchJobId + ".log";
        }
        PrintWriter writer = new PrintWriter(new FileWriter(landingZone.createFile(errorFileName)));
        return writer;

    }

    private long writeBatchJobPersistenceMetrics(NewBatchJob job, PrintWriter jobReportWriter) {
        long totalProcessed = 0;

        // TODO group counts by externallyUploadedResourceId

        List<Stage> stages = batchJobDAO.getBatchJobStages(job.getId());
        Iterator<Stage> it = stages.iterator();

        Stage stage;
        List<Metrics> metrics;

        Map<String, Metrics> combinedMetricsMap = new HashMap<String, Metrics>();

        while (it.hasNext()) {
            stage = it.next();
            metrics = stage.getMetrics();

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

            // update resource entries for zero-count reporting later
            resourceEntry.setRecordCount(metric.getRecordCount());
            resourceEntry.setErrorCount(metric.getErrorCount());
        }

        writeZeroCountPersistenceMetrics(job, jobReportWriter);

        return totalProcessed;
    }

    private void writeZeroCountPersistenceMetrics(NewBatchJob job, PrintWriter jobReportWriter) {
        // write out 0 count metrics for the input files

        for (ResourceEntry resourceEntry : job.getResourceEntries()) {
            if (resourceEntry.getResourceFormat() != null
                    && resourceEntry.getResourceFormat().equalsIgnoreCase(FileFormat.EDFI_XML.getCode())
                    && resourceEntry.getRecordCount() == 0 && resourceEntry.getErrorCount() == 0) {
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

    private void performJobCleanup(Exchange exchange, WorkNote workNote, Stage stage, NewBatchJob job) {
        if (job != null) {
            BatchJobUtils.completeStageAndJob(stage, job);
            batchJobDAO.saveBatchJob(job);
            batchJobDAO.releaseTenantLockForJob(job.getTenantId(), job.getId());
            batchJobDAO.cleanUpWorkNoteLatchAndStagedEntites(job.getId());
            broadcastFlushStats(exchange, workNote);
            cleanUpLZ(job);
        }

        cleanupStagingDatabase(workNote);

        TenantContext.setJobId(null);
    }

    private void writeSecurityLog(LogLevelType messageType, String message) {
        byte[] ipAddr = null;
        try {
            InetAddress addr = InetAddress.getLocalHost();

            // Get IP Address
            ipAddr = addr.getAddress();

        } catch (UnknownHostException e) {
            LOG.error("Error getting local host", e);
        }
        List<String> userRoles = Collections.emptyList();
        SecurityEvent event = new SecurityEvent();
        event.setTenantId(""); // Alpha MH (tenantId - written in 'message')
        event.setUser("");
        event.setTargetEdOrg("");
        event.setActionUri("writeLine");
        event.setAppId("Ingestion");
        event.setOrigin("");
        if (ipAddr != null) {
            event.setExecutedOn(ipAddr[0] + "." + ipAddr[1] + "." + ipAddr[2] + "." + ipAddr[3]);
        }
        event.setCredential("");
        event.setUserOrigin("");
        event.setTimeStamp(new Date());
        event.setProcessNameOrId(ManagementFactory.getRuntimeMXBean().getName());
        event.setClassName(this.getClass().getName());
        event.setLogLevel(messageType);
        event.setRoles(userRoles);
        event.setLogMessage(message);
        audit(event);
    }

    private void cleanupStagingDatabase(WorkNote workNote) {
        if ("true".equals(clearOnCompletion)) {
            neutralRecordMongoAccess.getRecordRepository().deleteStagedRecordsForJob(workNote.getBatchJobId());
            LOG.info("Successfully deleted all staged records for batch job: {}", workNote.getBatchJobId());
        } else {
            LOG.info("Not deleting staged records for batch job: {} --> clear on completion flag is set to FALSE",
                    workNote.getBatchJobId());
        }
    }

    /**
     * broadcast a message to all orchestra nodes to flush their execution stats
     *
     * @param exchange
     * @param workNote
     */
    private void broadcastFlushStats(Exchange exchange, WorkNote workNote) {
        try {
            ProducerTemplate template = new DefaultProducerTemplate(exchange.getContext());
            template.start();
            template.sendBody(this.commandTopicUri, "jobCompleted|" + workNote.getBatchJobId());
            template.stop();
        } catch (Exception e) {
            LOG.error("Error sending `that's all folks` message to the orchestra", e);
        }
    }

    private void cleanUpLZ(NewBatchJob job) {
        boolean isZipFile = false;
        for (ResourceEntry resourceEntry : job.getResourceEntries()) {
            if (FileFormat.ZIP_FILE.getCode().equalsIgnoreCase(resourceEntry.getResourceFormat())) {
                isZipFile = true;
            }
        }
        if (isZipFile) {
            String sourceId = job.getSourceId();
            if (sourceId != null) {
                File dir = new File(sourceId);
                FileUtils.deleteQuietly(dir);
            }
        } else {
            for (ResourceEntry resourceEntry : job.getResourceEntries()) {
                if (resourceEntry.getResourceFormat().equals(FileFormat.EDFI_XML.getCode())) {
                    File xmlFile = new File(resourceEntry.getResourceName());
                    FileUtils.deleteQuietly(xmlFile);
                }
            }
        }
    }

    public void setCommandTopicUri(String commandTopicUri) {
        this.commandTopicUri = commandTopicUri;
    }

    public void setBatchJobDAO(BatchJobDAO batchJobDAO) {
        this.batchJobDAO = batchJobDAO;
    }
}
