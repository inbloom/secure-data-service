package org.slc.sli.ingestion.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slc.sli.ingestion.BatchJobStageType;

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

    private String stageName;

    private String status;

    private Date startTimestamp;

    private Date stopTimestamp;

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

    public void setMetrics(List<Metrics> metrics) {
        this.metrics = metrics;
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
        this.setStartTimestamp(new Date());
    }

    public void stopStage() {
        this.setStatus("finished");
        this.setStopTimestamp(new Date());
    }

    public void addCompletedMetrics(Metrics metrics) {
        metrics.stopMetric();
        this.metrics.add(metrics);
    }

}
