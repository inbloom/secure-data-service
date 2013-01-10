@rc
Feature: User requests for a developer account in production but is redirected to sandbox environment
# currently works for production only. Not tested in sandbox.

Background:
  Given I have an open web browser

Scenario: As an app developer I request a production account but it's blocked
  And I go to the account registration page
  Then I am redirected to the developer get-started page
