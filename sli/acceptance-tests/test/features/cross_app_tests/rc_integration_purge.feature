@RALLY_US4835
@rc
Feature: RC Integration Cleanup - Purge Tenant

  Background: Make a connection to Mongo
    Given I have a connection to Mongo

    Scenario: SEA purge tenant data
      When I drop a control file to purge tenant data as "<SEA ADMIN>" with password "<SEA ADMIN PASSWORD>" to "<SERVER>"
      Then my tenant database should be cleared
      And the landing zone should contain a file with the message "All records processed successfully."
      And I should not see an error log file created
      And I should not see a warning log file created
