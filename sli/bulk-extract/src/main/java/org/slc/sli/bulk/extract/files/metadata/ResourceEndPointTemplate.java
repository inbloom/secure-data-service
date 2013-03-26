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
package org.slc.sli.bulk.extract.files.metadata;

import java.util.List;

/**
 * class to hold resource endpoint from api/resource.json.
 * @author tke
 *
 */
public class ResourceEndPointTemplate {

    private String path;
    private String doc;
    private String resourceClass;
    private boolean queryable;
    private List<ResourceEndPointTemplate> subResources;
    private String deprecatedVersion;
    private String deprecatedReason;
    private String availableSince;
    private boolean dateSearchDisallowed;
    private boolean blockGetRequest;


    /**
     * get doc.
     * @return doc
     */
    public String getDoc() {
        return doc;
    }

    /**
     * set doc.
     * @param doc doc
     */
    public void setDoc(String doc) {
        this.doc = doc;
    }


    /**
     * get resourceClass.
     * @return resourceClass
     */
    public String getResourceClass() {
        return resourceClass;
    }

    /**
     * set reourceClass.
     * @param resourceClass resourceClass
     */
    public void setResourceClass(String resourceClass) {
        this.resourceClass = resourceClass;
    }

    /**
     * get path.
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * set path.
     * @param path path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     *get sub resources.
     * @return subResources
     */
    public List<ResourceEndPointTemplate> getSubResources() {
        return subResources;
    }

    /**
     * set sub resources.
     * @param subResources sub resources.
     */
    public void setSubResources(List<ResourceEndPointTemplate> subResources) {
        this.subResources = subResources;
    }

    /**
     * get deprecated version.
     * @return deprecated version
     */
    public String getDeprecatedVersion() {
        return deprecatedVersion;
    }

    /**
     * set deprecated version.
     * @param depreciatedVersion deprecated version.
     */
    public void setDeprecatedVersion(String depreciatedVersion) {
        this.deprecatedVersion = depreciatedVersion;
    }

    /**
     * get deprecated reason.
     * @return deprecated reason.
     */
    public String getDeprecatedReason() {
        return deprecatedReason;
    }

    /**
     * set deprecated reason.
     * @param depreciatedReason deprecated reason
     */
    public void setDeprecatedReason(String depreciatedReason) {
        this.deprecatedReason = depreciatedReason;
    }

    /**
     * get available since.
     * @return available since
     */
    public String getAvailableSince() {
        return availableSince;
    }

    /**
     * set available since.
     * @param availableSince available since
     */
    public void setAvailableSince(String availableSince) {
        this.availableSince = availableSince;
    }

    /**
     * check if it is queryable.
     * @return true if queryable
     */
    public boolean isQueryable() {
        return queryable;
    }

    /**
     * set queryable.
     * @param queryable queryable
     */
    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    /**
     * check if dateSearchDisallowed.
     * @return true if dateSearchDisallowed
     */
    public boolean isDateSearchDisallowed() {
        return dateSearchDisallowed;
    }

    /**
     * set dateSearchDisallowed.
     * @param dateSearchDisallowed dateSearchDisallowed
     */
    public void setDateSearchDisallowed(boolean dateSearchDisallowed) {
        this.dateSearchDisallowed = dateSearchDisallowed;
    }

    /**
     * check if it blocks get request.
     * @return true if block get request.
     */
    public boolean isBlockGetRequest() {
        return blockGetRequest;
    }

    /**
     * set blockGetRequest.
     * @param blockGetRequest blockGetRequest
     */
    public void setBlockGetRequest(boolean blockGetRequest) {
        this.blockGetRequest = blockGetRequest;
    }
}

