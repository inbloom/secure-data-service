Feature: Use the Provisioning REST interface to provision a new sandbox application developer.

Scenario Outline: Deny access to users not using SLI Adminstrator credentials

	Given I am logged in using <Username> <Password> to realm "SLI"
	When I try to access the URI "/provision?stateOrganizationId=Test&tenantId=12345" with operation <Operation>
	Then I should be denied access
	Examples:
	| Username        | Password            | Operation |
	| "leader"        | "leader1234"        | "POST"    |

@wip	
Scenario Outline: Deny access to users using non-allowed methods

	Given I am logged in using <Username> <Password> to realm "SLI"
	When I try to access the URI "/provision?stateOrganizationId=Test&tenantId=12345" with operation <Operation>
	Then I should receive a return code of 405
  Examples:
  | Username        | Password            | Operation |
  | "leader"        | "leader1234"        | "PUT"     |
  | "administrator" | "administrator1234" | "DELETE"  |
  | "administrator" | "administrator1234" | "GET"  |

Scenario: Deny access to users using SLI Administrator credentials from non-SLI realms

	Given I am logged in using "badadmin" "badadmin1234" to realm "IL"
	When I try to access the URI "/provision?stateOrganizationId==Test&tenantId=12345" with operation "POST"
	Then I should be denied access

Scenario: Provision a new landing zone.

	Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
	When I try to access the URI "/provision?stateOrganizationId=Test&tenantId=12345" with operation "POST"
	Then I should receive a return code of 201

Scenario: Provision a new landing zone twice should fail.

	Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
	When I try to access the URI "/provision?stateOrganizationId=Test1&tenantId=12345" with operation "POST"
	Then I should receive a return code of 201
	Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
	When I try to access the URI "/provision?stateOrganizationId=Test1&tenantId=12345" with operation "POST"
	Then I should receive a return code of 409
