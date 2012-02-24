package org.slc.sli.api.resources.v1.entity;

import static org.slc.sli.api.resources.Resource.FULL_ENTITIES_PARAM;
import static org.slc.sli.api.resources.Resource.MAX_RESULTS_PARAM;
import static org.slc.sli.api.resources.Resource.SORT_BY_PARAM;
import static org.slc.sli.api.resources.Resource.SORT_ORDER_PARAM;
import static org.slc.sli.api.resources.Resource.START_INDEX_PARAM;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.resources.v1.PathConstants;
import org.slc.sli.api.service.query.SortOrder;

/**
 * Responds to requests for school-related information.
 * 
 * @author srupasinghe
 * @author kmyers
 * 
 */
@Path(PathConstants.SCHOOLS)
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE, Resource.SLC_LONG_JSON_MEDIA_TYPE })
public class SchoolResource {
    
    private final Resource crudDelegate;
    
    @Autowired
    public SchoolResource(EntityDefinitionStore entityDefs) {
        crudDelegate = new Resource(entityDefs.lookupByResourceName(PathConstants.SCHOOLS), entityDefs);
    }
    
    /**
     * Create a new $$school$$ entity.
     * 
     * @param newEntityBody
     *            entity data
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessable.} value.
     */
    @POST
    public Response createEntity(final EntityBody newEntityBody, @Context final UriInfo uriInfo) {
        return crudDelegate.createEntity(newEntityBody, uriInfo);
    }
    
    /**
     * Get a single $$school$$ entity
     * 
     * @param assessmentId
     *            The Id of the $$school$$.
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single assessment entity
     */
    @GET
    @Path("{id}")
    @Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE })
    public Response getEntity(@PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max,
            @QueryParam(FULL_ENTITIES_PARAM) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return crudDelegate.getEntity(id, sortBy, sortOrder, skip, max, fullEntities, uriInfo);
    }
    
    /**
     * Delete a $$school$$ entity
     * 
     * @param assessmentId
     *            The Id of the $$school$$.
     * @return Returns a NO_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{id}")
    public Response deleteEntity(@PathParam("id") final String id) {
        return crudDelegate.deleteEntity(id);
    }
    
    /**
     * Update an existing $$school$$ entity.
     * 
     * @param assessmentId
     *            The id of the $$school$$.
     * @param newEntityBody
     *            entity data
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{id}")
    public Response updateEntity(@PathParam("id") final String id, final EntityBody newEntityBody) {
        return crudDelegate.updateEntity(id, newEntityBody);
    }
    
}
