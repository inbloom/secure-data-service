@RALLY_US3431
Feature: Ingestion Mongo Tracking Aspect Test

As a SLI operator, I want to verify mongo tracking was successful after ingestion is completed.

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Mongo Tracking
  Given I post "Session1.zip" file as the payload of the ingestion job
  And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
  When zip file is scp to ingestion landing zone
  And a batch job for file "Session1.zip" is completed in database
  And a batch job log has been created

  Then I should see following map of entry counts in the corresponding batch job db collections:
        | collectionName              | count |
        | newBatchJob                 | 1     |

  And I check to find if complex record is in batch job collection:
        | collectionName | expectedRecordCount | searchParameter  | searchType  |
        | newBatchJob    | 1                   | executionStats   | object      |
