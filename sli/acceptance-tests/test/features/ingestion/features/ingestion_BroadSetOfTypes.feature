@RALLY_US5180
Feature: Ingest BroadSetOfTypes.zip for regeneration of fixture data

Background: I have a landing zone route configured
Given I am using local data store

Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "BroadSetOfTypes.zip" file as the payload of the ingestion job
    And the "Midgar" tenant db is empty
    When zip file is scp to ingestion landing zone
    Then a batch job for file "BroadSetOfTypes.zip" is completed in database
    And I should not see an error log file created
    And I should not see a warning log file created
    And the data from tenant "Midgar"is exported to "test/features/ingestion/test_data/delete_fixture_data/"
