@rc
Feature: User requests for an account for production or sandbox account

Background:
  Given I have an open web browser
  And I go to the account registration page on RC

Scenario: As an app developer I request a production account
  Given I go to the production account registration page
  And there is no registered account for "testdev.wgen.gmail.com" in LDAP
  When I fill out the field "First Name" as "Test"
  And I fill out the field "Last Name" as "Developer"
  And I fill out the field "Vendor" as "WGEN RC"
  And I fill out the field "Email" as "testdev.wgen@gmail.com"
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



Scenario: As an SLC Operator I want to approve the app developer account






