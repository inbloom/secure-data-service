@RALLY_US0025
@RALLY_US149
@RALLY_US0609
@RALLY_US1689
@RALLY_US1690
@RALLY_US1691
@RALLY_US1804
@RALLY_US1876
@RALLY_US1889
@RALLY_US1929
@RALLY_US1964
@RALLY_US2033
@RALLY_US2081
@RALLY_DE85
@RALLY_DE87
@RALLY_DE621
@RALLY_US3122
@RALLY_US3202
@RALLY_US3200
@RALLY_US4162
Feature: Acceptance Storied Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

@smoke @integration @IL-Daybreak
Scenario: Post a zip file containing all data for Illinois Daybreak as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName                      |
        | student                             |
        | studentSchoolAssociation            |
        | course                              |
        | educationOrganization               |
        | section                             |
        | studentSectionAssociation           |
        | staff                               |
        |staffEducationOrganizationAssociation|
        | teacherSchoolAssociation            |
        | teacherSectionAssociation           |
        | session                             |
        | assessment                          |
        | studentAssessmentAssociation        |
        | gradebookEntry                      |
        | studentTranscriptAssociation        |
        | studentGradebookEntry               |
        | parent                              |
        | studentParentAssociation            |
        | attendance                          |
        | program                             |
        | staffProgramAssociation             |
        | studentProgramAssociation           |
        | cohort                              |
        | staffCohortAssociation              |
        | studentCohortAssociation            |
        | studentCompetency                   |
        | studentCompetencyObjective          |
        | learningStandard                    |
        | learningObjective                   |
        | disciplineIncident                  |
        | disciplineAction                    |
        | studentDisciplineIncidentAssociation|
        | grade                               |
        | gradingPeriod                       |
        | calendarDate                        |
        | reportCard                          |
        | courseOffering                      |
        | studentAcademicRecord               |
        | graduationPlan                      |
  When zip file is scp to ingestion landing zone
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
        | assessment                  | 19    |
        | studentAssessmentAssociation| 203   |
        | studentTranscriptAssociation| 196   |
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
        | learningStandard            | 1463  |
        | learningObjective           | 135   |
        | disciplineIncident          | 2     |
        | disciplineAction            | 2     |
        | studentDisciplineIncidentAssociation| 4|
        | grade                       | 4     |
        | gradingPeriod               | 17    |
        | calendarDate                | 556   |
        | reportCard                  | 2     |
        | courseOffering              | 95    |
        | studentAcademicRecord       | 117   |
        | graduationPlan              | 3     |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 1                   | body.studentUniqueStateId      | 100000000                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 800000012                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 900000024                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 800000025                  | string               |
       | staff                       | 1                   | body.staffUniqueStateId        | cgray                      | string               |
       | staff                       | 2                   | body.race                | White                      | string               |
       | staff                       | 1                   | body.staffUniqueStateId  | rbraverman                 | string               |
       | staff                       | 2                   | body.name.verification   | Drivers license            | string               |
       | course                      | 1                   | body.courseTitle     | 1st Grade Homeroom         | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | South Daybreak Elementary  | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | IL-DAYBREAK                | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | IL                         | string               |
       | program                     | 1                   | body.programId      | ACC-TEST-PROG-1            | string               |
       | program                     | 1                   | body.programId      | ACC-TEST-PROG-2            | string               |
       | attendance                  | 75                  | body.schoolYearAttendance.schoolYear            | 2011-2012     | string     |
       | attendance                  | 11                  | body.schoolYearAttendance.attendanceEvent.event | Tardy         | string     |
       | attendance                  | 75                  | body.schoolYearAttendance.attendanceEvent.event | In Attendance | string     |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-1             | string               |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-2             | string               |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-3             | string               |
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
       | studentAssessmentAssociation | 25                 | body.studentAssessmentItems.assessmentItem.identificationCode | AssessmentItem-3    | string |
       | studentAssessmentAssociation | 25                 | body.studentAssessmentItems.assessmentItem.identificationCode | AssessmentItem-4    | string |
       | studentAssessmentAssociation | 24                 | body.studentAssessmentItems.assessmentResponse                | True                | string |
       | studentAssessmentAssociation | 24                 | body.studentAssessmentItems.assessmentItemResult              | Correct             | string |
       | studentAssessmentAssociation | 10                 | body.studentAssessmentItems.assessmentResponse                | False               | string |
       | studentAssessmentAssociation | 10                 | body.studentAssessmentItems.assessmentItemResult              | Incorrect           | string |
       | studentParentAssociation     | 3                  | body.contactPriority                                          | 1                   | integer|
       | studentParentAssociation     | 2                  | body.contactRestrictions                                      | NO CONTACT ALLOWED  | string |
       | graduationPlan              | 1                   | metaData.externalId                            | GP-STANDARD      | string  |
       | graduationPlan              | 1                   | metaData.externalId                            | GP-MINIMUM       | string  |
       | graduationPlan              | 1                   | metaData.externalId                            | GP-ADVANCED      | string  |
    And I should see "Processed 4151 records." in the resulting batch job file
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
    And I should see "InterchangeMasterSchedule.xml records considered: 192" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 192" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 41" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 41" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 495" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 495" in the resulting batch job file
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
    And I should see "InterchangeAssessmentMetadata-CCS-English.xml records considered: 1024" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-English.xml records ingested successfully: 1024" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CCS-English.xml records failed: 0" in the resulting batch job file
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
    And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records considered: 25" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records ingested successfully: 25" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ACT.xml records failed: 0" in the resulting batch job file
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

@smoke
Scenario: Check the collections: Clean Database
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue                             | searchType           |
     | session                     | 1                   | body.sessionName            | Spring 2010 East Daybreak Junior High   | string               |
     | session                     | 4                   | body.schoolYear             | 2009-2010                               | string               |
     | session                     | 13                  | body.term                   | Fall Semester                           | string               |
     | session                     | 3                   | body.beginDate              | 2011-09-06                              | string               |
     | session                     | 3                   | body.endDate                | 2011-05-16                              | string               |
     | session                     | 1                   | body.endDate                | 2002-12-16                              | string               |
     | session                     | 22                  | body.totalInstructionalDays | 75                                      | integer              |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-1         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-2         | string               |
     | program                     | 0                   | body.programId              | ACC-TEST-PROG-3         | string               |
     | program                     | 1                   | body.programType            | Regular Education       | string               |
     | program                     | 1                   | body.programType            | Remedial Education      | string               |
     | program                     | 1                   | body.programSponsor         | State Education Agency  | string               |
     | program                     | 1                   | body.programSponsor         | Local Education Agency  | string               |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-1          | string               |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-2          | string               |
     | cohort                      | 2                   | body.cohortScope            | District                | string               |
     | cohort                      | 1                   | body.cohortScope            | Statewide               | string               |
     | cohort                      | 1                   | body.academicSubject        | English                 | string               |
     | cohort                      | 1                   | body.academicSubject        | Mathematics             | string               |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-03-04              | string               |
     | disciplineAction            | 0                   | body.disciplineDate         | 2011-05-04              | string               |
     | disciplineAction            | 2                   | body.disciplineActionLength | 74                      | integer              |
And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | studentProgramAssociation   | 3                   | body.beginDate              | 2011-01-01              | string               |
     | studentProgramAssociation   | 3                   | body.beginDate              | 2011-03-01              | string               |
     | studentProgramAssociation   | 1                   | body.endDate                | 2011-12-31              | string               |
     | studentProgramAssociation   | 1                   | body.endDate                | 2011-12-01              | string               |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | staffProgramAssociation     | 1                   | body.studentRecordAccess    | true                    | boolean              |
     | staffProgramAssociation     | 2                   | body.studentRecordAccess    | false                   | boolean              |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-01-01              | string               |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-01-05              | string               |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-06-01              | string               |
     | staffProgramAssociation     | 1                   | body.endDate                | 2012-02-15              | string               |
And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-02-01              | string               |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-04-01              | string               |
     | studentCohortAssociation    | 1                   | body.endDate                | 2012-01-15              | string               |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Disruption              | string               |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Tardiness               | string               |
     | disciplineIncident          | 2                   | body.incidentLocation       | On School               | string               |
     | disciplineIncident          | 1                   | body.incidentDate           | 2011-02-01              | string               |
 And I check to find if record is in collection:
     | collectionName    | expectedRecordCount   | searchParameter              | searchValue              | searchType            |
     | gradebookEntry    | 2                     | body.dateAssigned            | 2011-09-15               | string                |
     | gradebookEntry    | 3                     | body.dateAssigned            | 2011-09-29               | string                |
     | gradebookEntry    | 3                     | body.dateAssigned            | 2011-10-13               | string                |
     | gradebookEntry    | 2                     | body.dateAssigned            | 2011-10-27               | string                |
     | gradebookEntry    | 1                     | body.gradebookEntryType      | Unit test                | string                |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | staffCohortAssociation      | 3                   | body.studentRecordAccess    | true                    | boolean              |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2011-01-01              | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2011-07-01              | string               |
     | staffCohortAssociation      | 1                   | body.endDate                | 2012-02-15              | string               |
And I check to find if record is in collection:
     | collectionName                          | expectedRecordCount | searchParameter                     | searchValue          | searchType           |
     | studentDisciplineIncidentAssociation    | 2                   | body.studentParticipationCode       | Perpetrator          | string               |
     | studentDisciplineIncidentAssociation    | 1                   | body.studentParticipationCode       | Witness              | string               |
     | studentDisciplineIncidentAssociation    | 1                   | body.studentParticipationCode       | Victim               | string               |
 And I check to find if record is in collection:
       | collectionName                | expectedRecordCount | searchParameter                       | searchValue             | searchType           |
       | studentTranscriptAssociation  | 196                 | body.courseAttemptResult              | Pass                    | string               |
       | studentTranscriptAssociation  | 10                  | body.finalNumericGradeEarned          | 90                      | integer              |
       | studentTranscriptAssociation  | 4                   | body.finalNumericGradeEarned          | 87                      | integer              |
       | studentTranscriptAssociation  | 2                   | body.finalNumericGradeEarned          | 82                      | integer              |
       | studentTranscriptAssociation  | 33                  | body.finalLetterGradeEarned           | B                       | string               |
       | studentTranscriptAssociation  | 60                  | body.gradeLevelWhenTaken              | Tenth grade             | string               |
       | studentAcademicRecord         | 100                 | body.cumulativeCreditsAttempted.credit| 5                       | integer              |
And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                              | searchValue      |  searchType           |
     | assessment                  | 1                   | body.assessmentItem.identificationCode       | AssessmentItem-1 |   string              |
     | assessment                  | 1                   | body.assessmentItem.identificationCode       | AssessmentItem-2 |   string              |
     | assessment                  | 1                   | body.assessmentItem.identificationCode       | AssessmentItem-3 |   string              |
     | assessment                  | 1                   | body.assessmentItem.identificationCode       | AssessmentItem-4 |   string              |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue              | searchType           |
     | courseOffering              | 1                   | body.localCourseCode        | 3rd Grade Homeroom       | string               |
     | courseOffering              | 1                   | body.localCourseCode        | Government-4             | string               |
And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                 | searchValue   | searchType           |
     | attendance                  | 0                   | body.schoolYearAttendance.attendanceEvent.date  | 2011-09-01    | string               |
     | attendance                  | 75                  | body.schoolYearAttendance.attendanceEvent.date  | 2011-11-10    | string               |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                  | searchValue                                      |searchType           |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName               | AP.AP Eng.AP-Eng-and-Literature                  |string               |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName               | AP.AP Eng.AP-Lang-and-Literature                 |string               |
     | studentAssessmentAssociation| 0                   | body.performanceLevelDescriptors.0.1.description | Extremely well qualified                         |string               |
#    | studentSchoolAssociation     | 7                   | body.classOf                                     | 2011-2012    |
And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                                     | searchValue                                      |searchType           |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName                                  | ACT                                              |string               |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-English                                      |string               |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-English-Usage                                |string               |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-English-Rhetorical                           |string               |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-Mathematics                                  |string               |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Math-Pre-Algebra                             |string               |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Math-Algebra                                 |string               |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Math-Plane-Geometry                          |string               |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-Reading                                      |string               |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Reading-SocialStudies                        |string               |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | ACT-Reading-Arts                                 |string               |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-Science                                      |string               |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode                         | ACT-Writing                                      |string               |
     | studentAssessmentAssociation| 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode                        | ACT-English                  |string               |
     | studentAssessmentAssociation| 25                  | body.studentObjectiveAssessments.objectiveAssessment.objectiveAssessments.0.identificationCode | ACT-English-Usage            |string               |
     | studentAssessmentAssociation| 25                  | body.studentObjectiveAssessments.objectiveAssessment.objectiveAssessments.1.identificationCode | ACT-English-Rhetorical       |string               |
     | studentAssessmentAssociation| 12                  | body.studentObjectiveAssessments.scoreResults.0.result                                         | 15                           |string               |
     | studentAssessmentAssociation| 25                  | body.studentObjectiveAssessments.scoreResults.0.assessmentReportingMethod                      | Scale score                  |string               |
     | studentAssessmentAssociation| 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode                        | ACT-English-Usage            |string               |
     | studentAssessmentAssociation| 6                   | body.studentObjectiveAssessments.scoreResults.0.result                                         | 10                           |string               |
     | studentAssessmentAssociation| 25                  | body.studentObjectiveAssessments.scoreResults.0.assessmentReportingMethod                      | Scale score                  |string               |
     | studentAssessmentAssociation| 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode                        | ACT-English-Rhetorical       |string               |
     | studentAssessmentAssociation| 9                   | body.studentObjectiveAssessments.scoreResults.0.result                                         | 8                            |string               |
     | studentAssessmentAssociation| 25                  | body.studentObjectiveAssessments.scoreResults.0.assessmentReportingMethod                      | Scale score                  |string               |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |searchType           |
     | parent                      | 1                   | body.parentUniqueStateId      | 9870036500      |string               |
     | parent                      | 1                   | body.parentUniqueStateId      | 6473283635      |string               |
     | parent                      | 1                   | body.parentUniqueStateId      | 0798132465      |string               |
     | parent                      | 1                   | body.parentUniqueStateId      | 3597672174      |string               |
   And I check to find if record is in collection:
     | collectionName                        | expectedRecordCount | searchParameter               | searchValue     |searchType           |
     | staffEducationOrganizationAssociation |          9          | body.beginDate                | 1967-08-13      | string              |
     | staffEducationOrganizationAssociation |          1          | body.beginDate                | 2000-01-01      | string              |

@smoke
Scenario: Verify deterministic ids generated: Clean Database
  And I check that ids were generated properly:
    | collectionName                       | deterministicId                      | field                     | value                                |
    | student                              | 415924a0-3174-a2f3-af05-64f09d3e3d3e | body.studentUniqueStateId | 800000025                            |
    | staff                                | 90f4ba0f-9fd0-1be0-3f83-dd8cb519ecc2 | body.staffUniqueStateId   | jstevenson                           |
    | staff                                | 98b905f7-5b5d-c695-9a61-5656fdb93482 | body.staffUniqueStateId   | linda.kim                            |
# uncomment when edorg has a deterministic id
#   | cohort                               | ???????????????????????????????????? | body.cohortIdentifier     | ACC-TEST-COH-1                       |
#   | cohort                               | ???????????????????????????????????? | body.educationOrgId       | ????                                 |
# uncomment when cohort has a deterministic id
#   | studentCohortAssociation             | ???????????????????????????????????? | body.staffId              | ????                                 |
#   | studentCohortAssociation             | ???????????????????????????????????? | body.cohortId               | ????                                 |
#   | studentCohortAssociation             | ???????????????????????????????????? | body.beginDate              | ????                                 |
# uncomment when disciplineIncident has a deterministic id
#    | studentDisciplineIncidentAssociation | ???????????????????????????????????? | body.studentId              | 3c06e659-95b7-c1cc-f82b-2dd134f60551 |
#    | studentDisciplineIncidentAssociation | ???????????????????????????????????? | body.disciplineIncidentId    | ???????????????????????????????????? |
# uncomment when course has a deterministic id
#   | studentTranscriptAssociation         | ???????????????????????????????????? | body.studentAcademicRecordId            | ????                                 |
#   | studentTranscriptAssociation         | ???????????????????????????????????? | body.courseId                | ????                                 |
#   | studentTranscriptAssociation         | ???????????????????????????????????? | body.courseAttemptResult            | ????                                 |
# uncomment when parent has a deterministic id
#   | studentParentAssociation             | ???????????????????????????????????? | body.studentId            | ????                                 |
#   | studentParentAssociation             | ???????????????????????????????????? | body.parentId            | ????                                 |
# uncomment when school has a deterministic id
#   | studentSchoolAssociation             | ???????????????????????????????????? | body.studentId            | ????                                 |
#   | studentSchoolAssociation             | ???????????????????????????????????? | body.schoolId            | ????                                 |
#   | studentSchoolAssociation             | ???????????????????????????????????? | body.entryDate            | ????                                 |
# uncomment when section has a deterministic id
#   | teacherSectionAssociation            | ???????????????????????????????????? | body.teacherId            | ????                                 |
#   | teacherSectionAssociation            | ???????????????????????????????????? | body.sectionId            | ????                                 |

@smoke
Scenario: Verify ingestion context stamping for Midgar: Populated Database
   And I check _id of stateOrganizationId "IL" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 75    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 95    |
     | courseOffering                        | 95    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 5     |
     | grade                                 | 4     |
     | gradebookEntry                        | 12    |
     | gradingPeriod                         | 17    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 9     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 97    |
     | serviceDescriptor                     | 0     |
     | session                               | 22    |
     | staff                                 | 13    |
     | staffCohortAssociation                | 3     |
     | staffEducationOrganizationAssociation | 10    |
     | staffProgramAssociation               | 3     |
     | student                               | 78    |
     | studentAcademicRecord                 | 117   |
     | studentAssessmentAssociation          | 203   |
     | studentCohortAssociation              | 6     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 315   |
     | studentParentAssociation              | 9     |
     | studentProgramAssociation             | 6     |
     | studentSchoolAssociation              | 167   |
     | studentSectionAssociation             | 297   |
     | studentTranscriptAssociation          | 196   |
     | teacherSchoolAssociation              | 3     |
     | teacherSectionAssociation             | 11    |
   And I check _id of stateOrganizationId "IL-DAYBREAK" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 75    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 95    |
     | courseOffering                        | 95    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 4     |
     | grade                                 | 4     |
     | gradebookEntry                        | 12    |
     | gradingPeriod                         | 17    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 9     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 97    |
     | serviceDescriptor                     | 0     |
     | session                               | 22    |
     | staff                                 | 10    |
     | staffCohortAssociation                | 2     |
     | staffEducationOrganizationAssociation | 7     |
     | staffProgramAssociation               | 3     |
     | student                               | 78    |
     | studentAcademicRecord                 | 117   |
     | studentAssessmentAssociation          | 203   |
     | studentCohortAssociation              | 6     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 315   |
     | studentParentAssociation              | 9     |
     | studentProgramAssociation             | 6     |
     | studentSchoolAssociation              | 167   |
     | studentSectionAssociation             | 297   |
     | studentTranscriptAssociation          | 196   |
     | teacherSchoolAssociation              | 3     |
     | teacherSectionAssociation             | 11    |
   And I check _id of stateOrganizationId "East Daybreak Junior High" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 29    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 40    |
     | courseOffering                        | 41    |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 2     |
     | gradebookEntry                        | 5     |
     | gradingPeriod                         | 12    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 7     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 1     |
     | section                               | 41    |
     | serviceDescriptor                     | 0     |
     | session                               | 7     |
     | staff                                 | 1     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 0     |
     | staffProgramAssociation               | 0     |
     | student                               | 29    |
     | studentAcademicRecord                 | 68    |
     | studentAssessmentAssociation          | 127   |
     | studentCohortAssociation              | 3     |
     | studentCompetency                     | 27    |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 143   |
     | studentParentAssociation              | 7     |
     | studentProgramAssociation             | 3     |
     | studentSchoolAssociation              | 61    |
     | studentSectionAssociation             | 175   |
     | studentTranscriptAssociation          | 148   |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 5     |
   And I check _id of stateOrganizationId "South Daybreak Elementary" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 24    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 30    |
     | courseOffering                        | 29    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 4     |
     | gradebookEntry                        | 4     |
     | gradingPeriod                         | 15    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 7     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 31    |
     | serviceDescriptor                     | 0     |
     | session                               | 10    |
     | staff                                 | 4     |
     | staffCohortAssociation                | 2     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 26    |
     | studentAcademicRecord                 | 9     |
     | studentAssessmentAssociation          | 20    |
     | studentCohortAssociation              | 4     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 105   |
     | studentParentAssociation              | 7     |
     | studentProgramAssociation             | 4     |
     | studentSchoolAssociation              | 55    |
     | studentSectionAssociation             | 107   |
     | studentTranscriptAssociation          | 48    |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 5     |
   And I check _id of stateOrganizationId "Daybreak Central High" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 25    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 25    |
     | courseOffering                        | 25    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 3     |
     | gradingPeriod                         | 10    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 2     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 0     |
     | section                               | 25    |
     | serviceDescriptor                     | 0     |
     | session                               | 5     |
     | staff                                 | 1     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 0     |
     | staffProgramAssociation               | 0     |
     | student                               | 25    |
     | studentAcademicRecord                 | 54    |
     | studentAssessmentAssociation          | 75    |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 75    |
     | studentParentAssociation              | 2     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 51    |
     | studentSectionAssociation             | 110   |
     | studentTranscriptAssociation          | 84    |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 1     |

@integration @IL-Sunset
Scenario: Post a zip file containing all data for Illinois Sunset as a payload of the ingestion job: Append Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Sunset"
   And I post "StoriedDataSet_IL_Sunset.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
   And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 183   |
        | studentSchoolAssociation    | 272   |
        | course                      | 96    |
        | educationOrganization       | 7     |
        | section                     | 100   |
        | studentSectionAssociation   | 402   |
        | staff                       | 21    |
        | staffEducationOrganizationAssociation| 16 |
        | teacherSchoolAssociation    | 4     |
        | teacherSectionAssociation   | 14    |
        | session                     | 23    |
        | assessment                  | 19    |
        | studentAssessmentAssociation| 203   |
        | studentTranscriptAssociation| 196   |
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
        | disciplineIncident          | 2     |
        | disciplineAction            | 2     |
        | studentDisciplineIncidentAssociation| 4|
        | grade                       | 4     |
        | reportCard                  | 2     |
        | courseOffering              | 96    |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 1                   | body.studentUniqueStateId      | 1000000000                 | string               |
       | staff                       | 1                   | body.staffUniqueStateId  | manthony                   | string               |
       | course                      | 1                   | body.courseTitle      | A.P. Calculus              | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId | Sunset Central High School | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId | IL-SUNSET                  | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId | IL                         | string               |
    And I should see "Processed 342 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 105" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 105" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 3" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 3" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 3" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 3" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 17" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 17" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 210" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 210" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file

Scenario: Verify ingestion inline context stamping for Midgar: Populated Database
   And I check _id of stateOrganizationId "IL" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 75    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 96    |
     | courseOffering                        | 96    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 7     |
     | grade                                 | 4     |
     | gradebookEntry                        | 12    |
     | gradingPeriod                         | 18    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 9     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 100   |
     | serviceDescriptor                     | 0     |
     | session                               | 23    |
     | staff                                 | 20    |
     | staffCohortAssociation                | 3     |
     | staffEducationOrganizationAssociation | 16    |
     | staffProgramAssociation               | 3     |
     | student                               | 183   |
     | studentAcademicRecord                 | 117   |
     | studentAssessmentAssociation          | 203   |
     | studentCohortAssociation              | 6     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 315   |
     | studentParentAssociation              | 9     |
     | studentProgramAssociation             | 6     |
     | studentSchoolAssociation              | 272   |
     | studentSectionAssociation             | 402   |
     | studentTranscriptAssociation          | 196   |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 14    |
   And I check _id of stateOrganizationId "IL-DAYBREAK" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 75    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 95    |
     | courseOffering                        | 95    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 4     |
     | grade                                 | 4     |
     | gradebookEntry                        | 12    |
     | gradingPeriod                         | 17    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 9     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 97    |
     | serviceDescriptor                     | 0     |
     | session                               | 22    |
     | staff                                 | 10    |
     | staffCohortAssociation                | 2     |
     | staffEducationOrganizationAssociation | 7     |
     | staffProgramAssociation               | 3     |
     | student                               | 78    |
     | studentAcademicRecord                 | 117   |
     | studentAssessmentAssociation          | 203   |
     | studentCohortAssociation              | 6     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 315   |
     | studentParentAssociation              | 9     |
     | studentProgramAssociation             | 6     |
     | studentSchoolAssociation              | 167   |
     | studentSectionAssociation             | 297   |
     | studentTranscriptAssociation          | 196   |
     | teacherSchoolAssociation              | 3     |
     | teacherSectionAssociation             | 11    |
   And I check _id of stateOrganizationId "East Daybreak Junior High" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 29    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 40    |
     | courseOffering                        | 41    |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 2     |
     | gradebookEntry                        | 5     |
     | gradingPeriod                         | 12    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 7     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 1     |
     | section                               | 41    |
     | serviceDescriptor                     | 0     |
     | session                               | 7     |
     | staff                                 | 1     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 0     |
     | staffProgramAssociation               | 0     |
     | student                               | 29    |
     | studentAcademicRecord                 | 68    |
     | studentAssessmentAssociation          | 127   |
     | studentCohortAssociation              | 3     |
     | studentCompetency                     | 27    |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 143   |
     | studentParentAssociation              | 7     |
     | studentProgramAssociation             | 3     |
     | studentSchoolAssociation              | 61    |
     | studentSectionAssociation             | 175   |
     | studentTranscriptAssociation          | 148   |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 5     |
   And I check _id of stateOrganizationId "South Daybreak Elementary" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 24    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 30    |
     | courseOffering                        | 29    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 4     |
     | gradebookEntry                        | 4     |
     | gradingPeriod                         | 15    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 7     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 31    |
     | serviceDescriptor                     | 0     |
     | session                               | 10    |
     | staff                                 | 4     |
     | staffCohortAssociation                | 2     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 26    |
     | studentAcademicRecord                 | 9     |
     | studentAssessmentAssociation          | 20    |
     | studentCohortAssociation              | 4     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 105   |
     | studentParentAssociation              | 7     |
     | studentProgramAssociation             | 4     |
     | studentSchoolAssociation              | 55    |
     | studentSectionAssociation             | 107   |
     | studentTranscriptAssociation          | 48    |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 5     |
   And I check _id of stateOrganizationId "Daybreak Central High" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 25    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 25    |
     | courseOffering                        | 25    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 3     |
     | gradingPeriod                         | 10    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 2     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 0     |
     | section                               | 25    |
     | serviceDescriptor                     | 0     |
     | session                               | 5     |
     | staff                                 | 1     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 0     |
     | staffProgramAssociation               | 0     |
     | student                               | 25    |
     | studentAcademicRecord                 | 54    |
     | studentAssessmentAssociation          | 75    |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 75    |
     | studentParentAssociation              | 2     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 51    |
     | studentSectionAssociation             | 110   |
     | studentTranscriptAssociation          | 84    |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 1     |
   And I check _id of stateOrganizationId "IL-SUNSET" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 2     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 1     |
     | courseOffering                        | 1     |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 2     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 1     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 1     |
     | reportCard                            | 0     |
     | section                               | 3     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 6     |
     | staffProgramAssociation               | 0     |
     | student                               | 105   |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 105   |
     | studentSectionAssociation             | 105   |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 3     |
   And I check _id of stateOrganizationId "Sunset Central High School" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 2     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 1     |
     | courseOffering                        | 1     |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 1     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 1     |
     | reportCard                            | 0     |
     | section                               | 3     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 4     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 105   |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 105   |
     | studentSectionAssociation             | 105   |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 3     |

@integration @NY-NYC
Scenario: Post a zip file containing all data for New York as a payload of the ingestion job: Append Database
Given I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
  And I post "StoriedDataSet_NY.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 191    |
        | studentSchoolAssociation    | 280   |
        | course                      | 104   |
        | educationOrganization       | 14    |
        | section                     | 116   |
        | studentSectionAssociation   | 410   |
        | staff                       | 58    |
        | staffEducationOrganizationAssociation| 37 |
        | teacherSchoolAssociation    | 20    |
        | teacherSectionAssociation   | 30    |
        | session                     | 27    |
        | assessment                  | 19    |
        | studentAssessmentAssociation| 203   |
        | studentTranscriptAssociation| 196   |
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
        | disciplineIncident          | 4     |
        | disciplineAction            | 3     |
        | studentDisciplineIncidentAssociation| 8|
        | grade                       | 4     |
        | reportCard                  | 2     |
        | courseOffering              | 104   |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 2                   | body.studentUniqueStateId     | 100000006                  | string               |
       | staff                       | 1                   | body.staffUniqueStateId  | jcarlyle                   | string               |
       | section                     | 1                   | body.uniqueSectionCode   | Mason201-Sec1              | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId | 1000000111                 | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId | NY-Parker                  | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId | NY                         | string               |
    And I should see "Processed 726 records." in the resulting batch job file
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
    And I should see "InterchangeMasterSchedule.xml records considered: 24" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 24" in the resulting batch job file
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

Scenario: Verify ingestion inline context stamping for Midgar and Hyrule: Populated Database
   And I check _id of stateOrganizationId "IL" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 75    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 96    |
     | courseOffering                        | 96    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 7     |
     | grade                                 | 4     |
     | gradebookEntry                        | 12    |
     | gradingPeriod                         | 18    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 9     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 100   |
     | serviceDescriptor                     | 0     |
     | session                               | 23    |
     | staff                                 | 20    |
     | staffCohortAssociation                | 3     |
     | staffEducationOrganizationAssociation | 16    |
     | staffProgramAssociation               | 3     |
     | student                               | 183   |
     | studentAcademicRecord                 | 117   |
     | studentAssessmentAssociation          | 203   |
     | studentCohortAssociation              | 6     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 315   |
     | studentParentAssociation              | 9     |
     | studentProgramAssociation             | 6     |
     | studentSchoolAssociation              | 272   |
     | studentSectionAssociation             | 402   |
     | studentTranscriptAssociation          | 196   |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 14    |
   And I check _id of stateOrganizationId "IL-DAYBREAK" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 75    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 95    |
     | courseOffering                        | 95    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 4     |
     | grade                                 | 4     |
     | gradebookEntry                        | 12    |
     | gradingPeriod                         | 17    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 9     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 97    |
     | serviceDescriptor                     | 0     |
     | session                               | 22    |
     | staff                                 | 10    |
     | staffCohortAssociation                | 2     |
     | staffEducationOrganizationAssociation | 7     |
     | staffProgramAssociation               | 3     |
     | student                               | 78    |
     | studentAcademicRecord                 | 117   |
     | studentAssessmentAssociation          | 203   |
     | studentCohortAssociation              | 6     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 315   |
     | studentParentAssociation              | 9     |
     | studentProgramAssociation             | 6     |
     | studentSchoolAssociation              | 167   |
     | studentSectionAssociation             | 297   |
     | studentTranscriptAssociation          | 196   |
     | teacherSchoolAssociation              | 3     |
     | teacherSectionAssociation             | 11    |
   And I check _id of stateOrganizationId "East Daybreak Junior High" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 29    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 40    |
     | courseOffering                        | 41    |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 2     |
     | gradebookEntry                        | 5     |
     | gradingPeriod                         | 12    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 7     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 1     |
     | section                               | 41    |
     | serviceDescriptor                     | 0     |
     | session                               | 7     |
     | staff                                 | 1     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 0     |
     | staffProgramAssociation               | 0     |
     | student                               | 29    |
     | studentAcademicRecord                 | 68    |
     | studentAssessmentAssociation          | 127   |
     | studentCohortAssociation              | 3     |
     | studentCompetency                     | 27    |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 143   |
     | studentParentAssociation              | 7     |
     | studentProgramAssociation             | 3     |
     | studentSchoolAssociation              | 61    |
     | studentSectionAssociation             | 175   |
     | studentTranscriptAssociation          | 148   |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 5     |
   And I check _id of stateOrganizationId "South Daybreak Elementary" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 24    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 30    |
     | courseOffering                        | 29    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 4     |
     | gradebookEntry                        | 4     |
     | gradingPeriod                         | 15    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 7     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 31    |
     | serviceDescriptor                     | 0     |
     | session                               | 10    |
     | staff                                 | 4     |
     | staffCohortAssociation                | 2     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 26    |
     | studentAcademicRecord                 | 9     |
     | studentAssessmentAssociation          | 20    |
     | studentCohortAssociation              | 4     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 105   |
     | studentParentAssociation              | 7     |
     | studentProgramAssociation             | 4     |
     | studentSchoolAssociation              | 55    |
     | studentSectionAssociation             | 107   |
     | studentTranscriptAssociation          | 48    |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 5     |
   And I check _id of stateOrganizationId "Daybreak Central High" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 25    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 25    |
     | courseOffering                        | 25    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 3     |
     | gradingPeriod                         | 10    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 2     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 0     |
     | section                               | 25    |
     | serviceDescriptor                     | 0     |
     | session                               | 5     |
     | staff                                 | 1     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 0     |
     | staffProgramAssociation               | 0     |
     | student                               | 25    |
     | studentAcademicRecord                 | 54    |
     | studentAssessmentAssociation          | 75    |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 75    |
     | studentParentAssociation              | 2     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 51    |
     | studentSectionAssociation             | 110   |
     | studentTranscriptAssociation          | 84    |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 1     |
   And I check _id of stateOrganizationId "IL-SUNSET" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 2     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 1     |
     | courseOffering                        | 1     |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 2     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 1     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 1     |
     | reportCard                            | 0     |
     | section                               | 3     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 6     |
     | staffProgramAssociation               | 0     |
     | student                               | 105   |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 105   |
     | studentSectionAssociation             | 105   |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 3     |
   And I check _id of stateOrganizationId "Sunset Central High School" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 2     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 1     |
     | courseOffering                        | 1     |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 1     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 1     |
     | reportCard                            | 0     |
     | section                               | 3     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 4     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 105   |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 105   |
     | studentSectionAssociation             | 105   |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 3     |
   And I check _id of stateOrganizationId "NY" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 8     |
     | courseOffering                        | 8     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 7     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 6     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 16    |
     | serviceDescriptor                     | 0     |
     | session                               | 4     |
     | staff                                 | 37    |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 21    |
     | staffProgramAssociation               | 0     |
     | student                               | 8     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 8     |
     | studentSectionAssociation             | 8     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 16    |
     | teacherSectionAssociation             | 16    |
   And I check _id of stateOrganizationId "NY-Dusk" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 4     |
     | courseOffering                        | 4     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 3     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 5     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 8     |
     | serviceDescriptor                     | 0     |
     | session                               | 2     |
     | staff                                 | 17    |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 9     |
     | staffProgramAssociation               | 0     |
     | student                               | 4     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 4     |
     | studentSectionAssociation             | 5     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 8     |
     | teacherSectionAssociation             | 8     |
   And I check _id of stateOrganizationId "NY-Parker" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 4     |
     | courseOffering                        | 4     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 3     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 4     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 8     |
     | serviceDescriptor                     | 0     |
     | session                               | 2     |
     | staff                                 | 17    |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 9     |
     | staffProgramAssociation               | 0     |
     | student                               | 4     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 4     |
     | studentSectionAssociation             | 4     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 8     |
     | teacherSectionAssociation             | 8     |
   And I check _id of stateOrganizationId "1000000112" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 2     |
     | courseOffering                        | 2     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 4     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 4     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 4     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 4     |
     | studentSectionAssociation             | 4     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 4     |
   And I check _id of stateOrganizationId "10000000121" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 2     |
     | courseOffering                        | 2     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 5     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 4     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 0     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 0     |
     | studentSectionAssociation             | 0     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 4     |
   And I check _id of stateOrganizationId "1000000111" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 2     |
     | courseOffering                        | 2     |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 3     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 4     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 0     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 0     |
     | studentSectionAssociation             | 0     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 4     |
   And I check _id of stateOrganizationId "1000000122" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 2     |
     | courseOffering                        | 2     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 3     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 4     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 4     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 4     |
     | studentSectionAssociation             | 5     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 4     |

Scenario: Post an append zip file containing append data for Illinois Daybreak as a payload of the ingestion job: Append Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "StoriedDataSet_IL_Daybreak_Append.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                       | count |
     | program                              | 4     |
     | cohort                               | 4     |
     | disciplineAction                     | 4     |
     | studentProgramAssociation            | 9     |
     | disciplineIncident                   | 7     |
     | staffProgramAssociation              | 7     |
     | studentCohortAssociation             | 6     |
     | gradebookEntry                       | 13    |
     | staffCohortAssociation               | 5     |
     | studentDisciplineIncidentAssociation | 9     |
     | studentTranscriptAssociation         | 200   |
     | studentAcademicRecord                | 121   |
     | courseOffering                       | 105   |
     | attendance                           | 75    |
     | assessment                           | 23    |
     | studentAssessmentAssociation         | 204   |
     | student                              | 193   |
     | parent                               | 12    |
     | studentParentAssociation             | 11    |
  And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-1         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-2         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-3         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-4         | string               |
     | program                     | 3                   | body.programType            | Regular Education       | string               |
     | program                     | 1                   | body.programType            | Remedial Education      | string               |
     | program                     | 1                   | body.programSponsor         | State Education Agency  | string               |
     | program                     | 3                   | body.programSponsor         | Local Education Agency  | string               |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-1          | string               |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-2          | string               |
     | cohort                      | 2                   | body.cohortIdentifier       | ACC-TEST-COH-3          | string               |
     | cohort                      | 3                   | body.cohortScope            | District                | string               |
     | cohort                      | 1                   | body.cohortScope            | Statewide               | string               |
     | cohort                      | 1                   | body.academicSubject        | English                 | string               |
     | cohort                      | 2                   | body.academicSubject        | Social Studies          | string               |
     | cohort                      | 1                   | body.academicSubject        | Mathematics             | string               |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-03-04              | string               |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-04-04              | string               |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-05-04              | string               |
     | disciplineAction            | 3                   | body.disciplineActionLength | 74                      | integer              |
     | studentProgramAssociation   | 3                   | body.beginDate              | 2011-01-01              | string               |
     | studentProgramAssociation   | 4                   | body.beginDate              | 2011-03-01              | string               |
     | studentProgramAssociation   | 2                   | body.beginDate              | 2011-05-01              | string               |
     | studentProgramAssociation   | 1                   | body.endDate                | 2011-12-31              | string               |
     | studentProgramAssociation   | 1                   | body.endDate                | 2012-02-15              | string               |
     | studentProgramAssociation   | 6                   | body.endDate                | 2012-04-12              | string               |
     | staffProgramAssociation     | 4                   | body.studentRecordAccess    | true                    | boolean              |
     | staffProgramAssociation     | 3                   | body.studentRecordAccess    | false                   | boolean              |
     | staffProgramAssociation     | 2                   | body.beginDate              | 2011-01-01              | string               |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-01-02              | string               |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-05-02              | string               |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-06-02              | string               |
     | staffProgramAssociation     | 3                   | body.endDate                | 2012-02-15              | string               |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-02-01              | string               |
     | studentCohortAssociation    | 1                   | body.endDate                | 2011-12-31              | string               |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-03-01              | string               |
     | gradebookEntry              | 4                   | body.dateAssigned           | 2011-10-13              | string               |
     | gradebookEntry              | 2                   | body.dateAssigned           | 2011-10-27              | string               |
     | gradebookEntry              | 4                   | body.gradebookEntryType     | Quiz                    | string               |
     | attendance                  | 75                  | body.schoolYearAttendance.attendanceEvent.date | 2011-09-06      |string               |
     | disciplineIncident          | 2                   | body.incidentIdentifier     | Disruption              | string               |
     | disciplineIncident          | 2                   | body.incidentIdentifier     | Tardiness               | string               |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Bullying                | string               |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Hazing                  | string               |
     | disciplineIncident          | 5                   | body.incidentLocation       | On School               | string               |
     | disciplineIncident          | 3                   | body.incidentDate           | 2011-02-01              | string               |
     | disciplineIncident          | 2                   | body.weapons                | Non-Illegal Knife       | string               |
     | staffCohortAssociation      | 5                   | body.studentRecordAccess    | true                    | boolean              |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2011-01-01              | string               |
     | staffCohortAssociation      | 2                   | body.beginDate              | 2011-07-01              | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2012-02-15              | string               |
     | staffCohortAssociation      | 1                   | body.endDate                | 2012-02-15              | string               |
     | studentDisciplineIncidentAssociation    | 4                   | body.studentParticipationCode       | Perpetrator          | string               |
     | studentDisciplineIncidentAssociation    | 2                   | body.studentParticipationCode       | Witness              | string               |
     | studentDisciplineIncidentAssociation    | 3                   | body.studentParticipationCode       | Victim               | string               |
     | studentTranscriptAssociation  | 200                 | body.courseAttemptResult              | Pass                    | string               |
    | studentTranscriptAssociation  | 10                  | body.finalNumericGradeEarned          | 90                      | integer              |
    | studentTranscriptAssociation  | 5                   | body.finalNumericGradeEarned          | 87                      | integer              |
    | studentTranscriptAssociation  | 3                   | body.finalNumericGradeEarned          | 82                      | integer              |
    | studentTranscriptAssociation  | 36                  | body.finalLetterGradeEarned           | B                       | string               |
    | studentTranscriptAssociation  | 64                  | body.gradeLevelWhenTaken              | Tenth grade             | string               |
     | studentAcademicRecord         | 104                 | body.cumulativeCreditsAttempted.credit| 5                       | integer              |
     | courseOffering              | 1                   | body.localCourseTitle       | Government-4A             | string               |
     | courseOffering              | 2                   | body.localCourseTitle       | Government-4              | string               |
     | courseOffering              | 0                   | body.localCourseTitle       | 3rd Grade Homeroom        | string               |
     | assessment                  | 1                   | body.assessmentItem.identificationCode      | AssessmentItem-1 |string                  |
     | assessment                  | 1                   | body.assessmentItem.identificationCode      | AssessmentItem-2 |string                  |
     | assessment                  | 1                   | body.assessmentItem.identificationCode      | AssessmentItem-3 |string                  |
     | assessment                  | 1                   | body.assessmentItem.identificationCode      | AssessmentItem-4 |string                  |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName               | AP.AP Eng.AP-Eng-and-Literature      |string                  |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName               | AP.AP Eng.AP-Lang-and-Literature     |string                  |
     | studentAssessmentAssociation| 8                   | body.performanceLevelDescriptors.0.1.description | Extremely well qualified             |string                  |
     | studentAssessmentAssociation| 26                  | body.studentAssessmentItems.assessmentItem.identificationCode  | AssessmentItem-4       |string                  |
     | studentAssessmentAssociation| 26                  | body.studentAssessmentItems.assessmentItem.identificationCode  | AssessmentItem-3       |string                  |
     | parent                      | 1                   | body.parentUniqueStateId      | 3152281864      |string                  |
     | parent                      | 1                   | body.parentUniqueStateId      | 2521899635      |string                  |
     | assessment                  | 3                   | body.assessmentFamilyHierarchyName             | READ2.READ 2.0.READ 2.0 Kindergarten                 | string |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | BOY                                              | string |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | MOY                                              | string |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | EOY                                              | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Writing                                      | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Math                                         | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Critical Reading                             | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Arithmetic         | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Algebra            | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Geometry           | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-English          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Reading          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Mathematics      | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Science          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Writing          | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode         | AssessmentItem-1 | string |
     | assessment                  | 1                   | body.assessmentItem.itemCategory               | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.maxRawScore                | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.correctResponse            | False            | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode         | AssessmentItem-2 | string |
     | assessment                  | 1                   | body.assessmentItem.itemCategory               | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.maxRawScore                | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.correctResponse            | True             | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode         | AssessmentItem-3 | string |
     | assessment                  | 1                   | body.assessmentItem.itemCategory               | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.correctResponse            | True             | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode         | AssessmentItem-4 | string |
     | assessment                  | 1                   | body.assessmentItem.itemCategory               | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.maxRawScore                | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.correctResponse            | False            | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Writing                       | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Mathematics             | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Reading-Arts            | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Math-Algebra            | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-English-Rhetorical   | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Math-Pre-Algebra    | string |
     | studentAssessmentAssociation | 26                  | body.studentAssessmentItems.assessmentItem.identificationCode | AssessmentItem-3    | string |
     | studentAssessmentAssociation | 25                  | body.studentAssessmentItems.assessmentResponse                | True                | string |
     | studentAssessmentAssociation | 26                  | body.studentAssessmentItems.assessmentItem.identificationCode | AssessmentItem-4    | string |
     | studentAssessmentAssociation | 10                  | body.studentAssessmentItems.assessmentResponse                | False               | string |
  When I find a record in "assessment" under "body.assessmentItem" where "identificationCode" is "AssessmentItem-1"
  Then the field "learningStandards" is an array of size 2
  And "learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-C.4"
  When I find a record in "assessment" under "body.assessmentItem" where "identificationCode" is "AssessmentItem-2"
  Then the field "learningStandards" is an array of size 2
  And "learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.3"
  When I find a record in "assessment" under "body.assessmentItem" where "identificationCode" is "AssessmentItem-3"
  Then the field "learningStandards" is an array of size 1
  And "learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.5"
  When I find a record in "assessment" under "body.assessmentItem" where "identificationCode" is "AssessmentItem-4"
  Then the field "learningStandards" is an array of size 1
  And "learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.6"
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 113 records." in the resulting batch job file
  And I should see "Program2.xml records considered: 4" in the resulting batch job file
  And I should see "Program2.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "Program2.xml records failed: 0" in the resulting batch job file
  And I should see "Cohort2.xml records considered: 1" in the resulting batch job file
  And I should see "Cohort2.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "Cohort2.xml records failed: 0" in the resulting batch job file
  And I should see "DisciplineAction2.xml records considered: 2" in the resulting batch job file
  And I should see "DisciplineAction2.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "DisciplineAction2.xml records failed: 0" in the resulting batch job file
  And I should see "StudentProgramAssociation2.xml records considered: 9" in the resulting batch job file
  And I should see "StudentProgramAssociation2.xml records ingested successfully: 9" in the resulting batch job file
  And I should see "StudentProgramAssociation2.xml records failed: 0" in the resulting batch job file
  And I should see "Staff2.xml records considered: 4" in the resulting batch job file
  And I should see "Staff2.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "Staff2.xml records failed: 0" in the resulting batch job file
  And I should see "StudentCohortAssociation2.xml records considered: 1" in the resulting batch job file
  And I should see "StudentCohortAssociation2.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "StudentCohortAssociation2.xml records failed: 0" in the resulting batch job file
  And I should see "GradeBookEntry2.xml records considered: 1" in the resulting batch job file
  And I should see "GradeBookEntry2.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "GradeBookEntry2.xml records failed: 0" in the resulting batch job file
  And I should see "DisciplineIncident2.xml records considered: 2" in the resulting batch job file
  And I should see "DisciplineIncident2.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "DisciplineIncident2.xml records failed: 0" in the resulting batch job file
  And I should see "StaffCohortAssociation2.xml records considered: 2" in the resulting batch job file
  And I should see "StaffCohortAssociation2.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "StaffCohortAssociation2.xml records failed: 0" in the resulting batch job file
  And I should see "Discipline2.xml records considered: 2" in the resulting batch job file
  And I should see "Discipline2.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "Discipline2.xml records failed: 0" in the resulting batch job file
  And I should see "StudentTranscriptAssociation2.xml records considered: 8" in the resulting batch job file
  And I should see "StudentTranscriptAssociation2.xml records ingested successfully: 8" in the resulting batch job file
  And I should see "StudentTranscriptAssociation2.xml records failed: 0" in the resulting batch job file
  And I should see "CourseOffering.xml records considered: 2" in the resulting batch job file
  And I should see "CourseOffering.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "CourseOffering.xml records failed: 0" in the resulting batch job file
  And I should see "actAssessment_CCSMapping.xml records considered: 1" in the resulting batch job file
  And I should see "actAssessment_CCSMapping.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "actAssessment_CCSMapping.xml records failed: 0" in the resulting batch job file
  And I should see "Grade_12_Math_CCS_G_SRT.xml records considered: 12" in the resulting batch job file
  And I should see "Grade_12_Math_CCS_G_SRT.xml records ingested successfully: 12" in the resulting batch job file
  And I should see "Grade_12_Math_CCS_G_SRT.xml records failed: 0" in the resulting batch job file
  And I should see "Grade_12_Math_CCS_G_C.xml records considered: 7" in the resulting batch job file
  And I should see "Grade_12_Math_CCS_G_C.xml records ingested successfully: 7" in the resulting batch job file
  And I should see "Grade_12_Math_CCS_G_C.xml records failed: 0" in the resulting batch job file
  And I should see "Grade_12_English_CCS_RI_11_12.xml records considered: 17" in the resulting batch job file
  And I should see "Grade_12_English_CCS_RI_11_12.xml records ingested successfully: 17" in the resulting batch job file
  And I should see "Grade_12_English_CCS_RI_11_12.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-AP-Eng.xml records considered: 6" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-AP-Eng.xml records ingested successfully: 6" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-AP-Eng.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudentAssessment-CgrayAP-English.xml records considered: 11" in the resulting batch job file
  And I should see "InterchangeStudentAssessment-CgrayAP-English.xml records ingested successfully: 11" in the resulting batch job file
  And I should see "InterchangeStudentAssessment-CgrayAP-English.xml records failed: 0" in the resulting batch job file
  And I should see "StudentAssessmentItem_ACTAssessmentItem_Mapping.xml records considered: 1" in the resulting batch job file
  And I should see "StudentAssessmentItem_ACTAssessmentItem_Mapping.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "StudentAssessmentItem_ACTAssessmentItem_Mapping.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeStudent.xml records considered: 13" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 12" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 1" in the resulting batch job file
  And I should see "InterchangeEducationOrgCalendar.xml records considered: 3" in the resulting batch job file
  And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records considered: 3" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file

Scenario: Concurrent job processing
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job for "Midgar-Daybreak"
    And I post "StoriedDataSet_NY.zip" file as the payload of the ingestion job for "Hyrule-NYC"
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
        | courseOffering              |
        | studentAcademicRecord       |

When zip file is scp to ingestion landing zone for "Midgar-Daybreak"
  And zip file is scp to ingestion landing zone for "Hyrule-NYC"
  And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database
  And a batch job for file "StoriedDataSet_NY.zip" is completed in database

Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 86    |
        | studentSchoolAssociation    | 175   |
        | course                      | 103   |
        | educationOrganization       | 12    |
        | section                     | 113   |
        | studentSectionAssociation   | 305   |
        | staff                       | 51    |
        | staffEducationOrganizationAssociation| 31 |
        | teacherSchoolAssociation    | 19    |
        | teacherSectionAssociation   | 27    |
        | session                     | 26    |
        | assessment                  | 19    |
        | studentAssessmentAssociation| 203   |
        | studentTranscriptAssociation| 196   |
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
        | learningStandard            | 1463  |
        | learningObjective           | 135   |
        | disciplineIncident          | 4     |
        | disciplineAction            | 3     |
    | studentDisciplineIncidentAssociation| 8 |
        | grade                       | 4     |
        | gradingPeriod               | 23    |
        | calendarDate                | 1112  |
        | reportCard                  | 2     |
        | courseOffering              | 103   |
        | studentAcademicRecord       | 117   |

Scenario: Verify concurrent ingestion inline context stamping for Midgar and Hyrule: Populated Database
   And I check _id of stateOrganizationId "IL" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 75    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 95    |
     | courseOffering                        | 95    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 5     |
     | grade                                 | 4     |
     | gradebookEntry                        | 12    |
     | gradingPeriod                         | 17    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 9     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 97    |
     | serviceDescriptor                     | 0     |
     | session                               | 22    |
     | staff                                 | 13    |
     | staffCohortAssociation                | 3     |
     | staffEducationOrganizationAssociation | 10    |
     | staffProgramAssociation               | 3     |
     | student                               | 78    |
     | studentAcademicRecord                 | 117   |
     | studentAssessmentAssociation          | 203   |
     | studentCohortAssociation              | 6     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 315   |
     | studentParentAssociation              | 9     |
     | studentProgramAssociation             | 6     |
     | studentSchoolAssociation              | 167   |
     | studentSectionAssociation             | 297   |
     | studentTranscriptAssociation          | 196   |
     | teacherSchoolAssociation              | 3     |
     | teacherSectionAssociation             | 11    |
   And I check _id of stateOrganizationId "IL-DAYBREAK" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 75    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 95    |
     | courseOffering                        | 95    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 4     |
     | grade                                 | 4     |
     | gradebookEntry                        | 12    |
     | gradingPeriod                         | 17    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 9     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 97    |
     | serviceDescriptor                     | 0     |
     | session                               | 22    |
     | staff                                 | 10    |
     | staffCohortAssociation                | 2     |
     | staffEducationOrganizationAssociation | 7     |
     | staffProgramAssociation               | 3     |
     | student                               | 78    |
     | studentAcademicRecord                 | 117   |
     | studentAssessmentAssociation          | 203   |
     | studentCohortAssociation              | 6     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 315   |
     | studentParentAssociation              | 9     |
     | studentProgramAssociation             | 6     |
     | studentSchoolAssociation              | 167   |
     | studentSectionAssociation             | 297   |
     | studentTranscriptAssociation          | 196   |
     | teacherSchoolAssociation              | 3     |
     | teacherSectionAssociation             | 11    |
   And I check _id of stateOrganizationId "East Daybreak Junior High" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 29    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 40    |
     | courseOffering                        | 41    |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 2     |
     | gradebookEntry                        | 5     |
     | gradingPeriod                         | 12    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 7     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 1     |
     | section                               | 41    |
     | serviceDescriptor                     | 0     |
     | session                               | 7     |
     | staff                                 | 1     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 0     |
     | staffProgramAssociation               | 0     |
     | student                               | 29    |
     | studentAcademicRecord                 | 68    |
     | studentAssessmentAssociation          | 127   |
     | studentCohortAssociation              | 3     |
     | studentCompetency                     | 27    |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 143   |
     | studentParentAssociation              | 7     |
     | studentProgramAssociation             | 3     |
     | studentSchoolAssociation              | 61    |
     | studentSectionAssociation             | 175   |
     | studentTranscriptAssociation          | 148   |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 5     |
   And I check _id of stateOrganizationId "South Daybreak Elementary" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 24    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 30    |
     | courseOffering                        | 29    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 4     |
     | gradebookEntry                        | 4     |
     | gradingPeriod                         | 15    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 7     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 2     |
     | section                               | 31    |
     | serviceDescriptor                     | 0     |
     | session                               | 10    |
     | staff                                 | 4     |
     | staffCohortAssociation                | 2     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 26    |
     | studentAcademicRecord                 | 9     |
     | studentAssessmentAssociation          | 20    |
     | studentCohortAssociation              | 4     |
     | studentCompetency                     | 59    |
     | studentCompetencyObjective            | 4     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 105   |
     | studentParentAssociation              | 7     |
     | studentProgramAssociation             | 4     |
     | studentSchoolAssociation              | 55    |
     | studentSectionAssociation             | 107   |
     | studentTranscriptAssociation          | 48    |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 5     |
   And I check _id of stateOrganizationId "Daybreak Central High" with tenantId "Midgar" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 25    |
     | calendarDate                          | 0     |
     | cohort                                | 3     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 25    |
     | courseOffering                        | 25    |
     | disciplineAction                      | 2     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 3     |
     | gradingPeriod                         | 10    |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 2     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 2     |
     | reportCard                            | 0     |
     | section                               | 25    |
     | serviceDescriptor                     | 0     |
     | session                               | 5     |
     | staff                                 | 1     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 0     |
     | staffProgramAssociation               | 0     |
     | student                               | 25    |
     | studentAcademicRecord                 | 54    |
     | studentAssessmentAssociation          | 75    |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 75    |
     | studentParentAssociation              | 2     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 51    |
     | studentSectionAssociation             | 110   |
     | studentTranscriptAssociation          | 84    |
     | teacherSchoolAssociation              | 1     |
     | teacherSectionAssociation             | 1     |
   And I check _id of stateOrganizationId "NY" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 8     |
     | courseOffering                        | 8     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 7     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 6     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 16    |
     | serviceDescriptor                     | 0     |
     | session                               | 4     |
     | staff                                 | 37    |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 21    |
     | staffProgramAssociation               | 0     |
     | student                               | 8     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 8     |
     | studentSectionAssociation             | 8     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 16    |
     | teacherSectionAssociation             | 16    |
   And I check _id of stateOrganizationId "NY-Dusk" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 4     |
     | courseOffering                        | 4     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 3     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 5     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 8     |
     | serviceDescriptor                     | 0     |
     | session                               | 2     |
     | staff                                 | 17    |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 9     |
     | staffProgramAssociation               | 0     |
     | student                               | 4     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 4     |
     | studentSectionAssociation             | 5     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 8     |
     | teacherSectionAssociation             | 8     |
   And I check _id of stateOrganizationId "NY-Parker" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 4     |
     | courseOffering                        | 4     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 3     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 4     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 8     |
     | serviceDescriptor                     | 0     |
     | session                               | 2     |
     | staff                                 | 17    |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 9     |
     | staffProgramAssociation               | 0     |
     | student                               | 4     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 4     |
     | studentSectionAssociation             | 4     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 8     |
     | teacherSectionAssociation             | 8     |
   And I check _id of stateOrganizationId "1000000112" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 2     |
     | courseOffering                        | 2     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 4     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 4     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 4     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 4     |
     | studentSectionAssociation             | 4     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 4     |
   And I check _id of stateOrganizationId "10000000121" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 2     |
     | courseOffering                        | 2     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 2     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 5     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 4     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 0     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 4     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 0     |
     | studentSectionAssociation             | 0     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 4     |
   And I check _id of stateOrganizationId "1000000111" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 2     |
     | courseOffering                        | 2     |
     | disciplineAction                      | 0     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 3     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 4     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 0     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 0     |
     | studentSectionAssociation             | 0     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 4     |
   And I check _id of stateOrganizationId "1000000122" with tenantId "Hyrule" is in metaData.edOrgs:
     | collectionName                        | count |
     | assessment                            | 0     |
     | attendance                            | 0     |
     | calendarDate                          | 0     |
     | cohort                                | 0     |
     | compentencyLevelDescriptor            | 0     |
     | course                                | 2     |
     | courseOffering                        | 2     |
     | disciplineAction                      | 1     |
     | disciplineIncident                    | 0     |
     | educationOrganization                 | 1     |
     | grade                                 | 0     |
     | gradebookEntry                        | 0     |
     | gradingPeriod                         | 3     |
     | graduationPlan                        | 0     |
     | learningObjective                     | 0     |
     | learningStandard                      | 0     |
     | parent                                | 0     |
     | performanceLevelDescriptor            | 0     |
     | program                               | 0     |
     | reportCard                            | 0     |
     | section                               | 4     |
     | serviceDescriptor                     | 0     |
     | session                               | 1     |
     | staff                                 | 7     |
     | staffCohortAssociation                | 0     |
     | staffEducationOrganizationAssociation | 3     |
     | staffProgramAssociation               | 0     |
     | student                               | 4     |
     | studentAcademicRecord                 | 0     |
     | studentAssessmentAssociation          | 0     |
     | studentCohortAssociation              | 0     |
     | studentCompetency                     | 0     |
     | studentCompetencyObjective            | 0     |
     | studentDisciplineIncidentAssociation  | 0     |
     | studentGradebookEntry                 | 0     |
     | studentParentAssociation              | 0     |
     | studentProgramAssociation             | 0     |
     | studentSchoolAssociation              | 4     |
     | studentSectionAssociation             | 5     |
     | studentTranscriptAssociation          | 0     |
     | teacherSchoolAssociation              | 4     |
     | teacherSectionAssociation             | 4     |

@IL-Daybreak
Scenario: Post a zip file containing new entities and deltas for existing entities. Validate updates and inserts.
    Given I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | gradebookEntry              | 12    |
        | studentGradebookEntry       | 315   |
        | studentCompetency           | 59    |
        | grade                       | 4     |
        | reportCard                  | 2     |
        | staffCohortAssociation      | 3     |
        | staffProgramAssociation     | 3     |
    And I check to find if record is in collection:
        | collectionName              | expectedRecordCount | searchParameter                | searchValue             | searchType           |
        | gradebookEntry              | 0                   | body.dateAssigned              | 2011-09-27              | string               |
        | studentGradebookEntry       | 0                   | body.letterGradeEarned         | Q                       | string               |
        | studentCompetency           | 0                   | body.competencyLevel.codeValue | 99                      | string               |
        | grade                       | 0                   | body.letterGradeEarned         | U                       | string               |
        | reportCard                  | 0                   | body.gpaGivenGradingPeriod     | 1.1                     | double               |
        | staffCohortAssociation      | 1                   | body.beginDate                 | 2011-01-01              | string               |
        | staffCohortAssociation      | 0                   | body.beginDate                 | 2011-01-02              | string               |
        | staffCohortAssociation      | 1                   | body.beginDate                 | 2010-01-15              | string               |
        | staffProgramAssociation     | 1                   | body.beginDate                 | 2011-01-01              | string               |
        | staffProgramAssociation     | 0                   | body.endDate                   | 2012-03-16              | string               |
        | staffProgramAssociation     | 1                   | body.beginDate                 | 2011-01-05              | string               |
        | staffProgramAssociation     | 0                   | body.beginDate                 | 2011-12-31              | string               |
        | staffProgramAssociation     | 1                   | body.endDate                   | 2012-02-15              | string               |
    When I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StoriedDataSet_IL_Daybreak_Deltas.zip" file as the payload of the ingestion job
    And zip file is scp to ingestion landing zone
    And a batch job log has been created
    Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | gradebookEntry              | 13    |
        | studentGradebookEntry       | 316   |
        | studentCompetency           | 59    |
        | grade                       | 4     |
        | reportCard                  | 2     |
        | staffCohortAssociation      | 4     |
        | staffProgramAssociation     | 4     |
    And I check to find if record is in collection:
        | collectionName              | expectedRecordCount | searchParameter                | searchValue             | searchType           |
        | gradebookEntry              | 1                   | body.dateAssigned              | 2011-09-27              | string               |
        | studentGradebookEntry       | 1                   | body.letterGradeEarned         | Q                       | string               |
        | studentCompetency           | 1                   | body.competencyLevel.codeValue | 99                      | string               |
        | grade                       | 1                   | body.letterGradeEarned         | U                       | string               |
        | reportCard                  | 1                   | body.gpaGivenGradingPeriod     | 1.1                     | double               |
        | staffCohortAssociation      | 1                   | body.beginDate                 | 2011-01-01              | string               |
        | staffCohortAssociation      | 1                   | body.beginDate                 | 2011-01-02              | string               |
        | staffCohortAssociation      | 1                   | body.beginDate                 | 2010-01-15              | string               |
        | staffProgramAssociation     | 1                   | body.beginDate                 | 2011-01-01              | string               |
        | staffProgramAssociation     | 1                   | body.endDate                   | 2012-03-16              | string               |
        | staffProgramAssociation     | 1                   | body.beginDate                 | 2011-01-05              | string               |
        | staffProgramAssociation     | 1                   | body.beginDate                 | 2011-12-31              | string               |
        | staffProgramAssociation     | 2                   | body.endDate                   | 2012-02-15              | string               |
