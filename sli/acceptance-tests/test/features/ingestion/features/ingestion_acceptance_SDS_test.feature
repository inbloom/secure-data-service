Feature: Acceptance Storied Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

@smoke @integration
Scenario: Post a zip file containing all data for Illinois Daybreak as a payload of the ingestion job: Clean Database
Given I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | student                     |
        | studentSchoolAssociation    |
        | course                      |
        | educationOrganization       |
        | school                      |
        | section                     |
        | studentSectionAssociation   |
        | teacher                     |
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
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | studentSchoolAssociation    | 167   |
        | course                      | 89    |
        | educationOrganization       | 3     |
        | school                      | 4     |
        | section                     | 90    |
        | studentSectionAssociation   | 259   |
        | teacher                     | 3     |
        | staff                       | 11    |
        | staffEducationOrganizationAssociation| 8|
        | teacherSchoolAssociation    | 4     |
        | teacherSectionAssociation   | 4     |
        | session                     | 22    |
        | assessment                  | 5     |
        | studentAssessmentAssociation| 141   |
        | studentTranscriptAssociation| 196   |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentSectionGradebookEntry| 78    |
        | attendance                  | 13650 |
        | program                     | 2     |
        | staffProgramAssociation     | 3     |
        | studentProgramAssociation   | 10    |
        | cohort                      | 3     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 9     |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 1                   | metaData.externalId      | 100000000                  | string               |
       | student                     | 1                   | metaData.externalId      | 800000012                  | string               |
       | student                     | 1                   | metaData.externalId      | 900000024                  | string               |
       | teacher                     | 1                   | metaData.externalId      | cgray                      | string               |
       | course                      | 1                   | metaData.externalId      | 1st Grade Homeroom         | string               |
       | school                      | 1                   | metaData.externalId      | South Daybreak Elementary  | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL-DAYBREAK                | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL                         | string               |
       | program                     | 1                   | metaData.externalId      | ACC-TEST-PROG-1            | string               |
       | program                     | 1                   | metaData.externalId      | ACC-TEST-PROG-2            | string               |
       | cohort                      | 1                   | metaData.externalId      | ACC-TEST-COH-1             | string               |
       | cohort                      | 1                   | metaData.externalId      | ACC-TEST-COH-2             | string               |
       | cohort                      | 1                   | metaData.externalId      | ACC-TEST-COH-3             | string               |
    And I should see "Processed 15292 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 99" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 99" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 22" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 22" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 90" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 90" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 33" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 33" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 491" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 491" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 640" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 640" in the resulting batch job file
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
    And I should see "InterchangeAssessmentMetadata-Learning.xml records considered: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-Learning.xml records ingested successfully: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-Learning.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records considered: 112" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records ingested successfully: 112" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records considered: 25" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records ingested successfully: 25" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Cgray-ACT.xml records failed: 0" in the resulting batch job file
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

@smoke @integration
Scenario: Post a zip file containing all data for Illinois Sunset as a payload of the ingestion job: Append Database
Given I post "StoriedDataSet_IL_Sunset.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 80    |
        | studentSchoolAssociation    | 169   |
        | course                      | 90    |
        | educationOrganization       | 3     |
        | school                      | 4     |
        | section                     | 91    |
        | studentSectionAssociation   | 261   |
        | teacher                     | 4     |
        | staff                       | 17    |
        | staffEducationOrganizationAssociation|11|
        | teacherSchoolAssociation    | 5     |
        | teacherSectionAssociation   | 5     |
        | session                     | 22    |
        | assessment                  | 5     |
        | studentAssessmentAssociation| 141   |
        | studentTranscriptAssociation| 196   |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentSectionGradebookEntry| 78    |
        | attendance                  | 13650 |
        | program                     | 2     |
        | staffProgramAssociation     | 3     |
        | studentProgramAssociation   | 10    |
        | cohort                      | 3     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 9     | 
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 1                   | metaData.externalId      | 1000000000                 | string               |
       | teacher                     | 1                   | metaData.externalId      | manthony                   | string               |
       | course                      | 1                   | metaData.externalId      | A.P. Calculus              | string               |
       | school                      | 1                   | metaData.externalId      | Sunset Central High School | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL-SUNSET                  | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL                         | string               |
    And I should see "Processed 23 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 2" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 2" in the resulting batch job file
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
    And I should see "InterchangeStaffAssociation.xml records considered: 12" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 12" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file

@smoke @integration
Scenario: Post a zip file containing all data for New York as a payload of the ingestion job: Append Database
Given I post "StoriedDataSet_NY.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 88    |
        | studentSchoolAssociation    | 177   |
        | course                      | 98    |
        | educationOrganization       | 6     |
        | school                      | 8     |
        | section                     | 107   |
        | studentSectionAssociation   | 269   |
        | teacher                     | 20    |
        | staff                       | 38    |
        | staffEducationOrganizationAssociation|20|
        | teacherSchoolAssociation    | 21    |
        | teacherSectionAssociation   | 21    |
        | session                     | 26    |
        | assessment                  | 5     |
        | studentAssessmentAssociation| 141   |
        | studentTranscriptAssociation| 196   |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentSectionGradebookEntry| 78    |
        | attendance                  | 13650 |
        | program                     | 2     |
        | staffProgramAssociation     | 3     |
        | studentProgramAssociation   | 10    |
        | cohort                      | 3     |
        | staffCohortAssociation      | 3     |
        | studentCohortAssociation    | 9     |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 2                   | metaData.externalId      | 100000006                  | string               |
       | staff                       | 1                   | metaData.externalId      | jcarlyle                   | string               |
       | section                     | 1                   | metaData.externalId      | Mason201-Sec1              | string               |
       | school                      | 1                   | metaData.externalId      | 1000000111                 | string               |
       | educationOrganization       | 1                   | metaData.externalId      | NY-Parker                  | string               |
       | educationOrganization       | 1                   | metaData.externalId      | NY                         | string               |
    And I should see "Processed 137 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 8" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 8" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 15" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 15" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 16" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 16" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 78" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 78" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 16" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 16" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
