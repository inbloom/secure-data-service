@RALLY_US1564
@security
@RALLY_US209
Feature: Security for discipline incident CRUD
  As a product owner, I want to validate that my discipline incident entity is properly secured up to current SLI standards

  Scenario: Authorized user tries to hit the disciplineIncident URL directly
    Given I am user "linda.kim" in IDP "IL"
    When I make an API call to get the disciplineIncident "DISC-INC-2"
    Then I receive a JSON response
 
  Scenario: Authorized user tries to hit the disciplineIncident URL directly
    Given I am user "rrogers" in IDP "IL"
    When I make an API call to get the disciplineIncident "DISC-INC-2"
    Then I receive a JSON response

  Scenario: Unauthorized authenticated user tries to hit the disciplineIncident URL directly
    Given I am user "cgray" in IDP "IL"
    When I make an API call to get the disciplineIncident "DISC-INC-2"
    Then I get a message that I am not authorized

 Scenario: Unauthorized authenticated user tries to hit the disciplineIncident URL directly
    Given I am user "akopel" in IDP "IL"
    When I make an API call to get the disciplineIncident "DISC-INC-2"
    Then I get a message that I am not authorized

