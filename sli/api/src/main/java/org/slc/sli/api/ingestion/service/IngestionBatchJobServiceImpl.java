package org.slc.sli.api.ingestion.service;

import java.util.List;

import org.slc.sli.api.ingestion.DAO.IngestionBatchJobDAO;
import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.slc.sli.api.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.stereotype.Service;

@Service
public class IngestionBatchJobServiceImpl implements IngestionBatchJobService {

	public static final int DEFAULT_INGESTION_LIMIT = 50;
	public static final int DEFAULT_OFFSET = 0;
	public static final String DEFAULT_SORT_ON = "jobStartTimestamp";
	public static final Order DEFAULT_SORT_ORDER = Order.ASCENDING;

	@Autowired
	IngestionBatchJobDAO ingestionBatchJobDAO;

	public List<IngestionBatchJob> find() {
		return find(DEFAULT_INGESTION_LIMIT, DEFAULT_OFFSET, DEFAULT_SORT_ON, DEFAULT_SORT_ORDER);
	}

	public List<IngestionBatchJob> find(int limit) {
		return find(limit, null, DEFAULT_SORT_ON, DEFAULT_SORT_ORDER);
	}

	public List<IngestionBatchJob> find(int limit, int offset) {
		return find(limit, offset, DEFAULT_SORT_ON, DEFAULT_SORT_ORDER);
	}

	public List<IngestionBatchJob> find(String sortOn, Order order) {
		return find(null, null, sortOn, order);
	}

	public List<IngestionBatchJob> find(int limit, String sortOn, Order order) {
		return find(limit, null, sortOn, order);
	}

	public List<IngestionBatchJob> find(Integer limit, Integer offset, String sortOn, Order order) {
		return ingestionBatchJobDAO.find(SecurityUtil.getTenantId(), limit, offset, sortOn, order);
	}
}