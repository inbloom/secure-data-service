package org.slc.sli.ingestion.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Model for the different stages of ingestion processing.
 *
 * @author dduran
 *
 */
public class Stage {

    // mongoTemplate requires this constructor.
    public Stage() {
    }

    private String jobId;

    private String stageName;
    private String status;
    private Date startTimestamp;
    private Date stopTimestamp;
    private long elapsedTime;
    private String sourceIp;
    private String hostname;
    private String processingInformation;

    private List<Metrics> metrics;

    public Stage(String stageName) {
        this.stageName = stageName;
        this.metrics = new LinkedList<Metrics>();
    }

    public Stage(String stageName, String status, Date startTimestamp, Date stopTimestamp, List<Metrics> metrics) {
        this.stageName = stageName;
        this.status = status;
        this.startTimestamp = startTimestamp;
        this.stopTimestamp = stopTimestamp;
        if (metrics == null) {
            metrics = new LinkedList<Metrics>();
        }
        this.metrics = metrics;
    }

    public static Stage createAndStartStage(BatchJobStageType batchJobStageType) {
        Stage stage = new Stage(batchJobStageType.getName());
        stage.startStage();
        return stage;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Date getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(Date stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public List<Metrics> getMetrics() {
        if (metrics == null) {
            metrics = new LinkedList<Metrics>();
        }
        return metrics;
    }

    public synchronized void addMetrics(Metrics metrics) {
        if (this.metrics == null) {
            this.metrics = new LinkedList<Metrics>();
        }
        this.metrics.add(metrics);
    }

    public void setMetrics(List<Metrics> metrics) {
        this.metrics = metrics;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public String getHostname() {
        return hostname;
    }

    public String getProcessingInformation() {
        return processingInformation;
    }

    public void setProcessingInformation(String processingInformation) {
        this.processingInformation = processingInformation;
    }

    public void update(String stageName, String status, Date startTimestamp, Date stopTimestamp) {
        if (stageName != null) {
            this.stageName = stageName;
        }
        if (status != null) {
            this.status = status;
        }
        if (startTimestamp != null) {
            this.startTimestamp = startTimestamp;
        }
        if (stopTimestamp != null) {
            this.stopTimestamp = stopTimestamp;
        }
    }

    public void startStage() {
        this.setStatus("running");
        this.setStartTimestamp(BatchJobUtils.getCurrentTimeStamp());
        this.sourceIp = BatchJobUtils.getHostAddress();
        this.hostname = BatchJobUtils.getHostName();
    }

    public void stopStage() {
        this.setStatus("finished");
        this.setStopTimestamp(BatchJobUtils.getCurrentTimeStamp());
        this.elapsedTime = calcElapsedTime();
    }

    public void addCompletedMetrics(Metrics metrics) {
        this.metrics.add(metrics);
    }

    private long calcElapsedTime() {
        if (stopTimestamp != null && startTimestamp != null) {
            return stopTimestamp.getTime() - startTimestamp.getTime();
        }
        return -1L;
    }

}
