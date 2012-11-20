@rc
Feature: User requests for a developer account in production or sandbox environments  
# currently works for production only. Not tested in sandbox.

Background:
  Given I have an open web browser

Scenario: SLC Operator logs into SAMT and deletes the developer account
    When I navigate to the Portal home page
    When I see the realm selector I authenticate to "Shared Learning Collaborative"   
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
    Then I should be on Portal home page
    Then I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "Administrative Account Management"

    And I switch to the iframe
    Then I delete the user "RCTest Developer" if exists
    And I switch to the iframe	

Scenario: As an app developer I request a production account and  I want to verify my registration email
  And I go to the account registration page
  When I fill out the field "First Name" as "RCTest"
  And I fill out the field "Last Name" as "Developer"
  And I fill out the field "Vendor" as "WGEN RC"
  And I fill out the field "Email" as "<DEVELOPER_EMAIL>"
  And I fill out the field "Password" as "<DEVELOPER_EMAIL_PASS>"
  And I fill out the field "Confirmation" as "<DEVELOPER_EMAIL_PASS>"
  Then my password is shown as a series of dots
  And a captcha form is shown
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click Accept
  Then I am directed to an acknowledgement page.

#As an app developer I want to verify my registration email
  Given I received an email to verify my email address
  When I click the link to verify my email address
  Then I should be notified that my email is verified

Scenario: As an SLC Operator I want to approve the app developer account
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
  And his account status is "pending"
  When I approve his account
  Then I am asked "Do you really want to approve this user account?"
  When I click on Ok
  Then I should see "Account was successfully updated"
  And his account status is "approved"
  And he should receive an email telling him his account is approved
