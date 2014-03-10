package org.slc.sli.api.ingestion.DAO;

import java.util.List;

import org.slc.sli.api.ingestion.model.IngestionBatchJob;

public interface IngestionBatchJobDAO {

	public List<IngestionBatchJob> find(int limit);

}
