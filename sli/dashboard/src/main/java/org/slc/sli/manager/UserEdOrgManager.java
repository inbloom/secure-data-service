package org.slc.sli.manager;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.EdOrgKey;
import org.slc.sli.entity.GenericEntity;
import org.slc.sli.manager.Manager.EntityMapping;
import org.slc.sli.manager.Manager.EntityMappingManager;

/**
 * Manager for hierarchy related manipulations
 * @author agrebneva
 *
 */
@EntityMappingManager
public interface UserEdOrgManager {
    public EdOrgKey getUserEdOrg(String token);
    @EntityMapping("userEdOrg")
    public GenericEntity getUserInstHierarchy(String token, Object key, Config.Data config);
    /**
     * GetStaff info and user admin flag.
     *
     * @param token - The staff entity
     * @return staff info
     */
    GenericEntity getStaffInfo(String token);
}