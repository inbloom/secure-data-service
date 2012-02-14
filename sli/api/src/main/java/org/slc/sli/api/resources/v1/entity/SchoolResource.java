package org.slc.sli.api.resources.v1.entity;


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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.Resource;
import org.slc.sli.api.resources.v1.BaseResource;

/**
 * Prototype new api end points and versioning 
 * @author srupasinghe
 *
 */
@Path("v1/schools")
@Component
@Scope("request")
@Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE })
public class SchoolResource extends BaseResource {
    
    private static final String TYPE_PATH = "schools";

    @Autowired
    public SchoolResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, TYPE_PATH);
    }
    
    /**
     * Returns all School entities 
     * 
     * @param newEntityBody
     * @param uriInfo
     * @return
     */
    @GET
    public Response getEntityCollection(@Context final UriInfo uriInfo) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }

    /**
     * Create a new school entity.
     *
     * @param newEntityBody
     *            entity data
     * @param uriInfo
     * @return Response with a status of CREATED and a Location header set pointing to where the new
     *         entity lives
     * @response.representation.201.mediaType HTTP headers with a Created status code and a Location
     *                                        value.
     */
    @POST
    public Response createEntity(final EntityBody newEntityBody,
            @Context final UriInfo uriInfo) {
        return super.createEntity(newEntityBody, uriInfo);
    }

    /**
     * Get a single school entity 
     *
     * @param id
     *            school id
     * @param skip
     *            number of results to skip
     * @param max
     *            maximum number of results to return
     * @param fullEntities
     *            whether or not the full entity should be returned or just the link.  Defaults to false
     * @param uriInfo
     * @return A single school entity
     * @response.representation.200.mediaType application/json
     * @response.representation.200.qname {http://www.w3.org/2001/XMLSchema}school
     */
    @GET
    @Path("{id}")
    @Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE })
    public Response getEntity(@PathParam("id") final String id,
            @QueryParam("start-index") @DefaultValue("0") final int skip,
            @QueryParam("max-results") @DefaultValue("50") final int max,
            @QueryParam(FULL_ENTITIES_PARAM) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return super.getEntity(id, skip, max, fullEntities, uriInfo);
    }

    /**
     * Delete a school entity
     *
     * @param typePath
     *            resourceUri of the entity
     * @param id
     *            id of the entity
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{id}")
    public Response deleteEntity(@PathParam("id") final String id) {
        return super.deleteEntity(id);
    }

    /**
     * Update an existing school entity.
     *
     * @param typePath
     *            resourceUri for the entity
     * @param id
     *            id of the entity
     * @param newEntityBody
     *            entity data that will used to replace the existing entity data
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{id}")
    public Response updateEntity(@PathParam("id") final String id,
            final EntityBody newEntityBody) {
        return super.updateEntity(id, newEntityBody);
    }
    
    /**
     * Returns all the student-school-associations that
     * reference the given school
     * @param id
     * @return
     */
    @GET
    @Path("{id}/student-school-associations")
    public Response getStudentSchoolAssociations(@PathParam("id") final String id) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
}
