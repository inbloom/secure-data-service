package org.slc.sli.ingestion.model.da;

import java.util.List;

import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;

/**
 *
 * @author dduran
 *
 */
public interface BatchJobDAO {

    void saveBatchJob(NewBatchJob newBatchJob);

    NewBatchJob findBatchJobById(String batchJobId);

    @Deprecated
    List<Error> findBatchJobErrors(String batchJobId);

    public Iterable<Error> getBatchJobErrors(String jobId, int limit);

    void saveError(Error error);
}
