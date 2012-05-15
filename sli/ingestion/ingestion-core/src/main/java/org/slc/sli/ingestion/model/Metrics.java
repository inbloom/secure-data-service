package org.slc.sli.ingestion.model;

/**
 * Model for metrics of an ingestion job
 * 
 * @author dduran
 * 
 */
public class Metrics {
    
    private String resourceId;
    private long recordCount;
    private long errorCount;
    
    public static Metrics newInstance(String resourceId) {
        Metrics metrics = new Metrics(resourceId);
        metrics.setRecordCount(0);
        metrics.setErrorCount(0);
        return metrics;
    }
    
    // mongoTemplate requires this constructor.
    public Metrics() {
    }
    
    public Metrics(String resourceId) {
        this.resourceId = resourceId;
    }
    
    public Metrics(String resourceId, long recordCount, long errorCount) {
        this.resourceId = resourceId;
        this.recordCount = recordCount;
        this.errorCount = errorCount;
    }
    
    public String getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
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
    
    public void update(String resourceId, long recordCount, long errorCount) {
        if (resourceId != null) {
            this.resourceId = resourceId;
        }
        this.recordCount = recordCount;
        this.errorCount = errorCount;
    }

    @Override
    public String toString() {
        return String.format("%s ->%d[%d]", this.resourceId, this.recordCount, this.errorCount);
    }
    
    
}
