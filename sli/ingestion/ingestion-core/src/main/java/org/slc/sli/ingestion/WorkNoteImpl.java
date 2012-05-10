package org.slc.sli.ingestion;

import java.io.Serializable;

/**
 * Unit of work, chunked by Maestro, to be executed by a member of the orchestra (pit).
 *
 * @author shalka
 */
public class WorkNoteImpl implements WorkNote, Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private final String batchJobId;
    private final IngestionStagedEntity ingestionStagedEntity;
    private final long rangeMinimum;
    private final long rangeMaximum;

    /**
     * Default constructor for the WorkOrder class.
     *
     * @param batchJobId
     * @param collection
     * @param minimum
     * @param maximum
     */
    public WorkNoteImpl(String batchJobId, IngestionStagedEntity ingestionStagedEntity, long minimum, long maximum) {
        this.batchJobId = batchJobId;
        this.ingestionStagedEntity = ingestionStagedEntity;
        this.rangeMinimum = minimum;
        this.rangeMaximum = maximum;
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
     * Gets the staged entity.
     *
     * @return staged entity to perform work on.
     */
    @Override
    public IngestionStagedEntity getIngestionStagedEntity() {
        return ingestionStagedEntity;
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
     * Gets the maximum value of the index [inclusive] to perform work on.
     *
     * @return maximum index value.
     */
    @Override
    public long getRangeMaximum() {
        return rangeMaximum;
    }

    @Override
    public String toString() {
        return "WorkNoteImpl [batchJobId=" + batchJobId + ", ingestionStagedEntity=" + ingestionStagedEntity
                + ", rangeMinimum=" + rangeMinimum + ", rangeMaximum=" + rangeMaximum + "]";
    }

}
