package org.slc.sli.client.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    public static final String COHORTS = "cohorts";
    public static final String COMPETENCY_LEVEL_DESCRIPTORS = "competencyLevelDescriptor";
    public static final String COMPETENCY_LEVEL_DESCRIPTOR_TYPES = "competencyLevelDescriptorTypes";
    public static final String COURSES = "courses";
    public static final String DISCIPLINE_INCIDENTS = "disciplineIncidents";
    public static final String DISCIPLINE_ACTIONS = "disciplineActions";
    public static final String EDUCATION_ORGANIZATIONS = "educationOrganizations";
    public static final String GRADEBOOK_ENTRIES = "gradebookEntries";
    public static final String GRADING_PERIODS = "gradingPeriods";
    public static final String PARENTS = "parents";
    public static final String PROGRAMS = "programs";
    public static final String REPORT_CARDS = "reportCards";
    public static final String SCHOOLS = "schools";
    public static final String SECTIONS = "sections";
    public static final String SESSIONS = "sessions";
    public static final String STAFF = "staff";
    public static final String STUDENTS = "students";
    public static final String STUDENT_COMPETENCIES = "studentCompetencies";
    public static final String STUDENT_COMPETENCY_OBJECTIVES = "studentCompetencyObjectives";
    public static final String LEARNINGOBJECTIVES = "learningObjectives";
    public static final String LEARNINGSTANDARDS = "learningStandards";
    public static final String STUDENT_SECTION_GRADEBOOK_ENTRIES = "studentSectionGradebookEntries";
    public static final String STUDENT_ACADEMIC_RECORDS = "studentAcademicRecords";
    public static final String TEACHERS = "teachers";
    public static final String USER_ACCOUNTS = "userAccounts";
    public static final String GRADES = "grades";

    public static final String SCHOOL_SESSION_ASSOCIATIONS = "school-session-associations";
    public static final String SECTION_ASSESSMENT_ASSOCIATIONS = "section-assessment-associations";
    public static final String COURSE_OFFERINGS = "courseOfferings";
    public static final String STUDENT_ASSESSMENT_ASSOCIATIONS = "student-assessment-associations";
    public static final String STUDENT_SECTION_ASSOCIATIONS = "student-section-associations";
    public static final String STUDENT_SCHOOL_ASSOCIATIONS = "student-school-associations";
    public static final String TEACHER_SCHOOL_ASSOCIATIONS = "teacher-school-associations";
    public static final String TEACHER_SECTION_ASSOCIATIONS = "teacher-section-associations";
    public static final String STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS = "staff-educationOrganization-associations";
    public static final String STUDENT_TRANSCRIPT_ASSOCIATIONS = "studentTranscriptAssociations";
    public static final String STUDENT_PARENT_ASSOCIATIONS = "student-parent-associations";
    public static final String STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS = "student-disciplineIncident-associations";
    public static final String STAFF_PROGRAM_ASSOCIATIONS = "staffProgramAssociations";
    public static final String STAFF_COHORT_ASSOCIATIONS = "staffCohortAssociations";
    public static final String STUDENT_COHORT_ASSOCIATIONS = "studentCohortAssociations";

    public static final String COHORT_GETTER = "getCohort";
    public static final String STUDENT_COHORT_ASSOCIATION_GETTER = "getStudentCohortAssociation";
    public static final String STUDENT_COHORT_ASSOCIATIONS_GETTER = "getStudentCohortAssociations";
    public static final String STAFF_COHORT_ASSOCIATION_GETTER = "getStaffCohortAssociation";
    public static final String STAFF_COHORT_ASSOCIATIONS_GETTER = "getStaffCohortAssociations";
    public static final String STUDENT_PROGRAM_ASSOCIATIONS = "studentProgramAssociations";
    public static final String ADMIN_DELEGATION = "adminDelegation";



    public static final Map<String, Set<String>> ENTITY_RESOURCE_NAME_MAPPING = new HashMap<String, Set<String>>();


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
        SINGULAR_LINK_NAMES.put(ResourceNames.COURSE_OFFERINGS, "getCourseOffering");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_ASSESSMENT_ASSOCIATIONS, "getStudentAssessment");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_SECTION_ASSOCIATIONS, "getStudentSectionAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STAFF_EDUCATION_ORGANIZATION_ASSOCIATIONS, "getStaffEducationOrgAssignmentAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_TRANSCRIPT_ASSOCIATIONS, "getCourseTranscript");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_PARENT_ASSOCIATIONS, "getStudentParentAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_DISCIPLINE_INCIDENT_ASSOCIATIONS, "getStudentDisciplineIncidentAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_PROGRAM_ASSOCIATIONS, "getStudentProgramAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STAFF_PROGRAM_ASSOCIATIONS, "getStaffProgramAssociation");
        SINGULAR_LINK_NAMES.put(ResourceNames.STAFF_COHORT_ASSOCIATIONS, ResourceNames.STAFF_COHORT_ASSOCIATION_GETTER);
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_COHORT_ASSOCIATIONS, ResourceNames.STUDENT_COHORT_ASSOCIATION_GETTER);

        SINGULAR_LINK_NAMES.put(ResourceNames.ASSESSMENTS, "getAssessment");
        SINGULAR_LINK_NAMES.put(ResourceNames.ATTENDANCES, "getAttendance");
        SINGULAR_LINK_NAMES.put(ResourceNames.COHORTS, ResourceNames.COHORT_GETTER);
        SINGULAR_LINK_NAMES.put(ResourceNames.COURSES, "getCourse");
        SINGULAR_LINK_NAMES.put(ResourceNames.DISCIPLINE_INCIDENTS, "getDisciplineIncident");
        SINGULAR_LINK_NAMES.put(ResourceNames.DISCIPLINE_ACTIONS, "getDisciplineAction");
        SINGULAR_LINK_NAMES.put(ResourceNames.EDUCATION_ORGANIZATIONS, "getEducationOrganization");
        SINGULAR_LINK_NAMES.put(ResourceNames.GRADEBOOK_ENTRIES, "getGradebookEntry");
        SINGULAR_LINK_NAMES.put(ResourceNames.GRADING_PERIODS, "getGradingPeriod");
        SINGULAR_LINK_NAMES.put(ResourceNames.PARENTS, "getParent");
        SINGULAR_LINK_NAMES.put(ResourceNames.PROGRAMS, "getProgram");
        SINGULAR_LINK_NAMES.put(ResourceNames.SECTIONS, "getSection");
        SINGULAR_LINK_NAMES.put(ResourceNames.LEARNINGOBJECTIVES, "getLearningObjective");
        SINGULAR_LINK_NAMES.put(ResourceNames.LEARNINGSTANDARDS, "getLearningStandard");
        SINGULAR_LINK_NAMES.put(ResourceNames.REPORT_CARDS, "getReportCard");
        SINGULAR_LINK_NAMES.put(ResourceNames.SESSIONS, "getSession");
        SINGULAR_LINK_NAMES.put(ResourceNames.SCHOOLS, "getSchool");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENTS, "getStudent");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_ACADEMIC_RECORDS, "getStudentAcademicRecord");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_SECTION_GRADEBOOK_ENTRIES, "getStudentGradebookEntry");
        SINGULAR_LINK_NAMES.put(ResourceNames.TEACHERS, "getTeacher");
        SINGULAR_LINK_NAMES.put(ResourceNames.STAFF, "getStaff");
        SINGULAR_LINK_NAMES.put(ResourceNames.USER_ACCOUNTS, "getUserAccount");
        SINGULAR_LINK_NAMES.put(ResourceNames.GRADES, "getGrade");
        SINGULAR_LINK_NAMES.put(ResourceNames.STUDENT_COMPETENCIES, "getStudentCompetency");
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
            } else if (e.getValue().endsWith("y")) {
                PLURAL_LINK_NAMES.put(e.getKey(), e.getValue().substring(0, e.getValue().length() - 1) + "ies");
            } else {
                PLURAL_LINK_NAMES.put(e.getKey(), e.getValue() + "s");
            }
        }
    }

}
