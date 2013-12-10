/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.slc.sli.ingestion;

//This probably should be read from xsd

/**
 *
 */
public enum ReferenceConverter {

    ASSESSMENT( "AssessmentReference", "assessment"),
    ASSESSMENTFAMILY( "AssessmentFamilyReference", "assessmentFamily"),
    ASSESSMENTITEM("AssessmentItemReference","assessmentItem"),
    ASSESSMENTPERIODDESCRIPTORREFERENCE("AssessmentPeriodDescriptorReference","assessmentPeriodDescriptor"),
    ATTENDANCEEVENT( "AttendanceEventReference", "attendanceEvent"),
    CALENDAR_DATE( "CalendarDateReference", "calendarDate"),
//    CLASS_PERIOD("ClassPeriodReference", "classPeriod"),
    COHORT("CohortReference","cohort"),
    COMPETENCYLEVELDESCRIPTOR( "CompetencyLevelDescriptorReference", "competencyLevelDescriptor"),
    COURSE( "CourseReference", "course"),
    COURSEOFFERING( "CourseOfferingReference", "courseOffering"),
    COURSETRANSCRIPT("CourseTranscriptReference", "courseTranscript"),
    DISCIPLINEACTION("DisciplineActionReference", "disciplineAction"),
    DISCIPLINEINCIDENT("DisciplineIncidentReference", "disciplineIncident"),
    EDUCATIONORGANIZATION( "EducationOrganizationReference", "educationOrganization"),
    GRADE( "GradeReference", "grade"),
    GRADEBOOKENTRY("GradebookEntryReference", "gradebookEntry"),
    GRADINGPERIOD( "GradingPeriodReference", "gradingPeriod"),
    GRADUATIONPLANREFERENCE("GraduationPlanReference", "graduationPlan"),
    LEARNINGOBJECTIVE("LearningObjectiveReference","learningObjective"),
    LEARNINGSTANDARD("LearningStandardReference","learningStandard"),
    LOCALEDUCATIONAGENCY( "LocalEducationAgencyReference", "localEducationAgency"),
    OBJECTIVEASSESSMENT("ObjectiveAssessmentReference","objectiveAssessment"),
    PARENT( "ParentReference", "parent"),
    PROGRAM ( "ProgramReference", "program"),
    REPORTCARD("ReportCardReference", "reportCard"),
    SCHOOL( "SchoolReference", "school"),
    SECTION("SectionReference","section"),
    SESSION( "SessionReference", "session"),
    STAFF( "StaffReference", "staff"),
    STAFFEDUCATIONORGASSIGNMENTASSOCIATION("StaffEducationOrgAssignmentAssociationReference","staffEducationOrgAssignmentAssociation"),
    STAFFPROGRAMASSOCIATION( "StaffProgramAssociationReference", "staffProgramAssociation"),
    STATEEDUCATIONAGENCY("StateEducationAgencyReference", "stateEducationAgency" ),
    STUDENT( "StudentReference", "student" ),
    STUDENTACADEMICRECORD("StudentAcademicRecordReference", "studentAcademicRecord"),
    STUDENTASSESSMENT("StudentAssessmentReference","studentAssessment"),
    STUDENTASSESSMENTITEM("StudentAssessmentItemReference","studentAssessmentItem"),
    STUDENTCOHORTASSOCIATIONREFERENCE("StudentCohortAssociationReference","studentCohortAssociation"),
    STUDENTCOMPETENCY("StudentCompetencyReference", "studentCompetency"),
    STUDENTCOMPETENCYOBJECTIVE("StudentCompetencyObjectiveReference", "studentCompetencyObjective"),
    STUDENTDISCIPLINEINCIDENTASSOCIATION("StudentDisciplineIncidentAssociationReference","studentDisciplineIncidentAssociation"),
    STUDENTGRADEBOOKENTRY("StudentGradebookEntryReference", "studentGradebookEntry"),
    STUDENTOBJECTIVEASSESSMENT("StudentObjectiveAssessmentReference","studentObjectiveAssessment"),
    STUDENTPARENTASSOCIATION("StudentParentAssociationReference", "studentParentAssociation"),
    STUDENTPROGRAMASSOCIATIONREFERENCE("StudentProgramAssociationReference","studentProgramAssociation"),
    STUDENTSCHOOLASSOCIATIONREFERENCE("StudentSchoolAssociationReference","studentSchoolAssociation"),
    STUDENTSECTIONASSOCIATIONREFERENCE("StudentSectionAssociationReference","studentSectionAssociation"),
    TEACHER("TeacherReference","teacher"),
    TEACHERSCHOOLASSOCIATION("TeacherSchoolAssociationReference","teacherSchoolAssociation"),
    TEACHERSECTIONASSOCIATION("TeacherSectionAssociationReference","teacherSectionAssociation");


    /*
     *
    EDORG( "EducationalOrgReference", "educationOrganization"),
    SCHOOL( "SchoolReference", "school"),
    GRADING_PERIOD( "GradingPeriodReference", "gradingPeriod"),
    SESSION( "SessionReference", "session"),
    COURSE_OFFERING(""),
    STUDENT_GRADES(),

    /*
    ATTENDANCE("AttendanceRefernece", "attendance"), // !!!!
    PERFORMANCE_LEVEL( "PerformanceLevelReference", "???"),  // !!!!
    */



    // *******************************************************************************************************



    private final String referenceName;
    private final String entityName;

    private ReferenceConverter(String referenceName, String entityName) {
        this.referenceName = referenceName;
        this.entityName = entityName;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public String getEntityName() {
        return entityName;
    }


    public static boolean isReferenceType( String typeName ) {
        return( (typeName != null && typeName.endsWith(REFERENCE )) ? true : false );
    }

    public static ReferenceConverter fromReferenceName(String refName) {

        ReferenceConverter found = null;
        for (ReferenceConverter converter : values()) {
            if (converter.getReferenceName().equals(refName)) {
                found = converter;
                break;
            }
        }
        return found;
    }

    private static final String REFERENCE="Reference";
}
