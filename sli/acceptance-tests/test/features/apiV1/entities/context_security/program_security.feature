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
	And the sli securityEvent collection is empty
	When I make an API call to get the student <Student>
    Then I should receive a return code of <Code>
     And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount      | searchParameter       | searchValue                          |
       | securityEvent       | <expectedRecordCount>    | body.userEdOrg        | <userEdOrg>                          |
       | securityEvent       | <expectedRecordCount>    | body.targetEdOrgList  | <targetEdOrg>                        |
       | securityEvent       | <expectedRecordCount>    | body.logMessage       | Access Denied:Cannot access entities |

	Examples:
	| Student          | Code | Comment                                                                 | userEdOrg     | targetEdOrg   | expectedRecordCount  |
	|"Randy Voelker"   |  200 | #current program, current student                                       | *             | *             | 0                    |
	|"Curtis Omeara"   |  403 | #old program, current student                                           | IL-SUNSET     | IL-SUNSET     | 1                    |
	|"Theresa Deguzman"|  403 | #current program, current student, student record flag false            | IL-SUNSET     | IL-SUNSET     | 1                    |
	|"Paul Bunker"     |  403 | #Current program, old student                                           | IL-SUNSET     | IL-SUNSET     | 1                    |
	|"Sabrina Knepper" |  403 | #current program, old student                                           | IL-SUNSET     | IL-SUNSET     | 1                    |
	|"Roberta Jones"   |  200 | #current program, old student but in current section taught by teacher  | IL-SUNSET     | IL-SUNSET     | 0                    |
    |"Christopher Bode"|  200 | #current program, current student                                       | *             | *             | 0                    |
