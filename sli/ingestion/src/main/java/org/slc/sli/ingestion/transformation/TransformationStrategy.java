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
    // TODO provide return type? ErrorReport?
    void perform(String batchJobId);

}
