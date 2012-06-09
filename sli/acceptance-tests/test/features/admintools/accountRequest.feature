@RALLY_US1112
Feature: User requests for an account for production or sandbox account

Background:
  Given I have an open web browser
  And I go to the account registration page

@production
Scenario: As a user I request for a production account
  Given I go to the production account registration page
  And there is no registered account for "<USER_ACCOUNT>" in the SLI database
  And there is no registered account for "<USER_ACCOUNT>" in LDAP
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "<USER_ACCOUNT_EMAIL>"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd123"
  Then my password is shown as a series of dots
  And a captcha form is shown
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click "Accept"
  Then I am directed to an acknowledgement page.
  And an email verification link for "<USER_ACCOUNT>" is generated
# Verify email address
  When I visit "<VALID VERIFICATION LINK>"
  Then I should see the text "Registration Complete!"
  And I should see the text "An administrator will email you when your account is ready."
# Unhappy path: already verified
  When I visit "<ALREADY VERIFIED LINK>"
  Then I should see the text "Account validation failed!"
  And I should see the text "Account previously verified."
# Unhappy path: invalid link
  When I visit "<INVALID VERIFICATION LINK>"
  Then I should see the text "Account validation failed!"
  And I should see the text "Invalid account verification code."

@production
Scenario: As an slc operator I want to register unique user accounts in the system
  Given I go to the production account registration page
  And there is an approved account with login name "<USER_ACCOUNT>"
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "<USER_ACCOUNT_EMAIL>"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd123"
  And when I click "Submit"
  Then I receive an error that the account already exists

@production
Scenario: As an slc operator I want to check if a user accepted EULA
  Given I go to the production account registration page
  And there is an approved account with login name "<USER_ACCOUNT>"
  When I query the database for EULA acceptance
  Then I get 1 record for "<USER_ACCOUNT>"
  And "First Name" is "Lance"
  And "Last Name" is "Alsop"
  And "Email" is "<USER_ACCOUNT_EMAIL>"
  And "Environment" is "Production"
  And "Vendor" is "Acme Corp"

@production
Scenario: Clicking the "cancel" button - registration form
  Given I go to the production account registration page
  And when I click "Cancel"
  Then I am redirected to the hosting website

@production
Scenario: Clicking the "reject" button - EULA page
  Given I go to the production account registration page
  And there is no registered account for "<USER_ACCOUNT>" in the SLI database
  And there is no registered account for "<USER_ACCOUNT>" in LDAP
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "<USER_ACCOUNT>"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd123"
  Then my password is shown as a series of dots
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click "Reject"
  Then I am redirected to the hosting website
  And the account for "<USER_ACCOUNT>" is removed from SLI database

@production
Scenario: Unhappy path: invalid form inputs
  Given I go to the production account registration page
  When I fill out the field "First Name" as ""
  And I fill out the field "Last Name" as ""
  And I fill out the field "Vendor" as ""
  And I fill out the field "Email" as ""
  And I fill out the field "Password" as ""
  And I fill out the field "Confirmation" as ""
  Then my password is shown as a series of dots
  And when I click "Submit"
  Then I should see the error message "blank"
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "<USER_ACCOUNT_EMAIL>"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd456"
  Then my password is shown as a series of dots
  And when I click "Submit"
  Then I should see the error message "match confirmation"

@sandbox
Scenario: As a user I request for a sandbox account
  Given I go to the sandbox account registration page
  And there is no registered account for "<USER_ACCOUNT>" in the SLI database
  And there is no registered account for "<USER_ACCOUNT>" in LDAP
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "<USER_ACCOUNT_EMAIL>"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd123"
  Then my password is shown as a series of dots
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click "Accept"
  Then I am directed to an acknowledgement page.
  And an email verification link for "<USER_ACCOUNT>" is generated
  When I visit "<VALID VERIFICATION LINK>"
  Then I should see the text "Registration Complete!"
  And I should see the text "An administrator will email you when your account is ready."

@sandbox
Scenario: As an slc operator I want to check if a user accepted EULA
  Given I go to the sandbox account registration page
  And there is an approved account with login name "<USER_ACCOUNT>"
  When I query the database for EULA acceptance
  Then I get 1 record for "<USER_ACCOUNT>"
  And "First Name" is "Lance"
  And "Last Name" is "Alsop"
  And "Email" is "<USER_ACCOUNT_EMAIL>"
  And "Environment" is "Sandbox"
  And "Vendor" is "Acme Corp"
