
@rc
Feature:  RC Integration SAMT Tests

Background:
Given I have an open web browser

Scenario: SLC Operator logs into SAMT and creates SEA Admin for tenant "RCTestTenant". SEA Admin then completes the user creation process.
  When I navigate to the User Management Page
  Then I will be redirected to realm selector web page
  When I select the "Shared Learning Collaborative" realm
  Then I am redirected to "Simple" login page
  When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
  Then I delete the RC SEA Administrator "RCTestFN RCTestLN" if exists
  Then I click on "Add User" button
  And I am redirected to "Add a User" page
  And I can directly update the "Full Name" field to "RCTestFN RCTestLN"
  And I can directly update the "Email" field to "testuser0.wgen@gmail.com"

  And I can select "SEA Administrator" from a choice between "SLC Operator, SEA Administrator, LEA Administrator, Ingestion User, Realm Administrator" Role
  And I can update the "Tenant" field to "RCTestTenant"
  And I can update the "EdOrg" field to "RCTestEdOrg"

  When I click button "Save"
  Then I am redirected to "Admin Account Management" page
  And a "Success" message is displayed
  And the new user has "Tenant" updated to "RCTestTenant"
  And the new user has "EdOrg" updated to "RCTestEdOrg"
  And the user has Roles as "SEA Administrator"

  Then I set my password
