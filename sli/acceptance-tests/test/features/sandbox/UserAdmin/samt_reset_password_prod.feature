@wip
@RALLY_US3458
Feature: When a admin sets a password for new account, TOU is not shown

Scenario Outline: User  sets thier passw.ord 
Given I have an account of <account_type>
When I access the password reset page
Then I <visible_stauts> a checkbox with term-of-use
And I have to enter my password

When I submit the form
Then the password is saved

When I cancel the form
Then the password is not saved
Examples:
|account_type         |visible_status|
|SEA Admin            |not shown     |
|LEA Admin            |not shown     |
|Realm Admin          |not shown     |
|Ingestion User       |not shown     |