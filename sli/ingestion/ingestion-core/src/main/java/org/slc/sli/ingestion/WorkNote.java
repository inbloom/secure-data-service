package org.slc.sli.ingestion;

/**
 * Unit of work, chunked by Maestro, to be executed by a member of the orchestra (pit).
 *
 * @author shalka
 */
public interface WorkNote {

    /**
     * Gets the batch job id.
     *
     * @return String representing batch job id.
     */
    String getBatchJobId();

    /**
     * Gets the staged entity.
     *
     * @return staged entity to perform work on.
     */
    IngestionStagedEntity getIngestionStagedEntity();

    /**
     * Gets the minimum value of the index [inclusive] to perform work on.
     *
     * @return minimum index value.
     */
    long getRangeMinimum();

    /**
     * Gets the maximum value of the index [inclusive] to perform work on.
     *
     * @return maximum index value.
     */
    long getRangeMaximum();

}
