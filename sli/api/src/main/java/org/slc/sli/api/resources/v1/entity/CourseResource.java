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

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.v1.DefaultCrudEndpoint;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 * CourseResource
 *
 * This educational entity represents the organization of subject matter and related learning experiences provided
 * for the instruction of students on a regular or systematic basis.
 *
 * This is similar to section except that a section is a specific instance of a course.
 */
@Path(PathConstants.V1 + "/" + PathConstants.COURSES)
@Component
@Scope("request")
public class CourseResource extends DefaultCrudEndpoint {

    @Autowired
    public CourseResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.COURSES);
    }

    /**
     * readAll
     *
     * @param offset
     *            starting position in results to return to user
     * @param limit
     *            maximum number of results to return to user (starting from offset)
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return all $$courses$$ entities for which the logged in User has permission and context.
     */
    @Override
    @GET
    public Response readAll(@QueryParam(ParameterConstants.OFFSET) @DefaultValue(ParameterConstants.DEFAULT_OFFSET) final int offset,
            @QueryParam(ParameterConstants.LIMIT) @DefaultValue(ParameterConstants.DEFAULT_LIMIT) final int limit,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.readAll(offset, limit, headers, uriInfo);
    }

    /**
     * create
     *
     * @param newEntityBody
     *            entity data
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *              URI information including path and query parameters
     * @return A 201 response on successfully created entity with the ID of the entity
     */
    @Override
    @POST
    public Response create(final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.create(newEntityBody, headers, uriInfo);
    }

    /**
     * read
     *
     * @param courseId
     *            The id (or list of ids) of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return A list of entities matching the list of ids queried for
     *
     */
    @Override
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}")
    public Response read(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(courseId, headers, uriInfo);
    }

    /**
     * delete
     *
     * @param courseId
     *            The Id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns a NOT_CONTENT status code
     */
    @Override
    @DELETE
    @Path("{" + ParameterConstants.COURSE_ID + "}")
    public Response delete(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.delete(courseId, headers, uriInfo);
    }

    /**
     * update
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
     */
    @Override
    @PUT
    @Path("{" + ParameterConstants.COURSE_ID + "}")
    public Response update(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            final EntityBody newEntityBody,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.update(courseId, newEntityBody, headers, uriInfo);
    }

    /**
     * getCourseOfferings
     *
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$courseOfferings$$ that references the given $$courses$$
     *
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS)
    public Response getCourseOfferings(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "courseId", courseId, headers, uriInfo);
    }


    /**
     * getCourseOfferingCourses
     *
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$sessions$$ associated to the given course through a $$courseOfferings$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS + "/" + PathConstants.SESSIONS)
    public Response getCourseOfferingCourses(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "courseId", courseId, "sessionId", ResourceNames.SESSIONS, headers, uriInfo);
    }


    /**
     * getStudentTranscriptAssociations
     *
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$studentTranscriptAssociations$$ that reference the given $$courses$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_TRANSCRIPTS)
    public Response getStudentTranscriptAssociations(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                     @Context HttpHeaders headers,
                                                     @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "courseId", courseId, headers, uriInfo);
    }

    /**
     * getStudentTranscriptAssociationStudents
     *
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$students$$ associated to the given course through a $$studentTranscriptAssociations$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_TRANSCRIPTS + "/" + PathConstants.STUDENTS)
    public Response getStudentTranscriptAssociationStudents(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                            @Context HttpHeaders headers,
                                                            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "courseId", courseId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }


    /**
     * getStudentParentAssociations
     *
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$studentParentAssociations$$ that reference the given $$courses$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS)
    public Response getStudentParentAssociations(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                     @Context HttpHeaders headers,
                                                     @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "courseId", courseId, headers, uriInfo);
    }

    /**
     * getStudentParentAssociationStudents
     *
     * @param courseId
     *            The id of the $$courses$$.
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return Returns each $$students$$ associated to the given course through a $$studentParentAssociations$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS + "/" + PathConstants.STUDENTS)
    public Response getStudentParentAssociationStudents(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                            @Context HttpHeaders headers,
                                                            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "courseId", courseId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }


}
