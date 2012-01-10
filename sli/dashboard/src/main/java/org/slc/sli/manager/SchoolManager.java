package org.slc.sli.manager;

import java.io.IOException;

import org.slc.sli.entity.School;

/**
 * 
 * @author dwu
 *
 */
public class SchoolManager extends Manager {

    private static SchoolManager instance = new SchoolManager();
    
    protected SchoolManager() {        
    }
    
    public static SchoolManager getInstance() {
        return instance;
    }    
    
    public School[] retrieveSchools(String token) throws IOException {
        return apiClient.getSchools(token);
    }
    
}


