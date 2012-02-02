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
	And I am authenticated to SEA/LEA IDP
	When I try to access the URI "/realm" with operation <Operation>
	Then I should be denied access
	Examples:
	| Username        | Password            | Operation |
	| "leader"        | "leader1234"        | "POST"    |
	| "educator"      | "educator1234"      | "GET"     |
	| "leader"        | "leader1234"        | "PUT"     |
	| "administrator" | "administrator1234" | "DELETE"  |

Scenario: Create a new realm

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I POST a new realm
	Then I should receive a return code of 201
     And I should receive a new ID for my new realm
     	
Scenario: Read a list of realms

  Given I am a valid "sli" end user "demo" with password "demo1234"
  And I am authenticated to SEA/LEA IDP
  When I GET a list of realms
  Then I should receive a return code of 200
  And I should see a list of valid realm objects

Scenario: Read an existing realm

Given I am a valid "sli" end user "demo" with password "demo1234"
And I am authenticated to SEA/LEA IDP
When I GET a specific realm "SLI"
Then I should receive a return code of 200
And I should see a valid object returned
	
Scenario: Update an existing realm

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I PUT to change the realm "Fake Realm" to add a mapping between default role "Educator" to role "Blah"
	Then I should receive a return code of 204
	
Scenario: Delete an existing realm

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I DELETE the realm "Another Fake Realm"
	Then I should receive a return code of 204

Scenario: Deny mappings from non-SLI Default roles to custom roles

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I add a mapping between default role "Governator" and custom role "blah" for realm "Fake Realm"
	Then I should receive a return code of 400

Scenario: Deny mapping the same custom role to multiple default SLI roles

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I add a mapping between default role "Educator" and custom role "foo" for realm "Fake Realm"
	Then I should receive a return code of 204
	When I add a mapping between default role "Leader" and custom role "foo" for realm "Fake Realm"
	Then I should receive a return code of 400

Scenario: Deny mapping the same custom role to the default role twice

  Given I am a valid "sli" end user "demo" with password "demo1234"
  And I am authenticated to SEA/LEA IDP
  When I add a mapping between default role "Aggregate Viewer" and custom role "Observer" for realm "Fake Realm"
  Then I should receive a return code of 204
  When I add a mapping between default role "Aggregate Viewer" and custom role "Observer" for realm "Fake Realm"
  Then I should receive a return code of 400
