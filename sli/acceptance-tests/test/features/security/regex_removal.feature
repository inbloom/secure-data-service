@smoke @RALLY_US209 @RALLY_US210 @DE2017
Feature: As of DE2017 the API should support regex

Background:
  Given format "application/json"

Scenario: Querying school entities using a partial match based on a regular expression
  Given I am logged in using "iladmin" "iladmin" to realm "SLI"
  When I navigate to GET "/v1/schools?nameOfInstitution=Chemistry%20Elementary"
  Then I should receive a return code of 200
  And I should receive a collection of "1" entities
  When I navigate to GET "/v1/schools?nameOfInstitution=~Elementary$"
  Then I should receive a return code of 200
  And I should receive a collection of "2" entities
