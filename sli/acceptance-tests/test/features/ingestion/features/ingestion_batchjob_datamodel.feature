Feature: Batchjob Datamodel Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing configured interchanges as a payload of the ingestion job: Clean Database
Given I post "BatchJobDataModel.zip" file as the payload of the ingestion job
    And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 1     |
#    And I check to find if record is in collection:
#       | collectionName              | expectedRecordCount | searchParameter          | searchValue                | searchType           |
#       | student                     | 1                   | metaData.externalId      | 100000000                  | string               |
   And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeEducationOrganization.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
