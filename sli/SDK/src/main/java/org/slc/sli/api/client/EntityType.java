package org.slc.sli.api.client;


/**
 * Enumeration of available entity types in SLI. Entities are currently mapped to
 * specific resources in the ReSTful API.
 * 
 * @author asaarela
 */
public enum EntityType {
    /** association destinations, the exact content depends on context. */
    TARGETS("targets"),
    /** sections that define the classes available during a session. */
    SECTIONS("sections"),
    /** Association between students and their sections. */
    STUDENT_SECTION_ASSOCIATIONS("student-section-associations"),
    /** School information */
    SCHOOLS("schools"),
    /** Student information */
    STUDENTS("students"),
    /** Course information */
    COURSES("courses"),
    /** Educational organization. */
    EDUCATIONAL_ORGANIZATIONS("educationOrganizations"),
    /** Association between a school and an educational organization */
    EDUCATIONAL_ORGANIZATION_SCHOOL_ASSOCIATIONS("educationOrganization-school-associations"),
    /** Association between on educational organization and another */
    EDUCATIONAL_ORGANIZATION_ASSOCOCATIONS("educationOrganization-associations"),
    /**
     * Resource home, exact location dependent on context. For example, a teacher has a different
     * home resource than an administrator.
     */
    HOME("home"),
    /** Mapping of a teacher to one or more sections */
    TEACHER_SECTION_ASSOCIATIONS("teacher-section-assocations"),
    /** Mapping of a teacher to one more more schools in which they teach. */
    TEACHER_SCHOOL_ASSOCIATIONS("teacher-school-associations"),
    /** Mapping of a course to one more more sections that offer the course. */
    COURSE_SECTION_ASSOCIATIONS("course-section-associations"),
    /** Mapping of a school to available sections. */
    SECTION_SCHOOL_ASSOCIATIONS("section-school-associations"),
    /** Mapping of a student to his or her assessments. */
    STUDENT_ASSESSMENT_ASSOCIATIONS("student-assessment-associations"),
    /** Assessment information. */
    ASSESSMENTS("assessments"),
    /** Teacher information. */
    TEACHERS("teachers");
    
    private final String path;
    
    /**
     * Basic constructor
     * @param path URL fragment used to interact with the entity type.
     */
    private EntityType(final String path) {
        this.path = path;
    }
    
    /**
     * Get the ReSTful URL fragment used to interact with the entity type.
     * 
     * @return String with the URL fragment.
     */
    public String getURL() {
        return path;
    }
}
