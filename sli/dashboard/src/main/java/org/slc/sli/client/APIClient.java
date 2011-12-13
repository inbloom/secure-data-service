package org.slc.sli.client;

import org.slc.sli.entity.School;

public interface APIClient {

    public School[] getSchools(final String token);
    
}
