package org.slc.sli.api.ingestion.DAO;

import java.util.List;

import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.springframework.data.mongodb.core.query.Order;

public interface IngestionBatchJobDAO {

	public List<IngestionBatchJob> find(String tenantId);
	public List<IngestionBatchJob> find(String tenantId, Integer limit);
	public List<IngestionBatchJob> find(String tenantId, Integer limit, Integer offset);
	public List<IngestionBatchJob> find(String tenantId, Integer limit, Integer offset, String sortOn);
	public List<IngestionBatchJob> find(String tenantId, Integer limit, Integer offset, String sortOn, Order order);

}
