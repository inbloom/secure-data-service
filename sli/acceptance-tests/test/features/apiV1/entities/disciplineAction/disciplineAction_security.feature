@RALLY_US1889
@security
@RALLY_US209
Feature: Security for discipline action CRUD
  As a product owner, I want to validate that my discipline action entity is properly secured up to current SLI standards

  Scenario: Authorized user tries to hit the disciplineAction URL directly
    Given I am user "linda.kim" in IDP "IL"
    When I make an API call to get the disciplineAction "DISC-ACT-1"
    Then I receive a JSON response
 
  Scenario: Authorized user tries to hit the disciplineAction URL directly
    Given I am user "rrogers" in IDP "IL"
    When I make an API call to get the disciplineAction "DISC-ACT-1"
    Then I receive a JSON response

  Scenario: Unauthorized authenticated user tries to hit the disciplineAction URL directly
    Given I am user "cgray" in IDP "IL"
    When I make an API call to get the disciplineAction "DISC-ACT-1"
    Then I get a message that I am not authorized

 Scenario: Unauthorized authenticated user tries to hit the disciplineAction URL directly
    Given I am user "akopel" in IDP "IL"
    When I make an API call to get the disciplineAction "DISC-ACT-1"
    Then I get a message that I am not authorized

