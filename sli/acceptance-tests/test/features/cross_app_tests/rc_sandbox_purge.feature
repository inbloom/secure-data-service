@RALLY_US4835
@rc
@sandbox
Feature: RC Integration Cleanup - Purge Tenant

  Background: Make a connection to Mongo
    Given I have a connection to Mongo
    And I am running in Sandbox mode

    Scenario: SEA purge tenant data
      When I drop a control file to purge tenant data as "<DEVELOPER_SB_EMAIL>" with password "<DEVELOPER_SB_EMAIL_PASS>" to "<SERVER>"
      Then my tenant database should be cleared
      And the landing zone should contain a file with the message "All records processed successfully."
      And the landing zone should contain a file with the message "Processed 0 records."
      And the landing zone should contain a file with the message "Purge process completed successfully."
      And I should not see an error log file created
      And I should not see a warning log file created
