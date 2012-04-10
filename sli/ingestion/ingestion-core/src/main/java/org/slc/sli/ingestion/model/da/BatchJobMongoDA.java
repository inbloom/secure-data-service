package org.slc.sli.ingestion.model.da;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

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

    public MongoTemplate getBatchJobMongoTemplate() {
        return template;
    }

    @Resource
    public void setBatchJobMongoTemplate(MongoTemplate mongoTemplate) {
        template = mongoTemplate;
    }
    
    @Override
    public BatchJobStatus saveBatchJob(NewBatchJob job) {
        if (job != null)
            template.save(job);
        return null;
    }

    @Override
    public NewBatchJob findBatchJobById(String batchJobId) {
        // TODO Auto-generated method stub
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
    public static BatchJobStatus logIngestionStageInfo(String ingestionJobId, String stageName, String status,
            String startTimestamp, String stopTimeStamp) {
        if (ingestionJobId == null || stageName == null) {
            BatchJobStatus logStatus = new BatchJobStatus(false, "JobId [" + ingestionJobId + "] " + "or StageName["
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
                    continue;
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
        return new BatchJobStatus(true, "Created Job Stage.", job);
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
    public static BatchJobStatus logIngestionMetricInfo(String ingestionJobId, String stageName, String resourceId,
            String sourceIp, String hostname, String startTimestamp, String stopTimeStamp, int recordCount,
            int errorCount) {
        if (ingestionJobId == null || stageName == null) {
            BatchJobStatus logStatus = new BatchJobStatus(false, "JobId [" + ingestionJobId + "] " + "or StageName["
                    + stageName + "] is null.", null);
            LOG.warn("Cannot log IngestionMetric status to MongoDB. ingestionJobId or stageName is null!");
            return logStatus;
        }
        if (sourceIp == null)
            sourceIp = thisIP;
        if (hostname == null)
            hostname = thisName;
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
        return new BatchJobStatus(true, "Created Job Metric.", job);
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
    public static BatchJobStatus logIngestionError(String ingestionJobId, String stageName, String resourceId,
            String sourceIp, String hostname, String recordIdentifier, String timestamp, String severity,
            String errorType, String errorDetail) {
        
        if (sourceIp == null)
            sourceIp = thisIP;
        
        if (hostname == null)
            hostname = thisName;
        
        if (timestamp == null) {
            // TODO: may need to make this faster
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss");
            timestamp = sdf.format(cal.getTime());
        }
        
        Error error = new Error(ingestionJobId, stageName, resourceId, sourceIp, hostname, recordIdentifier, timestamp,
                severity, errorType, errorDetail);
        template.save(error);
        return new BatchJobStatus(true, "Created Job Error.", error);
    }

    public static void logBatchError(String batchJobId, String severity, String errorType, String errorDetail) {
        logIngestionError(batchJobId, null, null, null, null, null, null, severity, errorType, errorDetail);
    }
    
    public static void logBatchStageError(String batchJobId, BatchJobStageType stage, String severity, String errorType, String errorDetail) {
        logIngestionError(batchJobId, stage.getName(), null, null, null, null, null, severity, errorType, errorDetail);
    }
    

}
