package org.slc.sli.api.client;


/**
 * Enumeration of available entity types in SLI. Entities are currently mapped to
 * specific resources in the ReSTful API.
 * 
 * @author asaarela
 */
public enum EntityType {
    TARGETS("targets"),
    SECTIONS("sections"),
    STUDENT_SECTION_ASSOCIATIONS("student-section-associations"),
    SCHOOLS("schools"),
    STUDENTS("students"),
    COURSES("courses"),
    EDUCATIONAL_ORGANIZATIONS("educationOrganizations"),
    EDUCATIONAL_ORGANIZATION_SCHOOL_ASSOCIATIONS("educationOrganization-school-associations"),
    EDUCATIONAL_ORGANIZATION_ASSOCOCATIONS("educationOrganization-associations"),
    HOME("home"),
    TEACHER_SECTION_ASSOCIATIONS("teacher-section-assocations"),
    TEACHER_SCHOOL_ASSOCIATIONS("teacher-school-associations"),
    COURSE_SECTION_ASSOCIATIONS("course-section-associations"),
    SECTION_SCHOOL_ASSOCIATIONS("section-school-associations"),
    STUDENT_ASSESSMENT_ASSOCIATIONS("student-assessment-associations"),
    ASSESSMENTS("assessments");
    
    final String path;
    
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
