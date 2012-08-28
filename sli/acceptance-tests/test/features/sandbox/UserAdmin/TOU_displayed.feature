@sandbox
@RALLY_US3456
Feature: When a app developer sets a password for new account, TOU is shown

Background:
Given I have an open web browser

Scenario Outline: User sets their password and accepts TOU
Given I have an account of <account_type> in <status> status
When I access the password reset page
Then I <visible_status> a checkbox with term-of-use
And I have to enter my password
And I <action_status> check the terms-of-use to submit the form

When I submit the form
Then the password is saved
And the terms-of-use is <save_action>

Examples:
|account_type         |status     |visible_status|action_status   |save_action|
|application_developer|TOU not set|am shown      |have to         |saved      |
|ingestion_user       |TOU not set|am shown      |have to         |saved      |
|Sandbox Administrator|TOU not set|am shown      |have to         |saved      | 
|application_developer|TOU set    |am not shown  |do not have to  |not saved  |
|ingestion_user       |TOU set    |am not shown  |do not have to  |not saved  |
|Sandbox Administrator|TOU set    |am not shown  |do not have to  |not saved  |
