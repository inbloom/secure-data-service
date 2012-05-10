@wip
Feature: User requests for an account for production or sandbox account

Background:
  Given I have an open web browser

@production
Scenario: As a user I request for a production account
  Given I go to the production account registration page
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "lalsop@acme.com"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd123"
  Then my password is shown as a series of dots
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click "Accept"
  Then I am directed to an acknowledgement page.
  And an email verification link is generated

@production
Scenario: Verifying email address
  When I visit "<VALID VERIFICATION LINK>"
  Then I should see the text "Registration Complete!"
  Then I should see the text "An administrator will email you when your account is ready."

@production
Scenario: Verifying email address - invalid link
  When I visit "<INVALID VERIFICATION LINK>"
  Then I should see the text "Account validation failed!"
  Then I should see the text "Invalid account verification code."

@production
Scenario: Verifying email address - already verified
  When I visit "<VALID VERIFICATION LINK>"
  Then I should see the text "Account validation failed!"
  Then I should see the text "Account previously verified."

@production
Scenario: As an slc operator I want to register unique user accounts in the system
  Given I go to the production account registration page
  And there is an approved account with login name "lalsop@acme.com"
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "lalsop@acme.com"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd123"
  And when I click "Submit"
  Then I receive an error that the account already exists

@production
Scenario: As an slc operator I want to check if a user accepted EULA
  Given I go to the production account registration page
  And there is an approved account with login name "lalsop@acme.com"
  When I query the database for EULA acceptance
  Then I get 1 record
  And "First Name" is "Lance"
  And "Last Name" is "Alsop"
  And "Vendor" is "Acme Corp"
  And "Email" is "lalsop@acme.com"

@production
Scenario: Clicking the "cancel" button - registration form
  Given I go to the production account registration page
  And when I click "Cancel"
  Then I am redirected to the hosting website

@production
Scenario: Clicking the "reject" button - EULA page
  Given I go to the production account registration page
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "lalsop@acme.com"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd123"
  Then my password is shown as a series of dots
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click "Reject"
  Then I am redirected to the hosting website

@sandbox
Scenario: As a user I request for a sandbox account
  Given I go to the sandbox account registration page
  When I fill out the field "First Name" as "Lance"
  And I fill out the field "Last Name" as "Alsop"
  And I fill out the field "Vendor" as "Acme Corp"
  And I fill out the field "Email" as "lalsop@acme.com"
  And I fill out the field "Password" as "dummypswd123"
  And I fill out the field "Confirmation" as "dummypswd123"
  Then my password is shown as a series of dots
  And when I click "Submit"
  Then my field entries are validated
  And I am redirected to a page with terms and conditions
  And when I click "Accept"
  Then I am directed to an acknowledgement page.
  And an email verification link is generated
  When I visit the email verification link
  Then I should see the text "Registration Complete!"