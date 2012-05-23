@wip
Feature: In order to securely access the API

Scenario: Authenticate with valid cookie and get an entity successfully

	Given I login with "demo" and "demo1234"
	  And I get an authentication cookie from the gatekeeper
	When I GET the url "/schools" using that cookie
     Then I should receive a return code of 200
       And I should see schools in the response body
       
Scenario: Redirected with blank cookie

	When I GET the url "/schools" using a blank cookie
	Then I should be redirected to the login page

Scenario: Redirected with an invalid cookie

	When I GET the url "/shools" using an invalid cookie
	Then I should be redirected to the login page

Scenario: Redirected if logged in with invalid user

	Given I login with "invalid" and "invalid1234"
	Then I should receive a return code of 401
