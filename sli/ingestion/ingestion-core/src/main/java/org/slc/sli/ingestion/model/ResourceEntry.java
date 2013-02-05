/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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

    private String resourceZipParent;

    private long recordCount;

    private long errorCount;

    public ResourceEntry() {
        // mongoTemplate requires this constructor.
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

    public long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    /**
     * @return the externallyUploadedResourceId
     */
    public String getExternallyUploadedResourceId() {
        return externallyUploadedResourceId;
    }

    /**
     * @param externallyUploadedResourceId
     *            the externallyUploadedResourceId to set
     */
    public void setExternallyUploadedResourceId(String externallyUploadedResourceId) {
        this.externallyUploadedResourceId = externallyUploadedResourceId;
    }

    public String getResourceZipParent() {
        return resourceZipParent;
    }

    public void setResourceZipParent(String resourceZipParent) {
        this.resourceZipParent = resourceZipParent;
    }
}
