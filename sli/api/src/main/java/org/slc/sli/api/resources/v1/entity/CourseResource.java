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

import org.slc.sli.api.client.constants.ResourceNames;
import org.slc.sli.api.client.constants.v1.ParameterConstants;
import org.slc.sli.api.client.constants.v1.PathConstants;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.resources.v1.DefaultCrudResource;

/**
 *
 * Represents the organization of subject matter and related learning experiences provided
 * for the instruction of students on a regular or systematic basis.
 *
 * This is similar to section except that a section is a specific instance of a course.
 *
 * For more information, see the schema for $$Course$$ resources.
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
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS)
    public Response getCourseOfferings(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "courseId", courseId, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_OFFERINGS + "/" + PathConstants.SESSIONS)
    public Response getCourseOfferingCourses(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
            @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.COURSE_OFFERINGS, "courseId", courseId, "sessionId", ResourceNames.SESSIONS, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_TRANSCRIPTS)
    public Response getStudentTranscriptAssociations(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                     @Context HttpHeaders headers,
                                                     @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "courseId", courseId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.COURSE_TRANSCRIPTS + "/" + PathConstants.STUDENTS)
    public Response getStudentTranscriptAssociationStudents(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                            @Context HttpHeaders headers,
                                                            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "courseId", courseId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }


    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS)
    public Response getStudentParentAssociations(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                     @Context HttpHeaders headers,
                                                     @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "courseId", courseId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.COURSE_ID + "}" + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS + "/" + PathConstants.STUDENTS)
    public Response getStudentParentAssociationStudents(@PathParam(ParameterConstants.COURSE_ID) final String courseId,
                                                            @Context HttpHeaders headers,
                                                            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "courseId", courseId, "studentId", ResourceNames.STUDENTS, headers, uriInfo);
    }

}
