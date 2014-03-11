package org.slc.sli.api.ingestion.DAO;

import java.util.List;

import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class IngestionBatchJobDAOImpl implements IngestionBatchJobDAO {
	
	private static final Logger LOG = LoggerFactory.getLogger(IngestionBatchJobDAO.class);

	@Autowired
	MongoTemplate ingestionBatchJobMongoTemplate;

	public List<IngestionBatchJob> find(String tenantId) {
		return find(tenantId, null, null, null, null);
	}

	public List<IngestionBatchJob> find(String tenantId, Integer limit) {
		return find(tenantId, limit, null, null, null);
	}

	public List<IngestionBatchJob> find(String tenantId, Integer limit, Integer offset) {
		return find(tenantId, limit, offset, null, null);
	}

	public List<IngestionBatchJob> find(String tenantId, Integer limit, Integer offset, String sortOn) {
		return find(tenantId, limit, offset, sortOn, null);
	}

	public List<IngestionBatchJob> find(String tenantId, Integer limit, Integer offset, String sortOn, Order order) {
		Query query = new Query();

		// Tenant Id is required.
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		/*
		 * We are going to allow for null for multiple items, so we'll do a null check on each.
		 */
		if (limit != null) {
			query.limit(limit);
		}
		
		if (offset != null) {
			query.skip(offset);
		}

		if (sortOn != null && !"".equals(sortOn)) {
			if (order != null) {
				query.sort().on(sortOn, order);
			} else {
				query.sort().on(sortOn, Order.ASCENDING);
			}
		}
		return ingestionBatchJobMongoTemplate.find(query, IngestionBatchJob.class);
	}

	public IngestionBatchJob findOne(String tenantId, String id) {
		Query query = new Query();
		
		query.addCriteria(Criteria.where("tenantId").is(tenantId));
		query.addCriteria(Criteria.where("_id").is(id));
		
		return ingestionBatchJobMongoTemplate.findOne(query, IngestionBatchJob.class);
	}
}
