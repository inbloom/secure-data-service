package org.slc.sli.ingestion.model;

import org.apache.commons.lang.time.FastDateFormat;


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

    private String startTimestamp;

    private String stopTimestamp;

    private long recordCount;

    private long errorCount;

    private static final String STR_TIMESTAMP_FORMAT = "yyyyMMdd hh:mm:ss.SSS";

    private static final FastDateFormat FORMATTER = FastDateFormat.getInstance(STR_TIMESTAMP_FORMAT);

    // mongoTemplate requires this constructor.
    public Metrics() {
    }

    public Metrics(String resourceId, String sourceIp, String hostname) {
        this(resourceId, sourceIp, hostname, null, null, 0, 0);
    }

    public Metrics(String resourceId, String sourceIp, String hostname, String startTimestamp, String stopTimestamp,
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

    public void update(String resourceId, String sourceIp, String hostname, String startTimestamp,
            String stopTimestamp, long recordCount, long errorCount) {
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
        this.setStartTimestamp(NewBatchJob.getCurrentTimeStamp());
    }

    public void stopMetric() {
        this.setStopTimestamp(NewBatchJob.getCurrentTimeStamp());
    }
}
