package org.slc.sli.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Defines for resource names exposed by API.
 * 
 * 
 * @author kmyers
 *
 */
public final class ResourceNames {

    public static final String AGGREGATIONS = "aggregations";
    public static final String AGGREGATION_DEFINITIONS = "aggregationDefinitions";
    public static final String ASSESSMENTS = "assessments";
    public static final String ASSESSMENT_FAMILIES = "assessmentFamilies";
    public static final String ATTENDANCES = "attendances";
    public static final String BELL_SCHEDULES = "bellSchedules";
    public static final String COHORTS = "cohorts";
    public static final String COURSES = "courses";
    public static final String DISCIPLINE_INCIDENTS = "disciplineIncidents";
    public static final String EDUCATION_ORGANIZATIONS = "educationOrganizations";
    public static final String GRADEBOOK_ENTRIES = "gradebookEntries";
    public static final String PARENTS = "parents";
    public static final String PROGRAMS = "programs";
    public static final String SCHOOLS = "schools";
    public static final String SECTIONS = "sections";
    public static final String SESSIONS = "sessions";
    public static final String STAFF = "staff";
    public static final String STUDENTS = "students";
    public static final String STUDENT_SECTION_GRADEBOOK_ENTRIES = "studentSectionGradebookEntries";
    public static final String TEACHERS = "teachers";
    
    public static final String SCHOOL_SESSION_ASSOCIATIONS = "school-session-associations";
    public static final String SECTION_ASSESSMENT_ASSOCIATIONS = "section-assessment-associations";
    public static final String SESSION_COURSE_ASSOCIATIONS = "session-course-associations";
    public static final String STUDENT_ASSESSMENT_ASSOCIATIONS = "student-assessment-associations";
    public static final String STUDENT_SECTION_ASSOCIATIONS = "student-section-associations";
    public static final String STUDENT_SCHOOL_ASSOCIATIONS = "student-school-associations";
    public static final String TEACHER_SCHOOL_ASSOCIATIONS = "teacher-school-associations";
    public static final String TEACHER_SECTION_ASSOCIATIONS = "teacher-section-associations";
    public static final String STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS = "staff-educationOrganization-associations";
    public static final String EDUCATION_ORGANIZATION_ASSOCIATIONS = "educationOrganization-associations";
    public static final String COURSE_SECTION_ASSOCIATIONS = "course-section-associations";
    public static final String STUDENT_TRANSCRIPT_ASSOCIATIONS = "studentTranscriptAssociations";
    

    
    public static final Map<String, String> ENTITY_RESOURCE_NAME_MAPPING = new HashMap<String, String>();
    

    /*
     * This map indicates how to display a link to a particular resource. This map assumes
     * that the destination is a single entity and not a collection.
     */
    public static final Map<String, String> SINGULAR_LINK_NAMES = new HashMap<String, String>();
    static {
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, "getStudentSchoolAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, "getTeacherSchoolAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, "getTeacherSectionAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS, "getSchoolSessionAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS, "getSectionAssessmentAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.SESSION_COURSE_ASSOCIATIONS, "getSessionCourseAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, "getStudentAssessmentAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "getStudentSectionAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "getStaffEducationOrganizationAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.EDUCATION_ORGANIZATION_ASSOCIATIONS, "getEducationOrganization");
        SINGULAR_LINK_NAMES.put(ResourceNames.COURSE_SECTION_ASSOCIATIONS, "getCourseSectionAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "getStudentTranscriptAssociation");
        
        SINGULAR_LINK_NAMES.put(ResourceNames.ASSESSMENTS, "getAssessment");
        SINGULAR_LINK_NAMES.put(ResourceNames.ATTENDANCES, "getAttendance");
        SINGULAR_LINK_NAMES.put(ResourceNames.BELL_SCHEDULES, "getBellSchedule");
        SINGULAR_LINK_NAMES.put(ResourceNames.COHORTS, "getCohort");
        SINGULAR_LINK_NAMES.put(ResourceNames.COURSES, "getCourse");
        SINGULAR_LINK_NAMES.put(ResourceNames.DISCIPLINE_INCIDENTS, "getDisciplineIncident");
        SINGULAR_LINK_NAMES.put(ResourceNames.EDUCATION_ORGANIZATIONS, "getEducationOrganization");
        SINGULAR_LINK_NAMES.put(ResourceNames.GRADEBOOK_ENTRIES, "getGradebookEntry");
        SINGULAR_LINK_NAMES.put(ResourceNames.PARENTS, "getParent");
        SINGULAR_LINK_NAMES.put(ResourceNames.PROGRAMS, "getProgram");
        SINGULAR_LINK_NAMES.put(ResourceNames.SECTIONS, "getSection");
        SINGULAR_LINK_NAMES.put(ResourceNames.SESSIONS, "getSession");
        SINGULAR_LINK_NAMES.put(ResourceNames.SCHOOLS, "getSchool");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENTS, "getStudent");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_SECTION_GRADEBOOK_ENTRIES, "getStudentSectionGradebookEntry");
        SINGULAR_LINK_NAMES.put(ResourceNames.TEACHERS, "getTeacher");
        SINGULAR_LINK_NAMES.put(ResourceNames.STAFF, "getStaff");
    }
    
    /*
     * This map indicates how to display a link to a particular resource. This map assumes
     * that the destination can be multiple entities.
     */
    public static final Map<String, String> PLURAL_LINK_NAMES = new HashMap<String, String>();
    static {
        for (Entry<String, String> e : SINGULAR_LINK_NAMES.entrySet()) {
            if (e.getKey().equals(ResourceNames.STAFF)) {
                PLURAL_LINK_NAMES.put(e.getKey(), e.getValue());
            } else if (e.getKey().endsWith("y")) { 
                PLURAL_LINK_NAMES.put(e.getKey(), e.getValue().substring(e.getValue().length() - 1) + "ies");
            } else {
                PLURAL_LINK_NAMES.put(e.getKey(), e.getValue() + "s");
            }
        }
    }
}
