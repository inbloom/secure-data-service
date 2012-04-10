Feature: Batchjob Datamodel Data Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

@wip
Scenario: Post a zip file containing configured interchanges as a payload of the ingestion job: Clean Database
Given I post "BatchJobDataModelMinimalWithError.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName              |
        | educationOrganization       |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | educationOrganization       | 1     |
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
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "InterchangeEducationOrganization.xml records considered: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeEducationOrganization.xml records failed: 0" in the resulting batch job file
