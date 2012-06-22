@RALLY_US1804
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

Scenario Outline: Teacher views students through valid Cohort Association

  Given I am user "manthony" in IDP "IL"
  When I make an API call to get the student <Student>
    Then I should receive a return code of <Code>
  Examples:
  | Student        | Code | Comment |
  |"Todd Angulo"   |  200 | #current cohort, current student|
  |"Agnes Trinh"   |  403 | #old cohort, current student|
  |"Stella Rego"   |  403 | #current cohort, current student, student record flag false|
  |"Glenda Koch"   |  403 | #Current cohort, old student|
  |"Johnny Tallent"|  403 | #current cohort, old student|
  |"Thelma Frasier"|  200 | #current cohort, current student|
