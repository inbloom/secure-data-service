@RALLY_US1964
Feature: StudentCohortAssociation Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "StudentCohortAssociation1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | program                     |
     | student                     |
     | educationOrganization       |
     | studentCohortAssociation    |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | program                     | 1     |
     | student                     | 3     |
     | educationOrganization       | 3     |
     | cohort                      | 3     |
     | studentCohortAssociation    | 2     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-02-01              | string               |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-05-15              | string               |
     | studentCohortAssociation    | 1                   | body.endDate                | 2011-12-31              | string               |
  And I should see "Processed 12 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Program1.xml records considered: 4" in the resulting batch job file
  And I should see "Program1.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "Program1.xml records failed: 0" in the resulting batch job file
  And I should see "Student1.xml records considered: 3" in the resulting batch job file
  And I should see "Student1.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "Student1.xml records failed: 0" in the resulting batch job file
  And I should see "Cohort1.xml records considered: 5" in the resulting batch job file
  And I should see "Cohort1.xml records ingested successfully: 5" in the resulting batch job file
  And I should see "Cohort1.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "StudentCohortAssociation2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | studentCohortAssociation    | 3     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-02-01              | string               |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-05-15              | string               |
     | studentCohortAssociation    | 1                   | body.endDate                | 2011-12-31              | string               |
     | studentCohortAssociation    | 1                   | body.beginDate              | 2011-03-01              | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Cohort2.xml records considered: 1" in the resulting batch job file
  And I should see "Cohort2.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "Cohort2.xml records failed: 0" in the resulting batch job file
