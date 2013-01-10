@RALLY_US2290
@RALLY_US2289
Feature: Ingestion Mongo Performance Test

Scenario: I want to profile mongoDB operations performance
Given I have a clean database
  And the minimal indexes on collection "performance_test"
When I insert "10000" records into collection "performance_test"
  And I select "10000" records from the collection "performance_test"
  And I update "10000" records in the collection "performance_test"
Then I should see results of database performance
