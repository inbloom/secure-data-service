package org.slc.sli.ingestion.transformation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.validation.DatabaseLoggingErrorReport;
import org.slc.sli.ingestion.validation.ErrorReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Base TransformationStrategy.
 * 
 * @author dduran
 * @author shalka
 */
public abstract class AbstractTransformationStrategy implements TransformationStrategy {
    
    protected static final String BATCH_JOB_ID_KEY = "batchJobId";
    protected static final String TYPE_KEY = "type";
    
    private String batchJobId;
    private Job job;
    private WorkNote workNote;
    
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
    
    @Override
    public void perform(Job job, WorkNote workNote) {
        this.setJob(job);
        this.setBatchJobId(job.getId());
        this.setWorkNote(workNote);
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
    
    /**
     * Gets the Work Note corresponding to this job.
     * 
     * @return Work Note containing information about collection and range to operate on.
     */
    public WorkNote getWorkNote() {
        return workNote;
    }
    
    /**
     * Sets the Work Note for this job.
     * 
     * @param workNote
     *            Work Note containing information about collection and range to operate on.
     */
    public void setWorkNote(WorkNote workNote) {
        this.workNote = workNote;
    }
    
    /**
     * Returns collection entities found in staging ingestion database. If a work note was not
     * provided for
     * the job, then all entities in the collection will be returned.
     * 
     * @param collectionName
     *            name of collection to be queried for.
     */
    public Map<Object, NeutralRecord> getCollectionFromDb(String collectionName) {
        Iterable<NeutralRecord> data;
        Query query = new Query().limit(0);
        
        WorkNote note = getWorkNote();
        if (note.getBatchSize() == 1) {
            Criteria limiter = Criteria.where("creationTime").gt(0);
            query.addCriteria(limiter);
        } else {
            Criteria limiter = Criteria.where("creationTime").gte(note.getRangeMinimum()).lt(note.getRangeMaximum());
            query.addCriteria(limiter);
        }
        
        data = getNeutralRecordMongoAccess().getRecordRepository().findByQueryForJob(collectionName, query,
                getJob().getId());
        Map<Object, NeutralRecord> collection = new HashMap<Object, NeutralRecord>();
        NeutralRecord tempNr = null;
        
        Iterator<NeutralRecord> neutralRecordIterator = data.iterator();
        while (neutralRecordIterator.hasNext()) {
            tempNr = neutralRecordIterator.next();
            collection.put(tempNr.getRecordId(), tempNr);
        }
        return collection;
    }
}
