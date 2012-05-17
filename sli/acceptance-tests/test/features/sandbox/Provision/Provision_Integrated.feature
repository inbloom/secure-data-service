@wip
Feature: Developer/Vendor can create a High Level Ed-Org and link it to the Landing Zone

Background:
Given I have an open web browser
And LDAP server has been setup and running

Scenario: As a Vendor/Developer I use a defined High Level Ed-Org to Provision my Landing Zone
Given there is an account in ldap for vendor "Macro Corp"
And the account has a tenantId "MacroCorp1234"
And I am authenticated to SLI IDP as user "<USERID>" with pass "<PASSWORD>"
When I go to the provisioning application web page
And I provision with high-level ed-org to "Test Ed Org"
Then I get the success message
And an ed-org is created in Mongo with the "stateOrganizationId" is "Test Ed Org" and "tenantId" is "MacroCorp1234"
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap
