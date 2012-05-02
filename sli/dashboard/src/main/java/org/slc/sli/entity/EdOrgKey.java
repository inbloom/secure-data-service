package org.slc.sli.entity;

import java.io.Serializable;

/**
 * Represents a simplified EdOrg path selection to be used to determine configs
 * and some other application related logic.
 *
 */
public class EdOrgKey implements Serializable {
    private static final long serialVersionUID = -6946791865233296339L;
    private String districtId;
    private String sliId;

    public EdOrgKey(String districtId) {
        this(districtId, null);
    }

    public EdOrgKey(String districtId, String sliId) {
        this.districtId = districtId;
        this.sliId = sliId;
    }

    /**
     * Get the id of the district
     * @return
     */
    public String getDistrictId() {
        return districtId;
    }

    /**
     * Get the SLI id of the district
     *
     * @return The SLI id
     */
    public String getSliId() {
        return sliId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime + ((districtId == null) ? 0 : districtId.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EdOrgKey other = (EdOrgKey) obj;
        if (districtId == null) {
            if (other.districtId != null) {
                return false;
            }
        } else if (!districtId.equals(other.districtId)) {
            return false;
        }
        return true;
    }
}
