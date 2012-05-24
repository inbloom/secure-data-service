@RALLY_US2488
@wip
Feature: Developer/Vendor can create a High Level Ed-Org and link it to the Landing Zone

Background: Vendor/Developer is logged into the Provisioning Tool

Scenario: As a Vendor/Developer I use a defined a High Level Ed-Org to Provision my Landing Zone
Given there is a production account in ldap for vendor "Macro Corp"
When I provision
Then a high-level ed-org is created in Mongo
And Landing-Zone provisioining API is invoked with the high-level ed-org information

