@RALLY_US4835
@rc
@sandbox
Feature: User requests for a developer account in sandbox environments  

Background:
  Given I have an open web browser

Scenario: SLC Operator logs into SAMT and deletes the developer account
    When I navigate to the Portal home page
    Then I will be redirected to realm selector web page
    When I click on the "Shared Learning Collaborative" realm in "Admin"
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

Scenario: As an app developer I request a sandbox account and  I want to verify my registration email
  And I go to the account registration page
  When I fill out the field "First Name" as "RCTest"
  And I fill out the field "Last Name" as "Developer"
  And I fill out the field "Vendor" as "WGEN RC"
  And I fill out the field "Email" as "<DEVELOPER_SB_EMAIL>"
  And I fill out the field "Password" as "<DEVELOPER_SB_EMAIL_PASS>"
  And I fill out the field "Confirmation" as "<DEVELOPER_SB_EMAIL_PASS>"
  Then my password is shown as a series of dots
  And a captcha form is shown
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click Accept
  Then I am directed to an acknowledgement page.

  Given I received an email to verify my email address
  When I click the link to verify my email address
  Then I should be notified that my email is verified
