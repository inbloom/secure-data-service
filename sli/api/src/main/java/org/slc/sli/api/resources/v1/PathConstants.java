package org.slc.sli.api.resources.v1;

/**
 * Constants used in URI path requests.
 * 
 * 
 * @author kmyers
 *
 */
class PathConstants {
    
    /**
     * Path for school data
     */
    public static final String SCHOOLS = "schools";
    
    /**
     * Path for student data
     */
    public static final String STUDENTS = "students";
    
    public static final String ASSESSMENTS = "assessments";
    public static final String BELL_SCHEDULES = "bellSchedules";
    public static final String SECTIONS = "sections";
    public static final String SESSIONS = "sessions";
    public static final String COHORTS = "cohorts";
    public static final String DISCIPLINE_INCIDENTS = "disciplineIncidents";
    public static final String PARENTS = "parents";
    public static final String PROGRAMS = "programs";
    public static final String TEACHERS = "teachers";
    
    /**
     * Path for student school association data
     */
    public static final String STUDENT_SCHOOL_ASSOCIATIONS = "student-school-associations";
    
    /**
     * Path for teacher school association data
     */
    public static final String TEACHER_SCHOOL_ASSOCIATIONS = "teacher-school-associations";
    
    /**
     * The version identifier in the URI.
     */
    public static final String V1 = "v1";
}
