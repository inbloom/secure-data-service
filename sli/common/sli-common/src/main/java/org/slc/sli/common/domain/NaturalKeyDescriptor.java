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

    @Override
    public boolean equals(Object o) {
        if (o instanceof NaturalKeyDescriptor) {
            NaturalKeyDescriptor nkd = (NaturalKeyDescriptor) o;
            if (nkd.getNaturalKeys().equals(this.getNaturalKeys())
                    && nkd.getTenantId().equals(this.getTenantId())
                    && nkd.getEntityType().equals(this.getEntityType())) {
                return true;
            }
        }
        return false;
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

}
