package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.slc.sli.common.constants.ResourceNames;
import org.slc.sli.common.constants.v1.ParameterConstants;
import org.slc.sli.common.constants.v1.PathConstants;

/**
 *
 * Represents the organization of subject matter and related learning experiences provided
 * for the instruction of students on a regular or systematic basis.
 *
 * This is similar to section except that a section is a specific instance of a course.
 */
@Path(PathConstants.V1 + "/" + PathConstants.COURSES)
@Component
@Scope("request")
public class CourseResource extends DefaultCrudResource {

    @Autowired
    public CourseResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.COURSES);
    }

    /**
     * Returns each $$SessionCourseAssociation$$ that references the given $$Course$$.
     *
     * @param courseId
     *            The id of the $$Course$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$SessionCourseAssociation$$ that references the given $$Course$$
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
     * Returns each $$Session$$ that is associated with the given $$Course$$ through $$SessionCourseAssociation$$.
     *
     * @param courseId
     *            The id of the $$Course$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$Session$$ that is associated with the given $$Course$$ through $$SessionCourseAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS + "/" + PathConstants.SESSIONS)
    public Response getCourseOfferingCourses(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "courseId", courseId, "sessionId", ResourceNames.SESSIONS, headers, uriInfo);
    }


    /**
     * Returns each $$StudentTranscriptAssociation$$ that references the given $$Course$$.
     *
     * @param courseId
     *            The id of the $$Course$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$StudentTranscriptAssociation$$ that references the given $$Course$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_TRANSCRIPTS)
    public Response getStudentTranscriptAssociations(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                     @Context HttpHeaders headers,
                                                     @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "courseId", courseId, headers, uriInfo);
    }

    /**
     * Returns each $$Student$$ that is associated with the given $$Course$$ through $$StudentTranscriptAssociation$$.
     *
     * @param courseId
     *            The id of the $$Course$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$Student$$ that is associated with the given $$Course$$ through $$StudentTranscriptAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_TRANSCRIPTS + "/" + PathConstants.STUDENTS)
    public Response getStudentTranscriptAssociationStudents(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                            @Context HttpHeaders headers,
                                                            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "courseId", courseId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }


    /**
     * Returns each $$StudentParentAssociation$$ that references the given $$Course$$.
     *
     * @param courseId
     *            The id of the $$Course$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$StudentParentAssociation$$ that references the given $$Course$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS)
    public Response getStudentParentAssociations(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                     @Context HttpHeaders headers,
                                                     @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "courseId", courseId, headers, uriInfo);
    }

    /**
     * Returns each $$Student$$ that is associated with the given $$Course$$ through $$StudentParentAssociation$$.
     *
     * @param courseId
     *            The id of the $$Course$$
     * @param headers
     *            HTTP Request Headers
     * @param uriInfo
     *            URI information including path and query parameters
     * @return each $$Student$$ that is associated with the given $$Course$$ through $$StudentParentAssociation$$
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS + "/" + PathConstants.STUDENTS)
    public Response getStudentParentAssociationStudents(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                            @Context HttpHeaders headers,
                                                            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "courseId", courseId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }

}
