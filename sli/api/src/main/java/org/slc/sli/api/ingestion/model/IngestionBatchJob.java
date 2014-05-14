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


package org.slc.sli.api.ingestion.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A basic model representing the newBatchJob collection in Mongo. Unfortunately, this is written
 * specifically for use with the ingestionJobs api as NewBatchJob has some functionality in
 * it that caused errors when just trying to get data out of the database.
 *
 * 
 * @author mbrush
 */

@Document(collection = "newBatchJob")
public class IngestionBatchJob {

    private Date jobStartTimestamp;

    @Id
    private String id;

    private String tenantId;

    private String sourceId;

    private String topLevelSourceId;

    private String status;

    private int totalFiles;

    private Map<String, String> batchProperties;

    private List<Stage> stages;

    private List<ResourceEntry> resourceEntries;

	private Date jobStopTimestamp;

    public Date getJobStartTimestamp() {
		return jobStartTimestamp;
	}

	public void setJobStartTimestamp(Date jobStartTimestamp) {
		this.jobStartTimestamp = jobStartTimestamp;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getTenantId() {
		return tenantId;
	}


	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}


	public String getSourceId() {
		return sourceId;
	}


	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}


	public String getTopLevelSourceId() {
		return topLevelSourceId;
	}


	public void setTopLevelSourceId(String topLevelSourceId) {
		this.topLevelSourceId = topLevelSourceId;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public int getTotalFiles() {
		return totalFiles;
	}


	public void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
	}


	public Map<String, String> getBatchProperties() {
		return batchProperties;
	}


	public void setBatchProperties(Map<String, String> batchProperties) {
		this.batchProperties = batchProperties;
	}


	public List<Stage> getStages() {
		return stages;
	}


	public void setStages(List<Stage> stages) {
		this.stages = stages;
	}


	public List<ResourceEntry> getResourceEntries() {
		return resourceEntries;
	}


	public void setResourceEntries(List<ResourceEntry> resourceEntries) {
		this.resourceEntries = resourceEntries;
	}


	public Date getJobStopTimestamp() {
		return jobStopTimestamp;
	}


	public void setJobStopTimestamp(Date jobStopTimestamp) {
		this.jobStopTimestamp = jobStopTimestamp;
	}

}
