package org.slc.sli.ingestion;

import java.io.Serializable;

/**
 * Unit of work, chunked by Maestro, to be executed by a member of the orchestra (pit).
 *
 * @author shalka
 */
public class WorkNoteImpl implements WorkNote, Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private String batchJobId;
    private String collection;
    private long rangeMinimum;
    private long rangeMaximum;

    /**
     * Default constructor for the WorkOrder class.
     *
     * @param batchJobId
     * @param collection
     * @param minimum
     * @param maximum
     */
    public WorkNoteImpl(String batchJobId, String collection, long minimum, long maximum) {
        this.setBatchJobId(batchJobId);
        this.setCollection(collection);
        this.setRangeMinimum(minimum);
        this.setRangeMaximum(maximum);
    }

    /**
     * Gets the batch job id.
     *
     * @return String representing batch job id.
     */
    @Override
    public String getBatchJobId() {
        return batchJobId;
    }

    /**
     * Sets the batch job id.
     *
     * @param batchJobId
     *            String representing the batch job id.
     */
    @Override
    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    /**
     * Gets the collection.
     *
     * @return collection to perform work on.
     */
    @Override
    public String getCollection() {
        return collection;
    }

    /**
     * Sets the collection to perform work on.
     *
     * @param collection
     *            collection to perform work on.
     */
    @Override
    public void setCollection(String collection) {
        this.collection = collection;
    }

    /**
     * Gets the minimum value of the index [inclusive] to perform work on.
     *
     * @return minimum index value.
     */
    @Override
    public long getRangeMinimum() {
        return rangeMinimum;
    }

    /**
     * Sets the minimum value of the index [inclusive] to perform work on.
     *
     * @param rangeMinimum
     *            minimum index value.
     */
    @Override
    public void setRangeMinimum(long rangeMinimum) {
        this.rangeMinimum = rangeMinimum;
    }

    /**
     * Gets the maximum value of the index [inclusive] to perform work on.
     *
     * @return maximum index value.
     */
    @Override
    public long getRangeMaximum() {
        return rangeMaximum;
    }

    /**
     * Sets the maximum value of the index [inclusive] to perform work on.
     *
     * @param rangeMaximum
     *            maximum index value.
     */
    @Override
    public void setRangeMaximum(long rangeMaximum) {
        this.rangeMaximum = rangeMaximum;
    }

    @Override
    public String toString() {
        return "WorkNoteImpl [batchJobId=" + batchJobId + ", collection=" + collection + ", rangeMinimum="
                + rangeMinimum + ", rangeMaximum=" + rangeMaximum + "]";
    }
}
