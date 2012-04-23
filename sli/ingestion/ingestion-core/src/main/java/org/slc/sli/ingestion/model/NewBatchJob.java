package org.slc.sli.ingestion.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.time.FastDateFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.slc.sli.common.util.performance.PutResultInContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;

/**
 * Model for ingestion jobs.
 *
 * @author dduran
 *
 */
@Document
public final class NewBatchJob implements Job {

    @Id
    private String id;

    private String sourceId;

    private String status;

    private int totalFiles;

    private Map<String, String> batchProperties;

    private List<Stage> stages;

    private List<ResourceEntry> resourceEntries;

    private static final String STR_TIMESTAMP_FORMAT = "yyyyMMdd hh:mm:ss.SSS";
    private static final FastDateFormat FORMATTER = FastDateFormat.getInstance(STR_TIMESTAMP_FORMAT);

    // mongoTemplate requires this constructor.
    public NewBatchJob() {
        this.batchProperties = new HashMap<String, String>();
        this.stages = new LinkedList<Stage>();
        this.resourceEntries = new LinkedList<ResourceEntry>();
    }

    public NewBatchJob(String id) {
        this.id = id;
        this.batchProperties = new HashMap<String, String>();
        this.stages = new LinkedList<Stage>();
        this.resourceEntries = new LinkedList<ResourceEntry>();
    }

    public NewBatchJob(String id, String sourceId, String status, int totalFiles, Map<String, String> batchProperties,
            List<Stage> stages, List<ResourceEntry> resourceEntries) {
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

    public static NewBatchJob createJobForFile(String fileName) {
        String id = createId(fileName);
        return new NewBatchJob(id);
    }

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

    /**
     * Method to return the List of metrics for a given stageType
     * returns null if no matching metrics are found
     *
     * @param stageType
     */
    public List<Metrics> getStageMetrics(BatchJobStageType stageType) {
        for (Stage stage : this.getStages()) {
            if (stageType.getName().equals(stage.getStageName())) {
                return stage.getMetrics();
            }
        }

        return Collections.emptyList();
    }

    /**
     * Method to return commonly formatted time stamp for batch job stages and metrics
     *
     * @return timeStamp
     */
    public static String getCurrentTimeStamp() {
        String timeStamp = FORMATTER.format(System.currentTimeMillis());
        return timeStamp;
    }

    /**
     * stops given stage and adds to this NewBatchJob instance
     *
     * @param stage
     */
    public void addCompletedStage(Stage stage) {
        stage.stopStage();
        this.stages.add(stage);
    }

    @Override
    public List<IngestionFileEntry> getFiles() {
        List<IngestionFileEntry> ingestionFileEntries = new ArrayList<IngestionFileEntry>();
        for (ResourceEntry resourceEntry : resourceEntries) {
            FileFormat fileFormat = FileFormat.findByCode(resourceEntry.getResourceFormat());
            FileType fileType = FileType.findByNameAndFormat(resourceEntry.getResourceType(), fileFormat);

            IngestionFileEntry ingestionFileEntry = new IngestionFileEntry(fileFormat, fileType,
                    resourceEntry.getResourceName(), resourceEntry.getChecksum());
            ingestionFileEntries.add(ingestionFileEntry);
        }
        return ingestionFileEntries;
    }

    @Override
    public boolean addFile(IngestionFileEntry ingestionFileEntry) {

        ResourceEntry resourceEntry = new ResourceEntry();
        resourceEntry.setResourceId(ingestionFileEntry.getFileName());
        resourceEntry.setResourceName(ingestionFileEntry.getFileName());
        resourceEntry.setChecksum(ingestionFileEntry.getChecksum());
        resourceEntry.setResourceFormat(ingestionFileEntry.getFileFormat().getCode());
        resourceEntry.setResourceType(ingestionFileEntry.getFileType().getName());
        resourceEntry.setExternallyUploadedResourceId(ingestionFileEntry.getFileName());

        return resourceEntries.add(resourceEntry);
    }

    @Override
    public FaultsReport getFaultsReport() {
        // TODO Auto-generated method stub
        return null;
    }
}
