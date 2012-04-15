package org.slc.sli.ingestion.model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Fault;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;

/**
 *
 * @author dduran
 *
 */
@Document
public final class Error {

    private String batchJobId;

    private String stageName;

    private String resourceId;

    private String sourceIp;

    private String hostname;

    private String recordIdentifier;

    private String timestamp;

    private String severity;

    private String errorType;

    private String errorDetail;

    // mongoTemplate requires this constructor.
    public Error() {
    }

    public Error(String batchJobId, String stageName, String resourceId, String sourceIp, String hostname,
            String recordIdentifier, String timestamp, String severity, String errorType, String errorDetail) {
        this.batchJobId = batchJobId;
        this.stageName = stageName;
        this.resourceId = resourceId;
        this.sourceIp = sourceIp;
        this.hostname = hostname;
        this.recordIdentifier = recordIdentifier;
        this.timestamp = timestamp;
        this.severity = severity;
        this.errorType = errorType;
        this.errorDetail = errorDetail;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
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

    public String getRecordIdentifier() {
        return recordIdentifier;
    }

    public void setRecordIdentifier(String recordIdentifier) {
        this.recordIdentifier = recordIdentifier;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public static void writeErrorsToMongo(String batchId, FaultsReport errorReport) {
        if (errorReport.hasErrors()) {
            List<Fault> faults = errorReport.getFaults();
            for (Fault fault : faults) {
                String errorType = new String();
                if (fault.isError()) {
                    errorType = "ERROR";
                } else if (fault.isWarning()) {
                    errorType = "WARNING";
                } else {
                    errorType = "OTHER";
                }
                BatchJobMongoDA.logBatchStageError(batchId, BatchJobStageType.ZIP_FILE_PROCESSING,
                        FaultType.TYPE_ERROR.getName(), errorType, fault.getMessage());
            }
        }
    }

}
