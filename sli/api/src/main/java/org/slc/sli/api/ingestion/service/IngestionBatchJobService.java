package org.slc.sli.api.ingestion.service;

import java.util.List;

import org.slc.sli.api.ingestion.model.IngestionBatchJob;

public interface IngestionBatchJobService {

	public List<IngestionBatchJob> find(int limit);

}