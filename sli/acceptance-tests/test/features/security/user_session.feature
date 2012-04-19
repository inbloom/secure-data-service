Feature: User Session Tests
Checking that sessions work as they should

Scenario: Granting access to valid user with valid, previously obtained access token

	Given the user has previously authenticated as "demo" with password "demo1234"
	And received a "valid" access token 
     When I try to access the resource "/api/rest/system/session/debug" using the user's credentials
	Then the user should get access to the resource
