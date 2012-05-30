package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;

/**
 * Represents the session resource. A session is the prescribed span of time
 * when an education institution is open, instruction is provided, and
 * students are under the direction and guidance of teachers and/or
 * education institution administration. A session may be interrupted
 * by one or more vacations.
 *
 * For detailed information, see the schema for $$Session$$ resources.
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.SESSIONS)
@Component
@Scope("request")
public class SessionResource extends DefaultCrudEndpoint {

    @Autowired
    public SessionResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.SESSIONS);
    }

    /**
     * Returns the requested collection of resource representations.
     */
    @Override
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * Creates a new resource using the given resource data.
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Returns the specified resource representation(s).
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.SESSION_ID + "}")
    public Response read(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(sessionId, headers, uriInfo);
    }

    /**
     * Deletes the specified resource.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.SESSION_ID + "}")
    public Response delete(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(sessionId, headers, uriInfo);
    }

    /**
     * Updates the specified resource using the given resource data.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.SESSION_ID + "}")
    public Response update(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(sessionId, newEntityBody, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.SCHOOL_SESSION_ASSOCIATIONS)
    public Response getSchoolSessionAssociations(@PathParam(ParameterConstants.SCHOOL_ID) final String sessionId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS, "sessionId", sessionId, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SCHOOL_ID + "}" + "/" + PathConstants.SCHOOL_SESSION_ASSOCIATIONS + "/" + PathConstants.SCHOOLS)
    public Response getSchoolSessionAssociationSchools(@PathParam(ParameterConstants.SCHOOL_ID) final String sessionId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS, "sessionId", sessionId, "schoolId", ResourceNames.SCHOOLS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SESSION_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS)
    public Response getCourseOfferings(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "sessionId", sessionId, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SESSION_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS + "/" + PathConstants.COURSES)
    public Response getCourseOfferingCourses(@PathParam(ParameterConstants.SESSION_ID) final String sessionId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "sessionId", sessionId, "courseId", ResourceNames.COURSES, headers, uriInfo);
    }

}
