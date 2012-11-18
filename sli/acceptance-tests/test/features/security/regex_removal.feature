@smoke @RALLY_US209 @RALLY_US210 @DE2017
Feature: As of DE2017 the API should support regex

Background:
  Given format "application/json"

Scenario: Querying school entities using the regex "nameOfInstitution=~High"
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
  When I navigate to GET "/v1/schools?nameOfInstitution=Daybreak%20Central%20High"
  Then I should receive a return code of 200
  And I should receive a collection of "1" entities
  When I navigate to GET "/v1/schools?nameOfInstitution=~High"
  Then I should receive a return code of 200
  And I should receive a collection of "7" entities
