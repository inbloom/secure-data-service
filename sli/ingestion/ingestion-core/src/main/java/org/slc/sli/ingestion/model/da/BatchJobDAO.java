package org.slc.sli.ingestion.model.da;

import org.slc.sli.ingestion.model.NewBatchJob;

/**
 *
 * @author dduran
 *
 */
public interface BatchJobDAO {

    BatchJobMongoDAStatus saveBatchJob(NewBatchJob newBatchJob);

    NewBatchJob findBatchJobById(String batchJobId);

    BatchJobMongoDAStatus findBatchJobErrors(String batchJobId);
}
