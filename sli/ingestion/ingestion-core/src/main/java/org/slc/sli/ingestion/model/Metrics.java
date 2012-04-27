package org.slc.sli.ingestion.model;


import java.util.Date;

import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Model for metrics of an ingestion job with resolution into its stage and resource
 *
 * @author dduran
 *
 */
public class Metrics {

    private String resourceId;

    private String sourceIp;

    private String hostname;

    private Date startTimestamp;

    private Date stopTimestamp;

    private long recordCount;

    private long errorCount;

    // mongoTemplate requires this constructor.
    public Metrics() {
    }

    public Metrics(String resourceId, String sourceIp, String hostname) {
        this(resourceId, sourceIp, hostname, null, null, 0, 0);
    }

    public Metrics(String resourceId, String sourceIp, String hostname, Date startTimestamp, Date stopTimestamp,
            long recordCount, long errorCount) {
        super();
        this.resourceId = resourceId;
        this.sourceIp = sourceIp;
        this.hostname = hostname;
        this.startTimestamp = startTimestamp;
        this.stopTimestamp = stopTimestamp;
        this.recordCount = recordCount;
        this.errorCount = errorCount;
    }

    public static Metrics createAndStart(String resourceId) {
        Metrics metrics = new Metrics(resourceId, BatchJobUtils.getHostAddress(), BatchJobUtils.getHostName());
        metrics.setRecordCount(0);
        metrics.setErrorCount(0);
        metrics.startMetric();
        return metrics;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public void update(String resourceId, String sourceIp, String hostname, Date startTimestamp,
            Date stopTimestamp, long recordCount, long errorCount) {
        if (resourceId != null) {
            this.resourceId = resourceId;
        }
        if (sourceIp != null) {
            this.sourceIp = sourceIp;
        }
        if (hostname != null) {
            this.hostname = hostname;
        }
        if (startTimestamp != null) {
            this.startTimestamp = startTimestamp;
        }
        if (stopTimestamp != null) {
            this.stopTimestamp = stopTimestamp;
        }
        this.recordCount = recordCount;
        this.errorCount = errorCount;
    }

    public void startMetric() {
        this.setStartTimestamp(new Date());
    }

    public void stopMetric() {
        this.setStopTimestamp(new Date());
    }
}
