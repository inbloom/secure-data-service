package org.slc.sli.manager;


import org.slc.sli.entity.School;
import org.slc.sli.util.SecurityUtil;

/**
 * Retrieves and applies necessary business logic to school data
 * 
 * @author dwu
 *
 */
public class SchoolManager extends Manager {

    public School[] getSchools() {
        String token = SecurityUtil.getToken();
        School[] schools = apiClient.getSchools(token);
        return schools;
    }
    
}


