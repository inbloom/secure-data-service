@rc
Feature: User requests for an account for production or sandbox account

Background:
  Given I have an open web browser
  And I go to the account registration page on RC

@production
Scenario: As an app developer I request a production account
  Given I go to the production account registration page
  And there is no registered account for "sliAppDeveloper@gmail.com" in LDAP
  When I fill out the field "First Name" as "Sli"
  And I fill out the field "Last Name" as "Developer"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "sliAppDeveloper@gmail.com"
  And I fill out the field "Password" as "sli123"
  And I fill out the field "Confirmation" as "sli123"
  Then my password is shown as a series of dots
  And a captcha form is shown
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click "Accept"
  Then I am directed to an acknowledgement page.
  And an email verification link for "sliAppDeveloper@gmail.com" is generated
# Verify email address
  When I visit "<VALID VERIFICATION LINK>"
  Then I should see the text "Email Confirmed"
  And I should see the text "You will be receiving an email with more information about your account."

@production
Scenario: As an app developer I want to verify my registration email



@production
Scenario: As an SLC Operator I want to approve the app developer account






