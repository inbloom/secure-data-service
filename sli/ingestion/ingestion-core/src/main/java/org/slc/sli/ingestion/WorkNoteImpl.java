package org.slc.sli.ingestion;

import java.io.Serializable;

/**
 * Unit of work, chunked by Maestro, to be executed by a member of the orchestra (pit).
 *
 * @author shalka
 */
public final class WorkNoteImpl implements WorkNote, Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private final String batchJobId;
    private final IngestionStagedEntity ingestionStagedEntity;
    private final int rangeMinimum;
    private final int rangeMaximum;
    private final int batchSize;

    /**
     * Default constructor for the WorkOrder class.
     *
     * @param batchJobId
     * @param collection
     * @param minimum
     * @param maximum
     */
    private WorkNoteImpl(String batchJobId, IngestionStagedEntity ingestionStagedEntity, int minimum, int maximum,
            int batchSize) {
        this.batchJobId = batchJobId;
        this.ingestionStagedEntity = ingestionStagedEntity;
        this.rangeMinimum = minimum;
        this.rangeMaximum = maximum;
        this.batchSize = batchSize;
    }

    /**
     * Create a simple WorkNote, note part of any batch.
     *
     * @param batchJobId
     * @param ingestionStagedEntity
     * @return
     */
    public static WorkNoteImpl createSimpleWorkNote(String batchJobId) {
        return new WorkNoteImpl(batchJobId, null, 0, 0, 0);
    }

    /**
     * Create a WorkNote that is a part of a batch of WorkNotes.
     *
     * @param batchJobId
     * @param ingestionStagedEntity
     * @param maximum
     * @param minimum
     * @param batchSize
     * @return
     */
    public static WorkNoteImpl createBatchedWorkNote(String batchJobId, IngestionStagedEntity ingestionStagedEntity,
            int minimum, int maximum, int batchSize) {
        return new WorkNoteImpl(batchJobId, ingestionStagedEntity, minimum, maximum, batchSize);
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
    public int getRangeMinimum() {
        return rangeMinimum;
    }

    /**
     * Gets the maximum value of the index [inclusive] to perform work on.
     *
     * @return maximum index value.
     */
    @Override
    public int getRangeMaximum() {
        return rangeMaximum;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchJobId == null) ? 0 : batchJobId.hashCode());
        result = prime * result + batchSize;
        result = prime * result + ((ingestionStagedEntity == null) ? 0 : ingestionStagedEntity.hashCode());
        result = prime * result + rangeMaximum;
        result = prime * result + rangeMinimum;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WorkNoteImpl other = (WorkNoteImpl) obj;
        if (batchJobId == null) {
            if (other.batchJobId != null) {
                return false;
            }
        } else if (!batchJobId.equals(other.batchJobId)) {
            return false;
        }
        if (batchSize != other.batchSize) {
            return false;
        }
        if (ingestionStagedEntity == null) {
            if (other.ingestionStagedEntity != null) {
                return false;
            }
        } else if (!ingestionStagedEntity.equals(other.ingestionStagedEntity)) {
            return false;
        }
        if (rangeMaximum != other.rangeMaximum) {
            return false;
        }
        if (rangeMinimum != other.rangeMinimum) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkNoteImpl [batchJobId=" + batchJobId + ", ingestionStagedEntity=" + ingestionStagedEntity
                + ", rangeMinimum=" + rangeMinimum + ", rangeMaximum=" + rangeMaximum + ", batchSize=" + batchSize
                + "]";
    }

}
