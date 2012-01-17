package org.slc.sli.api.service.query;

import org.springframework.data.mongodb.core.query.Query;

/**
 * Define the interface for converting http GET request query string
 * to spring data query object
 * 
 * @author dong liu <dliu@wgen.net>
 * 
 */
public interface QueryConverter {
    
    /**
     * @param entityType
     *            the entity type that query will be applied to
     * @param queryString
     *            the query string received from http GET request
     * @return the converted spring data query object
     */
    public Query stringToQuery(String entityType, String queryString);
    
    /**
     * @param entityType the entity type that query will be applied to
     * @param queryParam the query parameter that received from http GET request
     * @return the type of query parameter that matches the entity avro schema
     */
    public String findParamType(String entityType, String queryParam);
}
