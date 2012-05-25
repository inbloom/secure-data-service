package org.slc.sli.api.resources.v1.association;

import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

/**
 * Represents the course offering resource. A course offering represents
 * the association of a $$Course$$ with a $$Session$$ during which the
 * course is offered.
 *
 * For detailed information, see the schema for $$CourseOffering$$ resources.
 */
@Path(PathConstants.V1 + "/" + PathConstants.COURSE_OFFERINGS)
@Component
@Scope("request")
public class CourseOfferingResource extends DefaultCrudEndpoint {

    @Autowired
    public CourseOfferingResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.COURSE_OFFERINGS);
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
    @Consumes({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * Returns the specified resource representation(s).
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.COURSE_OFFERING_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON, MediaType.APPLICATION_XML })
    public Response read(@PathParam(ParameterConstants.COURSE_OFFERING_ID) final String courseOfferingId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(courseOfferingId, headers, uriInfo);
    }

    /**
     * Deletes the specified resource.
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.COURSE_OFFERING_ID + "}")
    public Response delete(@PathParam(ParameterConstants.COURSE_OFFERING_ID) final String courseOfferingId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(courseOfferingId, headers, uriInfo);
    }

    /**
     * Updates the specified resource using the given resource data.
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.COURSE_OFFERING_ID + "}")
    public Response update(@PathParam(ParameterConstants.COURSE_OFFERING_ID) final String courseOfferingId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(courseOfferingId, newEntityBody, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_OFFERING_ID + "}" + "/" + PathConstants.SESSIONS)
    public Response getSessions(@PathParam(ParameterConstants.COURSE_OFFERING_ID) final String courseOfferingId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
       return super.read(ResourceNames.COURSE_OFFERINGS, "_id", courseOfferingId, "sessionId", ResourceNames.SESSIONS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_OFFERING_ID + "}" + "/" + PathConstants.COURSES)
    public Response getCourses(@PathParam(ParameterConstants.COURSE_OFFERING_ID) final String courseOfferingId,
            @QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
       return super.read(ResourceNames.COURSE_OFFERINGS, "_id", courseOfferingId, "courseId", ResourceNames.COURSES, headers, uriInfo);
    }
}
