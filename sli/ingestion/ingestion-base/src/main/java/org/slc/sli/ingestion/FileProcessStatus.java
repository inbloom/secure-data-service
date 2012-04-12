package org.slc.sli.ingestion;

/**
 *
 */
public class FileProcessStatus {
    private String jobId;

    private long totalRecordCount;

    public String getJobId() {
        return this.jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public long getTotalRecordCount() {
        return this.totalRecordCount;
    }

    public void setTotalRecordCount(long totalRecordCount) {
        this.totalRecordCount = totalRecordCount;
    }
}
