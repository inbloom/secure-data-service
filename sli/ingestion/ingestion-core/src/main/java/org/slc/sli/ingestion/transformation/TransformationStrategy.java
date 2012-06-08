package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.WorkNote;

/**
 * @author dduran
 */
public interface TransformationStrategy {
    
    /**
     * Perform a specific transformation on the batch job with provided id.
     * 
     * @param job id of the current job
     */
    void perform(Job job);
    
    /**
     * Perform a specific transformation on the job with the provided id using the WorkNote to guide
     * what collection (and range) should be operated on.
     * 
     * @param job id of the current job
     * @param workNote work handed out by maestro to be performed by pit
     */
    void perform(Job job, WorkNote workNote);
    
}
