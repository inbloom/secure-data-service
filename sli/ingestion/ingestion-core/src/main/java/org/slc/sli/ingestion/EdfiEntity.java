package org.slc.sli.ingestion;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enum listing Ed-Fi entities with their direct dependencies
 * Provides services to lookup entities based on collection name
 * Provides services to perform an iterative diminiation lookup
 *
 * TODO: Needs to be migrated to common
 *
 * @author dkornishev
 *
 */
public enum EdfiEntity {

    SELF("self", Collections.<EdfiEntity>emptyList()),

    ASSESSMENT_FAMILY("assessmentFamily", Arrays.asList(SELF)),

    CALENDAR_DATE("calendarDate", Collections.<EdfiEntity>emptyList()),

    CLASS_PERIOD("classPeriod", Collections.<EdfiEntity>emptyList()),

    GRADUATION_PLAN("graduationPlan", Collections.<EdfiEntity>emptyList()),

    LEARNING_STANDARD("learningStandard", Collections.<EdfiEntity>emptyList()),

    LOCATION("location", Collections.<EdfiEntity>emptyList()),

    PARENT("parent", Collections.<EdfiEntity>emptyList()),

    PROGRAM("program", Collections.<EdfiEntity>emptyList()),

    STAFF("staff", Collections.<EdfiEntity>emptyList()),

    STUDENT("student", Collections.<EdfiEntity>emptyList()),

    TEACHER("teacher", Collections.<EdfiEntity>emptyList()),

    ASSESSMENT_PERIOD_DESCRIPTOR("assessmentPeriodDescriptor", Collections.<EdfiEntity>emptyList()),

    EDUCATION_ORGANIZATION("educationOrganization", Arrays.asList(PROGRAM)),

    ACADEMIC_WEEK("academicWeek", Arrays.asList(EdfiEntity.CALENDAR_DATE)),

    GRADING_PERIOD("gradingPeriod", Arrays.asList(CALENDAR_DATE)),

    LEARNING_OBJECTIVE("learningObjective", Arrays.asList(LEARNING_STANDARD, SELF)),

    STATE_EDUCATION_AGENCY("stateEducationAgency", Arrays.asList(PROGRAM, SELF)),

    EDUCATION_SERVICE_CENTER("educationServiceCenter", Arrays.asList(STATE_EDUCATION_AGENCY, PROGRAM, SELF)),

    LOCAL_EDUCATION_AGENCY("localEducationAgency", Arrays.asList(STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER,
            PROGRAM, SELF)),

    SCHOOL("school", Arrays.asList(LOCAL_EDUCATION_AGENCY, EDUCATION_ORGANIZATION, CLASS_PERIOD, SELF)),

    DIPLOMA("diploma", Arrays.asList(SCHOOL)),

    STUDENT_PARENT_ASSOCIATION("studentParentAssociation", Arrays.asList(STUDENT, PARENT)),

    STUDENT_PROGRAM_ASSOCIATION("studentProgramAssociation", Arrays.asList(STUDENT, PROGRAM, EDUCATION_ORGANIZATION)),

    STAFF_PROGRAM_ASSOCIATION("staffProgramAssociation", Arrays.asList(STAFF, PROGRAM)),

    ASSESSMENT_ITEM("assessmentItem", Arrays.asList(LEARNING_STANDARD)),

    BEHAVIOR_DESCRIPTOR("behaviorDescriptor", Arrays.asList(STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER,
            LOCAL_EDUCATION_AGENCY, SCHOOL)),

    COHORT("cohort", Arrays.asList(PROGRAM, STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY,
            SCHOOL)),

    COURSE("course", Arrays.asList(LEARNING_OBJECTIVE, LEARNING_STANDARD, STATE_EDUCATION_AGENCY,
            EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL)),

    DISCIPLINE_INCIDENT("disciplineIncident", Arrays.asList(SCHOOL, STAFF)),

    OBJECTIVE_ASSESSMENT("objectiveAssessment", Arrays.asList(LEARNING_OBJECTIVE, LEARNING_STANDARD, SELF)),

    SESSION("session", Arrays.asList(GRADING_PERIOD, CALENDAR_DATE, ACADEMIC_WEEK, STATE_EDUCATION_AGENCY,
            EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL)),

    STAFF_EDUCATION_ORG_ASSIGNMENT_ASSOCIATION("staffEducationOrgAssignmentAssociation", Arrays.asList(STAFF,
            STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL)),

    STUDENT_COMPETENCY_OBJECTIVE("studentCompetencyObjective", Arrays.asList(STATE_EDUCATION_AGENCY,
            EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL)),

    STUDENT_SCHOOL_ASSOCIATION("studentSchoolAssociation", Arrays.asList(STUDENT, SCHOOL, GRADUATION_PLAN)),

    TEACHER_SCHOOL_ASSOCIATION("teacherSchoolAssociation", Arrays.asList(TEACHER, SCHOOL)),

    COURSE_OFFERING("courseOffering", Arrays.asList(SCHOOL, SESSION, COURSE)),

    DISCIPLINE_ACTION("disciplineAction", Arrays.asList(STUDENT, DISCIPLINE_INCIDENT, STAFF, SCHOOL)),

    STUDENT_DISCIPLINE_INCIDENT_ASSOCIATION("studentDisciplineIncidentAssociation", Arrays.asList(STUDENT,
            DISCIPLINE_INCIDENT)),

    SECTION("section", Arrays.asList(COURSE_OFFERING, SCHOOL, SESSION, LOCATION, CLASS_PERIOD, PROGRAM)),

    STUDENT_SECTION_ASSOCIATION("studentSectionAssociation", Arrays.asList(STUDENT, SECTION)),

    ASSESSMENT("assessment", Arrays.asList(ASSESSMENT_ITEM, OBJECTIVE_ASSESSMENT, ASSESSMENT_FAMILY, SECTION)),

    ATTENDANCE_EVENT("attendanceEvent", Arrays.asList(STUDENT, SECTION, SCHOOL)),

    TEACHER_SECTION_ASSOCIATION("teacherSectionAssociation", Arrays.asList(TEACHER, SECTION)),

    STUDENT_COMPETENCY("studentCompetency", Arrays.asList(LEARNING_OBJECTIVE, STUDENT_COMPETENCY_OBJECTIVE,
            STUDENT_SECTION_ASSOCIATION)),

    GRADE("grade", Arrays.asList(GRADING_PERIOD, STUDENT_SECTION_ASSOCIATION)),

    REPORT_CARD("reportCard", Arrays.asList(GRADE, STUDENT_COMPETENCY, STUDENT, GRADING_PERIOD)),

    STUDENT_ASSESSMENT("studentAssessment", Arrays.asList(STUDENT, ASSESSMENT)),

    STUDENT_ACADEMIC_RECORD("studentAcademicRecord", Arrays.asList(STUDENT, SESSION, REPORT_CARD, DIPLOMA)),

    STUDENT_OBJECTIVE_ASSESSMENT("studentObjectiveAssessment", Arrays.asList(OBJECTIVE_ASSESSMENT, STUDENT_ASSESSMENT)),

    COURSE_TRANSCRIPT("courseTranscript", Arrays.asList(STUDENT_ACADEMIC_RECORD, STATE_EDUCATION_AGENCY,
            EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL, COURSE)),

    DISCIPLINE_DESCRIPTOR("disciplineDescriptor", Arrays.asList(STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER,
            LOCAL_EDUCATION_AGENCY, SCHOOL)),

    FEEDER_SCHOOL_ASSOCIATION("feederSchoolAssociation", Arrays.asList(STATE_EDUCATION_AGENCY,
            EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL)),

    OPEN_STAFF_POSITION("openStaffPosition", Arrays.asList(STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER,
            LOCAL_EDUCATION_AGENCY, SCHOOL)),

    BELL_SCHEDULE("bellSchedule", Collections.<EdfiEntity>emptyList()),

    COMPETENCY_LEVEL_DESCRIPTOR("competencyLevelDescriptor", Collections.<EdfiEntity>emptyList()),

    CREDENTIAL_FIELD_DESCRIPTOR("credentialFieldDescriptor", Collections.<EdfiEntity>emptyList()),

    PERFORMANCE_LEVEL_DESCRIPTOR("performanceLevelDescriptor", Collections.<EdfiEntity>emptyList()),

    SERVICE_DESCRIPTOR("serviceDescriptor", Collections.<EdfiEntity>emptyList()),

    GRADEBOOK_ENTRY("gradebookEntry", Arrays.asList(LEARNING_OBJECTIVE, LEARNING_STANDARD, SECTION, GRADING_PERIOD)),

    LEAVE_EVENT("leaveEvent", Arrays.asList(STAFF)),

    MEETING_TIME("meetingTime", Arrays.asList(CLASS_PERIOD)),

    RESTRAINT_EVENT("restraintEvent", Arrays.asList(STUDENT, SCHOOL, PROGRAM)),

    STAFF_COHORT_ASSOCIATION("staffCohortAssociation", Arrays.asList(STAFF, COHORT)),

    STUDENT_COHORT_ASSOCIATION("studentCohortAssociation", Arrays.asList(STUDENT, COHORT)),

    STAFF_EDUCATION_ORG_EMPLOYMENT_ASSOCIATION("staffEducationOrgEmploymentAssociation", Arrays.asList(STAFF,
            STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL)),

    STUDENT_GRADEBOOK_ENTRY("studentGradebookEntry", Arrays.asList(STUDENT_SECTION_ASSOCIATION, GRADEBOOK_ENTRY)),

    STUDENT_ASSESSMENT_ITEM("studentAssessmentItem", Arrays.asList(STUDENT_OBJECTIVE_ASSESSMENT, SELF)),

    STUDENT_CTE_PROGRAM_ASSOCIATION("studentCTEProgramAssociation", Arrays.asList(STUDENT, PROGRAM,
            STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL)),

    STUDENT_SPECIAL_ED_PROGRAM_ASSOCIATION("studentSpecialEdProgramAssociation", Arrays.asList(STUDENT, PROGRAM,
            STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL)),

    STUDENT_TITLE_PART_A_PROGRAM_ASSOCIATION("studentTitlePartAProgramAssociation", Arrays.asList(STUDENT, PROGRAM,
            STATE_EDUCATION_AGENCY, EDUCATION_SERVICE_CENTER, LOCAL_EDUCATION_AGENCY, SCHOOL));

    // *******************************************************************************************************
    private static final Map<String, EdfiEntity> ALIAS = new HashMap<String, EdfiEntity>();

    static {
        ALIAS.put("studentAssessmentAssociation", STUDENT_ASSESSMENT);
        ALIAS.put("studentSectionGradebookEntry", STUDENT_GRADEBOOK_ENTRY);
        ALIAS.put("attendance", ATTENDANCE_EVENT);
        ALIAS.put("staffEducationOrganizationAssociation", STAFF_EDUCATION_ORG_ASSIGNMENT_ASSOCIATION);
        ALIAS.put("studentTranscriptAssociation", COURSE_TRANSCRIPT);
    }

    /**
     * Removes entities which have parent entities in the same set
     *
     * @param impure
     *            set of edfi entities to purify
     * @return purifed set which only contains entities which have no dependencies in the provided
     *         set
     */
    public static Set<EdfiEntity> cleanse(Set<EdfiEntity> impure) {
        Set<EdfiEntity> pure = EnumSet.copyOf(impure);

        for (EdfiEntity outer : impure) {
            for (EdfiEntity inner : impure) {
                if (outer != inner && outer.getNeededEntities().contains(inner)) {
                    pure.remove(outer);
                    break;
                }
            }
        }

        return pure;
    }

    // *************************************************************************************
    private final String entityName;
    private final Set<EdfiEntity> neededEntities;

    private EdfiEntity(String entityName, List<EdfiEntity> needs) {
        this.entityName = entityName;
        this.neededEntities = needs.isEmpty() ? Collections.<EdfiEntity>emptySet() : new HashSet<EdfiEntity>(needs);
    }

    public String getEntityName() {
        return entityName;
    }

    /**
     * Returns a set of entities needed to perform ingestion of this entity
     * Performs SELF resolution
     *
     * Yes I know there is EnumSet, but it is so horribly implemented, and so horribly brittle
     * I had to switch to hashsets to make things work
     *
     * @return a set of dependencies
     */
    public Set<EdfiEntity> getNeededEntities() {
        Set<EdfiEntity> def = new HashSet<EdfiEntity>(this.neededEntities);
        if (def.remove(SELF)) {
            def.add(this);
        }

        return def;
    }

    public static EdfiEntity fromEntityName(String entityName) {

        EdfiEntity found = ALIAS.get(entityName);

        if (found == null) {
            for (EdfiEntity edfiEntity : values()) {
                if (edfiEntity.entityName.equals(entityName)) {
                    found = edfiEntity;
                    break;
                }
            }
        }
        return found;
    }

}
