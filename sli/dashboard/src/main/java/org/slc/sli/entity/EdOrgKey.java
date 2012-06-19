package org.slc.sli.entity;

import java.io.Serializable;

/**
 * Represents a simplified EdOrg path selection to be used to determine configs
 * and some other application related logic.
 *
 */
public class EdOrgKey implements Serializable {
    private static final long serialVersionUID = -6946791865233296339L;
    private String sliId;

    public EdOrgKey(String sliId) {
        this.sliId = sliId;
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
        return prime + ((sliId == null) ? 0 : sliId.hashCode());
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
        if (sliId == null) {
            if (other.sliId != null) {
                return false;
            }
        } else if (!sliId.equals(other.sliId)) {
            return false;
        }
        return true;
    }
}
