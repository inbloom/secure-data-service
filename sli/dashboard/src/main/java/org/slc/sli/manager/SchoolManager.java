package org.slc.sli.manager;


import java.util.List;

import org.slc.sli.entity.GenericEntity;
import org.slc.sli.entity.School;
import org.slc.sli.util.SecurityUtil;

/**
 * Retrieves and applies necessary business logic to school data
 * 
 * @author dwu
 *
 */
public class SchoolManager extends Manager {

    private EntityManager entityManager;
    
    public List<GenericEntity> getSchools() {
        String token = SecurityUtil.getToken();
        //School[] schools = apiClient.getSchools(token);
        List<GenericEntity> schools = entityManager.getSchools(token, null);
        return schools;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
}


