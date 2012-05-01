@security
Feature: Security for Program CRUD
  As a product owner, I want to validate that my program entity is properly secured up to current SLI standards

  Scenario: Authorized user tries to hit the program URL directly
    Given I am user "ckoch" in IDP "IL"
    When I make an API call to get the program "ACC-TEST-PROG-2"
    Then I receive a JSON response

  Scenario: Authorized user tries to hit the program URL directly
    Given I am user "rrogers" in IDP "IL"
    When I make an API call to get the program "ACC-TEST-PROG-1"
    Then I receive a JSON response

  Scenario: Unauthorized authenticated user tries to hit the program URL directly
    Given I am user "rrogers" in IDP "IL"
    When I make an API call to get the program "ACC-TEST-PROG-2"
    Then I get a message that I am not authorized

  Scenario: Unauthorized authenticated user tries to hit the program URL directly
    Given I am user "cgray" in IDP "IL"
    When I make an API call to get the program "ACC-TEST-PROG-2"
    Then I get a message that I am not authorized
