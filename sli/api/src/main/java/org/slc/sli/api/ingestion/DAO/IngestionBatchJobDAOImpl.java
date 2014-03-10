package org.slc.sli.api.ingestion.DAO;

import java.util.List;

import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class IngestionBatchJobDAOImpl implements IngestionBatchJobDAO {

	@Autowired
	MongoTemplate ingestionBatchJobMongoTemplate;

	public List<IngestionBatchJob> find(int limit) {
		Query query = new Query();
		query.limit(limit);
		return ingestionBatchJobMongoTemplate.find(query, IngestionBatchJob.class);
	}
}
