@wip
Feature: SLC operator approves/disables production accounts or disables sandbox accounts

Background: None

Scenario: As a slc operator I approve pending production account request
Given there is a production account request for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer" pending in the account request queue
When I approve the account request
Then a new account is created in production OpenAM with login name "Lplyer" and the role is "Vendor_Admin"
And an email is sent to the requestor with a link to the application registration tool

Scenario: As a slc operator I reject pending production account request
Given there is a production account request for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer" pending in the account request queue
When I reject the account request
Then a no account exists in production OpenAM with login name "Lplyer"

Scenario: As a slc operator I disable an approved production account
Given there is an approved production account  for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer" 
When I disable the account 
Then production OpenAM account with login name "Lplyer" is set as inactive

Scenario: As a developer my request for sandbox account is instantly accepted
Given I submit a request for a a sandbox account request for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer" pending in the account request queue
Then a new account is created automatically in sandbox OpenAM with login name "Lplyer" and the role is "Super_Admin"
And an email is sent to the requestor with a link to provision sandbox and a link for sandbox application registration tool

Scenario: As a slc operator I disable an approved sandbox account
Given there is an approved sandbox account  for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer" 
When I disable the account 
Then sandbox OpenAM account with login name "Lplyer" is set as inactive