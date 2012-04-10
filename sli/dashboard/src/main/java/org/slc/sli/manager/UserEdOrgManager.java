package org.slc.sli.manager;

import java.util.List;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.EdOrgKey;

/**
 * Manager for hierarchy related manipulations
 * @author agrebneva
 *
 */
public interface UserEdOrgManager {
    public List<GenericEntity> getUserInstHierarchy(String token);
    public EdOrgKey getUserEdOrg(String token);
}