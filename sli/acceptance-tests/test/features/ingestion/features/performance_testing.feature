Feature: Ingestion Performance Test

Background: I have a landing zone route configured
Given I am using destination-local data store
    And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job
Given I want to ingest locally provided data "PerformanceData.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
Then  I should say that we started processing