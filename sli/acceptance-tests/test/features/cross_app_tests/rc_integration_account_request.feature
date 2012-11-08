@rc
Feature: User requests for an account for production or sandbox account

Background: None

Scenario: As an app developer I request a production account
  Given I have an open web browser
  And I go to the account registration page
  When I fill out the field "First Name" as "RCTest"
  And I fill out the field "Last Name" as "Developer"
  And I fill out the field "Vendor" as "WGEN RC"
  And I fill out the field "Email" as "<DEVELOPER_EMAIL>"
  And I fill out the field "Password" as "test1234"
  And I fill out the field "Confirmation" as "test1234"
  Then my password is shown as a series of dots
  And a captcha form is shown
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click "Accept"
  Then I am directed to an acknowledgement page.

Scenario: As an app developer I want to verify my registration email
  Given I received an email to verify my email address
  When I click the link to verify my email address
  Then I should be notified that my email is verified

Scenario: As an SLC Operator I want to approve the app developer account
  Given I have an open web browser
  And I navigate to the Portal home page
  When I select the "Shared Learning Collaborative" realm 
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
