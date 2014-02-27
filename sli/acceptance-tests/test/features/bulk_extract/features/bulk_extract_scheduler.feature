@US5503
Feature: cron job scheduler for bulk-extracts

Scenario: Configure bulk extract scheduler
  Given I configurate the bulk extract scheduler script
  And I clean up the cron extraction zone
  When I run the bulk extract scheduler script
  Then I am willing to wait up to 90 seconds for the bulk extract scheduler cron job to start and complete
  And I clean up the scheduler jobs

