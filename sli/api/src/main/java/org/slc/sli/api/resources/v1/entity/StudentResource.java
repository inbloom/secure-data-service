/*
 * Copyright 2012 Shared Learning Collaborative, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.resources.v1.entity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
import org.slc.sli.api.constants.ParameterConstants;
import org.slc.sli.api.constants.PathConstants;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.representation.EntityResponse;
import org.slc.sli.api.resources.v1.DefaultCrudResource;

/**
 * This entity represents an individual for whom
 * instruction, services and/or care are provided
 * in an early childhood, elementary or secondary
 * educational program under the jurisdiction of a school,
 * education agency, or other institution
 * or program. A student is a person who has been enrolled in a
 * school or other educational institution. For more information, see the schema for the $$Student$$
 * resources.
 *
 * @author jstokes
 *
 */
@Path(PathConstants.V1 + "/" + PathConstants.STUDENTS)
@Component
@Scope("request")
public class StudentResource extends DefaultCrudResource {

    public static final String UNIQUE_STATE_ID = "studentUniqueStateId";
    public static final String NAME = "name";
    public static final String SEX = "sex";
    public static final String BIRTH_DATA = "birthData";
    public static final String HISPANIC_LATINO_ETHNICITY = "hispanicLatinoEthnicity";

    /*
     * Constants for readWithGrade
     */
    private static final String ENTRY_GRADE_LEVEL = "entryGradeLevel";
    private static final String ENTRY_DATE = "entryDate";
    private static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";
    private static final String GRADE_LEVEL = "gradeLevel";

    @Autowired
    public StudentResource(EntityDefinitionStore entityDefs) {
        super(entityDefs, ResourceNames.STUDENTS);
    }

    @Override
    protected List<String> getOptionalFields(UriInfo info) {
        List<String> origFields = super.getOptionalFields(info);
        List<String> optionalFields = origFields == null ? new ArrayList<String>() : new ArrayList<String>(origFields);
        optionalFields.add("gradeLevel");
        return optionalFields;
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_SECTION_ASSOCIATIONS)
    public Response getStudentSectionAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "studentId", studentId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_SECTION_ASSOCIATIONS + "/"
            + PathConstants.SECTIONS)
    public Response getStudentSectionAssociationSections(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "studentId", studentId, "sectionId",
                ResourceNames.SECTIONS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_SCHOOL_ASSOCIATIONS)
    public Response getStudentSchoolAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "studentId", studentId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_SCHOOL_ASSOCIATIONS + "/"
            + PathConstants.SCHOOLS)
    public Response getStudentSchoolAssociationSchools(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "studentId", studentId, "schoolId",
                ResourceNames.SCHOOLS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS)
    public Response getStudentAssessmentAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, "studentId", studentId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_ASSESSMENT_ASSOCIATIONS + "/"
            + PathConstants.ASSESSMENTS)
    public Response getStudentAssessmentAssociationsAssessments(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, "studentId", studentId, "assessmentId",
                ResourceNames.ASSESSMENTS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.ATTENDANCES)
    public Response getStudentsAttendance(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.ATTENDANCES, "studentId", studentId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.COURSE_TRANSCRIPTS)
    public Response getStudentTranscriptAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "studentId", studentId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.COURSE_TRANSCRIPTS + "/"
            + PathConstants.COURSES)
    public Response getStudentTranscriptAssociationCourses(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "studentId", studentId, "courseId",
                ResourceNames.COURSES, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS)
    public Response getStudentParentAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "studentId", studentId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_PARENT_ASSOCIATIONS + "/"
            + PathConstants.PARENTS)
    public Response getStudentParentAssociationCourses(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "studentId", studentId, "parentId",
                ResourceNames.PARENTS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS)
    public Response getStudentDisciplineIncidentAssociations(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS, "studentId", studentId, headers,
                uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS
            + "/" + PathConstants.DISCIPLINE_INCIDENTS)
    public Response getStudentDisciplineIncidentAssociationDisciplineIncidents(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS, "studentId", studentId,
                "disciplineIncidentId", ResourceNames.DISCIPLINE_INCIDENTS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_COHORT_ASSOCIATIONS)
    public Response getStudentCohortAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ParameterConstants.STUDENT_ID, studentId, headers,
                uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_COHORT_ASSOCIATIONS + "/"
            + PathConstants.COHORTS)
    public Response getStudentCohortAssociationCohorts(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ParameterConstants.STUDENT_ID, studentId,
                ParameterConstants.COHORT_ID, ResourceNames.COHORTS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_PROGRAM_ASSOCIATIONS)
    public Response getStudentProgramAssociations(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, "studentId", studentId, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.STUDENT_PROGRAM_ASSOCIATIONS + "/"
            + PathConstants.PROGRAMS)
    public Response getStudentProgramAssociationPrograms(
            @PathParam(ParameterConstants.STUDENT_ID) final String studentId, @Context HttpHeaders headers,
            @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, "studentId", studentId, "programId",
                ResourceNames.PROGRAMS, headers, uriInfo);
    }

    /**
     * Returns the requested collection of resources that are associated with the specified resource.
     */
    @GET
    @Path("{" + ParameterConstants.STUDENT_ID + "}" + "/" + PathConstants.REPORT_CARDS)
    public Response getReportCards(@PathParam(ParameterConstants.STUDENT_ID) final String studentId,
            @Context HttpHeaders headers, @Context final UriInfo uriInfo) {
        return super.read(ResourceNames.REPORT_CARDS, ParameterConstants.STUDENT_ID, studentId, headers, uriInfo);
    }
}
