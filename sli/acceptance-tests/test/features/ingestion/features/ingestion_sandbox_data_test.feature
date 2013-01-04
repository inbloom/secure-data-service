@RALLY_DE1030
Feature: Sandbox Medium Fidelity Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post a zip file containing the sandbox small fidelity data as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "IL-Daybreak"
    And I post "SmallSampleDataSet.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | studentSchoolAssociation    |
        | course                      |
        | educationOrganization       |
        | section                     |
        | studentSectionAssociation   |
        | staff                       |
        | staffEducationOrganizationAssociation|
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
        | session                     |
        | schoolSessionAssociation    |
        | assessment                  |
        | studentAssessment|
        | gradebookEntry              |
        | courseTranscript            |
        | studentGradebookEntry|
        | parent                      |
        | studentParentAssociation    |
        | attendance                  |
        | program                     |
        | staffProgramAssociation     |
        | studentProgramAssociation   |
        | cohort                      |
        | staffCohortAssociation      |
        | studentCohortAssociation    |
        | studentCompetency           |
        | studentCompetencyObjective  |
        | learningStandard            |
        | learningObjective           |
        | disciplineIncident          |
        | disciplineAction            |
        | studentDisciplineIncidentAssociation|
        | grade                       |
        | gradingPeriod               |
        | calendarDate                |
        | reportCard                  |
        | studentAcademicRecord       |
  When zip file is scp to ingestion landing zone
  And a batch job for file "SmallSampleDataSet.zip" is completed in database
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | studentSchoolAssociation    | 167   |
        | course                      | 95    |
        | educationOrganization       | 5     |
        | section                     | 97    |
        | studentSectionAssociation   | 297   |
        | staff                       | 14    |
        | staffEducationOrganizationAssociation| 10 |
        | teacherSchoolAssociation    | 3     |
        | teacherSectionAssociation   | 11    |
        | session                     | 22    |
        | schoolSessionAssociation    | 22    |
        | assessment                  | 19    |
        | studentAssessment| 203   |
        | courseTranscript            | 196   |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentGradebookEntry       | 315   |
        | attendance                  | 75    |
        | program                     | 2     |
        | staffProgramAssociation     | 3     |
        | studentProgramAssociation   | 6     |
        | cohort                      | 3     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 6     |
        | studentCompetency           | 59    |
        | studentCompetencyObjective  | 4     |
        | learningStandard            | 509   |
        | learningObjective           | 65    |
        | disciplineIncident          | 2     |
        | disciplineAction            | 2     |
		| studentDisciplineIncidentAssociation| 4|
        | grade                       | 4     |
        | gradingPeriod               | 17    |
        | calendarDate                | 556   |
        | reportCard                  | 2     |
        | studentAcademicRecord       | 117   |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 1                   | metaData.externalId      | 100000000                  | string               |
       | student                     | 1                   | metaData.externalId      | 800000012                  | string               |
       | student                     | 1                   | metaData.externalId      | 900000024                  | string               |
       | student                     | 1                   | metaData.externalId      | 800000025                  | string               |
       | staff                       | 1                   | metaData.externalId      | cgray                      | string               |
       | course                      | 1                   | metaData.externalId      | 1st Grade Homeroom         | string               |
       | educationOrganization       | 1                   | metaData.externalId      | South Daybreak Elementary  | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL-DAYBREAK                | string               |
       | educationOrganization       | 1                   | metaData.externalId      | STANDARD-SEA                         | string               |
       | program                     | 1                   | metaData.externalId      | ACC-TEST-PROG-1            | string               |
       | program                     | 1                   | metaData.externalId      | ACC-TEST-PROG-2            | string               |
       | attendance                  | 75                  | body.schoolYearAttendance.schoolYear            | 2011-2012     | string     |
       | attendance                  | 11                  | body.schoolYearAttendance.attendanceEvent.event | Tardy         | string     |
       | attendance                  | 75                  | body.schoolYearAttendance.attendanceEvent.event | In Attendance | string     |
       | cohort                      | 1                   | metaData.externalId      | ACC-TEST-COH-1             | string               |
       | cohort                      | 1                   | metaData.externalId      | ACC-TEST-COH-2             | string               |
       | cohort                      | 1                   | metaData.externalId      | ACC-TEST-COH-3             | string               |
       | disciplineIncident          | 1                   | body.incidentIdentifier  | Tardiness                  | string               |
       | disciplineIncident          | 1                   | body.incidentIdentifier  | Disruption                 | string               |
       | disciplineAction            | 1                   | body.disciplineDate      | 2011-03-04                 | string               |
       | disciplineAction            | 1                   | body.disciplineDate      | 2011-04-04                 | string               |
       | assessment                  | 1                   | body.assessmentItem.0.identificationCode       | AssessmentItem-1 | string  |
       | assessment                  | 1                   | body.assessmentItem.0.itemCategory             | True-False       | string  |
       | assessment                  | 1                   | body.assessmentItem.0.maxRawScore              | 5                | integer |
       | assessment                  | 1                   | body.assessmentItem.0.correctResponse          | False            | string  |
       | assessment                  | 1                   | body.assessmentItem.1.identificationCode       | AssessmentItem-2 | string  |
       | assessment                  | 1                   | body.assessmentItem.1.itemCategory             | True-False       | string  |
       | assessment                  | 1                   | body.assessmentItem.1.maxRawScore              | 5                | integer |
       | assessment                  | 1                   | body.assessmentItem.1.correctResponse          | True             | string  |
       | assessment                  | 1                   | body.assessmentItem.2.identificationCode       | AssessmentItem-3 | string  |
       | assessment                  | 1                   | body.assessmentItem.2.itemCategory             | True-False       | string  |
       | assessment                  | 1                   | body.assessmentItem.2.maxRawScore              | 5                | integer |
       | assessment                  | 1                   | body.assessmentItem.2.correctResponse          | True             | string  |
       | assessment                  | 1                   | body.assessmentItem.3.identificationCode       | AssessmentItem-4 | string  |
       | assessment                  | 1                   | body.assessmentItem.3.itemCategory             | True-False       | string  |
       | assessment                  | 1                   | body.assessmentItem.3.maxRawScore              | 5                | integer |
       | assessment                  | 1                   | body.assessmentItem.3.correctResponse          | False            | string  |
       | studentAssessment | 25                 | body.studentAssessmentItems.0.assessmentItem.identificationCode | AssessmentItem-3    | string |
       | studentAssessment | 17                 | body.studentAssessmentItems.0.assessmentResponse                | True                | string |
       | studentAssessment | 17                 | body.studentAssessmentItems.0.assessmentItemResult              | Correct             | string |
       | studentAssessment | 25                 | body.studentAssessmentItems.1.assessmentItem.identificationCode | AssessmentItem-4    | string |
       | studentAssessment | 22                 | body.studentAssessmentItems.1.assessmentResponse                | True                | string |
       | studentAssessment | 22                 | body.studentAssessmentItems.1.assessmentItemResult              | Correct             | string |
    And I should see "Processed 3029 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 102" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 102" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 595" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 595" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 97" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 97" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 41" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 41" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 492" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 492" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 709" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 709" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-READ2.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-READ2.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-READ2.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateTest.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateTest.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateTest.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateAssessments.xml records considered: 12" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateAssessments.xml records ingested successfully: 12" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateAssessments.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-Math.xml records considered: 574" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-Math.xml records ingested successfully: 574" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-Math.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim6thgrade.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim6thgrade.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim6thgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim7thgrade.xml records considered: 3" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim7thgrade.xml records ingested successfully: 3" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim7thgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records considered: 112" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records ingested successfully: 112" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records failed: 0" in the resulting batch job file
	And I should see "InterchangeStudentAssessment-Rbraverman3rdgrade.xml records considered: 3" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman3rdgrade.xml records ingested successfully: 3" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman3rdgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman4thgrade.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman4thgrade.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman4thgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman5thgrade.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman5thgrade.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman5thgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAttendance.xml records considered: 75" in the resulting batch job file
    And I should see "InterchangeAttendance.xml records ingested successfully: 75" in the resulting batch job file
    And I should see "InterchangeAttendance.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records considered: 18" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records ingested successfully: 18" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentProgram.xml records considered: 6" in the resulting batch job file
    And I should see "InterchangeStudentProgram.xml records ingested successfully: 6" in the resulting batch job file
    And I should see "InterchangeStudentProgram.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records considered: 12" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records ingested successfully: 12" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records considered: 8" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records ingested successfully: 8" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing the sandbox medium fidelity data as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "IL-Daybreak"
    And I post "MediumSampleDataSet.zip" file as the payload of the ingestion job
   And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | studentSchoolAssociation    |
        | course                      |
        | educationOrganization       |
        | section                     |
        | studentSectionAssociation   |
        | staff                       |
        | staffEducationOrganizationAssociation|
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
        | session                     |
        | schoolSessionAssociation    |
        | assessment                  |
        | studentAssessment|
        | gradebookEntry              |
        | courseTranscript            |
        | studentGradebookEntry       |
        | parent                      |
        | studentParentAssociation    |
        | attendance                  |
        | program                     |
        | staffProgramAssociation     |
        | studentProgramAssociation   |
        | cohort                      |
        | staffCohortAssociation      |
        | studentCohortAssociation    |
        | studentCompetency           |
        | studentCompetencyObjective  |
        | learningStandard            |
        | learningObjective           |
        | disciplineIncident          |
        | disciplineAction            |
        | studentDisciplineIncidentAssociation|
        | grade                       |
        | gradingPeriod               |
        | calendarDate                |
        | reportCard                  |
        | studentAcademicRecord       |
  When zip file is scp to ingestion landing zone
  And a batch job for file "MediumSampleDataSet.zip" is completed in database
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 500   |
        | studentSchoolAssociation    | 500   |
        | course                      | 50    |
        | educationOrganization       | 7     |
        | section                     | 300   |
        | studentSectionAssociation   | 2500  |
        | staff                       | 60    |
        | staffEducationOrganizationAssociation| 10 |
        | teacherSchoolAssociation    | 50    |
        | teacherSectionAssociation   | 300   |
        | session                     | 10    |
        | schoolSessionAssociation    | 10    |
        | assessment                  | 1     |
        | studentAssessment| 2500  |
        | courseTranscript            | 2500  |
        | parent                      | 0     |
        | studentParentAssociation    | 0     |
        | gradebookEntry              | 1     |
        | studentGradebookEntry       | 500   |
        | attendance                  | 500   |
        | program                     | 8     |
        | staffProgramAssociation     | 8     |
        | studentProgramAssociation   | 0     |
        | cohort                      | 10    |
        | staffCohortAssociation      | 10    |
        | studentCohortAssociation    | 741   |
        | studentCompetency           | 1000  |
        | studentCompetencyObjective  | 1     |
        | learningStandard            | 2     |
        | learningObjective           | 1     |
        | disciplineIncident          | 4785  |
        | disciplineAction            | 50    |
		| studentDisciplineIncidentAssociation| 4785|
        | grade                       | 2500  |
        | gradingPeriod               | 40    |
        | calendarDate                | 20    |
        | reportCard                  | 500   |
        | studentAcademicRecord       | 500   |
    And I should see "Processed 29990 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeEducationOrganization.xml records considered: 65" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 65" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 70" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 70" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 300" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 300" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 428" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 428" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 3005" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 3005" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 7502" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 7502" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAttendance.xml records considered: 500" in the resulting batch job file
    And I should see "InterchangeStudentAttendance.xml records ingested successfully: 500" in the resulting batch job file
    And I should see "InterchangeStudentAttendance.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records considered: 500" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records ingested successfully: 500" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records considered: 761" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records ingested successfully: 761" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records considered: 14355" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records ingested successfully: 14355" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records failed: 0" in the resulting batch job file
