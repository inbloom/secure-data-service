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
@RALLY_US4136
@RALLY_US4080
@RALLY_US4116
Feature: Acceptance Storied Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

@smoke @integration @IL-Daybreak
Scenario: Post a zip file containing all data for Illinois Daybreak as a payload of the ingestion job: Clean Database
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName                        |
        | assessment                            |
        | attendance                            |
        | calendarDate                          |
        | cohort                                |
        | competencyLevelDescriptor             |
        | course                                |
        | courseOffering                        |
        | disciplineAction                      |
        | disciplineIncident                    |
        | educationOrganization                 |
        | grade                                 |
        | gradebookEntry                        |
        | gradingPeriod                         |
        | graduationPlan                        |
        | learningObjective                     |
        | learningStandard                      |
        | parent                                |
        | program                               |
        | reportCard                            |
        | section                               |
        | session                               |
        | staff                                 |
        | staffCohortAssociation                |
        | staffEducationOrganizationAssociation |
        | staffProgramAssociation               |
        | student                               |
        | studentAcademicRecord                 |
        | studentAssessmentAssociation          |
        | studentCohortAssociation              |
        | studentCompetency                     |
        | studentCompetencyObjective            |
        | studentDisciplineIncidentAssociation  |
        | studentGradebookEntry                 |
        | studentParentAssociation              |
        | studentProgramAssociation             |
        | studentSchoolAssociation              |
        | studentSectionAssociation             |
        | studentTranscriptAssociation          |
        | teacherSchoolAssociation              |
        | teacherSectionAssociation             |
  When zip file is scp to ingestion landing zone
  And a batch job log has been created

Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | assessment                  | 19    |
        | attendance                  | 75    |
        | calendarDate                | 556   |
        | cohort                      | 3     |
        | competencyLevelDescriptor   | 6     |
        | course                      | 95    |
        | courseOffering              | 95    |
        | disciplineAction            | 2     |
        | disciplineIncident          | 2     |
        | educationOrganization       | 5     |
        | grade                       | 4     |
        | gradebookEntry              | 12    |
        | gradingPeriod               | 17    |
        | graduationPlan              | 4     |
        | learningObjective           | 197   |
        | learningStandard            | 1499  |
        | parent                      | 9     |
        | program                     | 2     |
        | reportCard                  | 2     |
        | section                     | 97    |
        | session                     | 22    |
        | staff                       | 14    |
        | staffCohortAssociation      | 3     |
        | staffEducationOrganizationAssociation| 10 |
        | staffProgramAssociation     | 7     |
        | student                     | 78    |
        | studentAcademicRecord       | 117   |
        | studentAssessmentAssociation| 203   |
        | studentCohortAssociation    | 6     |
        | studentCompetency           | 59    |
        | studentCompetencyObjective  | 4     |
        | studentDisciplineIncidentAssociation| 4|
        | studentGradebookEntry       | 315   |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | studentSchoolAssociation    | 167   |
        | studentSectionAssociation   | 297   |
        | studentTranscriptAssociation| 196   |
        | teacherSchoolAssociation    | 3     |
        | teacherSectionAssociation   | 11    |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | assessment                  | 1                   | body.assessmentItem.0.correctResponse          | False            | string  |
       | assessment                  | 1                   | body.assessmentItem.0.identificationCode       | AssessmentItem-1 | string  |
       | assessment                  | 1                   | body.assessmentItem.0.itemCategory             | True-False       | string  |
       | assessment                  | 1                   | body.assessmentItem.0.maxRawScore              | 5                | integer |
       | assessment                  | 1                   | body.assessmentItem.1.correctResponse          | True             | string  |
       | assessment                  | 1                   | body.assessmentItem.1.identificationCode       | AssessmentItem-2 | string  |
       | assessment                  | 1                   | body.assessmentItem.1.itemCategory             | True-False       | string  |
       | assessment                  | 1                   | body.assessmentItem.1.maxRawScore              | 5                | integer |
       | assessment                  | 1                   | body.assessmentItem.2.correctResponse          | True             | string  |
       | assessment                  | 1                   | body.assessmentItem.2.identificationCode       | AssessmentItem-3 | string  |
       | assessment                  | 1                   | body.assessmentItem.2.itemCategory             | True-False       | string  |
       | assessment                  | 1                   | body.assessmentItem.2.maxRawScore              | 5                | integer |
       | assessment                  | 1                   | body.assessmentItem.3.correctResponse          | False            | string  |
       | assessment                  | 1                   | body.assessmentItem.3.identificationCode       | AssessmentItem-4 | string  |
       | assessment                  | 1                   | body.assessmentItem.3.itemCategory             | True-False       | string  |
       | assessment                  | 1                   | body.assessmentItem.3.maxRawScore              | 5                | integer |
       | attendance                  | 11                  | body.schoolYearAttendance.attendanceEvent.event | Tardy         | string     |
       | attendance                  | 75                  | body.schoolYearAttendance.attendanceEvent.event | In Attendance | string     |
       | attendance                  | 75                  | body.schoolYearAttendance.schoolYear            | 2011-2012     | string     |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-1             | string               |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-2             | string               |
       | cohort                      | 1                   | body.cohortIdentifier      | ACC-TEST-COH-3             | string               |
       | course                      | 1                   | body.courseTitle     | 1st Grade Homeroom         | string               |
       | disciplineAction            | 1                   | body.disciplineDate      | 2011-03-04                 | string               |
       | disciplineAction            | 1                   | body.disciplineDate      | 2011-04-04                 | string               |
       | disciplineIncident          | 1                   | body.incidentIdentifier  | Disruption                 | string               |
       | disciplineIncident          | 1                   | body.incidentIdentifier  | Tardiness                  | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | IL                         | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | IL-DAYBREAK                | string               |
       | educationOrganization       | 1                   | body.stateOrganizationId      | South Daybreak Elementary  | string               |
       | graduationPlan              | 1                   | metaData.externalId                            | GP-ADVANCED      | string  |
       | graduationPlan              | 1                   | metaData.externalId                            | GP-MINIMUM       | string  |
       | graduationPlan              | 1                   | metaData.externalId                            | GP-STANDARD      | string  |
       | graduationPlan              | 3                   | body.educationOrganizationId                   | 36465c681a53a77d71e24285d58bf5af9085e537_id | string  |
       | graduationPlan              | 2                   | body.graduationPlanType                        | Minimum                                     | string  |
       | program                     | 1                   | body.programId      | ACC-TEST-PROG-1            | string               |
       | program                     | 1                   | body.programId      | ACC-TEST-PROG-2            | string               |
       | staff                       | 1                   | body.staffUniqueStateId        | cgray                      | string               |
       | staff                       | 1                   | body.staffUniqueStateId  | rbraverman                 | string               |
       | staff                       | 2                   | body.name.verification   | Drivers license            | string               |
       | staff                       | 2                   | body.race                | White                      | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 100000000                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 800000012                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 800000025                  | string               |
       | student                     | 1                   | body.studentUniqueStateId      | 900000024                  | string               |
       | studentAssessmentAssociation | 10                 | body.studentAssessmentItems.assessmentItemResult              | Incorrect           | string |
       | studentAssessmentAssociation | 10                 | body.studentAssessmentItems.assessmentResponse                | False               | string |
       | studentAssessmentAssociation | 24                 | body.studentAssessmentItems.assessmentItemResult              | Correct             | string |
       | studentAssessmentAssociation | 24                 | body.studentAssessmentItems.assessmentResponse                | True                | string |
       | studentAssessmentAssociation | 25                 | body.studentAssessmentItems.assessmentItem.identificationCode | AssessmentItem-3    | string |
       | studentAssessmentAssociation | 25                 | body.studentAssessmentItems.assessmentItem.identificationCode | AssessmentItem-4    | string |
       | studentParentAssociation     | 2                  | body.contactRestrictions                                      | NO CONTACT ALLOWED  | string |
       | studentParentAssociation     | 3                  | body.contactPriority                                          | 1                   | integer|
    And I should see "Processed 4260 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 108" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 108" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 595" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 595" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 192" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 192" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 45" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 45" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 496" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 496" in the resulting batch job file
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
    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records considered: 98" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records ingested successfully: 98" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-CommonCore.xml records failed: 0" in the resulting batch job file

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
     | staffProgramAssociation     | 4                   | body.studentRecordAccess    | true                    | boolean              |
     | staffProgramAssociation     | 3                   | body.studentRecordAccess    | false                   | boolean              |
     | staffProgramAssociation     | 2                   | body.beginDate              | 2011-01-01              | string               |
     | staffProgramAssociation     | 4                   | body.beginDate              | 2011-01-05              | string               |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-06-01              | string               |
     | staffProgramAssociation     | 4                   | body.endDate                | 2012-02-15              | string               |
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
    | collectionName                       | deterministicId                             | field                             | value                                |
    | competencyLevelDescriptor            | fb623d47656476ad67d8b698ee19d3a1932fd2ea_id | body.codeValue                    | Barely Competent 4                   |
    | educationOrganization                | 261472ed910549ecff6bb731f49362ed4d3fef05_id | body.stateOrganizationId  | IL                                   |
    | assessment                           | 0af135b55da9d8c1cfeb5226836bf40b19f58e8d_id | body.assessmentIdentificationCode.ID  | ACT                              |
    | educationOrganization                | 36465c681a53a77d71e24285d58bf5af9085e537_id | body.stateOrganizationId  | IL-DAYBREAK                          |
    | educationOrganization                | e479e04449d7a787bb8cce88335d8214f612416a_id | body.stateOrganizationId  | Daybreak Central High                |
    | student                              | 067198fd6da91e1aa8d67e28e850f224d6851713_id | body.studentUniqueStateId         | 800000025                            |
    | staff                                | dfec28d34c75a4d307d1e85579e26a81630f6a47_id | body.staffUniqueStateId           | jstevenson                           |
    | staff                                | c67d7320aabbeed6ef3ad321e4de250d14a27ac3_id | body.staffUniqueStateId           | linda.kim                            |
    | cohort                               | 859ea4f5642777470510bdc378f43184571f90d1_id | body.cohortIdentifier     | ACC-TEST-COH-2                       |
    | cohort                               | 859ea4f5642777470510bdc378f43184571f90d1_id | body.educationOrgId       | 36465c681a53a77d71e24285d58bf5af9085e537_id |
    | studentCohortAssociation             | 8020651b339b85058c6cd7400ba238cbb1e377f8_id | body.studentId            | 25369655c44d8d9346b356a75b8ac3552bb85e6e_id |
    | studentCohortAssociation             | 8020651b339b85058c6cd7400ba238cbb1e377f8_id | body.cohortId             | 9f522548066d6edebc551afc6c5214d3360cf539_id |
    | studentCohortAssociation             | 8020651b339b85058c6cd7400ba238cbb1e377f8_id | body.beginDate            | 2012-01-15                           |
    | studentAssessmentAssociation         | 37d2f0cd437b6939afd2ae0c6295d8f4085fb830_id | body.studentId            | 9b38ee8562b14f3201aff4995bac9bbafc3336a0_id |
    | studentAssessmentAssociation         | 37d2f0cd437b6939afd2ae0c6295d8f4085fb830_id | body.assessmentId         | be81697a6ad942136762996172b7030b933521da_id |
    | studentAssessmentAssociation         | 37d2f0cd437b6939afd2ae0c6295d8f4085fb830_id | body.administrationDate   | 2011-10-01                           |
    | studentCompetency                    | dfdec1686deef4c317d574ffd637ff12f2ff263f_id | body.competencyLevel.codeValue    | 777                                  |
    | studentCompetencyObjective           | 028d7f8e25584d3353c9691e6aab89156029dde8_id | body.studentCompetencyObjectiveId | SCO-K-1                              |
    | course                               | 7d636088296b5357f6fce410ec720794d71d846a_id | body.uniqueCourseId  | State-History-II-G7-50 |
    | course                               | 7d636088296b5357f6fce410ec720794d71d846a_id | body.schoolId  | f3261d8da17cbb2178f883afb966e2307cdbda53_id |
    | reportCard                           | b5a7854aa3b946dc0fc1a95506bd559a9228b495_id | body.studentId | 067198fd6da91e1aa8d67e28e850f224d6851713_id                       |
# disciplineAction
    | disciplineAction                     | 9a1a9dbb09f820022b2d6965599d2aa0ab32201e_id | body.responsibilitySchoolId          | e479e04449d7a787bb8cce88335d8214f612416a_id |
    | disciplineAction                     | 9a1a9dbb09f820022b2d6965599d2aa0ab32201e_id | body.disciplineActionIdentifier      | cap0-lea0-sch1-da0                   |
# disciplineIncident
    | disciplineIncident                   | adcbad0471feb245749a1792f9383a81a2eb6609_id | body.schoolId                        | e479e04449d7a787bb8cce88335d8214f612416a_id |
    | disciplineIncident                   | adcbad0471feb245749a1792f9383a81a2eb6609_id | body.incidentIdentifier              | Disruption                           |
# grade
    | grade                                | 8ff38e10a95a790ac41444105e1815ef3a940a2a_id | body.studentSectionAssociationId     | f489dcb5262469edc5278be810467d52d2eb5921_id |
    | grade                                | 8ff38e10a95a790ac41444105e1815ef3a940a2a_id | body.gradingPeriodId                 | fe565dbc1d33fce6da502b8b3671630602ac9c72_id |
# gradebookEntry
    | gradebookEntry                       | e0920728a77fb8d9a8c6b735fb0f5917c5c6997c_id | body.sectionId                       | d36ad186e632afeed132d7a6cd6fc8bd409e8d3d_id |
    | gradebookEntry                       | e0920728a77fb8d9a8c6b735fb0f5917c5c6997c_id | body.gradebookEntryType              | Quiz                                 |
    | gradebookEntry                       | e0920728a77fb8d9a8c6b735fb0f5917c5c6997c_id | body.dateAssigned                    | 2011-10-27                           |
# studentAcademicRecord
    | studentAcademicRecord                | c0ea573c1ee6a9e1a3f88470ce3f5880981dd4d0_id | body.studentId                       | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | studentAcademicRecord                | c0ea573c1ee6a9e1a3f88470ce3f5880981dd4d0_id | body.sessionId                       | 02e2f80a0952d4842821220965703da24d49d853_id |
# staffEducationOrganizationAssociation
    | staffEducationOrganizationAssociation | 8294eff8d11604b6d23312345eb761d92c8bb188_id | body.staffReference                 | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id |
    | staffEducationOrganizationAssociation | 8294eff8d11604b6d23312345eb761d92c8bb188_id | body.educationOrganizationReference | 261472ed910549ecff6bb731f49362ed4d3fef05_id |
    | staffEducationOrganizationAssociation | 8294eff8d11604b6d23312345eb761d92c8bb188_id | body.staffClassification            | Superintendent                       |
    | staffEducationOrganizationAssociation | 8294eff8d11604b6d23312345eb761d92c8bb188_id | body.beginDate                      | 1967-08-13                           |
     | studentDisciplineIncidentAssociation | 3ed0f6b236716378c9e4737df3d94093dfe32554_id | body.studentId              | 6578f984876bbf6f884c1be2ef415dbf4441db89_id |
     | studentDisciplineIncidentAssociation | 3ed0f6b236716378c9e4737df3d94093dfe32554_id | body.disciplineIncidentId    | 4d1d98c6d4c6adf3e2f6008d7896432eb655b22f_id |
# staffProgramAssociation
    | staffProgramAssociation               | 5c39f4b8dd9bff032a7e0e521f466a69e49ce692_id | body.staffId                        | a909105eca7591d418b2697d72df27ca632e16f8_id |
    | staffProgramAssociation               | 5c39f4b8dd9bff032a7e0e521f466a69e49ce692_id | body.programId                      | 983dd657325009aefa88a234fa18bdb1e11c82a8_id |
    | staffProgramAssociation               | 5c39f4b8dd9bff032a7e0e521f466a69e49ce692_id | body.beginDate                      | 2011-01-01                           |
# staffCohortAssociation
    | staffCohortAssociation               | 33fdb121bb81479c7b47c9c526cdf494c9148a86_id | body.staffId                        | 63d4be8a233db1fd14676f1535fa21fe4c5dd466_id |
    | staffCohortAssociation               | 33fdb121bb81479c7b47c9c526cdf494c9148a86_id | body.cohortId                       | b3bd8fc373ba4b067d3c96aad5fd3fe2c8678138_id |
    | staffCohortAssociation               | 33fdb121bb81479c7b47c9c526cdf494c9148a86_id | body.beginDate                      | 2011-07-01                           		 |
# teacherSchoolAssociation
    | teacherSchoolAssociation             | dbe1d7765afb058ca9d302b9979d697f9ef42f6f_id | body.teacherId                      | a965bf003819d48b507749091d282c851dd0507f_id |
    | teacherSchoolAssociation             | dbe1d7765afb058ca9d302b9979d697f9ef42f6f_id | body.programAssignment              | Regular Education                    |
    | teacherSchoolAssociation             | dbe1d7765afb058ca9d302b9979d697f9ef42f6f_id | body.schoolId                       | e479e04449d7a787bb8cce88335d8214f612416a_id |
# courseOffering
    | courseOffering                       | 81c4de13a78bbaef4a6a84283c28752b09abc449_id | body.schoolId                       | 93676ac4958b620c453bc3d438427dfb3d1c5fc8_id |
    | courseOffering                       | 81c4de13a78bbaef4a6a84283c28752b09abc449_id | body.sessionId                      | f50d73dc3bbfa3a25bb362a3e225c74162005b4e_id |
    | courseOffering                       | 81c4de13a78bbaef4a6a84283c28752b09abc449_id | body.localCourseCode                | 3rd Grade Homeroom                          |
   | studentTranscriptAssociation         | 6dcd76c7f1c176528a4530401211662ab97dc3ba_id | body.studentAcademicRecordId            | 39dc6fb4f73a616c637b48682faba1176ea23950_id                                 |
   | studentTranscriptAssociation         | 6dcd76c7f1c176528a4530401211662ab97dc3ba_id | body.courseId                | 45b19639f02b1f04a88cab31e543ae58adfcbb27_id                                 |
   | studentTranscriptAssociation         | 6dcd76c7f1c176528a4530401211662ab97dc3ba_id | body.courseAttemptResult            | Pass                                 |
   | studentParentAssociation             | 482360640e4db1dc0dd3755e699b25cfc9abf4a9_id | body.studentId            | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
   | studentParentAssociation             | 482360640e4db1dc0dd3755e699b25cfc9abf4a9_id | body.parentId             | 93616529c9acb1f9a5a88b8bf735d8a4277d6f08_id |
   | studentSchoolAssociation             | 8ec47c841fad392b0112063326429815796b19cb_id | body.studentId            | 067198fd6da91e1aa8d67e28e850f224d6851713_id  |
   | studentSchoolAssociation             | 8ec47c841fad392b0112063326429815796b19cb_id | body.schoolId            | 93676ac4958b620c453bc3d438427dfb3d1c5fc8_id |
   | studentSchoolAssociation             | 8ec47c841fad392b0112063326429815796b19cb_id | body.entryDate            | 2008-09-05                              |
   | studentSectionAssociation             | b21f28547fad9d644584b41cf80a0a9a6ac36d2b_id | body.studentId            | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
   | studentSectionAssociation             | b21f28547fad9d644584b41cf80a0a9a6ac36d2b_id | body.sectionId            | 51188a8ba3f9a2a9b57c61a99ec080299c5987f1_id |
   | studentSectionAssociation             | b21f28547fad9d644584b41cf80a0a9a6ac36d2b_id | body.beginDate            | 2011-09-01                              |
   | teacherSectionAssociation            | b73320567f24bfdfcba1a6d2e997897bc92f964b_id | body.teacherId            | 18f5c2652935c4881c8a88df04481e8c3aeb5aac_id |
   | teacherSectionAssociation            | b73320567f24bfdfcba1a6d2e997897bc92f964b_id | body.sectionId            | e2b3887f55834817a194a51604dd3fb03d8cacbc_id |
    | program                              | a50802f02c7e771d979f7d5b3870c500014e6803_id | body.programId            | ACC-TEST-PROG-1                      |
    | calendarDate                         | a4785ee1380871b68888ec317c39c9e8ef7e1346_id | body.date                 | 2010-10-13                           |
    | calendarDate                         | 356b451105c8cd5678f69eb7c3dce42d5ef4c873_id | body.date                 | 2010-10-14                           |
    | studentProgramAssociation            | e59cdf1afcb6d72eb71b435b1755beecf8b2171c_id | body.studentId            | 067198fd6da91e1aa8d67e28e850f224d6851713_id |
    | studentProgramAssociation            | e59cdf1afcb6d72eb71b435b1755beecf8b2171c_id | body.programId            | a50802f02c7e771d979f7d5b3870c500014e6803_id |
    | studentProgramAssociation            | e59cdf1afcb6d72eb71b435b1755beecf8b2171c_id | body.educationOrganizationId | 36465c681a53a77d71e24285d58bf5af9085e537_id |
    | studentProgramAssociation            | e59cdf1afcb6d72eb71b435b1755beecf8b2171c_id | body.beginDate            | 2011-03-01                           |
    | parent                               | aae71d23ffacfef68aa2eaa357c7259445daa0fe_id | body.parentUniqueStateId  | 3597672174             |
    | section                              | 0e8cac9da3ed16e7d4f01d7d4322521e7ca7821f_id | body.uniqueSectionCode    | 7th Grade Math - Sec 2 |
    | section                              | 0e8cac9da3ed16e7d4f01d7d4322521e7ca7821f_id | body.schoolId             | f3261d8da17cbb2178f883afb966e2307cdbda53_id |
    | gradingPeriod                        | fe565dbc1d33fce6da502b8b3671630602ac9c72_id | body.beginDate                           | 2011-04-11                           |
    | gradingPeriod                        | fe565dbc1d33fce6da502b8b3671630602ac9c72_id | body.gradingPeriodIdentity.gradingPeriod | Sixth Six Weeks                      |
    | gradingPeriod                        | fe565dbc1d33fce6da502b8b3671630602ac9c72_id | body.gradingPeriodIdentity.schoolId      | 93676ac4958b620c453bc3d438427dfb3d1c5fc8_id |
# session
    | session                              | da2b8c39e78ce881e8418633eb8119fd2fa889fd_id | body.sessionName                     | Spring 2011 Daybreak Central High    |
    | session                              | da2b8c39e78ce881e8418633eb8119fd2fa889fd_id | body.schoolId                        | e479e04449d7a787bb8cce88335d8214f612416a_id |
    | attendance                           | a50e0c3a19aafe0d0e15ba026415c08b6ddf1a8d_id | body.studentId                       | d010a8b710783e4fd409cc7a8ddd780cd16ff89b_id |
    | attendance                           | a50e0c3a19aafe0d0e15ba026415c08b6ddf1a8d_id | body.schoolId                        | f3261d8da17cbb2178f883afb966e2307cdbda53_id |
    | graduationPlan | 3dbd2591860e886886ed902a02d8324f041b3d81_id | body.educationOrganizationId | 36465c681a53a77d71e24285d58bf5af9085e537_id |
    | graduationPlan | 3dbd2591860e886886ed902a02d8324f041b3d81_id | body.graduationPlanType      | Minimum       |
    | graduationPlan | 26849264faf4eb7080720ed9d84fe14b21e4a5e0_id | body.graduationPlanType      | Minimum       |
    | learningObjective                    | e7ca691a652808cedd4fc8abd1275c94f9679e56_id | body.objective                       | The Revolutionary Period |
    | learningObjective                    | e7ca691a652808cedd4fc8abd1275c94f9679e56_id | body.academicSubject                 | Social Studies |
    | learningObjective                    | e7ca691a652808cedd4fc8abd1275c94f9679e56_id | body.objectiveGradeLevel             | Third grade |
    | learningStandard                     | 84a2dbad54ca44b613728cdfbe92d2e9a3bbcd9f_id | body.learningStandardId.identificationCode | 9DB2617F615743cfA8D225346AC4CB4D |

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
     | staffProgramAssociation               | 7     |
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
     | staffProgramAssociation               | 6     |
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
        | assessment                  | 19    |
        | attendance                  | 75    |
        | cohort                      | 3     |
        | course                      | 96    |
        | courseOffering              | 96    |
        | disciplineAction            | 2     |
        | disciplineIncident          | 2     |
        | educationOrganization       | 7     |
        | grade                       | 4     |
        | gradebookEntry              | 12    |
        | parent                      | 9     |
        | program                     | 2     |
        | reportCard                  | 2     |
        | section                     | 100   |
        | session                     | 23    |
        | staff                       | 21    |
        | staffCohortAssociation      | 3     |
        | staffEducationOrganizationAssociation| 16 |
        | staffProgramAssociation     | 7     |
        | student                     | 183   |
        | studentAssessmentAssociation| 203   |
        | studentCohortAssociation    | 6     |
        | studentDisciplineIncidentAssociation| 4|
        | studentGradebookEntry       | 315   |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | studentSchoolAssociation    | 272   |
        | studentSectionAssociation   | 402   |
        | studentTranscriptAssociation| 196   |
        | teacherSchoolAssociation    | 4     |
        | teacherSectionAssociation   | 14    |
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
     | staffProgramAssociation               | 7     |
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
     | staffProgramAssociation               | 6     |
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
        | assessment                  | 19    |
        | attendance                  | 75    |
        | cohort                      | 3     |
        | course                      | 104   |
        | courseOffering              | 104   |
        | disciplineAction            | 3     |
        | disciplineIncident          | 4     |
        | educationOrganization       | 14    |
        | grade                       | 4     |
        | gradebookEntry              | 12    |
        | parent                      | 9     |
        | program                     | 2     |
        | reportCard                  | 2     |
        | section                     | 116   |
        | session                     | 27    |
        | staff                       | 58    |
        | staffCohortAssociation      | 3     |
        | staffEducationOrganizationAssociation| 37 |
        | staffProgramAssociation     | 7     |
        | student                     | 191    |
        | studentAssessmentAssociation| 203   |
        | studentCohortAssociation    | 6     |
        | studentDisciplineIncidentAssociation| 8|
        | studentGradebookEntry       | 315   |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | studentSchoolAssociation    | 280   |
        | studentSectionAssociation   | 410   |
        | studentTranscriptAssociation| 196   |
        | teacherSchoolAssociation    | 20    |
        | teacherSectionAssociation   | 30    |
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
     | staffProgramAssociation               | 7     |
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
     | staffProgramAssociation               | 6     |
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
     | assessment                           | 23    |
     | attendance                           | 75    |
     | cohort                               | 4     |
     | courseOffering                       | 105   |
     | disciplineAction                     | 4     |
     | disciplineIncident                   | 7     |
     | gradebookEntry                       | 13    |
     | parent                               | 12    |
     | program                              | 4     |
     | staffProgramAssociation              | 16    |
     | student                              | 193   |
     | studentAcademicRecord                | 121   |
     | studentAssessmentAssociation         | 204   |
     | studentCohortAssociation             | 6     |
     | studentDisciplineIncidentAssociation | 9     |
     | studentParentAssociation             | 11    |
     | studentProgramAssociation            | 9     |
     | studentTranscriptAssociation         | 200   |
  And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName               | AP.AP Eng.AP-Eng-and-Literature      |string                  |
     | assessment                  | 1                   | body.assessmentFamilyHierarchyName               | AP.AP Eng.AP-Lang-and-Literature     |string                  |
     | assessment                  | 1                   | body.assessmentItem.correctResponse            | False            | string |
     | assessment                  | 1                   | body.assessmentItem.correctResponse            | False            | string |
     | assessment                  | 1                   | body.assessmentItem.correctResponse            | True             | string |
     | assessment                  | 1                   | body.assessmentItem.correctResponse            | True             | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode         | AssessmentItem-1 | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode         | AssessmentItem-2 | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode         | AssessmentItem-3 | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode         | AssessmentItem-4 | string |
     | assessment                  | 1                   | body.assessmentItem.identificationCode      | AssessmentItem-1 |string                  |
     | assessment                  | 1                   | body.assessmentItem.identificationCode      | AssessmentItem-2 |string                  |
     | assessment                  | 1                   | body.assessmentItem.identificationCode      | AssessmentItem-3 |string                  |
     | assessment                  | 1                   | body.assessmentItem.identificationCode      | AssessmentItem-4 |string                  |
     | assessment                  | 1                   | body.assessmentItem.itemCategory               | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.itemCategory               | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.itemCategory               | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.itemCategory               | True-False       | string |
     | assessment                  | 1                   | body.assessmentItem.maxRawScore                | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.maxRawScore                | 5                | integer |
     | assessment                  | 1                   | body.assessmentItem.maxRawScore                | 5                | integer |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | BOY                                              | string |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | EOY                                              | string |
     | assessment                  | 1                   | body.assessmentPeriodDescriptor.codeValue      | MOY                                              | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-English          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Mathematics      | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Reading          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Science          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | ACT-Writing          | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Critical Reading                             | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Math                                         | string |
     | assessment                  | 1                   | body.objectiveAssessment.identificationCode    | SAT-Writing                                      | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Algebra            | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Arithmetic         | string |
     | assessment                  | 1                   | body.objectiveAssessment.objectiveAssessments.identificationCode    | SAT-Math-Geometry           | string |
     | assessment                  | 3                   | body.assessmentFamilyHierarchyName             | READ2.READ 2.0.READ 2.0 Kindergarten                 | string |
     | attendance                  | 75                  | body.schoolYearAttendance.attendanceEvent.date | 2011-09-06      |string               |
     | cohort                      | 1                   | body.academicSubject        | English                 | string               |
     | cohort                      | 1                   | body.academicSubject        | Mathematics             | string               |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-1          | string               |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-2          | string               |
     | cohort                      | 1                   | body.cohortScope            | Statewide               | string               |
     | cohort                      | 2                   | body.academicSubject        | Social Studies          | string               |
     | cohort                      | 2                   | body.cohortIdentifier       | ACC-TEST-COH-3          | string               |
     | cohort                      | 3                   | body.cohortScope            | District                | string               |
     | courseOffering              | 0                   | body.localCourseTitle       | 3rd Grade Homeroom        | string               |
     | courseOffering              | 1                   | body.localCourseTitle       | Government-4A             | string               |
     | courseOffering              | 2                   | body.localCourseTitle       | Government-4              | string               |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-03-04              | string               |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-04-04              | string               |
     | disciplineAction            | 1                   | body.disciplineDate         | 2011-05-04              | string               |
     | disciplineAction            | 3                   | body.disciplineActionLength | 74                      | integer              |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Bullying                | string               |
     | disciplineIncident          | 1                   | body.incidentIdentifier     | Hazing                  | string               |
     | disciplineIncident          | 2                   | body.incidentIdentifier     | Disruption              | string               |
     | disciplineIncident          | 2                   | body.incidentIdentifier     | Tardiness               | string               |
     | disciplineIncident          | 2                   | body.weapons                | Non-Illegal Knife       | string               |
     | disciplineIncident          | 3                   | body.incidentDate           | 2011-02-01              | string               |
     | disciplineIncident          | 5                   | body.incidentLocation       | On School               | string               |
     | gradebookEntry              | 2                   | body.dateAssigned           | 2011-10-27              | string               |
     | gradebookEntry              | 4                   | body.dateAssigned           | 2011-10-13              | string               |
     | gradebookEntry              | 4                   | body.gradebookEntryType     | Quiz                    | string               |
     | parent                      | 1                   | body.parentUniqueStateId      | 2521899635      |string                  |
     | parent                      | 1                   | body.parentUniqueStateId      | 3152281864      |string                  |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-1         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-2         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-3         | string               |
     | program                     | 1                   | body.programId              | ACC-TEST-PROG-4         | string               |
     | program                     | 1                   | body.programSponsor         | State Education Agency  | string               |
     | program                     | 1                   | body.programType            | Remedial Education      | string               |
     | program                     | 3                   | body.programSponsor         | Local Education Agency  | string               |
     | program                     | 3                   | body.programType            | Regular Education       | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2011-01-01              | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2012-02-15              | string               |
     | staffCohortAssociation      | 1                   | body.endDate                | 2012-02-15              | string               |
     | staffCohortAssociation      | 2                   | body.beginDate              | 2011-07-01              | string               |
     | staffCohortAssociation      | 5                   | body.studentRecordAccess    | true                    | boolean              |
     | staffProgramAssociation     | 12                  | body.studentRecordAccess    | true                    | boolean              |
     | staffProgramAssociation     | 4                   | body.studentRecordAccess    | false                   | boolean              |
     | staffProgramAssociation     | 6                   | body.beginDate              | 2011-01-01              | string               |
     | staffProgramAssociation     | 2                   | body.beginDate              | 2011-01-02              | string               |
     | staffProgramAssociation     | 4                   | body.beginDate              | 2011-01-05              | string               |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-05-02              | string               |
     | staffProgramAssociation     | 1                   | body.beginDate              | 2011-06-01              | string               |
     | staffProgramAssociation     | 2                   | body.beginDate              | 2011-06-02              | string               |
     | staffProgramAssociation     | 9                   | body.endDate                | 2012-02-15              | string               |
     | studentAcademicRecord         | 104                 | body.cumulativeCreditsAttempted.credit| 5                       | integer              |
     | studentAssessmentAssociation | 10                  | body.studentAssessmentItems.assessmentResponse                | False               | string |
     | studentAssessmentAssociation | 25                  | body.studentAssessmentItems.assessmentResponse                | True                | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-English-Rhetorical   | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Math-Algebra            | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Math-Pre-Algebra    | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Mathematics             | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Reading-Arts            | string |
     | studentAssessmentAssociation | 25                  | body.studentObjectiveAssessments.objectiveAssessment.identificationCode    | ACT-Writing                       | string |
     | studentAssessmentAssociation | 26                  | body.studentAssessmentItems.assessmentItem.identificationCode | AssessmentItem-3    | string |
     | studentAssessmentAssociation | 26                  | body.studentAssessmentItems.assessmentItem.identificationCode | AssessmentItem-4    | string |
     | studentAssessmentAssociation| 26                  | body.studentAssessmentItems.assessmentItem.identificationCode  | AssessmentItem-3       |string                  |
     | studentAssessmentAssociation| 26                  | body.studentAssessmentItems.assessmentItem.identificationCode  | AssessmentItem-4       |string                  |
     | studentAssessmentAssociation| 8                   | body.performanceLevelDescriptors.0.1.description | Extremely well qualified             |string                  |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-02-01              | string               |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-03-01              | string               |
     | studentCohortAssociation    | 1                   | body.endDate                | 2011-12-31              | string               |
     | studentDisciplineIncidentAssociation    | 2                   | body.studentParticipationCode       | Witness              | string               |
     | studentDisciplineIncidentAssociation    | 3                   | body.studentParticipationCode       | Victim               | string               |
     | studentDisciplineIncidentAssociation    | 4                   | body.studentParticipationCode       | Perpetrator          | string               |
     | studentProgramAssociation   | 1                   | body.endDate                | 2011-12-31              | string               |
     | studentProgramAssociation   | 1                   | body.endDate                | 2012-02-15              | string               |
     | studentProgramAssociation   | 2                   | body.beginDate              | 2011-05-01              | string               |
     | studentProgramAssociation   | 3                   | body.beginDate              | 2011-01-01              | string               |
     | studentProgramAssociation   | 4                   | body.beginDate              | 2011-03-01              | string               |
     | studentProgramAssociation   | 6                   | body.endDate                | 2012-04-12              | string               |
     | studentTranscriptAssociation  | 200                 | body.courseAttemptResult              | Pass                    | string               |
     | studentTranscriptAssociation  | 10                  | body.finalNumericGradeEarned          | 90                      | integer              |
     | studentTranscriptAssociation  | 3                   | body.finalNumericGradeEarned          | 82                      | integer              |
     | studentTranscriptAssociation  | 36                  | body.finalLetterGradeEarned           | B                       | string               |
     | studentTranscriptAssociation  | 5                   | body.finalNumericGradeEarned          | 87                      | integer              |
     | studentTranscriptAssociation  | 64                  | body.gradeLevelWhenTaken              | Tenth grade             | string               |
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
  And I should see "Processed 118 records." in the resulting batch job file
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
  And I should see "Staff2.xml records considered: 9" in the resulting batch job file
  And I should see "Staff2.xml records ingested successfully: 9" in the resulting batch job file
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
        | assessment                  |
        | attendance                  |
        | calendarDate                |
        | cohort                      |
        | course                      |
        | courseOffering              |
        | disciplineAction            |
        | disciplineIncident          |
        | educationOrganization       |
        | grade                       |
        | gradebookEntry              |
        | gradingPeriod               |
        | learningObjective           |
        | learningStandard            |
        | parent                      |
        | program                     |
        | reportCard                  |
        | section                     |
        | session                     |
        | staff                       |
        | staffCohortAssociation      |
        | staffProgramAssociation     |
        | student                     |
        | studentAcademicRecord       |
        | studentAssessmentAssociation|
        | studentCohortAssociation    |
        | studentCompetency           |
        | studentCompetencyObjective  |
        | studentDisciplineIncidentAssociation|
        | studentGradebookEntry       |
        | studentParentAssociation    |
        | studentProgramAssociation   |
        | studentSchoolAssociation    |
        | studentSectionAssociation   |
        | studentTranscriptAssociation|
        | teacherSchoolAssociation    |
        | teacherSectionAssociation   |
        |staffEducationOrganizationAssociation|

When zip file is scp to ingestion landing zone for "Midgar-Daybreak"
  And zip file is scp to ingestion landing zone for "Hyrule-NYC"
  And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database
  And a batch job for file "StoriedDataSet_NY.zip" is completed in database

Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | assessment                  | 19    |
        | attendance                  | 75    |
        | calendarDate                | 1112  |
        | cohort                      | 3     |
        | course                      | 103   |
        | courseOffering              | 103   |
        | disciplineAction            | 3     |
        | disciplineIncident          | 4     |
        | educationOrganization       | 12    |
        | grade                       | 4     |
        | gradebookEntry              | 12    |
        | gradingPeriod               | 23    |
        | learningObjective           | 197   |
        | learningStandard            | 1499  |
        | parent                      | 9     |
        | program                     | 2     |
        | reportCard                  | 2     |
        | section                     | 113   |
        | session                     | 26    |
        | staff                       | 51    |
        | staffCohortAssociation      | 3     |
        | staffEducationOrganizationAssociation| 31 |
        | staffProgramAssociation     | 7     |
        | student                     | 86    |
        | studentAcademicRecord       | 117   |
        | studentAssessmentAssociation| 203   |
        | studentCohortAssociation    | 6     |
        | studentCompetency           | 59    |
        | studentCompetencyObjective  | 4     |
        | studentDisciplineIncidentAssociation| 8 |
        | studentGradebookEntry       | 315   |
        | studentParentAssociation    | 9     |
        | studentProgramAssociation   | 6     |
        | studentSchoolAssociation    | 175   |
        | studentSectionAssociation   | 305   |
        | studentTranscriptAssociation| 196   |
        | teacherSchoolAssociation    | 19    |
        | teacherSectionAssociation   | 27    |

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
     | staffProgramAssociation               | 7     |
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
     | staffProgramAssociation               | 6     |
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
        | staffProgramAssociation     | 7     |
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
        | staffProgramAssociation     | 2                   | body.beginDate                 | 2011-01-01              | string               |
        | staffProgramAssociation     | 0                   | body.endDate                   | 2012-03-16              | string               |
        | staffProgramAssociation     | 4                   | body.beginDate                 | 2011-01-05              | string               |
        | staffProgramAssociation     | 1                   | body.beginDate                 | 2011-06-01              | string               |
        | staffProgramAssociation     | 0                   | body.beginDate                 | 2011-12-31              | string               |
        | staffProgramAssociation     | 4                   | body.endDate                   | 2012-02-15              | string               |
        | staffProgramAssociation     | 4                   | body.studentRecordAccess       | true                    | boolean              |
        | staffProgramAssociation     | 3                   | body.studentRecordAccess       | false                   | boolean              |
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
        | staffProgramAssociation     | 11    |
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
        | staffProgramAssociation     | 2                   | body.beginDate                 | 2011-01-01              | string               |
        | staffProgramAssociation     | 2                   | body.endDate                   | 2012-03-16              | string               |
        | staffProgramAssociation     | 4                   | body.beginDate                 | 2011-01-05              | string               |
        | staffProgramAssociation     | 1                   | body.beginDate                 | 2011-06-01              | string               |
        | staffProgramAssociation     | 4                   | body.beginDate                 | 2011-12-31              | string               |
        | staffProgramAssociation     | 8                   | body.endDate                   | 2012-02-15              | string               |
        | staffProgramAssociation     | 10                  | body.studentRecordAccess       | true                    | boolean              |
        | staffProgramAssociation     | 1                   | body.studentRecordAccess       | false                   | boolean              |
