package org.slc.sli.ingestion.transformation;

import org.slc.sli.ingestion.Job;

/**
 * @author dduran
 */
public interface TransformationStrategy {
    
    /**
     * Perform a specific transformation on the batch job with provided id.
     * 
     * @param batchJobId
     */
    void perform(Job job);
    
}
