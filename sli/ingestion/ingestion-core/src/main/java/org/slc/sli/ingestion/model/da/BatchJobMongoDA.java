package org.slc.sli.ingestion.model.da;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;

/**
 *
 * @author ldalgado
 *
 */
@Component
public class BatchJobMongoDA implements BatchJobDAO {

    private static final Logger LOG = LoggerFactory.getLogger(BatchJobMongoDA.class);
    private static String thisIP = getHostIP();
    private static String thisName = getHostName();
    private static MongoTemplate template;
    private static final String STR_TIMESTAMP_FORMAT = "yyyyMMdd hh:mm:ss.SSS";
    private static final FastDateFormat FORMATTER = FastDateFormat.getInstance(STR_TIMESTAMP_FORMAT);

    public MongoTemplate getBatchJobMongoTemplate() {
        return template;
    }

    @Resource
    public void setBatchJobMongoTemplate(MongoTemplate mongoTemplate) {
        template = mongoTemplate;
    }

    @Override
    public BatchJobMongoDAStatus saveBatchJob(NewBatchJob job) {
        if (job != null) {
            template.save(job);
        }
        return null;
    }

    @Override
    public NewBatchJob findBatchJobById(String batchJobId) {
        Query query = new Query(Criteria.where("_id").is(batchJobId));
        return template.findOne(query, NewBatchJob.class);
    }

    /**
     *
     * @param IngestionJobId
     * @param stageName
     * @param status
     * @param startTimestamp
     * @param stopTimeStamp
     * @return
     */
    public static BatchJobMongoDAStatus logIngestionStageInfo(String ingestionJobId, String stageName, String status,
            String startTimestamp, String stopTimeStamp) {
        if (ingestionJobId == null || stageName == null) {
            BatchJobMongoDAStatus logStatus = new BatchJobMongoDAStatus(false, "JobId [" + ingestionJobId + "] " + "or StageName["
                    + stageName + "] is null.", null);
            LOG.warn("Cannot log NewBatchJob status to MongoDB. ingestionJobId or stageName is null!");
            return logStatus;
        }

        NewBatchJob job = template.findById(ingestionJobId, NewBatchJob.class);
        if (job != null) {
            boolean jobStageModified = false;
            for (Stage stage : job.getStages()) { // Check for NPE?
                String name = stage.getStageName();
                if (name != null && name.equals(stageName)) {
                    stage.update(stageName, status, startTimestamp, stopTimeStamp);
                    template.save(job);
                    jobStageModified = true;
                    break;
                }
            }
            if (!jobStageModified) {
                Stage newStage = new Stage(stageName, status, startTimestamp, stopTimeStamp, null /* metrics */);
                job.getStages().add(newStage);
                template.save(job);
            }
        } else {
            job = new NewBatchJob(ingestionJobId, null/* sourceId */, null/* status */, 0/* totalFiles */,
                    null/* batchProperties */, null/* stages */, null/* fileEntries */);
            Stage newStage = new Stage(stageName, status, startTimestamp, stopTimeStamp, null/* metrics */);
            job.getStages().add(newStage);
            template.save(job);
        }
        return new BatchJobMongoDAStatus(true, "Created Job Stage.", job);
    }

    /**
     *
     * @param ingestionJobId
     * @param stageName
     * @param resourceId
     * @param sourceIp
     * @param hostname
     * @param startTimestamp
     * @param stopTimeStamp
     * @param recordCount
     * @param errorCount
     * @return
     */
    public static BatchJobMongoDAStatus logIngestionMetricInfo(String ingestionJobId, String stageName, String resourceId,
            String sourceIp, String hostname, String startTimestamp, String stopTimeStamp, long recordCount,
            long errorCount) {
        if (ingestionJobId == null || stageName == null) {
            BatchJobMongoDAStatus logStatus = new BatchJobMongoDAStatus(false, "JobId [" + ingestionJobId + "] " + "or StageName["
                    + stageName + "] is null.", null);
            LOG.warn("Cannot log IngestionMetric status to MongoDB. ingestionJobId or stageName is null!");
            return logStatus;
        }
        if (sourceIp == null) {
            sourceIp = thisIP;
        }
        if (hostname == null) {
            hostname = thisName;
        }
        NewBatchJob job = template.findById(ingestionJobId, NewBatchJob.class);
        if (job != null) {
            boolean foundStage = false;
            for (Stage stage : job.getStages()) {
                String name = stage.getStageName();
                if (name != null && name.equals(stageName)) {
                    foundStage = true;
                    boolean foundMetric = false;
                    for (Metrics metric : stage.getMetrics()) {
                        String metricResourceId = metric.getResourceId();
                        if (metricResourceId != null && metricResourceId.equals(resourceId)) {
                            foundMetric = true;
                            metric.update(resourceId, sourceIp, hostname, startTimestamp, stopTimeStamp, recordCount,
                                    errorCount);
                            template.save(job);
                            break;
                        }
                    }
                    if (!foundMetric) {
                        Metrics metric = new Metrics(resourceId, sourceIp, hostname, startTimestamp, stopTimeStamp,
                                recordCount, errorCount);
                        stage.getMetrics().add(metric);
                        template.save(job);
                    }
                }
            }
            if (!foundStage) {
                String status = null;
                List<Metrics> metrics = new LinkedList<Metrics>();
                Stage newStage = new Stage(stageName, status, null/* startTimestamp */, null/* stopTimestamp */,
                        metrics);
                Metrics metric = new Metrics(resourceId, sourceIp, hostname, startTimestamp, stopTimeStamp,
                        recordCount, errorCount);
                newStage.getMetrics().add(metric);
                job.getStages().add(newStage);
                template.save(job);
            }
        } else {

            job = new NewBatchJob(ingestionJobId, null/* sourceId */, null/* status */, 0/* totalFiles */,
                    null/* batchProperties */, null/* stages */, null/* fileEntries */);
            Stage newStage = new Stage(stageName, null/* status */, null/* startTimestamp */, null/* stopTimestamp */,
                    null/* metrics */);
            Metrics metric = new Metrics(resourceId, sourceIp, hostname, startTimestamp, stopTimeStamp, recordCount,
                    errorCount);
            newStage.getMetrics().add(metric);
            job.getStages().add(newStage);
            template.save(job);
        }
        return new BatchJobMongoDAStatus(true, "Created Job Metric.", job);
    }

    private static String getHostIP() {
        String ip = "unknownIP";
        try {
            InetAddress host = InetAddress.getLocalHost();
            ip = host.getHostAddress();
        } catch (Exception e) {
            LOG.warn("Could not find machine name/ip.", e);
        }
        return ip;
    }

    private static String getHostName() {
        String name = "unknownName";
        try {
            InetAddress host = InetAddress.getLocalHost();
            name = host.getHostAddress() + "-" + host.getHostName();
        } catch (Exception e) {
            LOG.warn("Could not find machine name/ip.", e);
        }
        return name;
    }

    /**
     * Return all the errors associated with a batch job id
     * TODO support very large result sets
     *
     * @param jobId
     * @return
     */
    public BatchJobMongoDAStatus findBatchJobErrors(String jobId) {
        Query query = new Query(Criteria.where("batchJobId").is(jobId));
        List<Error> errors = template.find(query, Error.class, "error");
        return new BatchJobMongoDAStatus(true, "Returned errors for " + jobId, errors);
    }

    /**
     *
     * @param IngestionJobId
     * @param stageName
     * @param resourceId
     * @param sourceIp
     * @param hostname
     * @param recordIdentifier
     * @param timestamp
     * @param severity
     * @param errorType
     * @param errorDetail
     * @return
     */
    public static BatchJobMongoDAStatus logIngestionError(String ingestionJobId, String stageName, String resourceId,
            String sourceIp, String hostname, String recordIdentifier, String timestamp, String severity,
            String errorType, String errorDetail) {

        if (sourceIp == null) {
            sourceIp = thisIP;
        }

        if (hostname == null) {
            hostname = thisName;
        }

        Error error = new Error(ingestionJobId, stageName, resourceId, sourceIp, hostname, recordIdentifier, getCurrentTimeStamp(),
                severity, errorType, errorDetail);
        template.save(error);
        return new BatchJobMongoDAStatus(true, "Created Job Error.", error);
    }

    public static void logBatchError(String batchJobId, String severity, String errorType, String errorDetail) {
        logIngestionError(batchJobId, null, null, null, null, null, null, severity, errorType, errorDetail);
    }

    public static void logBatchStageError(String batchJobId, BatchJobStageType stage, String severity, String errorType, String errorDetail) {
        logIngestionError(batchJobId, stage.getName(), null, null, null, null, null, severity, errorType, errorDetail);
    }

    public static String getCurrentTimeStamp() {
        String timeStamp = FORMATTER.format(System.currentTimeMillis());
        return timeStamp;
    }
}
