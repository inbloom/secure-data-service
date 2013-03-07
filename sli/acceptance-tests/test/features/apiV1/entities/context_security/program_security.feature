@RALLY_US1803
@security
@RALLY_US209
Feature: Security for Program CRUD
  As a product owner, I want to validate that my program entity is properly secured up to current SLI standards

  Scenario Outline: Authorized user tries to hit the program URL directly
    Given I am user <User> in IDP "IL"
    When I make an API call to get the program <Program>
    Then I receive a JSON response
    Examples:
	|User     |Program          |
	|"ckoch"  |"ACC-TEST-PROG-2"|
	|"rrogers"|"ACC-TEST-PROG-1"|
	|"llogan" |"ACC-TEST-PROG-2"|
	|"cgray"  |"ACC-TEST-PROG-2"|

#  Scenario Outline: Unauthorized authenticated user tries to hit the program URL directly
#    Given I am user <User> in IDP "IL"
#    When I make an API call to get the program <Program>
#    Then I get a message that I am not authorized
#    Examples:
#	|User     |Program          |

Scenario Outline: Teacher views students through valid Program Association

	Given I am user "manthony" in IDP "IL"
	When I make an API call to get the student <Student>
    Then I should receive a return code of <Code>
	Examples:
	| Student          | Code | Comment |
	|"Randy Voelker"   |  200 | #current program, current student|
	|"Curtis Omeara"   |  403 | #old program, current student|
	|"Theresa Deguzman"|  403 | #current program, current student, student record flag false|
	|"Paul Bunker"     |  403 | #Current program, old student|
	|"Sabrina Knepper" |  403 | #current program, old student|
	|"Roberta Jones"   |  200 | #current program, old student but in current section taught by teacher|
	|"Christopher Bode"|  200 | #current program, current student|
