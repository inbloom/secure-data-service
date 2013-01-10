@RALLY_US2092 @RALLY_US2107 @RALLY_US2108 @RALLY_US2104 @RALLY_US2163
Feature: SLC operator approves/disables production accounts or disables sandbox accounts

Background: 
  Given I have a "mock" SMTP/Email server configured

@sandbox
Scenario: As a slc operator I disable an approved sandbox account
Given a sandbox account request for vendor "Macro Corp" 
And first name "Loraine" and last name "Plyler" 
And login name "Lplyer@macrocorp.com" approved in the account request queue
When I disable the account 
Then sandbox LDAP account with login name "Lplyer@macrocorp.com" is set as inactive
