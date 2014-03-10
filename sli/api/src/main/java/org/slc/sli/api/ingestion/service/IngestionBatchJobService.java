package org.slc.sli.api.ingestion.service;

import java.util.List;

import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.springframework.data.mongodb.core.query.Order;

public interface IngestionBatchJobService {

	public List<IngestionBatchJob> find();
	public List<IngestionBatchJob> find(int limit);
	public List<IngestionBatchJob> find(int limit, int offset);
	public List<IngestionBatchJob> find(String sortOn, Order order);
	public List<IngestionBatchJob> find(int limit, String sortOn, Order order);
	public List<IngestionBatchJob> find(Integer limit, Integer offset, String sortOn, Order order);

}