@RALLY_US2485
@sandbox
Feature: Limit the number of accounts in sandbox

Background:

Given I have an open web browser
And there is number_of_developer_accounts property in the configuration file for the sandbox environment

Scenario: As a Developer I get to create an account if there are less than 100 developer accounts
When the number of accounts already created in database is equal to <NUMBER_OF_DEVELOPER_ACCOUNTS>
And I hit User Registration for sandbox
Then I get an error message 
When when I enter my email address
And click "Submit"
Then my email address is stored in the database
And I get a success message
