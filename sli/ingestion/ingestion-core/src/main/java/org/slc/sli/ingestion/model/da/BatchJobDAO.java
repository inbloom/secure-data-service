/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
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

    public Iterable<Error> getBatchJobErrors(String jobId, String resourceId, FaultType type, int limit);

    void saveError(Error error);

    void saveBatchJobStage(String batchJobId, Stage stage);

    List<Stage> getBatchJobStages(String batchJobId);
    List<Stage> getBatchJobStages(String batchJobId, BatchJobStageType stageType);

    void insertRecordHash(String recordId, String newHashValues)
            throws DataAccessResourceFailureException;

    void updateRecordHash(RecordHash rh, String newHashValues)
            throws DataAccessResourceFailureException;

    void removeRecordHash( RecordHash rh )
        throws DataAccessResourceFailureException;

    void removeRecordHashByTenant(String tenantId);

    public RecordHash findRecordHash(String tenantId, String recordId);

    public MongoTemplate getMongoTemplate();

    boolean updateFileEntryLatch(String batchJobId, String filename);

    public boolean createFileLatch(String jobId, List<String> fileEntries);

    public boolean isDryRun(String jobId);

    public String getDuplicateDetectionMode(String jobId);
}
