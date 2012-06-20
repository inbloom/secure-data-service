# This no longer works because the User Account resource is now stored in the LDAP and not the API.
# Instead, unit tests for this is created in the rails admin app.

#@RALLY_US2485
#@sandbox
#Feature: Limit the number of accounts in sandbox
#
#Background:
#
#Given I have an open web browser
#And there is number_of_developer_accounts property in the configuration file for the sandbox environment
#
#Scenario: As a Developer I get to create an account if there are less than 100 developer accounts
#When the number of accounts already created in LDAP is equal to <NUMBER_OF_DEVELOPER_ACCOUNTS>
#And I hit User Registration for sandbox
#Then I get an error message
#
#Scenario: As a Developer I want to register for the waiting list for new developer accounts in the sandbox environment
#When the number of accounts already created in database is equal to <NUMBER_OF_DEVELOPER_ACCOUNTS>
#And I hit User Registration for sandbox
#Then I get an error message
#And I am presented with the waiting list screen
#When when I enter my email address "<USER_ACCOUNT_EMAIL>"
#And click "Submit"
#Then there is an LDAP account with my login name "<USER_ACCOUNT_EMAIL>"
#And I get a success message
