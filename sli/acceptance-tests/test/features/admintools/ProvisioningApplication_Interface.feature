@wip
Feature: Developer/Admin has an interface to create a High Level Ed-Org and link it to the Landing Zone

Background: Vendor/Developer is logged into the Provisioning Tool

Scenario: As a Admin I can define a High Level Ed-Org and Provision my Landing Zone
Given there is a production account in ldap for vendor "Macro Corp"
And I have an open web browser
And I am authenticated to SLI IDP as user "developer" with pass "developer1234"
When I go to the provisioning application
Then I can only enter a custom high-level ed-org
When I set the custom high-level ed-org to "Test Ed Org"
And I click the Provision button
Then I get the success message

@sandbox
Scenario: As a developer I can define a High Level Ed-Org and Provision my Landing Zone on sandbox
Given there is a sandbox account in ldap for vendor "Macro Corp"
And I have an open web browser
And I am authenticated to SLI IDP as user "developer" with pass "developer1234"
When I go to the provisioning application
Then I can select between the the high level ed-org of the sample data sets or enter a custom high-level ed-org
When I select the first sample data set
And I click the Provision button
Then I get the success message
