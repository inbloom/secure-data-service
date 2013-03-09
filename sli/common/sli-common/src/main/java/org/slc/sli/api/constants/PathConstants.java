/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
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


package org.slc.sli.api.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants used in URI path requests.
 *
 * @author kmyers
 *
 */
public class PathConstants {

    public static final String STUDENT_SCHOOL_ASSOCIATIONS = "studentSchoolAssociations";
    public static final String TEACHER_SCHOOL_ASSOCIATIONS = "teacherSchoolAssociations";
    public static final String TEACHER_SECTION_ASSOCIATIONS = "teacherSectionAssociations";
    public static final String COURSE_OFFERINGS = "courseOfferings";
    public static final String STUDENT_ASSESSMENTS = "studentAssessments";
    public static final String STUDENT_SECTION_ASSOCIATIONS = "studentSectionAssociations";
    public static final String STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS = "staffEducationOrgAssignmentAssociations";
    public static final String EDUCATION_ORGANIZATION_ASSOCIATIONS = "educationOrganizationAssociations";
    public static final String COURSE_SECTION_ASSOCIATIONS = "courseSectionAssociations";
    public static final String COURSE_TRANSCRIPTS = "courseTranscripts";
    public static final String STUDENT_PARENT_ASSOCIATIONS = "studentParentAssociations";
    public static final String STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS = "studentDisciplineIncidentAssociations";
    public static final String STAFF_COHORT_ASSOCIATIONS = "staffCohortAssociations";
    public static final String STUDENT_COHORT_ASSOCIATIONS = "studentCohortAssociations";
    public static final String STUDENT_WITH_GRADE = "studentWithGrade";

    public static final String ASSESSMENTS = "assessments";
    public static final String LEARNING_OBJECTIVES = "learningObjectives";
    public static final String PARENT_LEARNING_OBJECTIVES = "parentLearningObjectives";
    public static final String CHILD_LEARNING_OBJECTIVES = "childLearningObjectives";
    public static final String ATTENDANCES = "attendances";
    public static final String COHORTS = "cohorts";
    public static final String COMPETENCY_LEVEL_DESCRIPTORS = "competencyLevelDescriptor";
    public static final String COURSES = "courses";
    public static final String DISCIPLINE_INCIDENTS = "disciplineIncidents";
    public static final String DISCIPLINE_ACTIONS = "disciplineActions";
    public static final String EDUCATION_ORGANIZATIONS = "educationOrganizations";
    public static final String GRADEBOOK_ENTRIES = "gradebookEntries";
    public static final String GRADING_PERIODS = "gradingPeriods";
    public static final String LEARNING_STANDARDS = "learningStandards";
    public static final String PARENTS = "parents";
    public static final String PROGRAMS = "programs";
    public static final String REPORT_CARDS = "reportCards";
    public static final String SECTIONS = "sections";
    public static final String SESSIONS = "sessions";
    public static final String SCHOOLS = "schools";
    public static final String STUDENTS = "students";
    public static final String STUDENT_COMPETENCIES = "studentCompetencies";
    public static final String STUDENT_COMPETENCY_OBJECTIVES = "studentCompetencyObjectives";
    public static final String TEACHERS = "teachers";
    public static final String STAFF = "staff";
    public static final String STUDENT_ACADEMIC_RECORDS = "studentAcademicRecords";
    public static final String STUDENT_GRADEBOOK_ENTRIES = "studentGradebookEntries";
    public static final String STAFF_PROGRAM_ASSOCIATIONS = "staffProgramAssociations";
    public static final String STUDENT_PROGRAM_ASSOCIATIONS = "studentProgramAssociations";
    public static final String GRADES = "grades";
    public static final String GRADUATION_PLANS = "graduationPlans";

    public static final String CUSTOM_ENTITIES = "custom";

    public static final String CALCULATED_VALUES = "calculatedValues";

    public static final String AGGREGATIONS = "aggregations";

   /**
     * Paths to various helper functions
     */
    public static final String SECURITY_SESSION_CHECK = "system/session/check";

    /** resource for security session logout */
    public static final String SECURITY_SESSION_LOGOUT = "system/session/logout";

    /** resource for security session check */
    public static final String SECURITY_SESSION_DEBUG = "system/session/debug";

    /** Home resource */
    public static final String HOME = "home";


    /*
     * This map should go away when we switch basic definition store association names to camel
     * case.
     * This map is used when building the links to be returned. When building links we need to look
     * into definition store to get
     * the resource name. This resource name is then mapped to the new camel case name using this
     * map.
     */
    @Deprecated
    public static final Map<String, String> TEMP_MAP = new HashMap<String, String>();
    static {
        TEMP_MAP.put(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, STUDENT_SCHOOL_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, TEACHER_SCHOOL_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, TEACHER_SECTION_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.COURSE_OFFERINGS, COURSE_OFFERINGS);
        TEMP_MAP.put(ResourceNames.STUDENT_ASSESSMENTS, STUDENT_ASSESSMENTS);
        TEMP_MAP.put(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, STUDENT_SECTION_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.COURSE_TRANSCRIPTS, COURSE_TRANSCRIPTS);
        TEMP_MAP.put(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, STUDENT_PARENT_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, STUDENT_PROGRAM_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, STAFF_PROGRAM_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STAFF_COHORT_ASSOCIATIONS, STAFF_COHORT_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS, STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STAFF_COHORT_ASSOCIATIONS, STAFF_COHORT_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, STUDENT_COHORT_ASSOCIATIONS);

        TEMP_MAP.put(ResourceNames.ASSESSMENTS, ASSESSMENTS);
        TEMP_MAP.put(ResourceNames.LEARNINGOBJECTIVES, LEARNING_OBJECTIVES);
        TEMP_MAP.put(ResourceNames.LEARNINGSTANDARDS, LEARNING_STANDARDS);
        TEMP_MAP.put(ResourceNames.ATTENDANCES, ATTENDANCES);
        TEMP_MAP.put(ResourceNames.COHORTS, COHORTS);
        TEMP_MAP.put(ResourceNames.COMPETENCY_LEVEL_DESCRIPTORS, COMPETENCY_LEVEL_DESCRIPTORS);
        TEMP_MAP.put(ResourceNames.COURSES, COURSES);
        TEMP_MAP.put(ResourceNames.DISCIPLINE_INCIDENTS, DISCIPLINE_INCIDENTS);
        TEMP_MAP.put(ResourceNames.DISCIPLINE_ACTIONS, DISCIPLINE_ACTIONS);
        TEMP_MAP.put(ResourceNames.EDUCATION_ORGANIZATIONS, EDUCATION_ORGANIZATIONS);
        TEMP_MAP.put(ResourceNames.GRADEBOOK_ENTRIES, GRADEBOOK_ENTRIES);
        TEMP_MAP.put(ResourceNames.GRADING_PERIODS, GRADING_PERIODS);
        TEMP_MAP.put(ResourceNames.PARENTS, PARENTS);
        TEMP_MAP.put(ResourceNames.PROGRAMS, PROGRAMS);
        TEMP_MAP.put(ResourceNames.REPORT_CARDS, REPORT_CARDS);
        TEMP_MAP.put(ResourceNames.SECTIONS, SECTIONS);
        TEMP_MAP.put(ResourceNames.SESSIONS, SESSIONS);
        TEMP_MAP.put(ResourceNames.SCHOOLS, SCHOOLS);
        TEMP_MAP.put(ResourceNames.STUDENTS, STUDENTS);
        TEMP_MAP.put(ResourceNames.STUDENT_COMPETENCIES, STUDENT_COMPETENCIES);
        TEMP_MAP.put(ResourceNames.STUDENT_COMPETENCY_OBJECTIVES, STUDENT_COMPETENCY_OBJECTIVES);
        TEMP_MAP.put(ResourceNames.STAFF, STAFF);
        TEMP_MAP.put(ResourceNames.STUDENT_ACADEMIC_RECORDS, STUDENT_ACADEMIC_RECORDS);
        TEMP_MAP.put(ResourceNames.STUDENT_GRADEBOOK_ENTRIES, STUDENT_GRADEBOOK_ENTRIES);
        TEMP_MAP.put(ResourceNames.TEACHERS, TEACHERS);
        TEMP_MAP.put(ResourceNames.GRADES, GRADES);
        TEMP_MAP.put(ResourceNames.GRADUATION_PLANS, GRADUATION_PLANS);
    }

    public static final String V1 = "v1";
    public static final String V = "v";
    public static final String V1_0 = "v1.0";
    public static final String ID_PLACEHOLDER = "{id}";
    /** Main entry point for the SLI API ReSTful web service. */
    public static final String API_SERVER_PATH = "api/rest/v1";
    /** URL to check the validity of a SLI session. */
    public static final String SESSION_CHECK_PATH = "system/session/check";
}
