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

/**
 * Constants used in URI requests.
 *
 *
 * @author kmyers
 *
 */
public class ParameterConstants {

    /**
     * An indication not to start from the first result.
     */
    public static final String OFFSET = "offset";

    /**
     * Maximum number of results to display at one time.
     */
    public static final String LIMIT = "limit";

    /**
     * An indication not to start from the first result.
     */
    public static final String DEFAULT_OFFSET = "0";
    public static final int DEFAULT_OFFSET_INT = Integer.parseInt(DEFAULT_OFFSET);

    /**
     * Maximum number of results to display at one time.
     */
    public static final String DEFAULT_LIMIT = "50";
    public static final int DEFAULT_LIMIT_INT = Integer.parseInt(DEFAULT_LIMIT);

    /**
     * Number of links to traverse when presenting a high-level document.
     */
    public static final String EXPAND_DEPTH = "expandDepth";

    /**
     * Query parameter for fields to include.
     */
    public static final String SELECTOR = "selector";

    /**
     * Query parameter for fields to include.
     */
    public static final String INCLUDE_FIELDS = "includeFields";

    /**
     * Query parameter for fields to exclude.
     */
    public static final String EXCLUDE_FIELDS = "excludeFields";

    /**
     * Query parameter for sorting.
     */
    public static final String SORT_BY = "sortBy";

    /**
     * Query parameter for sort order.
     */
    public static final String SORT_ORDER = "sortOrder";

    /**
     * Query parameter for order ascending.
     */
    public static final String SORT_ASCENDING = "ascending";

    /**
     * Query parameter for order descending.
     */
    public static final String SORT_DESCENDING = "descending";

    public static final String HEADER_LINK = "Link";
    public static final String HEADER_TOTAL_COUNT = "TotalCount";


    /**
     * Query parameter for optional fields to include
     */
    public static final String OPTIONAL_FIELDS = "optionalFields";
    public static final String VIEWS = "views";

    /**
     * Query parameter for inclusion of custom entity.
     */
    public static final String INCLUDE_CUSTOM = "includeCustom";
    public static final String DEFAULT_INCLUDE_CUSTOM = "false";

    /**
     * Query parameter for inclusion of calculated values
     */
    public static final String INCLUDE_CALCULATED = "includeCalculatedValues";
    public static final String DEFAULT_INCLUDE_CALCULATED = "false";

    /**
     * Query parameter for inclusion of aggregate values
     */
    public static final String INCLUDE_AGGREGATES = "includeAggregates";
    public static final String DEFAULT_INCLUDE_AGGREGATES = "false";

    /**
     * Query parameter for date range filtering
     */
    public static final String SCHOOL_YEARS = "schoolYears";

    /**
     * Optional Fields
     */
    public static final String OPTIONAL_FIELD_ASSESSMENTS = "assessments";
    public static final String OPTIONAL_FIELD_ATTENDANCES = "attendances";
    public static final String OPTIONAL_FIELD_GRADEBOOK = "gradebook";
    public static final String OPTIONAL_FIELD_TRANSCRIPT = "transcript";
    public static final String OPTIONAL_FIELD_GRADE_LEVEL = "gradeLevel";

    /**
     * Entity/Association IDs
     */
    public static final String ASSESSMENT_ID = "assessmentId";
    public static final String COURSE_ID = "courseId";
    public static final String COMPETENCY_LEVEL_DESCRIPTOR_ID = "competencyLevelDescriptorId";
    public static final String COMPETENCY_LEVEL_DESCRIPTOR_TYPE_ID = "competencyLevelDescriptorTypeId";
    public static final String DISCIPLINE_INCIDENT_ID = "disciplineIncidentId";
    public static final String DISCIPLINE_ACTION_ID = "disciplineActionId";
    public static final String GRADEBOOK_ENTRY_ID = "gradebookEntryId";
    public static final String GRADUATION_PLAN_ID = "graduationPlanId";
    public static final String GRADING_PERIOD_ID = "gradingPeriodId";
    public static final String GRADING_PERIOD_REFERENCE = "gradingPeriodReference";
    public static final String PARENT_ID = "parentId";
    public static final String PROGRAM_ID = "programId";
    public static final String PROGRAM_REFERENCE = "programReference";
    public static final String SCHOOL_ID = "schoolId";
    public static final String SECTION_ID = "sectionId";
    public static final String SESSION_ID = "sessionId";
    public static final String STAFF_ID = "staffId";
    public static final String STAFF_REFERENCE = "staffReference";
    public static final String STUDENT_ID = "studentId";
    public static final String STUDENT_COMPETENCY_ID = "studentCompetencyId";
    public static final String STUDENT_COMPETENCY_OBJECTIVE_ID = "studentCompetencyObjectiveId";
    public static final String TEACHER_ID = "teacherId";
    public static final String STUDENT_GRADEBOOK_ENTRY_ID = "studentGradebookEntryId";
    public static final String STUDENT_ACADEMIC_RECORD_ID = "studentAcademicRecordId";
    public static final String COHORT_ID = "cohortId";
    public static final String EDUCATION_ORGANIZATION_ID = "educationOrganizationId";
    public static final String EDUCATION_ORGANIZATION_REFERENCE = "educationOrganizationReference";
    public static final String ATTENDANCE_ID = "attendanceId";
    public static final String LEARNING_STANDARD_ID = "learningStandardId";
    public static final String LEARNING_STANDARDS = "learningStandards";
    public static final String LEARNINGOBJECTIVE_ID = "learningObjectiveId";
    public static final String PARENT_LEARNING_OBJECTIVE = "parentLearningObjective";
    public static final String REPORT_CARD_ID = "reportCardId";
    public static final String SCHOOL_SESSION_ASSOCIATION_ID = "schoolSessionAssociationId";
    public static final String TEACHER_SCHOOL_ASSOCIATION_ID = "teacherSchoolAssociationId";
    public static final String TEACHER_SCHOOL_ASSOC_ID = "teacherSchoolAssociationId";
    public static final String TEACHER_SECTION_ASSOCIATION_ID = "teacherSectionAssociationId";
    public static final String STAFF_EDUCATION_ORGANIZATION_ID = "staffEducationOrganizationId";
    public static final String STUDENT_SECTION_ASSOCIATION_ID = "studentSectionAssociationId";
    public static final String STUDENT_SCHOOL_ASSOCIATION_ID = "studentSchoolAssociationId";
    public static final String COURSE_OFFERING_ID = "courseOfferingId";
    public static final String STUDENT_ASSESSMENT_ID = "studentAssessmentId";
    public static final String COURSE_TRANSCRIPT_ID = "courseTranscriptId";
    public static final String STUDENT_PARENT_ASSOCIATION_ID = "studentParentAssociationId";
    public static final String STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION_ID = "studentDisciplineIncidentAssociationId";
    public static final String STAFF_PROGRAM_ASSOCIATION_ID = "staffProgramAssociationId";
    public static final String STUDENT_PROGRAM_ASSOCIATION_ID = "studentProgramAssociationId";
    public static final String STUDENT_COHORT_ASSOCIATION_ID = "studentCohortAssociationId";
    public static final String STAFF_COHORT_ASSOCIATION_ID = "staffCohortAssociationId";
    public static final String USER_ACCOUNT_ID = "userAccountId";
    public static final String GRADE_ID = "gradeId";
    public static final String ID = "_id";
    public static final String END_DATE = "endDate";
    public static final String STUDENT_RECORD_ACCESS = "studentRecordAccess";
    public static final String PARENT_EDUCATION_AGENCY_REFERENCE = "parentEducationAgencyReference";
    public static final String EXIT_WITHDRAW_DATE = "exitWithdrawDate";
    public static final String STUDENT_UNIQUE_STATE_ID = "studentUniqueStateId";
    public static final String STAFF_UNIQUE_STATE_ID = "staffUniqueStateId";
    public static final String PARENT_UNIQUE_STATE_ID = "parentUniqueStateId";
    public static final String STATE_ORGANIZATION_ID = "stateOrganizationId";

	public static final String TEACHER_REFERENCE = "teacherReference";
}
