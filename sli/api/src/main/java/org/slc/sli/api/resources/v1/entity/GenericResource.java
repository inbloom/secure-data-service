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
import org.slc.sli.api.service.query.SortOrder;
/**
 * Support for a resource that has not been added to the API
 * 
 * @author nbrown
 * 
 */
@Path("{type}")
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE, Resource.SLC_LONG_JSON_MEDIA_TYPE })
public class GenericResource {
    private final EntityDefinitionStore entityDefs;
    
    @Autowired
    public GenericResource(EntityDefinitionStore entityDefs) {
        this.entityDefs = entityDefs;
    }
    
    @POST
    public Response createEntity(@PathParam("type") final String typePath, final EntityBody newEntityBody, @Context final UriInfo uriInfo) {
        return new Resource(entityDefs.lookupByResourceName(typePath), entityDefs).createEntity(newEntityBody, uriInfo);
    }

    @GET
    @Path("{id}")
    @Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE })
    public Response getEntity(@PathParam("type") final String typePath, @PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max,
            @QueryParam(FULL_ENTITIES_PARAM) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return new Resource(entityDefs.lookupByResourceName(typePath), entityDefs).getEntity(id, sortBy, sortOrder, skip, max, fullEntities, uriInfo);
    }

    @GET
    @Path("{id}")
    @Produces({ Resource.SLC_LONG_JSON_MEDIA_TYPE })
    public Response getFullEntities(@PathParam("type") final String typePath, @PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max, @Context final UriInfo uriInfo) {
        return new Resource(entityDefs.lookupByResourceName(typePath), entityDefs).getFullEntities(id, sortBy, sortOrder, skip, max, uriInfo);
    }

    @GET
    @Path("{id}/targets")
    @Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE })
    public Response getHoppedRelatives(@PathParam("type") final String typePath, @PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max,
            @QueryParam(FULL_ENTITIES_PARAM) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return new Resource(entityDefs.lookupByResourceName(typePath), entityDefs).getHoppedRelatives(id, sortBy, sortOrder, skip, max, fullEntities, uriInfo);
    }

    @GET
    @Path("{id}/targets")
    @Produces({ Resource.SLC_LONG_JSON_MEDIA_TYPE })
    public Response getFullHoppedRelatives(@PathParam("type") final String typePath, @PathParam("id") final String id,
            @QueryParam(SORT_BY_PARAM) @DefaultValue("") final String sortBy,
            @QueryParam(SORT_ORDER_PARAM) @DefaultValue("ascending") final SortOrder sortOrder,
            @QueryParam(START_INDEX_PARAM) @DefaultValue("0") final int skip,
            @QueryParam(MAX_RESULTS_PARAM) @DefaultValue("50") final int max, @Context final UriInfo uriInfo) {
        return new Resource(entityDefs.lookupByResourceName(typePath), entityDefs).getFullHoppedRelatives(id, sortBy, sortOrder, skip, max, uriInfo);
    }

    @DELETE
    @Path("{id}")
    public Response deleteEntity(@PathParam("type") final String typePath, @PathParam("id") final String id) {
        return new Resource(entityDefs.lookupByResourceName(typePath), entityDefs).deleteEntity(id);
    }

    @PUT
    @Path("{id}")
    public Response updateEntity(@PathParam("type") final String typePath, @PathParam("id") final String id, final EntityBody newEntityBody) {
        return new Resource(entityDefs.lookupByResourceName(typePath), entityDefs).updateEntity(id, newEntityBody);
    }
    
    
    
}
