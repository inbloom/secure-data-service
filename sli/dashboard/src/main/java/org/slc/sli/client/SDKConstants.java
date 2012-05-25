package org.slc.sli.client;

/**
 * Dashboard API url constants.
 *
 */
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
    public static final String ATTENDANCES = "/attendances";
    public static final String CUSTOM_DATA = "/custom";
    
    // SDK associations to append to base entities
    public static final String STAFF_EDORG_ASSIGNMENT_ASSOC = "/staffEducationOrgAssignmentAssociations";
    public static final String STUDENT_SCHOOL_ASSOC = "/studentSchoolAssociations";
    public static final String STUDENT_SECTION_ASSOC = "/studentSectionAssociations";
    public static final String TEACHER_SECTION_ASSOC = "/teacherSectionAssociations";
    public static final String STUDENT_ASSMT_ASSOC = "/studentAssessmentAssociations";
    public static final String STUDENT_GRADEBOOK_ASSOC = "/studentSectionGradebookEntries";
    public static final String STUDENT_TRANSCRIPT_ASSOC = "/studentTranscriptAssociations";
    public static final String STUDENT_ACADEMIC_RECORD_ASSOC = "/studentAcademicRecords";
    
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
    public static final String PARAM_OPTIONAL_FIELDS = "optionalFields";
    
    // SDK link names
    public static final String ED_ORG_LINK = "getEducationOrganization";
    public static final String SCHOOL_LINK = "getSchool";
    public static final String STUDENT_SCHOOL_ASSOCIATIONS_LINK = "getStudentSchoolAssociations";
    
    // SDK attributes
    public static final String EDORG_SLI_ID_ATTRIBUTE = "edOrgSliId";
    public static final String EDORG_ATTRIBUTE = "edOrg";

}
