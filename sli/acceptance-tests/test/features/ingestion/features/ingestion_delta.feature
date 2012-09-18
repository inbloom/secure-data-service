Feature: Ingestion Delta Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Job report should report deltas when SDS is ingested twice
    Given I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | student                     |
        | studentSchoolAssociation    |
        | course                      |
        | educationOrganization       |
        | section                     |
        | studentSectionAssociation   |
        | staff                       |
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
        | session                     |
        | assessment                  |
        | studentAssessmentAssociation|
        | studentTranscriptAssociation|
        | studentAcademicRecord       |
        | studentGradebookEntry       |
        | parent                      |
        | studentParentAssociation    |
        | graduationPlan              |
        | studentAcademicRecord       |

    And zip file is scp to ingestion landing zone
    And a batch job log has been created
    And zip file is scp to ingestion landing zone with name "Reingest-StoriedDataSet_IL_Daybreak.zip"
    And two batch job logs have been created

    And I should see " InterchangeStudent.xml student 78 deltas!" in the resulting batch job file
    And I should see " InterchangeEducationOrganization.xml course 95 deltas!" in the resulting batch job file
    And I should see " InterchangeEducationOrganization.xml school 3 deltas!" in the resulting batch job file
    And I should see " InterchangeEducationOrganization.xml program 2 deltas!" in the resulting batch job file
    And I should see " InterchangeEducationOrganization.xml localEducationAgency 1 deltas!" in the resulting batch job file
    And I should see " InterchangeEducationOrganization.xml stateEducationAgency 1 deltas!" in the resulting batch job file
    And I should see " InterchangeMasterSchedule.xml courseOffering 95 deltas!" in the resulting batch job file
    And I should see " InterchangeMasterSchedule.xml section 97 deltas!" in the resulting batch job file
    And I should see " InterchangeEducationOrgCalendar.xml calendarDate 556 deltas!" in the resulting batch job file
    And I should see " InterchangeEducationOrgCalendar.xml session 22 deltas!" in the resulting batch job file
    And I should see " InterchangeEducationOrgCalendar.xml gradingPeriod 17 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentProgram.xml studentProgramAssociation 6 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentEnrollment.xml studentSchoolAssociation 167 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentEnrollment.xml graduationPlan 3 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentEnrollment.xml studentSectionAssociation 325 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-ACT.xml objectiveAssessment 12 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-ACT.xml assessmentFamily 1 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-ACT.xml assessmentItem 4 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-ACT.xml assessment 1 deltas!" in the resulting batch job file
    And I should see " InterchangeStaffAssociation.xml teacherSchoolAssociation 3 deltas!" in the resulting batch job file
    And I should see " InterchangeStaffAssociation.xml staff 11 deltas!" in the resulting batch job file
    And I should see " InterchangeStaffAssociation.xml teacherSectionAssociation 11 deltas!" in the resulting batch job file
    And I should see " InterchangeStaffAssociation.xml staffEducationOrganizationAssociation 10 deltas!" in the resulting batch job file
    And I should see " InterchangeStaffAssociation.xml teacher 3 deltas!" in the resulting batch job file
    And I should see " InterchangeStaffAssociation.xml staffProgramAssociation 3 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-AP-Eng.xml assessmentFamily 4 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-AP-Eng.xml assessment 2 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-READ2.xml assessmentFamily 2 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-READ2.xml assessmentPeriodDescriptor 2 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-READ2.xml assessment 2 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-StateTest.xml assessmentFamily 5 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-StateTest.xml assessment 2 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-StateAssessments.xml assessmentFamily 25 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-StateAssessments.xml assessment 12 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-CCS-English.xml learningObjective 70 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-CCS-English.xml learningStandard 954 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-CCS-Math.xml learningObjective 65 deltas!" in the resulting batch job file
    And I should see " InterchangeAssessmentMetadata-CCS-Math.xml learningStandard 509 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Cgray-ACT.xml studentObjectiveAssessment 300 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Cgray-ACT.xml studentAssessmentAssociation 25 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Cgray-ACT.xml studentAssessmentItem 50 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-CgrayAP-English.xml studentAssessmentAssociation 50 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Lkim6thgrade.xml studentAssessmentAssociation 2 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Lkim7thgrade.xml studentAssessmentAssociation 3 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Lkim8thgrade.xml studentAssessmentAssociation 112 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Rbraverman1stgrade.xml studentAssessmentAssociation 4 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Rbraverman3rdgrade.xml studentAssessmentAssociation 3 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Rbraverman4thgrade.xml studentAssessmentAssociation 2 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentAssessment-Rbraverman5thgrade.xml studentAssessmentAssociation 2 deltas!" in the resulting batch job file
    And I should see " InterchangeAttendance.xml attendance 5550 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentGrade.xml studentTranscriptAssociation 196 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentGrade.xml studentAcademicRecord 117 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentGrade.xml studentCompetencyObjective 4 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentGrade.xml reportCard 2 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentGrade.xml studentCompetency 59 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentGrade.xml grade 4 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentGrade.xml studentGradebookEntry 315 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentGrade.xml gradebookEntry 12 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentParent.xml parent 9 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentParent.xml studentParentAssociation 9 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentCohort.xml cohort 3 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentCohort.xml studentCohortAssociation 6 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentCohort.xml staffCohortAssociation 3 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentDiscipline.xml disciplineAction 2 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentDiscipline.xml studentDisciplineIncidentAssociation 4 deltas!" in the resulting batch job file
    And I should see " InterchangeStudentDiscipline.xml disciplineIncident 2 deltas!" in the resulting batch job file
  
