package org.slc.sli.ingestion.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slc.sli.common.util.performance.PutResultInContext;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultsReport;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Model for ingestion jobs.
 *
 * @author dduran
 *
 */
@Document
public class NewBatchJob implements Job {

    @Id
    private String id;

    private String sourceId;

    private String topLevelSourceId;

    private String status;

    private int totalFiles;

    private Map<String, String> batchProperties;

    private List<StageSet> stages;

    private List<ResourceEntry> resourceEntries;

    // mongoTemplate requires this constructor.
    public NewBatchJob() {
        this.batchProperties = new HashMap<String, String>();
        this.stages = new LinkedList<StageSet>();
        this.resourceEntries = new LinkedList<ResourceEntry>();
    }

    public NewBatchJob(String id) {
        this.id = id;
        this.batchProperties = new HashMap<String, String>();
        this.stages = new LinkedList<StageSet>();
        this.resourceEntries = new LinkedList<ResourceEntry>();
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

        this.stages = new LinkedList<StageSet>();
        if (listOfStages != null) {
            for (int i = 0; i < listOfStages.size(); i++) {
                this.stages.add(new StageSet(listOfStages.get(i)));
            }
        }        

        this.resourceEntries = new LinkedList<ResourceEntry>();
        if (resourceEntries != null) {
            this.resourceEntries = resourceEntries;
        }        
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
            return UUID.randomUUID().toString();
        } else {
            return filename + "-" + UUID.randomUUID().toString();
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
        this.topLevelSourceId = deriveTopLevelSourceId(sourceId);
    }

    private String deriveTopLevelSourceId(String sourceId) {
        String derivedTopLevelSourceId = sourceId;

        int index = sourceId.indexOf(".done");
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

    /*
    public List<Stage> getStages() {
        return stages;
    }
    */

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

        for (StageSet sts : this.stages) {
            for (Stage s: sts.getChunks()) {
                if (stageType.getName().equals(s.getStageName())) {
                    m.addAll(s.getMetrics());
                }
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
        boolean inExisting = false;
        for (int i = 0; i < this.stages.size(); i++) {
            if (!inExisting && this.stages.get(i).getStageName().equals(stage.getStageName())) {
                this.stages.get(i).addStage(stage);
                inExisting = true;
            }
        }

        if (!inExisting) {
            this.stages.add(new StageSet(stage));
        }
    }

    public void addStageChunk(Stage stage) {
        this.addStage(stage);
    }

    /*
    public void addStageChunk(Stage stage) {
        boolean stageFound = false;

        Stage stageToUpdate;
        for (int i = 0; i < this.stages.size(); i++) {
            if (this.stages.get(i).getStageName().equals(stage.getStageName())) {
                stageFound = true;
                stageToUpdate = this.stages.remove(i);

                //insert stage with chunks
                for (Metrics m : stage.getMetrics()) {
                    stageToUpdate.addMetrics(m);
                }

                this.stages.add(stageToUpdate);
            }
        }

        if (!stageFound) {
            this.stages.add(stage);
        }

    }
    */

    @Override
    public List<IngestionFileEntry> getFiles() {
        List<IngestionFileEntry> ingestionFileEntries = new ArrayList<IngestionFileEntry>();

        // create IngestionFileEntry items from eligible ResourceEntry items
        for (ResourceEntry resourceEntry : resourceEntries) {
            String lzPath = resourceEntry.getTopLevelLandingZonePath();
            FileFormat fileFormat = FileFormat.findByCode(resourceEntry.getResourceFormat());
            if (fileFormat != null && resourceEntry.getResourceType() != null) {

                FileType fileType = FileType.findByNameAndFormat(resourceEntry.getResourceType(), fileFormat);
                if (fileType != null) {
                    IngestionFileEntry ingestionFileEntry = new IngestionFileEntry(fileFormat, fileType,
                            resourceEntry.getResourceId(), resourceEntry.getChecksum(), lzPath);
                    ingestionFileEntries.add(ingestionFileEntry);
                }
            }
        }

        // assign neutral record files to IngestionFileEntry
        // for (ResourceEntry resourceEntry : resourceEntries) {
        // if
        // (FileFormat.NEUTRALRECORD.getCode().equalsIgnoreCase(resourceEntry.getResourceFormat())
        // && resourceEntry.getResourceName() != null) {
        // for (IngestionFileEntry ife : ingestionFileEntries) {
        // if (ife.getFileName().equals(resourceEntry.getExternallyUploadedResourceId())) {
        // ife.setNeutralRecordFile(new File(resourceEntry.getResourceName()));
        // break;
        // }
        // }
        // }
        // }
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
        resourceEntry.setTopLevelLandingZonePath(ingestionFileEntry.getTopLevelLandingZonePath());

        return resourceEntries.add(resourceEntry);
    }

    @Override
    public FaultsReport getFaultsReport() {
        // TODO Auto-generated method stub
        return null;
    }
}
