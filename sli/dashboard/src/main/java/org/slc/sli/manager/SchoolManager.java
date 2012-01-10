package org.slc.sli.manager;

import java.io.IOException;

import org.slc.sli.entity.School;

/**
 * Retrieves and applies necessary business logic to school data
 * 
 * @author dwu
 *
 */
public class SchoolManager extends Manager {

    public School[] retrieveSchools(String token) throws IOException {
        return apiClient.getSchools(token);
    }
    
}


