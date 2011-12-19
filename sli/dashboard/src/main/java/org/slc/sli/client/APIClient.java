package org.slc.sli.client;

import org.slc.sli.entity.School;
/**
 * 
 * TODO: Write Javadoc
 *
 */
public interface APIClient {

    public School[] getSchools(final String token);
    
}
