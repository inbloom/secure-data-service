package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;

/**
 * Base TransformationStrategy.
 *
 * @author dduran
 *
 */
public abstract class AbstractTransformationStrategy implements TransformationStrategy {

    private String batchJobId;
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Override
    public void perform(String batchJobId) {
        this.setBatchJobId(batchJobId);
        this.performTransformation();
    }
    
    abstract void performTransformation();

    /**
     * @return the neutralRecordMongoAccess
     */
    public NeutralRecordMongoAccess getNeutralRecordMongoAccess() {
        return neutralRecordMongoAccess;
    }

    /**
     * @param neutralRecordMongoAccess the neutralRecordMongoAccess to set
     */
    public void setNeutralRecordMongoAccess(NeutralRecordMongoAccess neutralRecordMongoAccess) {
        this.neutralRecordMongoAccess = neutralRecordMongoAccess;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

}
