@wip
Feature: OAuth 2.0 Authentication to SLI
As a service provider, I want to use the OAuth 2.0 specification to allow external applications access to my services

Scenario: Granting access to valid user with no prior session information

	Given I have no session information
	When I access a resource
	Then I am told to redirect my user to authenticate
	When the user logs in to the SLI-IDP Login page
	Then the user should be granted a valid access token
	And the user should be redirected to the application
	
Scenario: Granting access to valid user with valid, previously obtained session token & refresh token

	Given a user visits my application with a valid token and a valid refresh token
	When I try to access the application
	Then the user should get access to the application

#Question: Should it matter if the refresh token is expired?  Based on my current understanding if the access token is still valid it won't check the refresh token
Scenario: Granting access to valid user with valid, previously obtained session token & expired refresh token

	Given a user visits my application with a valid access token and a expired refresh token
	When I try to access the application
	Then the user should get access to the application

Scenario: Granting access to valid user with expired session & valid refresh token

	Given a user visits my application with a expired access token and a valid refresh token
	And my access token is denied
	And I try to use my refresh token to get a new access token
	And I get a new access token
	When I present my new access token
	Then the user should get access to the application

Scenario: Granting access to valid user with both expired session & refresh token

	Given a user visits my application with a expired access token and a expired refresh token
	And my access token is denied
	And I try to use my refresh token to get a new access token 
	And my refresh token is denied
	And I am told to redirect my user to authenticate
	When the user logs in to the SLI-IDP Login page
	Then the user should be granted a valid access token
	And the user should get access to the application
	
#Question: Presumably access tokens are encrypted.  If they have an obviously false access token will they be given an opportunity to supply a refresh token?
Scenario: Denying access to user who provides self-created (spoofed) tokens

	Given a user visits my application with a made up access token and a made up refresh token
	And my access token is denied
	And I try to use my refresh token to get a new access token
	And my refresh token is denied
	And the user is redirected to authenticate
