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


package org.slc.sli.dashboard.client;

/**
 * Dashboard API url constants.
 *
 *@deprecated use org.slc.sli.api.constants.PathConstants instead
 */
@Deprecated
public class SDKConstants {

    // SDK entities
    public static final String HOME_ENTITY = "/home/";
    public static final String SESSIONS_ENTITY = "/sessions/";
    public static final String SECTIONS_ENTITY = "/sections/";
    public static final String EDORGS_ENTITY = "/educationOrganizations/";
    public static final String SCHOOLS_ENTITY = "/schools/";
    public static final String COURSES_ENTITY = "/courses/";
    public static final String STAFF_ENTITY = "/staff/";
    public static final String TEACHERS_ENTITY = "/teachers/";
    public static final String STUDENTS_ENTITY = "/students/";
    public static final String ATTENDANCES_ENTITY = "/attendances/";
    public static final String ASSESSMENTS_ENTITY = "/assessments/";
    public static final String ACADEMIC_RECORDS_ENTITY = "/studentAcademicRecords/";

    // SDK resources to append to base entities
    public static final String EDORGS = "/educationOrganizations";
    public static final String SECTIONS = "/sections";
    public static final String STUDENTS = "/students";
    public static final String PARENTS = "/parents";
    public static final String ATTENDANCES = "/attendances";
    public static final String CUSTOM_DATA = "/custom";
    public static final String STUDENT_GRADEBOOK_ENTRY = "/studentGradebookEntries";

    // SDK associations to append to base entities
    public static final String STAFF_EDORG_ASSIGNMENT_ASSOC = "/staffEducationOrgAssignmentAssociations";
    public static final String STUDENT_SCHOOL_ASSOC = "/studentSchoolAssociations";
    public static final String STUDENT_PARENT_ASSOC = "/studentParentAssociations";
    public static final String STUDENT_SECTION_ASSOC = "/studentSectionAssociations";
    public static final String TEACHER_SECTION_ASSOC = "/teacherSectionAssociations";
    public static final String TEACHER_SCHOOL_ASSOC = "/teacherSchoolAssociations";
    public static final String STUDENT_ASSESSMENTS = "/studentAssessments";
    public static final String COURSE_TRANSCRIPTS = "/courseTranscripts";
    public static final String STUDENT_ACADEMIC_RECORD_ASSOC = "/studentAcademicRecords";
    public static final String COURSE_OFFERINGS = "/courseOfferings/";

    // SDK query parameters
    public static final String PARAM_PAGE_SIZE = "limit";
    public static final String PARAM_PAGE_NUMBER = "offset";
    public static final String PARAM_SORT_BY = "sortBy";
    public static final String PARAM_SORT_ORDER = "sortOrder";
    public static final String PARAM_SORT_ORDER_ASCENDING = "ascending";
    public static final String PARAM_SORT_ORDER_DESCENDING = "descending";
    public static final String PARAM_EVENT_DATE = "eventDate";
    public static final String PARAM_ENTRY_DATE = "entryDate";
    public static final String PARAM_STUDENT_ID = "studentId";
    public static final String PARAM_FIRST_NAME = "name.firstName";
    public static final String PARAM_LAST_NAME = "name.lastSurname";
    public static final String PARAM_SECTION_ID = "sectionId";
    //public static final String PARAM_OPTIONAL_FIELDS = "optionalFields";
    public static final String PARAM_OPTIONAL_FIELDS = "views";
    public static final String PARAM_SCHOOL_ID = "schoolId";


    // SDK link names
    public static final String ED_ORG_LINK = "getEducationOrganization";
    public static final String SCHOOL_LINK = "getSchool";
    public static final String STUDENT_SCHOOL_ASSOCIATIONS_LINK = "getStudentSchoolAssociations";

    // SDK attributes
    public static final String EDORG_SLI_ID_ATTRIBUTE = "edOrgSliId";
    public static final String EDORG_ATTRIBUTE = "edOrg";

}
