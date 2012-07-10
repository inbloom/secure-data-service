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

package org.slc.sli.ingestion.model.da;

import java.util.List;

import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;

/**
 * Data access object for batch job data.
 *
 * @author dduran
 *
 */
public interface BatchJobDAO {

    void saveBatchJob(NewBatchJob newBatchJob);

    NewBatchJob findBatchJobById(String batchJobId);

    /**
     * Why is this deprecated?
     *
     * @param batchJobId
     * @return
     */
    @Deprecated
    List<Error> findBatchJobErrors(String batchJobId);

    public Iterable<Error> getBatchJobErrors(String jobId, int limit);

    void saveError(Error error);

    void saveBatchJobStageSeparatelly(String batchJobId, Stage stage);

    List<Stage> getBatchStagesStoredSeperatelly(String batchJobId);

    /**
     * Try to acquire a lock on the provided tenant, on behalf of the provided job id.
     *
     * @param tenantId
     * @param batchJobId
     *
     * @return true if lock was acquired. false otherwise.
     */
    boolean attemptTentantLockForJob(String tenantId, String batchJobId);

    /**
     * Release lock (if present) for the given tenant if it is held by a job with the provided id.
     *
     * @param tenantId
     * @param batchJobId
     */
    void releaseTenantLockForJob(String tenantId, String batchJobId);
}
