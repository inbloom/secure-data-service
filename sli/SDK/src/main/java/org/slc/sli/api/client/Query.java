package org.slc.sli.api.client;

import java.util.Map;

/**
 * Query parameters for requests to the SLI ReSTful API service. Parameters are encoded
 * and passed to the service with the request.
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