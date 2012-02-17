package org.slc.sli.api.resources.v1;

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
     * @param offset 
     *      entity at which to start the results
     * @param limit 
     *      max number of entities to be displayed
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return requested information or error status
     */
    public Response readAll(int offset, int limit, UriInfo uriInfo);

    /**
     * Reads one or more entities from a specific location or collection.
     * 
     * @param idList 
     *      a single ID or a comma separated list of IDs
     * @param fullEntities 
     *      whether or not to resolve association links to the entities they reference
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return requested information or error status
     */
    public Response read(String idList, boolean fullEntities, UriInfo uriInfo);

    /**
     * Creates a new entity in a specific location or collection.
     * 
     * @param newEntityBody 
     *      new map of keys/values for entity
     * @param uriInfo 
     *      URI information including path and query parameters
     * @return resulting status from request
     */
    public Response create(EntityBody newEntityBody, UriInfo uriInfo);

    /**
     * Updates a given entity in a specific location or collection.
     * 
     * @param id
     *      ID of object being updated
     * @param newEntityBody 
     *      new map of keys/values for entity
     * @return resulting status from request
     */
    public Response update(String id, EntityBody newEntityBody);

    /**
     * Deletes a given entity from a specific location or collection.
     * 
     * @param id
     *      ID of object being deleted
     * @return resulting status from request
     */
    public Response delete(String id);
}
