package org.slc.sli.ingestion.validation;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 *
 * @author bsuzuki
 *
 */
public class DatabaseLoggingErrorReport implements Serializable, ErrorReport {

    private static final long serialVersionUID = 2190485796418439710L;

    private final String batchJobId;
    private final BatchJobStageType stage;
    private final String resourceId;

    private boolean hasErrors;

    @Autowired
    BatchJobDAO batchJobDAO;

    public DatabaseLoggingErrorReport(String batchJobId, BatchJobStageType stageType, String resourceId) {
        this.batchJobId = batchJobId;
        this.stage = stageType;
        this.resourceId = resourceId;
    }

    @Override
    public void fatal(String message, Object sender) {
        error(message, sender);
    }

    @Override
    public void error(String message, Object sender) {

        String recordIdentifier = null;
        Error error = Error.createIngestionError(batchJobId, (stage == null) ? null : stage.getName(), resourceId,
                BatchJobUtils.getHostName(), BatchJobUtils.getHostAddress(), recordIdentifier,
                FaultType.TYPE_ERROR.getName(), FaultType.TYPE_ERROR.getName(), message);
        batchJobDAO.saveError(error);

        hasErrors = true;
    }

    @Override
    public void warning(String message, Object sender) {

        String recordIdentifier = null;
        Error error = Error.createIngestionError(batchJobId, (stage == null) ? "" : stage.getName(), resourceId,
                BatchJobUtils.getHostName(), BatchJobUtils.getHostAddress(), recordIdentifier,
                FaultType.TYPE_WARNING.getName(), FaultType.TYPE_WARNING.getName(), message);
        batchJobDAO.saveError(error);
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public BatchJobStageType getBatchJobStage() {
        return stage;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

}
