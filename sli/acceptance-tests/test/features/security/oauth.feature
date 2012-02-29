@wip
Feature: OAuth 2.0 Authentication to SLI
As a service provider, I want to use the OAuth 2.0 specification to allow external applications access to my services

Scenario: Granting access to valid user with no prior session information

	Given a user has no session information
	When I try to access the resource "/students" using the user's credentials
	And I am told to redirect my user to authenticate
	When the user logs in to the SLI-IDP Login page as "demo" with password "demo1234"
	Then the user should be granted a valid access token
	And the user should have access to the resource
	
Scenario: Granting access to valid user with valid, previously obtained session token & refresh token

	Given the user has previously authenticated as "demo" with password "demo1234"
	And received a "valid" access token 
	And received a "valid" refresh token
     When I try to access the resource "admin" using the user's credentials
	Then the user should get access to the resource

Scenario: Granting access to valid user with valid, previously obtained session token & expired refresh token

  Given the user has previously authenticated as "demo" with password "demo1234"
  And received a "valid" access token 
  And received a "expired" refresh token
  When I try to access the resource "admin" using the user's credentials
  Then the user should get access to the resource

Scenario: Granting access to valid user with expired session & valid refresh token

	Given a user visits the resource "/students" 
	And the user has previously authenticated as "demo" with password "demo1234"
	And the user has a "expired" access token
	And the user has a "valid" refresh token
	And the user submits their "access" token
	And the user's access token is denied
	And the user submits their "refresh" token
	And the user gets a new "access" token
	When the user submits their access token
	Then the user should get access to the resource

Scenario: Granting access to valid user with both expired session & refresh token

	Given a user visits the resource "/students"
	And the user has a "expired" access token
	And the user has a "expired" refresh token
	And the user submits their "access" token
	And the user's "access" token is denied
	And the user submits their "refresh" token
	And the user's "refresh" token is denied
	And the user is redirected to authenticate
	When the user logs in to the SLI-IDP Login page as "demo" with password "demo1234"
	Then the user should be granted a valid access token
	And the user should get access to the resource
	
#Question: Presumably access tokens are encrypted.  If they have an obviously false access token will they be given an opportunity to supply a refresh token?
Scenario: Denying access to user who provides self-created (spoofed) tokens

	Given a user visits the resource "/students" 
	And the user has a made up access token
	And the user has a made up refresh token
	And the user's "access" token is denied
	And the user's "refresh" token is denied
	And the user is redirected to authenticate
