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
     * Sets the batch job id.
     *
     * @param batchJobId
     *            String representing the batch job id.
     */
    void setBatchJobId(String batchJobId);

    /**
     * Gets the collection.
     *
     * @return collection to perform work on.
     */
    String getCollection();

    /**
     * Sets the collection to perform work on.
     *
     * @param collection
     *            collection to perform work on.
     */
    void setCollection(String collection);

    /**
     * Gets the minimum value of the index [inclusive] to perform work on.
     *
     * @return minimum index value.
     */
    long getRangeMinimum();

    /**
     * Sets the minimum value of the index [inclusive] to perform work on.
     *
     * @param rangeMinimum
     *            minimum index value.
     */
    void setRangeMinimum(long rangeMinimum);

    /**
     * Gets the maximum value of the index [inclusive] to perform work on.
     *
     * @return maximum index value.
     */
    long getRangeMaximum();

    /**
     * Sets the maximum value of the index [inclusive] to perform work on.
     *
     * @param rangeMaximum
     *            maximum index value.
     */
    void setRangeMaximum(long rangeMaximum);
}
