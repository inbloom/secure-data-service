package org.slc.sli.api.resources.v1.entity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slc.sli.api.resources.v1.DefaultCrudResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.client.constants.ResourceNames;
import org.slc.sli.client.constants.v1.ParameterConstants;
import org.slc.sli.client.constants.v1.PathConstants;

/**
 * Represents the definition of a section resource. A section is an educational entity that
 * represents a setting in which organized instruction of course content is provided to one
 * or more students for a given period of time. See $$Course$$ resource for details.
 * A section is associated with a student, teacher, and assessment through $$StudentSectionAssociation$$,
 * $$TeacherSectionAssociation$$, and $$SectionAssessmentAssociation$$.
 *
 * For more details about the resources, see $$Student$$, $$Teacher$$ and $$Assessment$$ resources.
 *
 * For detailed information, see the schema for $$Section$$ resources.
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.SECTIONS)
@Component
@Scope("request")
public class SectionResource extends DefaultCrudResource {

    @Autowired
    public SectionResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.SECTIONS);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SECTION_ID + "}" + "/" + PathConstants.STUDENT_SECTION_ASSOCIATIONS)
    public Response getStudentSectionAssociations(@PathParam(ParameterConstants.SECTION_ID) final String sectionId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "sectionId", sectionId, headers,
                uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SECTION_ID + "}" + "/" + PathConstants.STUDENT_SECTION_ASSOCIATIONS + "/"
            + PathConstants.STUDENTS)
    public Response getStudentSectionAssociationStudents(
            @PathParam(ParameterConstants.SECTION_ID) final String sectionId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        
        // add optional field to the query params. always include the student's grade level.
        uriInfo.getQueryParameters(true).add(ParameterConstants.OPTIONAL_FIELDS, "gradeLevel");

        return super.read(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "sectionId", sectionId, "studentId",
                ResourceNames.STUDENTS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SECTION_ID + "}" + "/" + PathConstants.TEACHER_SECTION_ASSOCIATIONS)
    public Response getTeacherSectionAssociations(@PathParam(ParameterConstants.SECTION_ID) final String sectionId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "sectionId", sectionId, headers,
                uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SECTION_ID + "}" + "/" + PathConstants.TEACHER_SECTION_ASSOCIATIONS + "/"
            + PathConstants.TEACHERS)
    public Response getTeacherSectionAssociationTeachers(
            @PathParam(ParameterConstants.SECTION_ID) final String sectionId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "sectionId", sectionId, "teacherId",
                ResourceNames.TEACHERS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SECTION_ID + "}" + "/" + PathConstants.SECTION_ASSESSMENT_ASSOCIATIONS)
    public Response getSectionAssessmentAssociations(@PathParam(ParameterConstants.SECTION_ID) final String sectionId,
                                                  @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS, "sectionId", sectionId, headers,
                uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.SECTION_ID + "}" + "/" + PathConstants.SECTION_ASSESSMENT_ASSOCIATIONS + "/"
            + PathConstants.ASSESSMENTS)
    public Response getSectionAssessmentAssociationAssessments(
            @PathParam(ParameterConstants.SECTION_ID) final String sectionId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS, "sectionId", sectionId, "assessmentId",
                ResourceNames.ASSESSMENTS, headers, uriInfo);
    }
}
