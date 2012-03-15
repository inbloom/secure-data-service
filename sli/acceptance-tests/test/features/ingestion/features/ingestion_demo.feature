Feature: Demo Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DemoData.zip" file as the payload of the ingestion job
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
        | studentTranscriptAssociation|
        | studentSectionGradebookEntry|
When zip file is scp to ingestion landing zone
#    And "30" seconds have elapsed
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 92    |
        | studentSchoolAssociation    | 123   |
        | course                      | 15    |
        | educationOrganization       | 3     |
        | school                      | 5     |
        | section                     | 25    |
        | studentSectionAssociation   | 158   |
        | teacher                     | 4     |
        | teacherSchoolAssociation    | 6     |
        | teacherSectionAssociation   | 20    |
        | session                     | 8     |
        | assessment                  | 15    |
        | studentAssessmentAssociation| 100   |
        | studentTranscriptAssociation| 90    |
#        | studentSectionGradebookEntry| 60    |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 1                   | metaData.externalId      | 530425896                  | string               |
       | student                     | 1                   | metaData.externalId      | 784204643                  | string               |
       | teacher                     | 1                   | metaData.externalId      | cgray                      | string               |
       | course                      | 1                   | metaData.externalId      | 1st Grade Homeroom         | string               |
       | school                      | 1                   | metaData.externalId      | South Daybreak Elementary  | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL-DAYBREAK                | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL                         | string               |
    And I should see "Processed 727 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 93" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 93" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 23" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 23" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 8" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 8" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 25" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 25" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 30" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 30" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 283" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 283" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records considered: 15" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records ingested successfully: 15" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment.xml records considered: 100" in the resulting batch job file
    And I should see "InterchangeStudentAssessment.xml records ingested successfully: 100" in the resulting batch job file
    And I should see "InterchangeStudentAssessment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 150" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 150" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DemoData.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
    And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
        | collectionName              | count |
        | student                     | 92    |
        | studentSchoolAssociation    | 123   |
        | course                      | 15    |
        | educationOrganization       | 3     |
        | school                      | 5     |
        | section                     | 25    |
        | studentSectionAssociation   | 158   |
        | teacher                     | 4     |
        | teacherSchoolAssociation    | 6     |
        | teacherSectionAssociation   | 20    |
        | session                     | 8     |
        | assessment                  | 15    |
        | studentAssessmentAssociation| 100   |
        | studentTranscriptAssociation| 90    |
#        | studentSectionGradebookEntry| 60    |
    And I check to find if record is in collection:
       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
       | student                     | 1                   | metaData.externalId      | 530425896                  | string               |
       | student                     | 1                   | metaData.externalId      | 784204643                  | string               |
       | teacher                     | 1                   | metaData.externalId      | cgray                      | string               |
       | course                      | 1                   | metaData.externalId      | 1st Grade Homeroom         | string               |
       | school                      | 1                   | metaData.externalId      | South Daybreak Elementary  | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL-DAYBREAK                | string               |
       | educationOrganization       | 1                   | metaData.externalId      | IL                         | string               |
    And I should see "Processed 727 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeStudent.xml records considered: 93" in the resulting batch job file
    And I should see "InterchangeStudent.xml records ingested successfully: 93" in the resulting batch job file
    And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records considered: 23" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 23" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records considered: 8" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records ingested successfully: 8" in the resulting batch job file
    And I should see "InterchangeEducationOrgCalendar.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records considered: 25" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records ingested successfully: 25" in the resulting batch job file
    And I should see "InterchangeMasterSchedule.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered: 30" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 30" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records considered: 283" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records ingested successfully: 283" in the resulting batch job file
    And I should see "InterchangeStudentEnrollment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records considered: 15" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records ingested successfully: 15" in the resulting batch job file
    And I should see "InterchangeAssessmentMetadata.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentAssessment.xml records considered: 100" in the resulting batch job file
    And I should see "InterchangeStudentAssessment.xml records ingested successfully: 100" in the resulting batch job file
    And I should see "InterchangeStudentAssessment.xml records failed: 0" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records considered: 150" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records ingested successfully: 150" in the resulting batch job file
    And I should see "InterchangeStudentGrade.xml records failed: 0" in the resulting batch job file
