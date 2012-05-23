@RALLY_US1889
@RALLY_US1964
Feature: Acceptance Storied Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

@smoke @integration @IL-Daybreak
Scenario: Post a zip file containing all data for Illinois Daybreak as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "IL-Daybreak"
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | studentSchoolAssociation    |
        | course                      |
        | educationOrganization       |
        | section                     |
        | studentSectionAssociation   |
        | staff                       |
        |staffEducationOrganizationAssociation|
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
        | session                     |
        | assessment                  |
        | studentAssessmentAssociation|
        | gradebookEntry              |
        | studentTranscriptAssociation|
        | studentSectionGradebookEntry|
        | parent                      |
        | studentParentAssociation    |
        | attendance                  |
        | program                     |
        | staffProgramAssociation     |
        | studentProgramAssociation   |
        | cohort                      |
        | staffCohortAssociation      |
        | studentCohortAssociation    |
        | learningStandard            |
        | learningObjective           |
        | disciplineIncident          |
        | disciplineAction            |
		| studentDisciplineIncidentAssociation|
        | grade                       |
        | gradingPeriod               |
        | calendarDate                |
        | reportCard                  |
  When zip file is scp to ingestion landing zone
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | studentSchoolAssociation    | 167   |
        | course                      | 95    |
        | educationOrganization       | 7     |
        | section                     | 97    |
        | studentSectionAssociation   | 296   |
        | staff                       | 14    |
        | staffEducationOrganizationAssociation| 11|
        | teacherSchoolAssociation    | 3     |
        | teacherSectionAssociation   | 10    |
        | session                     | 22    |
        | assessment                  | 19    |
        | studentAssessmentAssociation| 203   |
        | studentTranscriptAssociation| 196   |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentSectionGradebookEntry| 315   |
        | attendance                  | 75    |
        | program                     | 2     |
        | staffProgramAssociation     | 3     |
        | studentProgramAssociation   | 10    |
        | cohort                      | 3     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 9     |
        | learningStandard            | 36    |
        | learningObjective           | 63    |
        | disciplineIncident          | 2     |
        | disciplineAction            | 2     |
		| studentDisciplineIncidentAssociation| 4|
        | grade                       | 4     |
        | gradingPeriod               | 17    |
        | calendarDate                | 556   |
        | reportCard                  | 2     |
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
       | educationOrganization       | 1                   | metaData.externalId      | IL                         | string               |
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
       | studentAssessmentAssociation | 25                 | body.studentAssessmentItems.0.assessmentItem.identificationCode | AssessmentItem-3    | string |
       | studentAssessmentAssociation | 17                 | body.studentAssessmentItems.0.assessmentResponse                | True                | string |
       | studentAssessmentAssociation | 17                 | body.studentAssessmentItems.0.assessmentItemResult              | Correct             | string |
       | studentAssessmentAssociation | 25                 | body.studentAssessmentItems.1.assessmentItem.identificationCode | AssessmentItem-4    | string |
       | studentAssessmentAssociation | 22                 | body.studentAssessmentItems.1.assessmentResponse                | True                | string |
       | studentAssessmentAssociation | 22                 | body.studentAssessmentItems.1.assessmentItemResult              | Correct             | string |
    And I should see "Processed 16074 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 104" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 104" in the resulting batch job file
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
    And I should see "InterchangeStudentEnrollment.xml records considered: 491" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 491" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 646" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 646" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records ingested successfully: 2" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateAssessments.xml records considered: 12" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateAssessments.xml records ingested successfully: 12" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-StateAssessments.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records considered: 99" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records ingested successfully: 99" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records failed: 0" in the resulting batch job file
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
    And I should see "InterchangeAttendance.xml records considered: 13650" in the resulting batch job file
    And I should see "InterchangeAttendance.xml records ingested successfully: 13650" in the resulting batch job file
    And I should see "InterchangeAttendance.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records considered: 18" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records ingested successfully: 18" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentProgram.xml records considered: 10" in the resulting batch job file
    And I should see "InterchangeStudentProgram.xml records ingested successfully: 10" in the resulting batch job file
    And I should see "InterchangeStudentProgram.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records considered: 15" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records ingested successfully: 15" in the resulting batch job file
    And I should see "InterchangeStudentCohort.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records considered: 8" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records ingested successfully: 8" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records failed: 0" in the resulting batch job file

@smoke @integration @IL-Sunset
Scenario: Post a zip file containing all data for Illinois Sunset as a payload of the ingestion job: Append Database
Given I am using preconfigured Ingestion Landing Zone for "IL-Sunset"
  And I post "StoriedDataSet_IL_Sunset.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 81    |
        | studentSchoolAssociation    | 170   |
        | course                      | 96    |
        | educationOrganization       | 7     |
        | section                     | 98    |
        | studentSectionAssociation   | 299   |
        | staff                       | 21    |
        | staffEducationOrganizationAssociation|17|
        | teacherSchoolAssociation    | 4     |
        | teacherSectionAssociation   | 11    |
        | session                     | 22    |
        | assessment                  | 19    |
        | studentAssessmentAssociation| 203   |
        | studentTranscriptAssociation| 196   |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentSectionGradebookEntry| 315   |
        | attendance                  | 75    |
        | program                     | 2     |
        | staffProgramAssociation     | 3     |
        | studentProgramAssociation   | 10    |
        | cohort                      | 3     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 9     |
        | disciplineIncident          | 2     |
        | disciplineAction            | 2     |
		| studentDisciplineIncidentAssociation| 4|
        | grade                       | 4     |
        | reportCard                  | 2     |
   And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 1                   | metaData.externalId      | 1000000000                 | string               |
       | staff                       | 1                   | metaData.externalId      | manthony                   | string               |
       | course                      | 1                   | metaData.externalId      | A.P. Calculus              | string               |
       | educationOrganization       | 1                   | metaData.externalId      | Sunset Central High School | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL-SUNSET                  | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL                         | string               |
    And I should see "Processed 29 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 3" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 3" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 3" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 3" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 15" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 15" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 6" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 6" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file

@smoke @integration @NY-NYC
Scenario: Post a zip file containing all data for New York as a payload of the ingestion job: Append Database
Given I am using preconfigured Ingestion Landing Zone for "NY-NYC"
  And I post "StoriedDataSet_NY.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 89    |
        | studentSchoolAssociation    | 178   |
        | course                      | 104   |
        | educationOrganization       | 14    |
        | section                     | 114   |
        | studentSectionAssociation   | 307   |
        | staff                       | 58    |
        | staffEducationOrganizationAssociation|38|
        | teacherSchoolAssociation    | 20    |
        | teacherSectionAssociation   | 27    |
        | session                     | 26    |
        | assessment                  | 19    |
        | studentAssessmentAssociation| 203   |
        | studentTranscriptAssociation| 196   |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentSectionGradebookEntry| 315   |
        | attendance                  | 75    |
        | program                     | 2     |
        | staffProgramAssociation     | 3     |
        | studentProgramAssociation   | 10    |
        | cohort                      | 3     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 9     |
        | disciplineIncident          | 4     |
        | disciplineAction            | 3     |
		| studentDisciplineIncidentAssociation| 8|
        | grade                       | 4     |
        | reportCard                  | 2     |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 2                   | metaData.externalId      | 100000006                  | string               |
       | staff                       | 1                   | metaData.externalId      | jcarlyle                   | string               |
       | section                     | 1                   | metaData.externalId      | Mason201-Sec1              | string               |
       | educationOrganization       | 1                   | metaData.externalId      | 1000000111                 | string               |
       | educationOrganization       | 1                   | metaData.externalId      | NY-Parker                  | string               |
       | educationOrganization       | 1                   | metaData.externalId      | NY                         | string               |
    And I should see "Processed 718 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 8" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 8" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 15" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 15" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 566" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 566" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 16" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 16" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 90" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 90" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 16" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 16" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records considered: 7" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records ingested successfully: 7" in the resulting batch job file
    And I should see "InterchangeStudentDiscipline.xml records failed: 0" in the resulting batch job file
