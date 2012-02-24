package org.slc.sli.api.resources.v1;

import java.util.HashMap;
import java.util.Map;

import org.slc.sli.api.config.ResourceNames;

/**
 * Constants used in URI path requests.
 * 
 * 
 * @author kmyers
 *
 */
public class PathConstants {

    public static final String STUDENT_SCHOOL_ASSOCIATIONS = "studentSchoolAssociations";
    public static final String TEACHER_SCHOOL_ASSOCIATIONS = "teacherSchoolAssociations";
    public static final String SCHOOL_EDUCATION_ORGANIZATION_ASSOCIATIONS = "educationOrganizationSchoolAssociations";
    public static final String TEACHER_SECTION_ASSOCIATIONS = "teacherSectionAssociations";    
    public static final String SCHOOL_SESSION_ASSOCIATIONS = "schoolSessionAssociations";
    public static final String SECTION_ASSESSMENT_ASSOCIATIONS = "sectionAssessmentAssociations";
    public static final String SESSION_COURSE_ASSOCIATIONS = "sessionCourseAssociations";
    public static final String STUDENT_ASSESSMENT_ASSOCIATIONS = "studentAssessmentAssociations";
    public static final String STUDENT_SECTION_ASSOCIATIONS = "studentSectionAssociations";
    public static final String STAFF_EDUCATIONORGANIZATION_ASSOCIATIONS = "staffEducationOrganizationAssociations";
    public static final String EDUCATION_ORGANIZATION_ASSOCIATIONS = "educationOrganizationAssociations";
    public static final String COURSE_SECTION_ASSOCIATIONS = "courseSectionAssociations";
    
    public static final String ASSESSMENTS = "assessments";
    public static final String BELL_SCHEDULES = "bellSchedules";
    public static final String COHORTS = "cohorts";
    public static final String COURSES = "courses";
    public static final String DISCIPLINE_INCIDENTS = "disciplineIncidents";
    public static final String EDUCATION_ORGANIZATIONS = "educationOrganizations";
    public static final String PARENTS = "parents";
    public static final String PROGRAMS = "programs";
    public static final String SECTIONS = "sections";
    public static final String SESSIONS = "sessions";
    public static final String SCHOOLS = "schools";
    public static final String STUDENTS = "students";  
    public static final String TEACHERS = "teachers";
    
    /*
     * This map should go away when we switch basic definition store association names to camel case.
     * This map is used when building the links to be returned. When building links we need to look into definition store to get
     * the resource name. This resource name is then mapped to the new camel case name using this map.
     */
    public static final Map<String, String> TEMP_MAP = new HashMap<String, String>();
    static {
        TEMP_MAP.put(ResourceNames.STUDENT_SCHOOL_ASSOCIATIONS, STUDENT_SCHOOL_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.TEACHER_SCHOOL_ASSOCIATIONS, TEACHER_SCHOOL_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.EDUCATION_ORGANIZATION_SCHOOL_ASSOCIATIONS, SCHOOL_EDUCATION_ORGANIZATION_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.TEACHER_SECTION_ASSOCIATIONS, TEACHER_SECTION_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.SCHOOL_SESSION_ASSOCIATIONS, SCHOOL_SESSION_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.SECTION_ASSESSMENT_ASSOCIATIONS, SECTION_ASSESSMENT_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.SESSION_COURSE_ASSOCIATIONS, SESSION_COURSE_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, STUDENT_ASSESSMENT_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, STUDENT_SECTION_ASSOCIATIONS);
        
        TEMP_MAP.put(ResourceNames.STAFF_EDUCATIONORGANIZATION_ASSOCIATIONS, STAFF_EDUCATIONORGANIZATION_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.EDUCATION_ORGANIZATION_ASSOCIATIONS, EDUCATION_ORGANIZATION_ASSOCIATIONS);
        TEMP_MAP.put(ResourceNames.COURSE_SECTION_ASSOCIATIONS, COURSE_SECTION_ASSOCIATIONS);
    }

    public static final String V1 = "v1";
}
