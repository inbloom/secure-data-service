package org.slc.sli.client;

/**
 * Dashboard API url constants.
 *
 */
public class ClientConstants {

    // base urls
    static final String STAFF_URL = "/v1/staff/";
    static final String EDORGS_URL = "/v1/educationOrganizations/";
    static final String SCHOOLS_URL = "/v1/schools";
    public static final String SECTIONS_URL = "/v1/sections/";
    static final String STUDENTS_URL = "/v1/students/";
    static final String TEACHERS_URL = "/v1/teachers/";
    static final String HOME_URL = "/v1/home/";
    static final String ASSMT_URL = "/v1/assessments/";
    static final String SESSION_URL = "/v1/sessions/";
    static final String STUDENT_ASSMT_ASSOC_URL = "/v1/studentAssessmentAssociations/";
    static final String STUDENT_SECTION_GRADEBOOK = "/v1/studentSectionGradebookEntries";
    static final String STUDENT_ACADEMIC_RECORD_URL = "/v1/studentAcademicRecords";

    // resources to append to base urls
    static final String SDK_EDORGS_URL = "/educationOrganizations/";    
    static final String STAFF_EDORG_ASSOC = "/staffEducationOrgAssignmentAssociations/educationOrganizations";
    static final String ATTENDANCES = "/attendances";
    static final String STUDENT_SECTION_ASSOC = "/studentSectionAssociations";
    static final String TEACHER_SECTION_ASSOC = "/teacherSectionAssociations";
    static final String STUDENT_ASSMT_ASSOC = "/studentAssessmentAssociations";
    static final String SECTIONS = "/sections";
    static final String STUDENTS = "/students";
    static final String STUDENT_TRANSCRIPT_ASSOC = "/studentTranscriptAssociations";
    static final String CUSTOM_DATA = "/custom";

    // link names
    static final String ED_ORG_LINK = "getEducationOrganization";
    static final String SCHOOL_LINK = "getSchool";
    static final String STUDENT_SCHOOL_ASSOCIATIONS_LINK = "getStudentSchoolAssociations";
}
