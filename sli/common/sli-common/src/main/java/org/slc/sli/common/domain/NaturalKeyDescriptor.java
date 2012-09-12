package org.slc.sli.common.domain;

import java.util.HashMap;
import java.util.Map;

public class NaturalKeyDescriptor {
    protected Map<String, String> naturalKeys;
    protected String tenantId;
    protected String entityType;

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

    public NaturalKeyDescriptor() {
        this(null, null, null);
    }

    public NaturalKeyDescriptor(Map<String, String> naturalKeys) {
        this(naturalKeys, null, null);
    }

    public NaturalKeyDescriptor(Map<String, String> naturalKeys, String tenantId, String entityType) {
        this.naturalKeys = naturalKeys;
        this.tenantId = tenantId;
        this.entityType = entityType;
        if (this.naturalKeys == null) {
            this.naturalKeys = new HashMap<String, String>();
        }
        if (this.tenantId == null) {
            this.tenantId = "";
        }
        if (this.entityType == null) {
            this.entityType = "";
        }
    }
}
