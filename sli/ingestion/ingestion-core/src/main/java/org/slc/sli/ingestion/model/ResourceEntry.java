package org.slc.sli.ingestion.model;

/**
 * Model for ingestion file entries
 *
 * @author dduran
 *
 */
public final class ResourceEntry {

    private String resourceId;

    private String resourceName;

    private String resourceFormat;

    private String resourceType;

    private String externallyUploadedResourceId;

    private String checksum;

    private String topLevelLandingZonePath;

    private int recordCount;

    private int errorCount;

    // mongoTemplate requires this constructor.
    public ResourceEntry() {
    }

    public void update(String fileFormat, String fileType, String checksum, int recordCount, int errorCount) {
        if (fileFormat != null) {
            this.resourceFormat = fileFormat;
        }
        if (fileType != null) {
            this.resourceType = fileType;
        }
        if (checksum != null) {
            this.checksum = checksum;
        }
        this.recordCount = recordCount;
        this.errorCount = errorCount;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceFormat() {
        return resourceFormat;
    }

    public void setResourceFormat(String resourceFormat) {
        this.resourceFormat = resourceFormat;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getTopLevelLandingZonePath() {
        return topLevelLandingZonePath;
    }

    public void setTopLevelLandingZonePath(String topLevelLandingZone) {
        this.topLevelLandingZonePath = topLevelLandingZone;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    /**
     * @return the externallyUploadedResourceId
     */
    public String getExternallyUploadedResourceId() {
        return externallyUploadedResourceId;
    }

    /**
     * @param externallyUploadedResourceId the externallyUploadedResourceId to set
     */
    public void setExternallyUploadedResourceId(String externallyUploadedResourceId) {
        this.externallyUploadedResourceId = externallyUploadedResourceId;
    }
}
