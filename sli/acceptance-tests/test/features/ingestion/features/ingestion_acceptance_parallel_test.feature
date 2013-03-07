@RALLY_US2033
Feature: Acceptance Storied Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone for "IL-Daybreak"
  And I am using preconfigured Ingestion Landing Zone for "NY-NYC"

Scenario: Post zip files containing all data for Illinois Daybreak and NY NYC as the payload for parallel ingestion jobs: Clean Database
Given the following collections are empty in datastore:
        | collectionName                        |
        | student                               |
        | studentSchoolAssociation              |
        | course                                |
        | educationOrganization                 |
        | school                                |
        | section                               |
        | studentSectionAssociation             |
        | teacher                               |
        | staff                                 |
        | staffEducationOrganizationAssociation |
        | teacherSchoolAssociation              |
        | teacherSectionAssociation             |
        | session                               |
        | assessment                            |
        | studentAssessment          |
        | gradebookEntry                        |
        | courseTranscript                      |
        | studentGradebookEntry                 |
        | parent                                |
        | studentParentAssociation              |
        | attendance                            |
        | program                               |
        | staffProgramAssociation               |
        | studentProgramAssociation             |
        | cohort                                |
        | staffCohortAssociation                |
        | studentCohortAssociation              |
        | learningStandard                      |
        | disciplineIncident                    |
  And I post "Parallel_IL_Daybreak.zip" file as the payload of the ingestion job for "IL-Daybreak"
  And I post "Parallel_NY.zip" file as the payload of the ingestion job for "NY-NYC"
When zip file is scp to ingestion landing zone for "IL-Daybreak"
  And zip file is scp to ingestion landing zone for "NY-NYC"
  And a batch job for file "Parallel_IL_Daybreak.zip" is completed in database
  And a batch job for file "Parallel_NY.zip" is completed in database
  And a batch job log has been created for "IL-Daybreak"
  And a batch job log has been created for "NY-NYC"
Then I should see following map of entry counts in the corresponding collections:
        | collectionName                        | count |
        | student                               | 86    |
        | studentSchoolAssociation              | 175   |
        | course                                | 97    |
        | educationOrganization                 | 6     |
        | school                                | 8     |
        | section                               | 106   |
        | studentSectionAssociation             | 298   |
        | teacher                               | 19    |
        | staff                                 | 32    |
        | staffEducationOrganizationAssociation | 17    |
        | teacherSchoolAssociation              | 20    |
        | teacherSectionAssociation             | 20    |
        | session                               | 26    |
        | assessment                            | 5     |
        | studentAssessment          | 116   |
        | courseTranscript                      | 196   |
        | parent                                | 9     |
        | studentParentAssociation              | 9     |
        | gradebookEntry                        | 12    |
        | studentGradebookEntry                 | 78    |
#        | attendance                            | 13650 |
        | program                               | 2     |
        | staffProgramAssociation               | 3     |
        | studentProgramAssociation             | 10    |
        | cohort                                | 3     |
        | staffCohortAssociation                | 3     |
        | studentCohortAssociation              | 9     |
        | learningStandard                      | 10    |
        | disciplineIncident                    | 2     |
  And I check to find if record is in collection:
        | collectionName                        | expectedRecordCount | searchParameter          | searchValue                | searchType           |
        | student                               | 1                   | metaData.externalId      | 100000000                  | string               |
        | student                               | 1                   | metaData.externalId      | 800000012                  | string               |
        | student                               | 1                   | metaData.externalId      | 900000024                  | string               |
        | teacher                               | 1                   | metaData.externalId      | cgray                      | string               |
        | course                                | 1                   | metaData.externalId      | 1st Grade Homeroom         | string               |
        | school                                | 1                   | metaData.externalId      | South Daybreak Elementary  | string               |
        | educationOrganization                 | 1                   | metaData.externalId      | IL-DAYBREAK                | string               |
        | educationOrganization                 | 1                   | metaData.externalId      | IL                         | string               |
        | program                               | 1                   | metaData.externalId      | ACC-TEST-PROG-1            | string               |
        | program                               | 1                   | metaData.externalId      | ACC-TEST-PROG-2            | string               |
        | cohort                                | 1                   | metaData.externalId      | ACC-TEST-COH-1             | string               |
        | cohort                                | 1                   | metaData.externalId      | ACC-TEST-COH-2             | string               |
        | cohort                                | 1                   | metaData.externalId      | ACC-TEST-COH-3             | string               |
        | disciplineIncident                    | 1                   | body.incidentIdentifier  | Whack-a-mole               | string               |
        | disciplineIncident                    | 1                   | body.incidentIdentifier  | Underwater cruise          | string               |

    And I should see "Processed 15277 records." in the resulting batch job file for "IL-Daybreak"
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered for processing: 78" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudent.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrganization.xml records considered for processing: 99" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 99" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrganization.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrgCalendar.xml records considered for processing: 22" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 22" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrgCalendar.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeMasterSchedule.xml records considered for processing: 90" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 90" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeMasterSchedule.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStaffAssociation.xml records considered for processing: 33" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 33" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStaffAssociation.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentEnrollment.xml records considered for processing: 485" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 485" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentEnrollment.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentGrade.xml records considered for processing: 640" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 640" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentGrade.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-READ2.xml records considered for processing: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-READ2.xml records ingested successfully: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-READ2.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-StateTest.xml records considered for processing: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-StateTest.xml records ingested successfully: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-StateTest.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ACT.xml records considered for processing: 5" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ACT.xml records ingested successfully: 5" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ACT.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-Learning.xml records considered for processing: 10" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-Learning.xml records ingested successfully: 10" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-Learning.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records considered for processing: 112" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records ingested successfully: 112" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records considered for processing: 4" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records ingested successfully: 4" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
#    And I should see "InterchangeAttendance.xml records considered for processing: 13650" in the resulting batch job file for "IL-Daybreak"
#    And I should see "InterchangeAttendance.xml records ingested successfully: 13650" in the resulting batch job file for "IL-Daybreak"
#    And I should see "InterchangeAttendance.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentParent.xml records considered for processing: 18" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentParent.xml records ingested successfully: 18" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentParent.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentProgram.xml records considered for processing: 10" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentProgram.xml records ingested successfully: 10" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentProgram.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentCohort.xml records considered for processing: 15" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentCohort.xml records ingested successfully: 15" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentCohort.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentDiscipline.xml records considered for processing: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentDiscipline.xml records ingested successfully: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentDiscipline.xml records failed processing: 0" in the resulting batch job file for "IL-Daybreak"
#  And I should see "Processed 137 records." in the resulting batch job file for "NY-NYC"
#  And I should not see an error log file created for "NY-NYC"
#  And I should see "InterchangeStudent.xml records considered for processing: 8" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudent.xml records ingested successfully: 8" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudent.xml records failed processing: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrganization.xml records considered for processing: 15" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 15" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrganization.xml records failed processing: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrgCalendar.xml records considered for processing: 4" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 4" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrgCalendar.xml records failed processing: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeMasterSchedule.xml records considered for processing: 16" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeMasterSchedule.xml records ingested successfully: 16" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeMasterSchedule.xml records failed processing: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStaffAssociation.xml records considered for processing: 78" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStaffAssociation.xml records ingested successfully: 78" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStaffAssociation.xml records failed processing: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudentEnrollment.xml records considered for processing: 16" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 16" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudentEnrollment.xml records failed processing: 0" in the resulting batch job file for "NY-NYC"
