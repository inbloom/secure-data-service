@RALLY_US3458
Feature: When a admin sets a password for new account, TOU is not shown

Background:
Given I have an open web browser
And LDAP server has been setup and running

@production
Scenario Outline: User sets their password 
Given I have an account of "<account_type>"
When I access the production password reset page
Then I "<visible_stauts>" a checkbox with term-of-use
And I will have to enter my password

When I submit the reset password form
Then the new password is saved

#When I cancel the form
#Then the password is not saved
Examples:
|account_type         |visible_status|
|SEA Administrator    |not shown     |
|LEA Administrator    |not shown     |
|Realm Administrator  |not shown     |
|Ingestion User       |not shown     |