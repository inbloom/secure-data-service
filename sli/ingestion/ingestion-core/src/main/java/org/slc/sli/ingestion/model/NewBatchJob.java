/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

package org.slc.sli.ingestion.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 * Model for ingestion jobs.
 *
 * @author dduran
 *
 */
@Document
public class NewBatchJob implements Job {

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

    // mongoTemplate requires this constructor.
    public NewBatchJob() {
        this.batchProperties = new HashMap<String, String>();
        this.stages = new LinkedList<Stage>();
        this.resourceEntries = new LinkedList<ResourceEntry>();
        initStartTime();
    }

    public NewBatchJob(String id, String tenantId) {
        this.id = id;
        this.tenantId = tenantId;
        this.batchProperties = new HashMap<String, String>();
        this.stages = new LinkedList<Stage>();
        this.resourceEntries = new LinkedList<ResourceEntry>();
        initStartTime();
    }

    public NewBatchJob(String id, String sourceId, String status, int totalFiles, Map<String, String> batchProperties,
            List<Stage> listOfStages, List<ResourceEntry> resourceEntries) {
        this.id = id;
        this.sourceId = sourceId;

        this.topLevelSourceId = deriveTopLevelSourceId(sourceId);

        this.status = status;
        this.totalFiles = totalFiles;
        if (batchProperties != null) {
            this.batchProperties = batchProperties;
        }

        this.stages = new LinkedList<Stage>();
        if (listOfStages != null) {
            this.stages = listOfStages;
        }

        this.resourceEntries = new LinkedList<ResourceEntry>();
        if (resourceEntries != null) {
            this.resourceEntries = resourceEntries;
        }

        initStartTime();
    }

    private void initStartTime() {
        jobStartTimestamp = BatchJobUtils.getCurrentTimeStamp();

    }

    public void stop() {
        jobStopTimestamp = BatchJobUtils.getCurrentTimeStamp();
    }

    /**
     * generates a new unique ID
     */
    public static String createId(String filename) {
        if (filename == null) {
            return UUID.randomUUID().toString();
        } else {
            return filename + "-" + UUID.randomUUID().toString();
        }
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getProperty(String key) {
        return batchProperties.get(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String value = batchProperties.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    @Override
    public Set<String> propertyNames() {
        return batchProperties.keySet();
    }

    @Override
    public void setProperty(String name, String value) {
        batchProperties.put(name, value);
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    private String deriveTopLevelSourceId(String sourceId) {
        String derivedTopLevelSourceId = sourceId;

        int index = sourceId.indexOf("unzip");
        if (index != -1) {
            derivedTopLevelSourceId = sourceId.substring(0, index);
        }

        return derivedTopLevelSourceId;
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

    public Date getJobStartTimestamp() {
        return new Date(jobStartTimestamp.getTime());
    }

    public Date getJobStopTimestamp() {
        return new Date(jobStopTimestamp.getTime());
    }

    public List<ResourceEntry> getResourceEntries() {
        return resourceEntries;
    }

    public synchronized void addResourceEntry(ResourceEntry resourceEntry) {
        if (this.resourceEntries == null) {
            this.resourceEntries = new LinkedList<ResourceEntry>();
        }
        this.resourceEntries.add(resourceEntry);
    }

    /**
     * Method to return the ResourceEntry for a given resourceId
     * returns null if no matching entry is found
     *
     * @param resourceId
     */
    public ResourceEntry getResourceEntry(String resourceId) {
        if (resourceId != null) {
            for (ResourceEntry entry : this.getResourceEntries()) {
                if (resourceId.equals(entry.getResourceId())) {
                    return entry;
                }
            }
        } else {
            throw new IllegalArgumentException("Cannot get resource for null resourceId");
        }

        return null;
    }

    public List<ResourceEntry> getNeutralRecordResourceForType(FileType fileType) {
        List<ResourceEntry> nrResourcesForType = new ArrayList<ResourceEntry>();

        if (fileType != null) {
            for (ResourceEntry entry : this.getResourceEntries()) {
                if (FileFormat.NEUTRALRECORD.getCode().equals(entry.getResourceFormat())
                        && fileType.getName().equals(entry.getResourceType())) {
                    nrResourcesForType.add(entry);
                }
            }
        }
        return nrResourcesForType;
    }

    /**
     * Method to return the List of metrics for a given stageType
     * returns null if no matching metrics are found
     *
     * @param stageType
     */
    public List<Metrics> getStageMetrics(BatchJobStageType stageType) {
        List<Metrics> m = new LinkedList<Metrics>();

        for (Stage s : this.stages) {
            if (stageType.getName().equals(s.getStageName())) {
                m.addAll(s.getMetrics());
            }
        }

        if (m.size() > 0) {
            return m;
        }

        return Collections.emptyList();
    }

    /**
     * adds stage to this NewBatchJob instance
     *
     * @param stage
     */
    public void addStage(Stage stage) {

        this.stages.add(stage);
    }

    public void addStageChunk(Stage stage) {
        this.addStage(stage);
    }

    @Override
    public List<IngestionFileEntry> getFiles() {
        List<IngestionFileEntry> ingestionFileEntries = new ArrayList<IngestionFileEntry>();

        // create IngestionFileEntry items from eligible ResourceEntry items
        for (ResourceEntry resourceEntry : resourceEntries) {
            FileFormat fileFormat = FileFormat.findByCode(resourceEntry.getResourceFormat());
            if (fileFormat != null && resourceEntry.getResourceType() != null) {

                FileType fileType = FileType.findByNameAndFormat(resourceEntry.getResourceType(), fileFormat);
                if (fileType != null) {
                    IngestionFileEntry ingestionFileEntry = new IngestionFileEntry(resourceEntry.getResourceZipParent(), fileFormat, fileType,
                            resourceEntry.getResourceId(), resourceEntry.getChecksum());
                    ingestionFileEntries.add(ingestionFileEntry);
                }
            }
        }

        return ingestionFileEntries;
    }

    public ResourceEntry getZipResourceEntry() {
        ResourceEntry zipResourceEntry = null;
        for (ResourceEntry resourceEntry : resourceEntries) {
            if (FileFormat.ZIP_FILE.getCode().equalsIgnoreCase(resourceEntry.getResourceFormat())) {
                zipResourceEntry = resourceEntry;
                break;
            }
        }
        return zipResourceEntry;
    }

}
