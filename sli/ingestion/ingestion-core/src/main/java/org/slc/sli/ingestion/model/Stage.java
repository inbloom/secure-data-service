package org.slc.sli.ingestion.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Model for the different stages of ingestion processing.
 *
 * @author dduran
 *
 */
public class Stage {

    //mongoTemplate requires this constructor.
    public Stage() {
    }

    private String stageName;

    private String status;

    private String startTimestamp;

    private String stopTimestamp;

    private List<Metrics> metrics;

    public Stage(String stageName, String status,
            String startTimestamp, String stopTimestamp, List<Metrics> metrics) {
        super();
        this.stageName = stageName;
        this.status = status;
        this.startTimestamp = startTimestamp;
        this.stopTimestamp = stopTimestamp;
        if (metrics == null) {
            metrics = new  LinkedList<Metrics>();
        }
        this.metrics = metrics;
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

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public String getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(String stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public List<Metrics> getMetrics() {
        if (metrics == null) {
            metrics = new  LinkedList<Metrics>();
        }
        return metrics;
    }

    public void setMetrics(List<Metrics> metrics) {
        this.metrics = metrics;
    }

    public void update(String stageName, String status, String startTimestamp, String stopTimestamp) {
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
        this.setStartTimestamp(NewBatchJob.getCurrentTimeStamp());
    }

    public void stopStage() {
        this.setStatus("finished");
        this.setStopTimestamp(NewBatchJob.getCurrentTimeStamp());
    }

}
