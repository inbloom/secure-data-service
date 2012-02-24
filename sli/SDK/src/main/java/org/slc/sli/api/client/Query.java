package org.slc.sli.api.client;

import java.util.Map;

/**
 * 
 *
 * @author asaarela
 */
public interface Query {
    
    /**
     * Get the query parameters associated with this query instance.
     * 
     * @return
     */
    public abstract Map<String, Object> getParameters();
    
}