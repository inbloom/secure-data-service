@wip
Feature: Application Registration Entity
As an OAuth application developer for SLI, I want to create a registration entity in SLI so that it can authenticate with SLI

Scenario: CRUD operations on Applications

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I navigate to POST "/apps"
	Then I should receive a return code of 201
     And I should receive an ID for the newly created application
	When I navigate to GET "/apps/<New App ID>"
	Then I should receive a return code of 200
     And I should receive the data for the specified application entry
	When I navigate to DELETE "/apps/<New App ID>"
	Then I should receive a return code of 204
     And I should no longer be able to get that application's data'

Scenario: Deny creation when specifying invalid fields

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I POST an application specifying an invalid field
	Then I should receive a return code of 400

Scenario: Deny access when logging in as invalid user

	Given I am a valid "sli" end user "baduser" with password "baduser1234"
	And I am authenticated to SEA/LEA IDP
	When I navigate to GET "/apps/<Testing App>"
	Then I should receive a return code of 403

Scenario Outline: Deny creation when user specifying auto-generated field

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I POST an application specifying the auto-generated field <Field> 
	Then I should receive a return code of 400
	Examples:
	| Field           |
	| "client_id"     |
	| "client_secret" |

@wip
Scenario Outline: Deny update when user specifying read-only auto-generated field

	Given I am a valid "sli" end user "demo" with password "demo1234"
	And I am authenticated to SEA/LEA IDP
	When I PUT an application specifying the auto-generated field <Field> 
	Then I should receive a return code of 400
	Examples:
	| Field           |
	| "client_id"     |
	| "client_secret" |
