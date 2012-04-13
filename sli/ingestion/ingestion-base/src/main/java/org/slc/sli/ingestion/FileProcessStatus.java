package org.slc.sli.ingestion;

/**
 *
 */
public class FileProcessStatus {
    private String jobId;

    private long totalRecordCount;

    private String outputFile;

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

    public String getOutputFileName() {
        return this.outputFile;
    }

    public void setOutputFileName(String outputFile) {
        this.outputFile = outputFile;
    }
}
