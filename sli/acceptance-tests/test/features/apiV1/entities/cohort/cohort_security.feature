@security
@RALLY_US209
Feature: Security for Cohort CRUD
  As a product owner, I want to validate that my cohort entity is properly secured up to current SLI standards

  Scenario: Authorized user tries to hit the cohort URL directly
    Given I am user "sbantu" in IDP "IL"
    When I make an API call to get the cohort "ACC-TEST-COH-2"
    Then I receive a JSON response

  Scenario Outline: Unauthorized authenticated user tries to hit the cohort URL directly
    Given I am user <User> in IDP "IL"
    When I make an API call to get the cohort "ACC-TEST-COH-2"
    Then I get a message that I am not authorized
	Examples:
	|User    |
	|"llogan"|
	|"cgray" |
