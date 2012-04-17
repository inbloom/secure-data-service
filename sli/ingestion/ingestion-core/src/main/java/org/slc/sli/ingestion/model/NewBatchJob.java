package org.slc.sli.ingestion.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.slc.sli.common.util.performance.PutResultInContext;
import org.slc.sli.ingestion.BatchJobStageType;

/**
 * Model for ingestion jobs.
 *
 * @author dduran
 *
 */
@Document
public final class NewBatchJob {

    @Id
    private String id;

    private String sourceId;

    private String status;

    private int totalFiles;

    private Map<String, String> batchProperties;

    private List<Stage> stages;

    private List<ResourceEntry> resourceEntries;

    /**
     * generates a new unique ID
     */
    @PutResultInContext(returnName = "ingestionBatchJobId")
    public static String createId(String filename) {
        if (filename == null) {
            return System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        } else {
            return filename + "-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        }
    }

    public NewBatchJob(String id) {
        this.id = id;
        List<Stage> stages = new LinkedList<Stage>();
        this.stages = stages;
        List<ResourceEntry> resourceEntries = new LinkedList<ResourceEntry>();
        this.resourceEntries = resourceEntries;
    }

    //mongoTemplate requires this constructor.
    public NewBatchJob() {
    }

    public NewBatchJob(String id, String sourceId, String status,
            int totalFiles, Map<String, String> batchProperties,
            List<Stage> stages, List<ResourceEntry> resourceEntries) {
        super();
        this.id = id;
        this.sourceId = sourceId;
        this.status = status;
        this.totalFiles = totalFiles;
        if (batchProperties == null) {
            batchProperties = new HashMap<String, String>();
        }
        this.batchProperties = batchProperties;
        if (stages == null) {
            stages = new LinkedList<Stage>();
        }
        this.stages = stages;
        if (resourceEntries == null) {
            resourceEntries = new LinkedList<ResourceEntry>();
        }
        this.resourceEntries = resourceEntries;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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

    public List<ResourceEntry> getResourceEntries() {
        return resourceEntries;
    }

    public String getId() {
        return id;
    }

    /**
     * Method to return the ResourceEntry for a given resourceId
     * returns null if no matching entry is found
     *
     * @param resourceId
     */
    public ResourceEntry getResourceEntry(String resourceId) {
        for (ResourceEntry entry : this.getResourceEntries()) {
            String entryResourceId = entry.getResourceId();
            if (entryResourceId.equals(resourceId)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Method to return the List of metrics for a given stageType
     * returns null if no matching metrics are found
     *
     * @param stageType
     */
    public List<Metrics> getStageMetrics(BatchJobStageType stageType) {
        for (Stage stage : this.getStages()) {
            if (stage.getStageName().equals(stageType.getName())) {
                return stage.getMetrics();
            }
        }

        return null;
    }


}
