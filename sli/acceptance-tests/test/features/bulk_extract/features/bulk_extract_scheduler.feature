@US5503
Feature: cron job scheduler for bulk-extracts

Scenario: Cleanup the existing crontab
   Given the current crontab is empty

@scheduler
Scenario: Configure bulk extract scheduler
   Given the local bulk extract script path and the scheduling config path
   And I clean up the cron extraction zone
   Then I run the bulk extract scheduler script
   When I am willing to wait upto 90 seconds for the bulk extract scheduler cron job to start and complete
   And I clear crontab
