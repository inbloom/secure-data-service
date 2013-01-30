Feature: Test the long lived session token generator

Scenario: Create a long lived session for user sgoddard
	Given I used the long lived session token generator script to create a token for user "sgoddard" with role "Educator" in tenant "Hyrule"  for realm "NY-STATE" that will expire in "300" seconds with client_id "AT1k3PdHzX" 
	Then I should see that my role is "Educator"
	And I navigate to GET "/v1/sections"
	And I should receive a return code of 200
