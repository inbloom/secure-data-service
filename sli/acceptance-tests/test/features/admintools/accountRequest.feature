@RALLY_US1112
Feature: User requests for an account for production or sandbox account

Background:
  Given I have an open web browser

@production
Scenario: As a user I request for a production account, and is redirected to get-start page
  Given I go to the account registration page
  And I go to the production account registration page
  Then I am redirected to the hosting website

@sandbox
Scenario: As a user I request for a sandbox account
  Given I go to the account registration page
  And I go to the sandbox account registration page
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
  Then I should see the text "Email Confirmed"
  And I should see the text "You will be receiving an email with more information about your account."

@sandbox
Scenario: As an slc operator I want to check if a user accepted EULA
  Given I go to the account registration page
  And I go to the sandbox account registration page
  And there is an approved account with login name "<USER_ACCOUNT>"
  When I query LDAP for EULA acceptance for account with login name "<USER_ACCOUNT>"
  Then I get a record for "<USER_ACCOUNT>"
  And "First Name" is "Lance"
  And "Last Name" is "Alsop"
  And "Email" is "<USER_ACCOUNT_EMAIL>"
  And "Vendor" is "Acme Corp"

@sandbox
Scenario: As a user I request for a sandbox account while using random unicode characters
  Given I go to the account registration page
  And I go to the sandbox account registration page
  And there is no registered account for "<USER_ACCOUNT>" in LDAP
  When I fill out the field "First Name" as "Λance"
  And I fill out the field "Last Name" as "Alsöp"
  And I fill out the field "Vendor" as "⚔ Corp"
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
  Then I should see the text "Email Confirmed"
  And I should see the text "You will be receiving an email with more information about your account."
