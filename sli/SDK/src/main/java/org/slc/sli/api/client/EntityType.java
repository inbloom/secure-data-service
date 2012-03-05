package org.slc.sli.api.client;


/**
 * Enumeration of available entity types in SLI. Entities are currently mapped to
 * specific resources in the ReSTful API.
 * 
 * @author asaarela
 */
public enum EntityType {
    /**
     * Note: this is overly complex for what we're doing here but we're inconsistent in our
     * naming conventions between entity type name, ReST path, and entity type reported by the API
     * in JSON. If these become reconciled, this enum becomes simple.
     */
    
    /** association destinations, the exact content depends on context. */
    TARGETS(null, "targets"),
    
    /** sections that define the classes available during a session. */
    SECTIONS("section", "sections"),
    
    /** Association between students and their school(s). */
    STUDENT_SCHOOL_ASSOCIATIONS("studentSchoolAssociation", "student-school-associations"),
    
    /** Association between students and their sections. */
    STUDENT_SECTION_ASSOCIATIONS("studentSectionAssociation", "student-section-associations"),
    
    /** School information */
    SCHOOLS("school", "schools"),
    
    /** Student information */
    STUDENTS("student", "students"),
    
    /** Course information */
    COURSES("course", "courses"),
    
    /** Educational organization. */
    EDUCATIONAL_ORGANIZATIONS("educationOrganization", "education-organizations"),
    
    /** Association between a school and an educational organization */
    EDUCATIONAL_ORGANIZATION_SCHOOL_ASSOCIATIONS("educationOrganizationSchoolAssociation",
            "educationOrganization-school-associations"),
    
    /** Association between on educational organization and another */
    EDUCATIONAL_ORGANIZATION_ASSOCOCATIONS("educationOrganizationAssociation", "educationOrganization-associations"),
    
    /**
     * Resource home, exact location dependent on context. For example, a teacher has a different
     * home resource than an administrator.
     */
    HOME(null, "home"),
    
    /** Mapping of a teacher to one or more sections */
    TEACHER_SECTION_ASSOCIATIONS("teacherSectionAssociation", "teacher-section-assocations"),
    
    /** Mapping of a teacher to one more more schools in which they teach. */
    TEACHER_SCHOOL_ASSOCIATIONS("teacherSchoolAssociation", "teacher-school-associations"),
    
    /** Mapping of a course to one more more sections that offer the course. */
    COURSE_SECTION_ASSOCIATIONS("courseSectionAssociation", "course-section-associations"),
    
    /** Mapping of a school to available sections. */
    SECTION_SCHOOL_ASSOCIATIONS("sectionSchoolAssociation", "section-school-associations"),
    
    /** Mapping of a student to his or her assessments. */
    STUDENT_ASSESSMENT_ASSOCIATIONS("studentAssessmentAssociation", "student-assessment-associations"),
    
    /** Assessment information. */
    ASSESSMENTS("assessment", "assessments"),
    
    /** Teacher information. */
    TEACHERS("teacher", "teachers"),
    
    /** Generic type used when no matching type is found */
    GENERIC("generic", null);
    
    private final String type;
    private final String resource;
    
    
    /**
     * Basic constructor
     * 
     * @param type
     *            Entity type as reported by the API.
     * @param path
     *            Resource name.
     */
    private EntityType(final String type, final String resource) {
        this.type = type;
        this.resource = resource;
    }
    
    /**
     * Get the type associated with the entity.
     * 
     * @return EntityType
     */
    public String getEntityType() {
        return type;
    }
    
    /**
     * Get the ReSTful URL fragment used to interact with the entity type.
     * 
     * @return String with the URL fragment.
     */
    public String getResource() {
        return resource;
    }
    
    @Override
    public String toString() {
        return type;
    }
    
    /**
     * Find a matching enum given the json type.
     * 
     * @param s
     *            String to match
     * @return matching EntityType or null if not found.
     */
    public static EntityType byType(final String s) {
        for (EntityType t : EntityType.values()) {
            if (t.type != null && t.type.equals(s)) {
                return t;
            }
        }
        return null;
    }
    
}
