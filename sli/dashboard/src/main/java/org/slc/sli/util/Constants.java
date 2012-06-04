package org.slc.sli.util;

/**
 * This class is for constants that are used in multiple places throughout the
 * application.
 * Constants used only in one class should be kept in that class.
 * 
 * @author dwu
 * 
 */
public final class Constants {
    // API related URLs
    public static final String API_PREFIX = "api/rest";
    
    public static final String SESSION_CHECK_PREFIX = "api/rest/system/session/check";
    
    public static final String PROGRAM_ELL = "limitedEnglishProficiency";
    public static final String PROGRAM_FRE = "schoolFoodServicesEligibility";
    
    /*
     * \"name\": \"limitedEnglishProficiency\",
     * \"name\": \"ELL_FORMER\",
     * \"name\": \"504\",
     * \"name\": \"504_FORMER\",
     * \"name\": \"IEP\",
     * \"name\": \"IEP_FORMER\",
     * \"name\": \"LEA\",
     * \"name\": \"RPT\",
     * \"name\": \"RED\",
     * \"name\": \"schoolFoodServiceEligibility\",
     */
    // view config strings - TODO: should these be changed to enums?
    public static final String VIEW_TYPE_STUDENT_LIST = "listOfStudents";
    public static final String VIEW_TYPE_STUDENT_PROFILE_PAGE = "studentProfilePage";
    public static final String FIELD_TYPE_ASSESSMENT = "assessment";
    public static final String FIELD_TYPE_STUDENT_INFO = "studentInfo";
    public static final String FIELD_LOZENGES_POSITION_FRONT = "pre";
    public static final String FIELD_LOZENGES_POSITION_BACK = "post";
    public static final String FIELD_TYPE_HISTORICAL_GRADE = "historicalGrade";
    public static final String FIELD_TYPE_HISTORICAL_COURSE = "historicalCourse";
    public static final String FIELD_TYPE_UNIT_GRADE = "unitTestGrade";
    public static final String FIELD_TYPE_CURRENT_TERM_GRADE = "currentTermGrade";
    public static final String CONFIG_ASSESSMENT_FILTER = "assessmentFilter";
    public static final String OVERALL_CONTAINER_PAGE = "overall_container";
    public static final String PAGE_TO_INCLUDE = "page_to_include";
    
    // model map keys
    public static final String MM_KEY_LOZENGE_CONFIG = "lozengeConfigs";
    public static final String MM_KEY_VIEW_CONFIG = "viewConfig";
    public static final String MM_KEY_VIEW_CONFIGS = "viewConfigs";
    public static final String MM_VIEW_DATA_CONFIG_JSON = "viewDataConfig";
    public static final String MM_KEY_LAYOUT = "layout";
    public static final String MM_KEY_DATA = "data";
    public static final String MM_KEY_ASSESSMENTS = "assessments";
    public static final String MM_KEY_STUDENTS = "students";
    public static final String MM_KEY_WIDGET_FACTORY = "widgetFactory";
    public static final String MM_KEY_CONSTANTS = "constants";
    public static final String MM_KEY_ATTENDANCE = "attendances";
    public static final String MM_KEY_HISTORICAL = "historicaldata";
    public static final String MM_KEY_GRADEBOOK_ENTRY_DATA = "gradebookEntryData";
    public static final String MM_KEY_LOGGER = "logger";
    public static final String MM_COMPONENT_ID = "componentId";
    public static final String MM_ENTITY_ID = "entityId";
    
    // entity attributes
    public static final String ATTR_ROOT = "root";
    public static final String ATTR_AUTHENTICATED = "authenticated";
    public static final String ATTR_AUTH_FULL_NAME = "full_name";
    public static final String ATTR_AUTH_ROLES = "sliRoles";
    public static final String ATTR_SELECTED_POPULATION = "selectedPopulation";
    public static final String ATTR_COURSES = "courses";
    public static final String ATTR_SCHOOL_ID = "schoolId";
    public static final String ATTR_SCHOOLS = "schools";
    public static final String ATTR_SECTION = "section";
    public static final String ATTR_SECTIONS = "sections";
    public static final String ATTR_COURSE = "course";
    public static final String ATTR_SECTION_NAME = "sectionName";
    public static final String ATTR_LINK = "link";
    public static final String ATTR_HREF = "href";
    public static final String ATTR_SELF = "self";
    public static final String ATTR_REL = "rel";
    public static final String ATTR_LINKS = "links";
    public static final String ATTR_STUDENT_UIDS = "studentUIDs";
    public static final String ATTR_ID = "id";
    public static final String ATTR_CUSTOM_DATA = "customData";
    public static final String ATTR_ASSESSMENT_NAME = "assessmentName";
    public static final String ATTR_ASSESSMENT_ID = "assessmentId";
    public static final String ATTR_ADMIN_DATE = "administrationDate";
    public static final String ATTR_ASSESSMENT_PERF_LEVEL = "assessmentPerformanceLevel";
    public static final String ATTR_ASSESSMENT_REPORTING_METHOD = "assessmentReportingMethod";
    public static final String ATTR_ASSESSMENT_TITLE = "assessmentTitle";
    public static final String ATTR_ASSESSMENT_FAMILY_HIERARCHY_NAME = "assessmentFamilyHierarchyName";
    public static final String ATTR_ASSESSMENT_PERIOD_DESCRIPTOR = "assessmentPeriodDescriptor";
    public static final String ATTR_PERFORMANCE_LEVEL_DESCRIPTOR = "performanceLevelDescriptors";
    public static final String ATTR_CODE_VALUE = "codeValue";
    public static final String ATTR_ASSESSMENT_PERIOD_BEGIN_DATE = "beginDate";
    public static final String ATTR_ASSESSMENT_PERIOD_END_DATE = "endDate";
    public static final String ATTR_STUDENT_OBJECTIVE_ASSESSMENTS = "studentObjectiveAssessments";
    public static final String ATTR_OBJECTIVE_ASSESSMENT = "objectiveAssessment";
    public static final String ATTR_IDENTIFICATIONCODE = "identificationCode";
    public static final String ATTR_DESCRIPTION = "description";
    public static final String ATTR_RESULT = "result";
    public static final String ATTR_SCORE_RESULTS = "scoreResults";
    public static final String ATTR_MINIMUM_SCORE = "minimumScore";
    public static final String ATTR_MAXIMUM_SCORE = "maximumScore";
    public static final String ATTR_STUDENT_ID = "studentId";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_FIRST_NAME = "firstName";
    public static final String ATTR_LAST_SURNAME = "lastSurname";
    public static final String ATTR_MIDDLE_NAME = "middleName";
    public static final String ATTR_FULL_NAME = "fullName";
    public static final String ATTR_PROGRAMS = "programs";
    public static final String ATTR_YEAR = "year";
    public static final String ATTR_SCALE_SCORE = "Scale score";
    public static final String ATTR_RAW_SCORE = "Raw score";
    public static final String ATTR_MASTERY_LEVEL = "Mastery level";
    public static final String ATTR_PERF_LEVEL = "perfLevel";
    public static final String ATTR_PERCENTILE = "Percentile";
    public static final String ATTR_LEXILE_SCORE = "Other";
    public static final String ATTR_ED_ORGS = "educationOrganizations";
    public static final String ATTR_ED_ORG_ID = "educationOrganizationId";
    public static final String ATTR_ED_ORG_CHILD_ID = "educationOrganizationChildId";
    public static final String ATTR_ED_ORG_PARENT_ID = "educationOrganizationParentId";
    public static final String ATTR_ORG_CATEGORIES = "organizationCategories";
    public static final String ATTR_NAME_OF_INST = "nameOfInstitution";
    public static final String ATTR_ASSESSMENT_FAMILY = "assessmentFamily";
    public static final String ATTR_ASSESSMENTS = "assessments";
    public static final String ATTR_COHORT_YEAR = "cohortYear";
    public static final String ATTR_UNIQUE_SECTION_CODE = "uniqueSectionCode";
    public static final String ATTR_STUDENT_ASSESSMENTS = "studentAssessments";
    public static final String ATTR_COURSE_ID = "courseId";
    public static final String ATTR_STUDENT_ATTENDANCES = "attendances";
    public static final String ATTR_STUDENT_ATTENDANCES_1 = "attendances.1";
    public static final String ATTR_PARENT_EDORG = "parentEducationAgencyReference";
    public static final String ATTR_TEACHER_ID = "teacherId";
    public static final String ATTR_TEACHER_NAME = "teacherName";
    public static final String ATTR_SECTION_ID = "sectionId";
    public static final String ATTR_SUBJECTAREA = "subjectArea";
    public static final String ATTR_COURSE_TITLE = "courseTitle";
    public static final String ATTR_SCHOOL_YEAR = "schoolYear";
    public static final String ATTR_SESSION_ID = "sessionId";
    public static final String ATTR_FINAL_LETTER_GRADE = "finalLetterGradeEarned";
    public static final String ATTR_FINAL_NUMERIC_GRADE = "finalNumericGradeEarned";
    public static final String ATTR_SESSIONS = "sessions";
    public static final String ATTR_SESSION = "session";
    public static final String ATTR_CLASSROOM_POSITION = "classroomPosition";
    public static final String ATTR_TERM = "term";
    public static final String ATTR_NUMERIC_GRADE_EARNED = "numericGradeEarned";
    public static final String ATTR_DATE_FULFILLED = "dateFulfilled";
    public static final String ATTR_HOMEROOM_INDICATOR = "homeroomIndicator";
    public static final String ATTR_PERSONAL_TITLE_PREFIX = "personalTitlePrefix";
    public static final String ATTR_GRADEBOOK = "gradebook";
    public static final String ATTR_GRADEBOOK_ENTRY_ID = "gradebookEntryId";
    public static final String ATTR_GRADEBOOK_ENTRY_TYPE = "gradebookEntryType";
    public static final String ATTR_GRADEBOOK_ENTRIES = "gradebookEntries";
    public static final String ATTR_GRADE_LEVEL = "gradeLevel";
    public static final String ATTR_GRADE_LEVEL_WHEN_TAKEN = "gradeLevelWhenTaken";
    public static final String ATTR_GRADE_LEVELS = "gradeLevels";
    public static final String ATTR_STUDENT_ENROLLMENT = "studentEnrollment";
    public static final String ATTR_SCHOOL = "school";
    public static final String ATTR_HEADER_STRING = "headerString";
    public static final String ATTR_FOOTER_STRING = "footerString";
    public static final String ATTR_OPTIONAL_FIELDS = "optionalFields";
    public static final String ATTR_GRADEBOOK_VIEW = "gradebookView";
    public static final String ATTR_STUDENT_SECTION_GRADEBOOK = "studentGradebookEntries";
    public static final String ATTR_ATTENDANCE_DATE = "date";
    public static final String ATTR_ATTENDANCE_EVENT_CATEGORY = "event";
    public static final String ATTR_ABSENCE_COUNT = "absenceCount";
    public static final String ATTR_TARDY_COUNT = "tardyCount";
    public static final String ATTR_ATTENDANCE_RATE = "attendanceRate";
    public static final String ATTR_TARDY_RATE = "tardyRate";
    public static final String ATTR_STUDENTS = "students";
    public static final String ATTR_TRANSCRIPT = "transcript";
    public static final String ATTR_STUDENT_TRANSCRIPT_ASSOC = "courseTranscripts";
    public static final String ATTR_STUDENT_SECTION_ASSOC = "studentSectionAssociations";
    public static final String ATTR_INTERNAL_METADATA = "meta";
    public static final String ATTR_GRADE_LEVEL_ASSESSED = "gradeLevelAssessed";
    public static final String ATTR_STUDENT_GRADEBOOK_ENTRIES = "studentGradebookEntries";
    public static final String ATTR_LETTER_GRADE_EARNED = "letterGradeEarned";
    
    public static final String ATTR_CREDENTIALS_CODE_FOR_IT_ADMIN = "IT Admin";

    public static final String ATTR_CREDENTIALS_LIST_ATTRIBUTE = "credentials";
    public static final String ATTR_CREDENTIAL_FIELD_ATTRIBUTE = "credentialField";
    public static final String ATTR_CREDENTIAL_CODE_ATTRIBUTE = "codeValue";
    
    public static final String ATTR_CUMULATIVE_GPA = "cumulativeGradePointAverage";
    public static final String ATTR_STUDENT_ACADEMIC_RECORDS = "studentAcademicRecords";
    public static final String ATTR_SESSION_BEGIN_DATE = "beginDate";
    public static final String ATTR_SESSION_END_DATE = "endDate";
    public static final String ATTR_DATE_FORMAT = "yyyy-mm-dd";
    
    public static final String ATTR_GRADE_EARNED = "gradeEarned";
    public static final String ATTR_SEARCH_STRING = "searchString";
    public static final String ATTR_NUM_RESULTS = "numResults";
    public static final String ATTR_SEARCH_PAGE_NUM = "searchPageNum";
    public static final String ATTR_SEARCH_PAGE_SIZE = "searchPageSize";
    public static final String ATTR_SEARCH_MAX_PAGE_NUM = "searchMaxPageNum";
    public static final String ATTR_ERROR_HEADING = "errorHeading";
    public static final String ATTR_ERROR_CONTENT = "errorContent";
    public static final String ATTR_ERROR_DETAILS_ENABLED = "debugEnabled";
    public static final String ATTR_ERROR_DETAILS = "errorDetails";
    public static final String ATTR_STUDENT_PARENT_ASSOCIATION = "studentParentAssociation";
    public static final String ATTR_RELATION = "relation";
    public static final String ATTR_CONTACT_PRIORITY = "contactPriority";
    public static final String ATTR_PRIMARY_CONTACT_STATUS = "primaryContactStatus";
    
    // Teacher constants
    public static final String TEACHER_OF_RECORD = "Teacher of Record";
    
    public static final String HISTORICAL_DATA_VIEW = "Historical Data";
    public static final String MIDDLE_SCHOOL_VIEW = "Middle School ELA View";
    
    public static final String ATTR_NAME_WITH_LINK = "name_w_link";
    
    public static final String PARAM_INCLUDE_FIELDS = "includeFields";
    
    // Program Participation Constants
    public static final String SHOW_ELL_LOZENGE = "Limited";
    
    // AddressType Constants
    public static final String TYPE_ADDRESS_HOME = "Home";
    public static final String TYPE_ADDRESS_PHYSICAL = "Physical";
    public static final String TYPE_ADDRESS_BILLING = "Billing";
    public static final String TYPE_ADDRESS_MAILING = "Mailing";
    public static final String TYPE_ADDRESS_OTHER = "Other";
    public static final String TYPE_ADDRESS_TEMPORARY = "Temporary";
    public static final String TYPE_ADDRESS_WORK = "Work";
    
    // TelephoneType Constants
    public static final String TYPE_TELEPHONE_HOME = "Home";
    public static final String TYPE_TELEPHONE_WORK = "Work";
    public static final String TYPE_TELEPHONE_MOBILE = "Mobile";
    public static final String TYPE_TELEPHONE_EMERGENCY_1 = "Emergency 1";
    public static final String TYPE_TELEPHONE_EMERGENCY_2 = "Emergency 2";
    public static final String TYPE_TELEPHONE_FAX = "Fax";
    public static final String TYPE_TELEPHONE_OTHER = "Other";
    public static final String TYPE_TELEPHONE_UNLISTED = "Unlisted";
    
    // EmailType Constants
    public static final String TYPE_EMAIL_HOME_PERSONAL = "Home/Personal";
    public static final String TYPE_EMAIL_WORK = "Work";
    public static final String TYPE_EMAIL_ORGANIZATION = "Organization";
    public static final String TYPE_EMAIL_OTHER = "Other";
    
    public static final String CONTEXT_ROOT_PATH = "CONTEXT_ROOT_PATH";
    public static final String CONTEXT_PREVIOUS_PATH = "CONTEXT_PREVIOUS_PATH";
    
    // extra elements added by API
    public static final String METADATA = "metaData";
    public static final String EXTERNAL_ID = "externalId";
    
    // Grades for Sections Constants
    public static final String SECTION_LETTER_GRADE = "letterGrade";
    public static final String SECTION_COURSE = "courseTitle";
    
    // Roles
    public static final String ROLE_IT_ADMINISTRATOR = "IT Administrator";
    public static final String ROLE_EDUCATOR = "Educator";
    public static final String ROLE_LEADER = "Leader";
    
    // Ed-org types
    public static final String ATTR_ED_ORG = "edOrg";
    public static final String STATE_EDUCATION_AGENCY = "State Education Agency";
    public static final String LOCAL_EDUCATION_AGENCY = "Local Education Agency";
    
    public static final int MAX_RESULTS = 0;
    public static final String LIMIT = "limit";
    public static final String ATTR_PARENTS = "parents";

    /**
     * Contains the possible values for FRE participation
     */
    public static enum FREParticipation {
        FREE("Free"), REDUCED_PRICE("Reduced Price");
        
        private final String value;
        
        FREParticipation(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
    }
    
}
