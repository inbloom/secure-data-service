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
     * Returns all School entities for which the logged in User has permission and context.
     * 
     * @param uriInfo
     * @param offset starting position in results to return to user
     * @param limit maximum number of results to return to user (starting from offset)
     * @return
     */
    @GET
    public Response getEntityCollection(@Context final UriInfo uriInfo,
            @QueryParam("offset") @DefaultValue("0") final int offset,
            @QueryParam("limit") @DefaultValue("50") final int limit) {
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
     *            The Id of the School.
     * @param fullEntities
     *            whether or not the full entity should be returned or just the link. Defaults to
     *            false
     * @param uriInfo
     * @return A single school entity
     * @response.representation.200.mediaType application/json
     * @response.representation.200.qname {http://www.w3.org/2001/XMLSchema}school
     */
    @GET
    @Path("{schoolId}")
    @Produces({ Resource.JSON_MEDIA_TYPE, Resource.SLC_JSON_MEDIA_TYPE })
    public Response getEntity(@PathParam("schoolId") final String schoolId,
            @QueryParam(FULL_ENTITIES_PARAM) @DefaultValue("false") final boolean fullEntities,
            @Context final UriInfo uriInfo) {
        return super.getEntity(schoolId, fullEntities, uriInfo);
    }

    /**
     * Delete a school entity
     * 
     * @param id
     *            The Id of the School.
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{schoolId}")
    public Response deleteEntity(@PathParam("schoolId") final String schoolId) {
        return super.deleteEntity(schoolId);
    }

    /**
     * Update an existing school entity.
     * 
     * @param schoolId
     *            The Id of the School.
     * @param newEntityBody
     *            entity data that will used to replace the existing entity data
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{schoolId}")
    public Response updateEntity(@PathParam("schoolId") final String schoolId,
            final EntityBody newEntityBody) {
        return super.updateEntity(schoolId, newEntityBody);
    }

    /**
     * Returns all the student-school-associations that
     * reference the given school
     * 
     * @param schoolId
     *            The Id of the School.
     * @return
     */
    @GET
    @Path("{schoolId}/student-school-associations")
    public Response getStudentSchoolAssociations(@PathParam("schoolId") final String schoolId) {
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }
}
