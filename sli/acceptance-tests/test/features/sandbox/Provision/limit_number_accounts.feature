@RALLY_US2485
@wip
Feature: Limit the number of accounts in sandbox

Background:
Given there is number_of_developer_accounts property in the configuration file for the sandbox environment

Scenario: As a Developer I get to create an account if there are less than 100 developer accounts
When I click on  <USER_REGISTRATION_PAGE> for sandbox
And the number of accounts already created in database is equal to <NUMBER_OF_DEVELOPER_ACCOUNTS>
Then I get an error message 
When when I enter my email address
And click "Submit"
Then my email address is stored in the database
And I am redirected to the SLC webpage
