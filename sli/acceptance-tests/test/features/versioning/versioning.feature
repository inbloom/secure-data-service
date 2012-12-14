@RALLY_US3567
Feature: Entity versioning and migration strategies
  As a system that supports versions, I want to start tracking versions of entities.

  Background: None

  @DB_MIGRATION_BEFORE_API_STARTS
  Scenario: Remove all records in the metaData collection
    Given I drop the "metaData" collection

  @DB_MIGRATION_AFTER_API_STARTS
  Scenario: Check that after starting the API, documents exist in the collection
    Then there should be 42 records in the "metaData" collection
    And "SARJE" field is "0" for all records
    And "mongo_sv" field is "1" for all records
    And "dal_sv" field is "1" for all records

  @DB_MIGRATION_AFTER_UPVERSIONING
  Scenario: Check that the updated versions were detected on startup and thus SARJE has been signaled
    Then there should be 42 records in the "metaData" collection
    And "SARJE" field is "1" for all records
    And "mongo_sv" field is "1" for all records
    And "dal_sv" field is "999999" for all records

  @DB_MIGRATION_AFTER_UPVERSIONING
  Scenario: API requests for an student get transformed using the test transformation
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"  
    When I navigate to GET "/<STAFF URI>/<'Rick Rogers' ID>"
    Then "favoriteSubject" should be "Math"
    And "sex" should not exist
    And "name" should not exist
    And "nameData" should exist
