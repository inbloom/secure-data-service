@wip
Feature: OAuth 2.0 Authentication to SLI
As a service provider, I want to use the OAuth 2.0 specification to allow external applications access to my services

Scenario: Granting access to valid user with no prior session information

	Given something
	When something
	Then something
	
Scenario: Granting access to valid user with valid, previously obtained session token & refresh token

	Given something
	When something
	Then something

Scenario: Granting access to valid user with valid, previously obtained session token & expired refresh token

	Given something
	When something
	Then something

Scenario: Granting access to valid user with expired session & valid refresh token

	Given something
	When something
	Then something

Scenario: Granting access to valid user with both expired session & refresh token

	Given something
	When something
	Then something

Scenario: Denying access to user who provides self-created (spoofed) tokens

	Given something
	When something
	Then something
