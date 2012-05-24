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
        | studentAssessmentAssociation          |
        | gradebookEntry                        |
        | studentTranscriptAssociation          |
        | studentSectionGradebookEntry          |
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
        | studentAssessmentAssociation          | 116   |
        | studentTranscriptAssociation          | 196   |
        | parent                                | 9     |
        | studentParentAssociation              | 9     |
        | gradebookEntry                        | 12    |
        | studentSectionGradebookEntry          | 78    |
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
#        | student                               | 78                  | metaData.tenantId        | IL                         | string               |
#        | student                               | 8                   | metaData.tenantId        | NY                         | string               |
#        | studentSchoolAssociation              | 167                 | metaData.tenantId        | IL                         | string               |
#        | studentSchoolAssociation              | 8                   | metaData.tenantId        | NY                         | string               |
#        | course                                | 89                  | metaData.tenantId        | IL                         | string               |
#        | course                                | 8                   | metaData.tenantId        | NY                         | string               |
#        | educationOrganization                 | 3                   | metaData.tenantId        | IL                         | string               |
#        | educationOrganization                 | 3                   | metaData.tenantId        | NY                         | string               |
#        | school                                | 4                   | metaData.tenantId        | IL                         | string               |
#        | school                                | 4                   | metaData.tenantId        | NY                         | string               |
#        | section                               | 90                  | metaData.tenantId        | IL                         | string               |
#        | section                               | 16                  | metaData.tenantId        | NY                         | string               |
#        | studentSectionAssociation             | 259                 | metaData.tenantId        | IL                         | string               |
#        | studentSectionAssociation             | 8                   | metaData.tenantId        | NY                         | string               |
#        | teacher                               | 3                   | metaData.tenantId        | IL                         | string               |
#        | teacher                               | 16                  | metaData.tenantId        | NY                         | string               |
#        | staff                                 | 11                  | metaData.tenantId        | IL                         | string               |
#        | staff                                 | 21                  | metaData.tenantId        | NY                         | string               |
#        | staffEducationOrganizationAssociation | 8                   | metaData.tenantId        | IL                         | string               |
#        | staffEducationOrganizationAssociation | 9                   | metaData.tenantId        | NY                         | string               |
#        | teacherSchoolAssociation              | 4                   | metaData.tenantId        | IL                         | string               |
#        | teacherSchoolAssociation              | 16                  | metaData.tenantId        | NY                         | string               |
#        | teacherSectionAssociation             | 4                   | metaData.tenantId        | IL                         | string               |
#        | teacherSectionAssociation             | 16                  | metaData.tenantId        | NY                         | string               |
#        | session                               | 22                  | metaData.tenantId        | IL                         | string               |
#        | session                               | 4                   | metaData.tenantId        | NY                         | string               |
#        | assessment                            | 5                   | metaData.tenantId        | IL                         | string               |
#        | assessment                            | 0                   | metaData.tenantId        | NY                         | string               |
#        | studentAssessmentAssociation          | 116                 | metaData.tenantId        | IL                         | string               |
#        | studentAssessmentAssociation          | 0                   | metaData.tenantId        | NY                         | string               |
#        | studentTranscriptAssociation          | 196                 | metaData.tenantId        | IL                         | string               |
#        | studentTranscriptAssociation          | 0                   | metaData.tenantId        | NY                         | string               |
#        | parent                                | 9                   | metaData.tenantId        | IL                         | string               |
#        | parent                                | 0                   | metaData.tenantId        | NY                         | string               |
#        | studentParentAssociation              | 9                   | metaData.tenantId        | IL                         | string               |
#        | studentParentAssociation              | 0                   | metaData.tenantId        | NY                         | string               |
#        | gradebookEntry                        | 12                  | metaData.tenantId        | IL                         | string               |
#        | gradebookEntry                        | 0                   | metaData.tenantId        | NY                         | string               |
#        | studentSectionGradebookEntry          | 78                  | metaData.tenantId        | IL                         | string               |
#        | studentSectionGradebookEntry          | 0                   | metaData.tenantId        | NY                         | string               |
#        | attendance                            | 13650               | metaData.tenantId        | IL                         | string               |
#        | attendance                            | 0                   | metaData.tenantId        | NY                         | string               |
#        | program                               | 2                   | metaData.tenantId        | IL                         | string               |
#        | program                               | 0                   | metaData.tenantId        | NY                         | string               |
#        | staffProgramAssociation               | 3                   | metaData.tenantId        | IL                         | string               |
#        | staffProgramAssociation               | 0                   | metaData.tenantId        | NY                         | string               |
#        | studentProgramAssociation             | 10                  | metaData.tenantId        | IL                         | string               |
#        | studentProgramAssociation             | 0                   | metaData.tenantId        | NY                         | string               |
      And I should see "Processed 15273 records." in the resulting batch job file for "IL-Daybreak"
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrganization.xml records considered: 99" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 99" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 22" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 22" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeMasterSchedule.xml records considered: 90" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 90" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStaffAssociation.xml records considered: 33" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 33" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentEnrollment.xml records considered: 485" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 485" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentGrade.xml records considered: 640" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 640" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records considered: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records ingested successfully: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records considered: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records ingested successfully: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ACT.xml records considered: 1" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ACT.xml records ingested successfully: 1" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-ACT.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-Learning.xml records considered: 10" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-Learning.xml records ingested successfully: 10" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeAssessmentMetadata-Learning.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records considered: 112" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records ingested successfully: 112" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records considered: 4" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records ingested successfully: 4" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
#    And I should see "InterchangeAttendance.xml records considered: 13650" in the resulting batch job file for "IL-Daybreak"
#    And I should see "InterchangeAttendance.xml records ingested successfully: 13650" in the resulting batch job file for "IL-Daybreak"
#    And I should see "InterchangeAttendance.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentParent.xml records considered: 18" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentParent.xml records ingested successfully: 18" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentParent.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentProgram.xml records considered: 10" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentProgram.xml records ingested successfully: 10" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentProgram.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentCohort.xml records considered: 15" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentCohort.xml records ingested successfully: 15" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentCohort.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentDiscipline.xml records considered: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentDiscipline.xml records ingested successfully: 2" in the resulting batch job file for "IL-Daybreak"
    And I should see "InterchangeStudentDiscipline.xml records failed: 0" in the resulting batch job file for "IL-Daybreak"
#  And I should see "Processed 137 records." in the resulting batch job file for "NY-NYC"
#  And I should not see an error log file created for "NY-NYC"
#  And I should see "InterchangeStudent.xml records considered: 8" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudent.xml records ingested successfully: 8" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrganization.xml records considered: 15" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 15" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrgCalendar.xml records considered: 4" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 4" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeMasterSchedule.xml records considered: 16" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeMasterSchedule.xml records ingested successfully: 16" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStaffAssociation.xml records considered: 78" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStaffAssociation.xml records ingested successfully: 78" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudentEnrollment.xml records considered: 16" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 16" in the resulting batch job file for "NY-NYC"
#  And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file for "NY-NYC"
