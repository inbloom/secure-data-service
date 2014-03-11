package org.slc.sli.api.ingestion.service;

import java.util.List;

import org.slc.sli.api.ingestion.DAO.IngestionBatchJobDAO;
import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.slc.sli.api.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.stereotype.Service;

@Service
public class IngestionBatchJobServiceImpl implements IngestionBatchJobService {

	private static final Logger LOG = LoggerFactory.getLogger(IngestionBatchJobService.class);

	public static final int DEFAULT_INGESTION_LIMIT = 50;
	public static final int DEFAULT_OFFSET = 0;
	public static final String DEFAULT_SORT_ON = "jobStartTimestamp";
	public static final Order DEFAULT_SORT_ORDER = Order.DESCENDING;

	@Autowired
	IngestionBatchJobDAO ingestionBatchJobDAO;

	public List<IngestionBatchJob> find() {
		return find(DEFAULT_INGESTION_LIMIT, DEFAULT_OFFSET, DEFAULT_SORT_ON, DEFAULT_SORT_ORDER);
	}

	public List<IngestionBatchJob> find(Integer limit) {
		return find(limit, null, DEFAULT_SORT_ON, DEFAULT_SORT_ORDER);
	}

	public List<IngestionBatchJob> find(Integer limit, Integer offset) {
		return find(limit, offset, DEFAULT_SORT_ON, DEFAULT_SORT_ORDER);
	}

	public List<IngestionBatchJob> find(String sortOn, Order order) {
		return find(null, null, sortOn, order);
	}

	public List<IngestionBatchJob> find(Integer limit, String sortOn, Order order) {
		return find(limit, null, sortOn, order);
	}

	public List<IngestionBatchJob> find(Integer limit, Integer offset, String sortOn, Order order) {
		LOG.info("Find with all params getting called");
		return ingestionBatchJobDAO.find(SecurityUtil.getTenantId(), limit, offset, sortOn, order);
	}

	public IngestionBatchJob findOne(String id) {
		return ingestionBatchJobDAO.findOne(SecurityUtil.getTenantId(), id);
	}
}