@wip
Feature: User requests for an account for production or sandbox account

Background: None

Scenario: As a user I request for a production account
Given I go to the production account registration page
When I fill out all the required fields as "First Name" as "Lance"
And "Last Name" as "Alsop""
And "Vendor" as  "Acme Corp"
And "Email" as "lalsop@acme.com"
And "Password" as "dummypswd123"
Then my password is shown as a series of dots
And when I click submit
Then my field entries are validated
And I am redirected to a page with terms and conditions
When I click on submit I am directed to an acknolwedgement page.

Scenario: As a user I request for a sandbox account
Given I go to the sandbox account registration page
When I fill out all the required fields as "First Name" as "Lance"
And "Last Name" as "Alsop""
And "Vendor" as  "Acme Corp"
And "Email" as "lalsop@acme.com"
And "Password" as "dummypswd123"
Then my password is shown as a series of dots
And when I click submit
Then my field entries are validated
And I am redirected to a page with terms and conditions
When I click on submit I am directed to an acknolwedgement page.

Scenario: As a slc operator I want to check if a user accepted EULA
Given there is an approved account with login name 'lalsop@acme.com'
When I query the database for EULA acceptance
Then  I get 1 record with "First Name" as "Lance"
And "Last Name" as "Alsop""
And "Vendor" as  "Acme Corp"
And "Email" as "lalsop@acme.com"

Scenario: As a slc operator I want to unique user accounts in the system
Given there is an approved account with login name 'lalsop@acme.com'
When user fills out all the required fields as "First Name" as "Lance"
And "Last Name" as "Alsop""
And "Vendor" as  "Acme Corp"
And "Email" as "lalsop@acme.com"
And "Password" as "password1234"
And user clicks submit
Then user recieves an error that the account information exists
