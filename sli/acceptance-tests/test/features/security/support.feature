Feature: Authenticated Users can get an address to send support emails.

Scenario: Authenticated SLI user asks for support email

	Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
	When I make an API call to get the support email
	Then I receive JSON response that includes the address
	
Scenario: Unauthenticated user asks for support email

	When I GET the url "/v1/system/support/email" using a blank cookie
	Then I should receive a "401" response