@RALLY_US4835
@rc
@sandbox
Feature: User requests for a developer account in sandbox environments  

Background:
  Given I have an open web browser
  And I am running in Sandbox mode

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

  And he should receive an email telling him his account is approved
