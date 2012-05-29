@RALLY_US1964
Feature: Cohort Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "Cohort1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | program                     |
     | educationOrganization       |
     | cohort                      |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | program                     | 1     |
     | educationOrganization       | 3     |
     | cohort                      | 2     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-1          | string               |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-2          | string               |
     | cohort                      | 1                   | body.cohortScope            | District                | string               |
     | cohort                      | 1                   | body.cohortScope            | Statewide               | string               |
     | cohort                      | 1                   | body.academicSubject        | English                 | string               |
     | cohort                      | 1                   | body.academicSubject        | Mathematics             | string               |
  And I should see "Processed 6 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Program1.xml records considered: 4" in the resulting batch job file
  And I should see "Program1.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "Program1.xml records failed: 0" in the resulting batch job file
  And I should see "Cohort1.xml records considered: 2" in the resulting batch job file
  And I should see "Cohort1.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "Cohort1.xml records failed: 0" in the resulting batch job file


Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "Cohort2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | program                     | 1     |
     | educationOrganization       | 3     |
     | cohort                      | 3     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-1          | string               |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-2          | string               |
     | cohort                      | 1                   | body.cohortIdentifier       | ACC-TEST-COH-3          | string               |
     | cohort                      | 2                   | body.cohortScope            | District                | string               |
     | cohort                      | 1                   | body.cohortScope            | Statewide               | string               |
     | cohort                      | 1                   | body.academicSubject        | English                 | string               |
     | cohort                      | 1                   | body.academicSubject        | Social Studies          | string               |
     | cohort                      | 1                   | body.academicSubject        | Mathematics             | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Cohort2.xml records considered: 1" in the resulting batch job file
  And I should see "Cohort2.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "Cohort2.xml records failed: 0" in the resulting batch job file
