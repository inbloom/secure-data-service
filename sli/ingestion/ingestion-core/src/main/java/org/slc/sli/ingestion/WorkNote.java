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
