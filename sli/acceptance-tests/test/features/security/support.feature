Feature: Authenticated Users can get an address to send support emails.

Scenario: Authenticated SLI user asks for support email

	Given I login with "demo" and "demo1234"
	When I make an API call to get the support email
	Then I receive JSON response that includes the address