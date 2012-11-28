@rc
@sandbox
Feature: Sandbox account approval tests 

Background:
  Given I have an open web browser

Scenario: As a slc operator I disable an approved sandbox account
 And I navigate to the Portal home page
  When I see the realm selector I authenticate to "Shared Learning Collaborative"      
  Then I am redirected to "Simple" login page
  When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
  Then I should be on Portal home page
  Then I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And I click on Account Approval
  And I switch to the iframe
  Then I should be on the Authorize Developer Account page
  And I should see an account with name "RCTest Developer"
  And his account status is "approved"
  When I click the "Disable" button
  And I am asked "Do you really want to disable this user account?"
  When I click on Ok
  Then his account status changed to "disabled"

Scenario: As a developer I cannot log in with a disabled account
 And I navigate to the Portal home page
  When I see the realm selector I authenticate to "Shared Learning Collaborative"      
  Then I am redirected to "Simple" login page
  When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page
 Then I am informed that authentication has failed

Scenario: As a slc operator I enable an approved sandbox account
 And I navigate to the Portal home page
  When I see the realm selector I authenticate to "Shared Learning Collaborative"      
  Then I am redirected to "Simple" login page
  When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
  Then I should be on Portal home page
  Then I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And I click on Account Approval
  And I switch to the iframe
  Then I should be on the Authorize Developer Account page
  And I should see an account with name "RCTest Developer"
  And his account status is "disabled"
  When I click the "Enable" button
  Then his account status changed to "approved"
