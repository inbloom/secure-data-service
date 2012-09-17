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

    Then I should see "[duplicates] InterchangeMasterSchedule.xml: 192" in the resulting batch job file
    And I should see "[duplicates] InterchangeEducationOrganization.xml: 102" in the resulting batch job file
    And I should see "[duplicates] InterchangeStaffAssociation.xml: 41" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudent.xml: 78" in the resulting batch job file
    And I should see "[duplicates] InterchangeAssessmentMetadata-ACT.xml: 18" in the resulting batch job file
    And I should see "[duplicates] InterchangeEducationOrgCalendar.xml: 595" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentProgram.xml: 6" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentEnrollment.xml: 495" in the resulting batch job file
    And I should see "[duplicates] InterchangeAssessmentMetadata-READ2.xml: 6" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-Cgray-ACT.xml: 375" in the resulting batch job file
    And I should see "[duplicates] InterchangeAssessmentMetadata-StateTest.xml: 7" in the resulting batch job file
    And I should see "[duplicates] InterchangeAssessmentMetadata-AP-Eng.xml: 6" in the resulting batch job file
    And I should see "[duplicates] InterchangeAssessmentMetadata-CCS-Math.xml: 574" in the resulting batch job file
    And I should see "[duplicates] InterchangeAssessmentMetadata-StateAssessments.xml: 37" in the resulting batch job file
    And I should see "[duplicates] InterchangeAssessmentMetadata-CCS-English.xml: 1024" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-CgrayAP-English.xml: 50" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-Rbraverman4thgrade.xml: 2" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-Lkim6thgrade.xml: 2" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-Lkim7thgrade.xml: 3" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-Rbraverman3rdgrade.xml: 3" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-Rbraverman1stgrade.xml: 4" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-Lkim8thgrade.xml: 112" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentParent.xml: 18" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentGrade.xml: 709" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentDiscipline.xml: 8" in the resulting batch job file
    And I should see "[duplicates] InterchangeAttendance.xml: 5550" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentAssessment-Rbraverman5thgrade.xml: 2" in the resulting batch job file
    And I should see "[duplicates] InterchangeStudentCohort.xml: 12" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 0 records." in the resulting batch job file
  
