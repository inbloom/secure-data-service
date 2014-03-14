/*
 * Copyright 2012-2014 inBloom, Inc. and its affiliates.
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

package org.slc.sli.api.ingestion.DAO;

import java.util.List;

import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * A basic implementation of the IngestionBatchJobDAO interface that uses a MongoTemplate
 * for performing requests of mongo.
 * 
 * All queries to this collection need to be tenant specific so that a user cannot see items
 * from another tenant. This has various methods for getting data to support pagination as
 * well as sortability
 * 
 * @author mbrush
 */

@Component
public class IngestionBatchJobDAOImpl implements IngestionBatchJobDAO {

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
