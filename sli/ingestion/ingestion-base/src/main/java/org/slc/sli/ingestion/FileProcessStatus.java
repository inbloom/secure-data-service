package org.slc.sli.ingestion;

/**
 *
 */
public class FileProcessStatus {
    private String jobId;

    private long totalRecordCount;

    private String outputFilePath;
    private String outputFileName;

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
        return this.outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOutputFilePath() {
        return this.outputFilePath;
    }

    public void setOutputFilePath(String outputFile) {
        this.outputFilePath = outputFile;
    }
}
