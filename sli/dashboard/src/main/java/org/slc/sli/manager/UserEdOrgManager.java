package org.slc.sli.manager;

import java.util.List;

import org.slc.sli.entity.Config;
import org.slc.sli.entity.Config.Data;
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
    public List<GenericEntity> getUserInstHierarchy(String token);
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
    @EntityMapping("userCoursesSections")
    GenericEntity getUserCoursesAndSections(String token, Object schoolIdObj, Data config);
}