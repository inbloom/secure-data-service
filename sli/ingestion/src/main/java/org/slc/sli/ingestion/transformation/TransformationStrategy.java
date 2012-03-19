package org.slc.sli.ingestion.transformation;

/**
 * @author dduran
 */
public interface TransformationStrategy {

    /**
     * Perform a specific transformation on the batch job with provided id.
     *
     * @param batchJobId
     */
    void perform(String batchJobId);

}
