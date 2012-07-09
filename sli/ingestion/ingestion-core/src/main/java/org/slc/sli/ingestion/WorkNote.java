/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.ingestion;

import java.io.Serializable;

/**
 * Unit of work, chunked by Maestro, to be executed by a member of the orchestra (pit).
 *
 * @author shalka
 */
public final class WorkNote implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private final String batchJobId;
    private final IngestionStagedEntity ingestionStagedEntity;
    private final long startTime;
    private final long endTime;
    private final long recordsInRange;

    private int batchSize;

    /**
     * Default constructor for the WorkOrder class.
     *
     * @param batchJobId
     * @param collection
     * @param minimum
     * @param maximum
     */
    private WorkNote(String batchJobId, IngestionStagedEntity ingestionStagedEntity, long startTime, long endTime,
            long recordsInRange, int batchSize) {
        this.batchJobId = batchJobId;
        this.ingestionStagedEntity = ingestionStagedEntity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recordsInRange = recordsInRange;
        this.batchSize = batchSize;
    }

    /**
     * Create a simple WorkNote, note part of any batch.
     *
     * @param batchJobId
     * @param ingestionStagedEntity
     * @return
     */
    public static WorkNote createSimpleWorkNote(String batchJobId) {
        long now = System.currentTimeMillis();
        return new WorkNote(batchJobId, null, now, now, 0L, 0);
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
    public static WorkNote createBatchedWorkNote(String batchJobId, IngestionStagedEntity ingestionStagedEntity,
            long startTime, long endTime, long recordsInRage, int batchSize) {
        return new WorkNote(batchJobId, ingestionStagedEntity, startTime, endTime, recordsInRage, batchSize);
    }

    /**
     * Gets the batch job id.
     *
     * @return String representing batch job id.
     */
    public String getBatchJobId() {
        return batchJobId;
    }

    /**
     * Gets the staged entity.
     *
     * @return staged entity to perform work on.
     */
    public IngestionStagedEntity getIngestionStagedEntity() {
        return ingestionStagedEntity;
    }

    /**
     * Gets the minimum value of the index [inclusive] to perform work on.
     *
     * @return minimum index value.
     */
    public long getRangeMinimum() {
        return startTime;
    }

    /**
     * Gets the maximum value of the index [exclusive] to perform work on.
     *
     * @return maximum index value.
     */
    public long getRangeMaximum() {
        return endTime;
    }

    /**
     * The expected number of records that have:
     * (creationTime >= rangeMinimum && creationTime < rangeMaximum)
     *
     * @return # records within range.
     */
    public long getRecordsInRange() {
        return recordsInRange;
    }

    /**
     * The size of the batch that this WorkNote may be a part of.
     *
     * @return
     */
    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchJobId == null) ? 0 : batchJobId.hashCode());
        result = prime * result + batchSize;
        result = prime * result + (int) (endTime ^ (endTime >>> 32));
        result = prime * result + ((ingestionStagedEntity == null) ? 0 : ingestionStagedEntity.hashCode());
        result = prime * result + (int) (recordsInRange ^ (recordsInRange >>> 32));
        result = prime * result + (int) (startTime ^ (startTime >>> 32));
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
        WorkNote other = (WorkNote) obj;
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
        if (endTime != other.endTime) {
            return false;
        }
        if (ingestionStagedEntity == null) {
            if (other.ingestionStagedEntity != null) {
                return false;
            }
        } else if (!ingestionStagedEntity.equals(other.ingestionStagedEntity)) {
            return false;
        }
        if (recordsInRange != other.recordsInRange) {
            return false;
        }
        if (startTime != other.startTime) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "WorkNote [batchJobId=" + batchJobId + ", ingestionStagedEntity=" + ingestionStagedEntity
                + ", startTime=" + startTime + ", endTime=" + endTime + ", recordsInRange=" + recordsInRange
                + ", batchSize=" + batchSize + "]";
    }

}
