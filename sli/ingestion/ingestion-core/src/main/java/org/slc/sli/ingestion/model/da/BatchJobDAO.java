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

    List<Error> findBatchJobErrors(String batchJobId);

    void saveError(Error error);
}
