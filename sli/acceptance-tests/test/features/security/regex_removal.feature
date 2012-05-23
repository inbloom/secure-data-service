@RALLY_US210
Feature: As an SLI application I want to prevent attackers from executing regular expression DoS attacks
  This means the API should no longer support query by regular expression

Background:
  Given format "application/json"

Scenario: Querying school entities using the regex "nameOfInstitution=~High"
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
  When I navigate to GET "/v1/schools?nameOfInstitution=Daybreak%20Central%20High"
  Then I should receive a return code of 200
  And I should receive a collection of "1" entities
  When I navigate to GET "/v1/schools?nameOfInstitution=~High"
  Then I should receive a return code of 200
  And I should receive a collection of "0" entities
