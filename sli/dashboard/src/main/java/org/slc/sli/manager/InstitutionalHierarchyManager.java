package org.slc.sli.manager;

import java.util.List;

import org.slc.sli.entity.GenericEntity;

/**
 * Manager for hierarchy related manipulations
 * @author agrebneva
 *
 */
public interface InstitutionalHierarchyManager {
    public List<GenericEntity> getUserInstHierarchy(String token);
    public String getUserDistrictId(String token);
}