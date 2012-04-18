package org.slc.sli.common.constants;

/**
 *
 * Enumeration of available entity types in SLI. Entities are currently mapped to
 * specific resources in the API.
 *
 * @author asaarela
 */
public enum EntityType {
    /*
     * Note: this is overly complex for what we're doing here but we're inconsistent in our
     * naming conventions between entity type name, ReST path, and entity type reported by the API
     * in JSON. If these become reconciled, this enum becomes simple.
     */

    /** resource for security session check */
    SECURITY_SESSION_CHECK("securitySessionCheck", "system/session/check"),

    /** resource for security session logout */
    SECURITY_SESSION_LOGOUT("securitySessionLogout", "system/session/logout"),

    /** resource for security session check */
    SECURITY_SESSION_DEBUG("securitySessionDebug", "system/session/debug"),

    /** association destinations, the exact content depends on context. */
    TARGETS(null, "targets"),

    /** sections that define the classes available during a session. */
    SECTIONS("section", "sections"),

    /** Association between students and their school(s). */
    STUDENT_SCHOOL_ASSOCIATIONS("studentSchoolAssociation", "studentSchoolAssociations"),

    /** Association between students and their sections. */
    STUDENT_SECTION_ASSOCIATIONS("studentSectionAssociation", "studentSectionAssociations"),

    /** School information */
    SCHOOLS("school", "schools"),

    /** staff information */
    STAFFS("staff", "staffs"),

    /** Student information */
    STUDENTS("student", "students"),

    /** Course information */
    COURSES("course", "courses"),

    /** Educational organization. */
    EDUCATIONAL_ORGANIZATIONS("educationOrganization", "educationOrganizations"),

    /** Association between a school and an educational organization */
    EDUCATIONAL_ORGANIZATION_SCHOOL_ASSOCIATIONS("educationOrganizationSchoolAssociation",
            "educationOrganizationSchoolAssociations"),

    /** Association between one educational organization and another */
    EDUCATIONAL_ORGANIZATION_ASSOCOCATIONS("educationOrganizationAssociation", "educationOrganizationAssociations"),

    /**
     * Resource home, exact location dependent on context. For example, a teacher has a different
     * home resource than an administrator.
     */
    HOME(null, "home"),

    /** Mapping of a teacher to one or more sections */
    TEACHER_SECTION_ASSOCIATIONS("teacherSectionAssociation", "teacherSectionAssociations"),

    /** Mapping of a teacher to one more more schools in which they teach. */
    TEACHER_SCHOOL_ASSOCIATIONS("teacherSchoolAssociation", "teacherSchoolAssociations"),

    /** Mapping of a course to one more more sections that offer the course. */
    COURSE_SECTION_ASSOCIATIONS("courseSectionAssociation", "courseSectionAssociations"),

    /** Mapping of a school to available sections. */
    SECTION_SCHOOL_ASSOCIATIONS("sectionSchoolAssociation", "sectionSchoolAssociations"),

    /** Mapping of a student to his or her assessments. */
    STUDENT_ASSESSMENT_ASSOCIATIONS("studentAssessmentAssociation", "studentAssessmentAssociations"),

    /** Assessment information. */
    ASSESSMENTS("assessment", "assessments"),

    /** Teacher information. */
    TEACHERS("teacher", "teachers"),

    /** DisciplineIncident information. */
    DISCIPLINE_INCIDENTS("disciplineIncident", "disciplineIncidents"),

    /** disciplineAction information. */
    DISCIPLINE_ACTIONS("disciplineAction", "disciplineActions"),

    /** Association between students and their disciplineIncidents. */
    STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS("studentDisciplineIncidentAssociation",
            "studentDisciplineIncidentAssociations"),

    /** Program information */
    PROGRAMS("program", "programs"),

    /** Association between students and programs */
    STUDENT_PROGRAM_ASSOCIATIONS("studentProgramAssociation", "studentProgramAssociations"),

    /** Association between staffs and programs */
    STAFF_PROGRAM_ASSOCIATIONS("staffProgramAssociation", "studentProgramAssociations"),

    /** Program information */
    COHORTS("cohort", "cohorts"),

    /** Association between students and cohort */
    STUDENT_COHORT_ASSOCIATIONS("studentCohortAssociation", "studentCohortAssociations"),

    /** Association between staffs and cohorts */
    STAFF_COHORT_ASSOCIATIONS("staffCohortAssociation", "studentCohortAssociations"),

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
     * Find a matching enum given the API type.
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