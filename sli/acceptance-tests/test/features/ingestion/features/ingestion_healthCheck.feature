@RALLY_US3054
Feature: Ingestion HealthCheck Test

As a SLI operator, I want to verify if ingestion is responsive.

Background: I have a landing zone route configured
Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Startup HealthCheck
  When I navigate to the Ingestion Service HealthCheck page and submit login credentials "admin" "admin"
  And the following collections are empty in batch job datastore:
        | collectionName              |
        | newBatchJob                 |
  Then I receive a JSON response
  And the response should include version, startTime, lastActivity, lastActivityTime, heartBeats
  And the value of "lastActivity" should be "StartUp"

Scenario: HealthCheck after Ingestion
  Given I post "Session1.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And a batch job for file "Session1.zip" is completed in database
  And a batch job log has been created
  And I navigate to the Ingestion Service HealthCheck page and submit login credentials "admin" "admin"
  Then I receive a JSON response
  And the response should include version, startTime, lastActivity, lastActivityTime, heartBeats
