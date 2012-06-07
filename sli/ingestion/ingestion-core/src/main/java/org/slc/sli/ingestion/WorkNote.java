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
     * Gets the minimum value of the indexed field creationTime [inclusive] to perform work on.
     *
     * @return minimum index value.
     */
    long getRangeMinimum();

    /**
     * Gets the maximum value of the indexed field creationTime [inclusive] to perform work on.
     *
     * @return maximum index value.
     */
    long getRangeMaximum();

    /**
     * If this WorkNote is a part of a batch of WorkNotes, the size of said batch.
     *
     * @return size of batch. zero if not a part of a batch.
     */
    int getBatchSize();

    /**
     * Set total number of batches that this work note is a part of
     *
     * @param batchSize
     */
    void setBatchSize(int batchSize);

}
