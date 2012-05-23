Feature: Use the Provisioning REST interface to provision a new sandbox application developer

Scenario Outline: Deny access to users not using SLI Adminstrator credentials

	Given I am logged in using <Username> <Password> to realm "SLI"
	When I try to access the URI "/provision" with operation <Operation> and <StateOrganizationId> and <TenantId> 
	Then I should be denied access
	Examples:
	| Username        | Password            | Operation | StateOrganizationId | TenantId |
	| "leader"        | "leader1234"        | "POST"    | "Test"              | "12345"  |

Scenario Outline: Deny access to users using non-allowed methods

	Given I am logged in using <Username> <Password> to realm "SLI"
	When I try to access the URI "/provision" with operation <Operation> and <StateOrganizationId> and <TenantId> 
	Then I should receive a return code of 405
	Examples:
	| Username        | Password            | Operation | StateOrganizationId | TenantId |
	| "leader"        | "leader1234"        | "PUT"     | "Test"              | "12345"  |
	| "administrator" | "administrator1234" | "DELETE"  | "Test"              | "12345"  |
	| "administrator" | "administrator1234" | "GET"     | "Test"              | "12345"  |

Scenario Outline: Deny access to users using SLI Administrator credentials from non-SLI realms

	Given I am logged in using <Username> <Password> to realm "IL"
	When I try to access the URI "/provision" with operation <Operation> and <StateOrganizationId> and <TenantId> 
	Then I should be denied access
	Examples:
	| Username        | Password            | Operation | StateOrganizationId | TenantId |
	| "badadmin"      | "badadmin1234"      | "POST"    | "Test"              | "12345"  |

Scenario Outline: Provision a new landing zone

	Given I am logged in using <Username> <Password> to realm "SLI"
	When I try to access the URI "/provision" with operation <Operation> and <StateOrganizationId> and <TenantId> 	
	Then I should receive a return code of 201
	And I should see a top level ed org is created with "stateOrganizationId" is <StateOrganizationId> and "tenantId" is <<TenantId>
	And I should see this ed org is Authorized to use Apps "Databrowser" and "SLC Dashboards"
	Examples:
	| Username         | Password             | Operation | StateOrganizationId | TenantId |
	| "fakerealmadmin" | "fakerealmadmin1234" | "POST"    | "Test"              | "12345"  |

Scenario Outline: Provision a new landing zone twice should fail

	Given I am logged in using <Username> <Password> to realm "SLI"
	When I try to access the URI "/provision" with operation <Operation> and <StateOrganizationId> and <TenantId> 	
	Then I should receive a return code of 201
	Then I should receive a return code of 201
	And I should see a top level ed org is created with "stateOrganizationId" is <StateOrganizationId> and "tenantId" is <<TenantId>
	And I should see this ed org is Authorized to use Apps "Databrowser" and "SLC Dashboards"
	Given I am logged in using <Username> <Password> to realm "SLI"
	When I try to access the URI "/provision" with operation <Operation> and <StateOrganizationId> and <TenantId> 	
	Then I should receive a return code of 409
	Examples:
	| Username         | Password             | Operation | StateOrganizationId | TenantId |
	| "fakerealmadmin" | "fakerealmadmin1234" | "POST"    | "Test1"             | "12345"  |
	
