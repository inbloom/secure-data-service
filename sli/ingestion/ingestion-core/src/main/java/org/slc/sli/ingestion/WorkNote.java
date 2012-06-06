package org.slc.sli.ingestion;

import java.util.Date;

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
    Date getRangeMinimum();

    /**
     * Gets the maximum value of the index [inclusive] to perform work on.
     *
     * @return maximum index value.
     */
    Date getRangeMaximum();

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
