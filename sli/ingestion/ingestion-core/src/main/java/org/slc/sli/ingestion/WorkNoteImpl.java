package org.slc.sli.ingestion;

import java.io.Serializable;
import java.util.Date;

/**
 * Unit of work, chunked by Maestro, to be executed by a member of the orchestra (pit).
 *
 * @author shalka
 */
public final class WorkNoteImpl implements WorkNote, Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private final String batchJobId;
    private final IngestionStagedEntity ingestionStagedEntity;
    private final Date startTime;
    private final Date endTime;

    private int batchSize;

    /**
     * Default constructor for the WorkOrder class.
     *
     * @param batchJobId
     * @param collection
     * @param minimum
     * @param maximum
     */
    private WorkNoteImpl(String batchJobId, IngestionStagedEntity ingestionStagedEntity, Date startTime, Date endTime, int batchSize) {
        this.batchJobId = batchJobId;
        this.ingestionStagedEntity = ingestionStagedEntity;
        this.startTime = startTime;
        this.endTime = endTime;
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
        return new WorkNoteImpl(batchJobId, null, new Date(), new Date(), 0);
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
    public static WorkNoteImpl createBatchedWorkNote(String batchJobId, IngestionStagedEntity ingestionStagedEntity, Date startTime, Date endTime, int batchSize) {
        return new WorkNoteImpl(batchJobId, ingestionStagedEntity, startTime, endTime, batchSize);
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
    public Date getRangeMinimum() {
        return startTime;
    }

    /**
     * Gets the maximum value of the index [inclusive] to perform work on.
     *
     * @return maximum index value.
     */
    @Override
    public Date getRangeMaximum() {
        return endTime;
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
        result = prime * result + (int) endTime.getTime();
        result = prime * result + (int) startTime.getTime();

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
        if (startTime != other.startTime) {
            return false;
        }
        if (endTime != other.endTime) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkNoteImpl [batchJobId=" + batchJobId + ", ingestionStagedEntity=" + ingestionStagedEntity
                + ", startTime=" + startTime + ", endTime=" + endTime + ", batchSize=" + batchSize
                + "]";
    }

    @Override
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;

    }

}
