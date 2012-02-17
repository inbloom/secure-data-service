package org.slc.sli.api.service.query;

import org.springframework.data.mongodb.core.query.Query;

/**
 * Define the interface for converting http GET request query string
 * to spring data query object
 * 
 * The general format of a query string that uses filtering is as follows:
 * 
 * ?attribute1=value1&attribute2>value2&attribute3!=value3&attribute4>=value4...
 * 
 * Use dot "." to represent the nested fields:
 * field.nestedField1.nestedField2
 * 
 * Some examples of query string (for student entity)
 * 
 * ?name.firstName=Joe&name.lastSurname=Brown
 * ?birthData.birthDate>=1995-01-01
 * ?studentUniqueStateId=123456789
 * ?name.firstName!=Joe
 * 
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
    public Query stringToQuery(String entityType, String queryString, String sortBy, SortOrder sortOrder);
    
    /**
     * @param entityType
     *            the entity type that query will be applied to
     * @param queryString
     *            the query string received from http GET request
     * @return the converted spring data query object
     */
    public Query stringToQuery(String entityType, String queryString);
    
    /**
     * @param entityType
     *            the entity type that query will be applied to
     * @param queryParam
     *            the query parameter that received from http GET request
     * @return the type of query parameter that matches the entity avro schema
     */
    public String findParamType(String entityType, String queryParam);
}
