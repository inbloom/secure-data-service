package org.slc.sli.api.resources.v1.entity;

import org.slc.sli.api.config.ResourceNames;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.util.ResourceUtil;
import org.slc.sli.api.resources.v1.CrudEndpoint;
import org.slc.sli.api.resources.v1.HypermediaType;
import org.slc.sli.api.resources.v1.ParameterConstants;
import org.slc.sli.api.resources.v1.PathConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

/**
 * Prototype new api end points and versioning
 * 
 * @author jstokes
 * 
 */
@Path(PathConstants.V1 + "/" + PathConstants.COURSES)
@Component
@Scope("request")
@Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
public class CourseResource {
    
    /**
     * Logging utility.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseResource.class);
    
    /*
     * Interface capable of performing CRUD operations.
     */
    private final CrudEndpoint crudDelegate;

    @Autowired
    public CourseResource(CrudEndpoint crudDelegate) {
        this.crudDelegate = crudDelegate;
    }

    /**
     * Returns all $$courses$$ entities for which the logged in User has permission and context.
     * 
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        ResourceUtil.putValue(headers.getRequestHeaders(), ParameterConstants.LIMIT, limit);
        ResourceUtil.putValue(headers.getRequestHeaders(), ParameterConstants.OFFSET, offset);
        return this.crudDelegate.readAll(ResourceNames.COURSES, headers, uriInfo);
    }

    /**
     * Create a new $$courses$$ entity.
     * 
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *              URI information including path and query parameters
     * @return result of CRUD operation
     * @response.param {@name Location} {@style header} {@type
     *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI where the created
     *                 item is accessable.}
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    public Response create(final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.create(ResourceNames.COURSES, newEntityBody, headers, uriInfo);
    }

    /**
     * Get a single $$courses$$ entity
     * 
     * @param courseId
     *            The Id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A single course entity
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}")
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    public Response read(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.COURSES, courseId, headers, uriInfo);
    }

    /**
     * Delete a $$courses$$ entity
     * 
     * @param courseId
     *            The Id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @DELETE
    @Path("{" + ParameterConstants.COURSE_ID + "}")
    public Response delete(@PathParam(ParameterConstants.COURSE_ID) final String courseId, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.delete(ResourceNames.COURSES, courseId, headers, uriInfo);
    }

    /**
     * Update an existing $$courses$$ entity.
     * 
     * @param courseId
     *            The id of the $$courses$$.
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Response with a NOT_CONTENT status code
     * @response.representation.204.mediaType HTTP headers with a Not-Content status code.
     */
    @PUT
    @Path("{" + ParameterConstants.COURSE_ID + "}")
    public Response update(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            final EntityBody newEntityBody, 
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return this.crudDelegate.update(ResourceNames.COURSES, courseId, newEntityBody, headers, uriInfo);
    }
    
    /**
     * Returns each $$sessionCourseAssociations$$ that
     * references the given $$courses$$
     * 
     * @param courseId
     *            The id of the $$courses$$.
     * @param offset
     *            Index of the first result to return
     * @param limit
     *            Maximum number of results to return.
     * @param expandDepth
     *            Number of hops (associations) for which to expand entities.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.SESSION_COURSE_ASSOCIATIONS)
    public Response getSessionCourseAssociations(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.SESSION_COURSE_ASSOCIATIONS, "courseId", courseId, headers, uriInfo);
    }
    

    /**
     * Returns each $$sessions$$ associated to the given session through
     * a $$sessionCourseAssociations$$ 
     * 
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.SESSION_COURSE_ASSOCIATIONS + "/" + PathConstants.SESSIONS)
    public Response getSessionCourseAssociationCourses(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.SESSION_COURSE_ASSOCIATIONS, "courseId", courseId, "sessionId", ResourceNames.SESSIONS, headers, uriInfo);
    }
    

    /**
     * student transcript associations
     * 
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.STUDENT_TRANSCRIPT_ASSOCIATIONS)
    public Response getStudentTranscriptAssociations(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "courseId", courseId, headers, uriInfo);
    }
    
    /**
     * student transcript associations - students lookup
     * 
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return result of CRUD operation
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, HypermediaType.VENDOR_SLC_JSON })
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.STUDENT_TRANSCRIPT_ASSOCIATIONS + "/" + PathConstants.STUDENTS)
    public Response getStudentTranscriptAssociationStudents(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers, 
            @Context final UriInfo uriInfo) {
        return this.crudDelegate.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "courseId", courseId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }


}
