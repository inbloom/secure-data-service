package org.slc.sli.api.ingestion.service;

import java.util.List;

import org.slc.sli.api.ingestion.DAO.IngestionBatchJobDAO;
import org.slc.sli.api.ingestion.model.IngestionBatchJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IngestionBatchJobServiceImpl implements IngestionBatchJobService {

	@Autowired
	IngestionBatchJobDAO ingestionJobDAO;

	public List<IngestionBatchJob> find(int limit) {
		return ingestionJobDAO.find(limit);
	}
}