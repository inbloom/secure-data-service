Feature: Acceptance Canonical Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "AcceptanceCanonicalData.zip" file as the payload of the ingestion job
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
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | studentSchoolAssociation    | 156   |
        | course                      | 8     |
        | educationOrganization       | 3     |
        | school                      | 4     |
        | section                     | 9     |
        | studentSectionAssociation   | 209   |
        | teacher                     | 3     |
        | teacherSchoolAssociation    | 4     |
        | teacherSectionAssociation   | 4     |
        | session                     | 10    |
        | assessment                  | 4     |
        | studentAssessmentAssociation| 116   |
        | studentTranscriptAssociation| 50    |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentSectionGradebookEntry| 315   |
        | attendance                  | 14    |
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
    And I should see "Processed 1026 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 15" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 15" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 10" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 10" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 9" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 9" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 11" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 11" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 365" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 365" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 377" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 377" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records considered: 6" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records ingested successfully: 6" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records considered: 7" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records ingested successfully: 7" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records considered: 112" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records ingested successfully: 112" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAttendance.xml records considered: 14" in the resulting batch job file 
    And I should see "InterchangeAttendance.xml records ingested successfully: 14" in the resulting batch job file 
    And I should see "InterchangeAttendance.xml records failed: 0" in the resulting batch job file 
    And I should see "InterchangeStudentParent.xml records considered: 18" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records ingested successfully: 18" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records failed: 0" in the resulting batch job file


Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "AcceptanceCanonicalData.zip" file as the payload of the ingestion job
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
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 78    |
        | studentSchoolAssociation    | 156   |
        | course                      | 8     |
        | educationOrganization       | 3     |
        | school                      | 4     |
        | section                     | 9     |
        | studentSectionAssociation   | 209   |
        | teacher                     | 3     |
        | teacherSchoolAssociation    | 4     |
        | teacherSectionAssociation   | 4     |
        | session                     | 10    |
        | assessment                  | 4     |
        | studentAssessmentAssociation| 116   |
        | studentTranscriptAssociation| 50    |
        | parent                      | 9     |
        | studentParentAssociation    | 9     |
        | gradebookEntry              | 12    |
        | studentSectionGradebookEntry| 315   |
        | attendance                  | 14    |
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
    And I should see "Processed 1026 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 78" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 15" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 15" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 10" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 10" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 9" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 9" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 11" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 11" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 365" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 365" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 377" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 377" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records considered: 6" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records ingested successfully: 6" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-DIBELS.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records considered: 7" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records ingested successfully: 7" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata-ISAT.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records considered: 112" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records ingested successfully: 112" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Lkim8thgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records considered: 4" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records ingested successfully: 4" in the resulting batch job file
    And I should see "InterchangeStudentAssessment-Rbraverman1stgrade.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAttendance.xml records considered: 14" in the resulting batch job file 
    And I should see "InterchangeAttendance.xml records ingested successfully: 14" in the resulting batch job file 
    And I should see "InterchangeAttendance.xml records failed: 0" in the resulting batch job file 
    And I should see "InterchangeStudentParent.xml records considered: 18" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records ingested successfully: 18" in the resulting batch job file
    And I should see "InterchangeStudentParent.xml records failed: 0" in the resulting batch job file
