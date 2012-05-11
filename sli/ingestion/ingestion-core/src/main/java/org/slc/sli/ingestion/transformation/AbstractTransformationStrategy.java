package org.slc.sli.ingestion.transformation;

import org.springframework.beans.factory.annotation.Autowired;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.validation.DatabaseLoggingErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;

/**
 * Base TransformationStrategy.
 *
 * @author dduran
 *
 */
public abstract class AbstractTransformationStrategy implements TransformationStrategy {

    protected static final String BATCH_JOB_ID_KEY = "batchJobId";
    private String batchJobId;
    private Job job;
    @Autowired
    private NeutralRecordMongoAccess neutralRecordMongoAccess;
    @Autowired
    private BatchJobDAO batchJobDAO;

    @Override
    public void perform(Job job) {
        this.setJob(job);
        this.setBatchJobId(job.getId());
        this.performTransformation();
    }

    protected abstract void performTransformation();

    /**
     * @return the neutralRecordMongoAccess
     */
    public NeutralRecordMongoAccess getNeutralRecordMongoAccess() {
        return neutralRecordMongoAccess;
    }

    /**
     * @param neutralRecordMongoAccess
     *            the neutralRecordMongoAccess to set
     */
    public void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        this.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public ErrorReport getErrorReport(String fileName) {
        return new DatabaseLoggingErrorReport(this.batchJobId, BatchJobStageType.TRANSFORMATION_PROCESSOR, fileName,
                this.batchJobDAO);
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

}
