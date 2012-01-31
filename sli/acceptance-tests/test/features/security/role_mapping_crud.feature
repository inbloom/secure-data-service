@wip
Feature: Custom Role Mapping functions and Realm Listing functions
As an administrator tool application, I should have access to API calls to perform CRUD operations to allow custom role mapping
As any SLI application, I can access an API resource that only returns a list of realms, even while unauthenticated

Scenario: Unauthenticated users can access a list of realms

	Given I have not yet authenticated
	When I make a call to get the list of realms
	Then I should see a response that contains the list of realms
	And I should see a URL for each realm that links to their IDP
	And I should not see any data about any realm's role-mapping

Scenario Outline: Deny access to users not using SLI Adminstrator credentials

	Given I am a valid "sli" end user <Username> with password <Password>
	When I try to access the URI "/pub/roles/mapping/" with operation <Operation>
	Then I should be denied access
	Examples:
	| Username        | Password            | Operation |
	| "leader"        | "leader1234"        | "POST"    |
	| "educator"      | "educator1234"      | "GET"     |
	| "leader"        | "leader1234"        | "PUT"     |
	| "administrator" | "administrator1234" | "DELETE"  |

Scenario: Create a custom role mapping

	Given I am a valid "sli" end user "demo" with password "demo1234"
	When I POST a mapping between default role "Educator" and custom role "blah" for realm "SLI"
	Then I should receive a return code of 201
	
Scenario: Read an existing role mapping

	Given I am a valid "sli" end user "demo" with password "demo1234"
	When I GET a list of role mappings for realm "SLI"
	Then I should receive a return code of 200
	And I should see a valid object returned

Scenario: Update an existing role mapping

	Given I am a valid "sli" end user "demo" with password "demo1234"
	When I PUT to change the mapping between default role "Educator" and custom role "blah" to role "Blah" for realm "SLI"
	Then I should receive a return code of 204
	
Scenario: Delete an existing role mapping

	Given I am a valid "sli" end user "demo" with password "demo1234"
	When I DELETE a mapping between the default role "Educator" and custom role "Blah" for realm "SLI"
	Then I should receive a return code of 204

Scenario: Deny duplicated creations

	Given I am a valid "sli" end user "demo" with password "demo1234"
	When I POST a mapping between default role "Educator" and custom role "blah" for realm "SLI"
	Then I should receive a return code of 201
	When I duplicate the previous POST request to map the default role "Educator" to custom role "blah" for realm "SLI"
	Then I should receive a return code of 400
	
Scenario: Deny duplicated deletions

	Given I am a valid "sli" end user "demo" with password "demo1234"
	When I DELETE a mapping between the default role "Educator" and custom role "Blah" for realm "SLI"
	Then I should receive a return code of 201
	When I duplicate the previous DELETE request to unmap the default role "Educator" to custom role "blah" for realm "SLI"
	Then I should receive a return code of 400

Scenario: Deny mappings from non-SLI Default roles to custom roles

	Given I am a valid "sli" end user "demo" with password "demo1234"
	When I POST a mapping between default role "Governator" and custom role "blah" for realm "SLI"
	Then I should receive a return code of 400

Scenario: Deny mapping the same custom role to multiple default SLI roles

	Given I am a valid "sli" end user "demo" with password "demo1234"
	When I POST a mapping between default role "Educator" and custom role "blah" for realm "SLI"
	Then I should receive a return code of 201
	When I POST a mapping between default role "Leader" and custom role "blah" for realm "SLI"
	Then I should receive a return code of 400
