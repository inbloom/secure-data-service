@RALLY_US2281
Feature: Developer/Vendor can create a High Level Ed-Org and link it to the Landing Zone

Background:
Given I have an open web browser
And LDAP server has been setup and running


Scenario: As a Vendor/Developer I use a defined High Level Ed-Org to Provision my Landing Zone
Given there is an account in ldap for vendor "Macro Corp"
And the account has a tenantId "MacroCorp1234"
When the developer go to the provisioning application web page
And the developer is authenticated to Simple IDP as user "<USERID>" with pass "<PASSWORD>"
And I provision with high-level ed-org to "<EDORG_NAME>"
Then I get the success message
And an ed-org is created in Mongo with the "stateOrganizationId" is "<EDORG_NAME>"
And a request to provision a landing zone is made
And the directory structure for the landing zone is stored in ldap
