package org.slc.sli.api.resources.v1;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.representation.EntityBody;

/**
 * The operations a CRUD endpoint should be able to perform (Create, Read, Update, Delete)
 * 
 * @author kmyers
 *
 */
public interface CrudEndpoint {
    
    /**
     * Reads all entities from a specific location or collection.
     * 
     * @param resourceName
     *      where the entity should be located
     * @param headers 
     *      HTTP header information (which includes request headers) 
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return requested information or error status
     */
    public Response readAll(String resourceName, HttpHeaders headers, UriInfo uriInfo);
    
    /**
     * Reads one or more entities from a specific location or collection.
     * 
     * @param resourceName
     *      where the entity should be located
     * @param key 
     *      field to be queried against
     * @param value 
     *      expected value to be found in the key 
     * @param headers 
     *      HTTP header information (which includes request headers) 
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return requested information or error status
     */
    public Response read(String resourceName, String key, String value, HttpHeaders headers, UriInfo uriInfo);
    
    /**
     * Searches "resourceName" for entries where "key" equals "value", then for each result 
     * uses "idkey" field's value to query "resolutionResourceName" against the ID field. 
     * 
     * @param resourceName
     *      where the entity should be located
     * @param key 
     *      field to be queried against (when searching resources)
     * @param value 
     *      expected value to be found in the key 
     * @param idKey 
     *      field in resource that contains the ID to be resolved
     * @param resolutionResourceName 
     *      where to query for the entitity with the ID taken from the "idKey" field
     * @param headers 
     *      HTTP header information (which includes request headers) 
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return requested information or error status
     */
    public Response read(String resourceName, String key, String value, String idKey, String resolutionResourceName, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Reads one or more entities from a specific location or collection.
     * 
     * @param resourceName
     *      where the entity should be located
     * @param idList 
     *      a single ID or a comma separated list of IDs
     * @param headers 
     *      HTTP header information (which includes request headers) 
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return requested information or error status
     */
    public Response read(String resourceName, String idList, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Creates a new entity in a specific location or collection.
     * 
     * @param resourceName
     *      where the entity should be located
     * @param newEntityBody 
     *      new map of keys/values for entity
     * @param headers 
     *      HTTP header information (which includes request headers) 
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return resulting status from request
     */
    public Response create(String resourceName, EntityBody newEntityBody, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Updates a given entity in a specific location or collection.
     * 
     * @param resourceName
     *      where the entity should be located
     * @param id
     *      ID of object being updated
     * @param newEntityBody 
     *      new map of keys/values for entity
     * @param headers 
     *      HTTP header information (which includes request headers) 
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return resulting status from request
     */
    public Response update(String resourceName, String id, EntityBody newEntityBody, HttpHeaders headers, UriInfo uriInfo);

    /**
     * Deletes a given entity from a specific location or collection.
     * 
     * @param resourceName
     *      where the entity should be located
     * @param id
     *      ID of object being deleted
     * @param headers 
     *      HTTP header information (which includes request headers) 
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return resulting status from request
     */
    public Response delete(String resourceName, String id, HttpHeaders headers, UriInfo uriInfo);
}
