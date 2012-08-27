@RALLY_US3567
Feature: Entity versioning and migration strategies
  As a system that supports versions, I want to start tracking versions of entities.

  Background: None

  @DB_MIGRATION_BEFORE_API_STARTS
  Scenario: Remove all records in the metaData collection
    Given I drop the "metaData" collection

  @DB_MIGRATION_AFTER_API_STARTS
  Scenario: Check that after starting the API, documents exist in the collection
    Then there should be 41 records in the "metaData" collection
    And "SARJE" field is "0" for all records
    And "mongo_sv" field is "1" for all records
    And "dal_sv" field is "1" for all records

  @DB_MIGRATION_AFTER_UPVERSIONING
  Scenario: Check that the updated versions were detected on startup and thus SARJE has been signaled
    Then there should be 41 records in the "metaData" collection
    And "SARJE" field is "1" for all records
    And "mongo_sv" field is "1" for all records
    And "dal_sv" field is "999999" for all records
