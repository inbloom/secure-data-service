Feature: In order to retrive session details from the API

Scenario: Authenticate with valid cookie and get the session debug context successfully
        
    Given I login with "demo" and "demo1234"
    And I get an authentication cookie from the gatekeeper
	When I GET the url "/system/session/debug" using that cookie
    Then I should receive a return code of "200"
    And I should see the session debug context in the response body

Scenario: Deny access when request session debug context without cookie

	When I GET the url "/system/session/debug" using a blank cookie
    Then I should receive a return code of "401"

Scenario: Deny access when request session debug context with invalid cookie

	When I GET the url "/system/session/debug" using an invalid cookie
    Then I should receive a return code of "401"

Scenario: Access the session check resource with valid authentication cookie

    Given I login with "demo" and "demo1234"
    And I get an authentication cookie from the gatekeeper
	When I GET the url "/system/session/check" using that cookie
    Then I should receive a return code of "200"
    And I should see the authenticated object in the response body

Scenario: Access the session check resource without authentication cookie

	When I GET the url "/system/session/check" using a blank cookie
    Then I should receive a return code of "200"
    And I should see the non-authenticated object in the response body

Scenario: Access the session check resource with invalid authentication cookie
        
	When I GET the url "/system/session/check" using an invalid cookie
    Then I should receive a return code of "200"
    And I should see the non-authenticated object in the response body

