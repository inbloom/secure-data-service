package org.slc.sli.ingestion;

import static org.slc.sli.ingestion.model.da.BatchJobMongoDA.logIngestionError;

import java.io.Serializable;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Validation Report callback based to populate batch job database IngestionErrors.
 * Caches batchJobId, stageType, resourceId, and hasError
 * 
 * @author bsuzuki
 *
 */
public class DatabaseLoggingErrorReport implements Serializable, ErrorReport {

    private static final long serialVersionUID = 2190485796418439710L;

    private String batchJobId = null;
    private BatchJobStageType stage = null;
    private String resourceId = null;
    private boolean privateInformation = false;  // TODO decide whether we need to encrypt or can identify an input record
                                                 //  by initial input file and line number
    private boolean hasErrors = false;
    
//    logIngestionError(String ingestionJobId, 
//            String errorId, 
//            String stageName, 
//            String fileId,
//            String sourceIp,
//            String hostname,
//            String recordIdentifier, 
//            String timestamp, 
//            String severity, 
//            String errorType, 
//            String errorDetail)
            
    public DatabaseLoggingErrorReport() {
    }

    public DatabaseLoggingErrorReport(String id, BatchJobStageType stageType, String resourceId) {
        this.batchJobId = id;
        this.stage = stageType;
        this.resourceId = resourceId;
    }
    
    @Override
    public void fatal(String message, Object sender) {
        error(message, sender);
    }

    @Override
    public void error(String message, Object sender) {

//        if (privateInformation) {
//            // TODO remove or encrypt message in favor of recordId
//        }
            
        logIngestionError(batchJobId, 
                (stage == null) ? null : stage.getName(), 
                resourceId, 
                BatchJobUtils.getHostName(), 
                BatchJobUtils.getHostAddress(), 
                null, 
                BatchJobUtils.getTimeStamp(), 
                FaultType.TYPE_ERROR.getName(), // TODO define severity based on logging strategy
                FaultType.TYPE_ERROR.getName(),
                message);
        hasErrors = true;
    }

    @Override
    public void warning(String message, Object sender) {

//      if (privateInformation) {
//      // TODO remove or encrypt message in favor of recordId
//  }
      
        logIngestionError(batchJobId, 
                (stage == null) ? "" : stage.getName(), 
                resourceId, 
                BatchJobUtils.getHostName(), 
                BatchJobUtils.getHostAddress(), 
                null, 
                BatchJobUtils.getTimeStamp(), 
                FaultType.TYPE_WARNING.getName(),  // TODO define severity based on logging strategy
                FaultType.TYPE_WARNING.getName(),
                message);
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

    /**
     * @return the batchJobId
     */
    public String getBatchJobId() {
        return batchJobId;
    }

    /**
     * @param batchJobid the batchJobId to set
     */
    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    /**
     * @return the stage
     */
    public BatchJobStageType getBatchJobStage() {
        return stage;
    }

    /**
     * @param stage the stage to set
     */
    public void setBatchJobStage(BatchJobStageType stage) {
        this.stage = stage;
    }

    /**
     * @return the resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * @param resourceId the resourceId to set
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * @return the encryptMessage
     */
    public boolean isPrivateInformation() {
        return privateInformation;
    }

    /**
     * @param encryptMessage the encryptMessage to set
     */
    public void setPrivateInformation(boolean containsPrivateInformation) {
        this.privateInformation = containsPrivateInformation;
    }

    /**
     * @param hasErrors the hasErrors to set
     */
    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }
    
    public void resetCachedInformation() {
        batchJobId = null;
        stage = null;
        resourceId = null;
        privateInformation = false;
        hasErrors = false;
    }
}
