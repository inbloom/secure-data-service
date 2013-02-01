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

package org.slc.sli.common.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains data needed to create a deterministic id
 *
 */
public class NaturalKeyDescriptor {
    private Map<String, String> naturalKeys;
    private String tenantId;
    private String entityType;
    private boolean naturalKeysNotNeeded;
    private String parentId;

    public NaturalKeyDescriptor() {
        this(null, null, null, null);
    }

    public NaturalKeyDescriptor(Map<String, String> naturalKeys) {
        this(naturalKeys, null, null, null);
    }

    public NaturalKeyDescriptor(Map<String, String> naturalKeys, String tenantId, String entityType, String parentId) {
        this.naturalKeys = naturalKeys;
        this.tenantId = tenantId;
        this.entityType = entityType;
        this.parentId = parentId;
        if (this.naturalKeys == null) {
            this.naturalKeys = new HashMap<String, String>();
        }
        if (this.tenantId == null) {
            this.tenantId = "";
        }
        if (this.entityType == null) {
            this.entityType = "";
        }
        if (this.parentId == null) {
            this.parentId = "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NaturalKeyDescriptor) {
            NaturalKeyDescriptor nkd = (NaturalKeyDescriptor) o;
            if (nkd.getNaturalKeys().equals(this.getNaturalKeys())
                    && nkd.getTenantId().equals(this.getTenantId())
                    && nkd.getParentId().equals(this.getParentId())
                    && nkd.getEntityType().equals(this.getEntityType())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 42;

        if (this.getNaturalKeys() != null) {
            result = 37 * result + this.getNaturalKeys().hashCode();
        }

        if (this.getTenantId() != null) {
            result = 37 * result + this.getTenantId().hashCode();
        }

        if (this.getEntityType() != null) {
            result = 37 * result + this.getEntityType().hashCode();
        }

        if (this.getParentId() != null) {
            result = 37 * result + this.getParentId().hashCode();
        }

        return result;
    }

    public Map<String, String> getNaturalKeys() {
        return naturalKeys;
    }

    public void setNaturalKeys(Map<String, String> naturalKeys) {
        this.naturalKeys = naturalKeys;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public boolean isNaturalKeysNotNeeded() {
        return naturalKeysNotNeeded;
    }

    public void setNaturalKeysNotNeeded(boolean naturalKeysNotNeeded) {
        this.naturalKeysNotNeeded = naturalKeysNotNeeded;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        if (parentId != null && parentId.length() == 43) {
            this.parentId = parentId;
        }
    }
}
