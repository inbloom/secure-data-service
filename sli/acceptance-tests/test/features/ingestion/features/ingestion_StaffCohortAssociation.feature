@RALLY_US1964
@RALLY_DE267
@RALLY_DE326
Feature: StaffCohortAssociation Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "StaffCohortAssociation1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | cohort                      |
     | staff                       |
     | staffCohortAssociation      |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | cohort                      | 3     |
     | staff                       | 3     |
     | staffCohortAssociation      | 3     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | staffCohortAssociation      | 3                   | body.studentRecordAccess    | true                    | boolean              |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2011-01-01              | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2010-01-15              | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2011-07-01              | string               |
     | staffCohortAssociation      | 1                   | body.endDate                | 2012-02-15              | string               |
  And I should see "Processed 9 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Cohort1.xml records considered: 3" in the resulting batch job file
  And I should see "Cohort1.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "Cohort1.xml records failed: 0" in the resulting batch job file
  And I should see "Staff1.xml records considered: 3" in the resulting batch job file
  And I should see "Staff1.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "Staff1.xml records failed: 0" in the resulting batch job file
  And I should see "StaffCohortAssociation1.xml records considered: 3" in the resulting batch job file
  And I should see "StaffCohortAssociation1.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "StaffCohortAssociation1.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "StaffCohortAssociation2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | cohort                      | 3     |
     | staff                       | 3     |
     | staffCohortAssociation      | 4     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | staffCohortAssociation      | 4                   | body.studentRecordAccess    | true                    | boolean              |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2011-01-01              | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2010-01-15              | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2011-07-01              | string               |
     | staffCohortAssociation      | 1                   | body.beginDate              | 2012-02-15              | string               |
     | staffCohortAssociation      | 1                   | body.endDate                | 2012-02-15              | string               |
  And I should see "Processed 10 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Cohort2.xml records considered: 3" in the resulting batch job file
  And I should see "Cohort2.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "Cohort2.xml records failed: 0" in the resulting batch job file
  And I should see "Staff2.xml records considered: 3" in the resulting batch job file
  And I should see "Staff2.xml records ingested successfully: 3" in the resulting batch job file
  And I should see "Staff2.xml records failed: 0" in the resulting batch job file
  And I should see "StaffCohortAssociation2.xml records considered: 4" in the resulting batch job file
  And I should see "StaffCohortAssociation2.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "StaffCohortAssociation2.xml records failed: 0" in the resulting batch job file
