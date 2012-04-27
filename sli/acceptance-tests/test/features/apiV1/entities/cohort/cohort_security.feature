@security
Feature: Security for Cohort CRUD
  As a product owner, I want to validate that my cohort entity is properly secured up to current SLI standards

  Scenario: Authorized user tries to hit the cohort URL directly
    Given I am user "sbantu" in IDP "IL"
    When I make an API call to get the cohort "ACC-TEST-COH-2"
    Then I receive a JSON response

  Scenario: Unauthorized authenticated user tries to hit the cohort URL directly
    Given I am user "rrogers" in IDP "IL"
    When I make an API call to get the cohort "ACC-TEST-COH-2"
    Then I get a message that I am not authorized

  Scenario: Unauthorized authenticated user tries to hit the cohort URL directly
    Given I am user "cgray" in IDP "IL"
    When I make an API call to get the cohort "ACC-TEST-COH-2"
    Then I get a message that I am not authorized
