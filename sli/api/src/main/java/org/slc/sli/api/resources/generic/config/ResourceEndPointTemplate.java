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
package org.slc.sli.api.resources.generic.config;

import java.util.List;

/**
 * Template for holding a resource
 *
 * @author srupasinghe
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


    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public String getResourceClass() {
        return resourceClass;
    }

    public void setResourceClass(String resourceClass) {
        this.resourceClass = resourceClass;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ResourceEndPointTemplate> getSubResources() {
        return subResources;
    }

    public void setSubResources(List<ResourceEndPointTemplate> subResources) {
        this.subResources = subResources;
    }

    public String getDeprecatedVersion() {
        return deprecatedVersion;
    }

    public void setDeprecatedVersion(String depreciatedVersion) {
        this.deprecatedVersion = depreciatedVersion;
    }

    public String getDeprecatedReason() {
        return deprecatedReason;
    }

    public void setDeprecatedReason(String depreciatedReason) {
        this.deprecatedReason = depreciatedReason;
    }

    public String getAvailableSince() {
        return availableSince;
    }

    public void setAvailableSince(String availableSince) {
        this.availableSince = availableSince;
    }
    
    public boolean isQueryable() {
        return queryable;
    }

    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    public boolean isDateSearchDisallowed() {
        return dateSearchDisallowed;
    }

    public void setDateSearchDisallowed(boolean dateSearchDisallowed) {
        this.dateSearchDisallowed = dateSearchDisallowed;
    }
}
