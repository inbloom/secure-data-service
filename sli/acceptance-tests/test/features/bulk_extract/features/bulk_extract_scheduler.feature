@US5503
Feature: cron job scheduler for bulk-extracts

Scenario: Cleanup the existing crontab
   Given the current crontab is empty

Scenario: Configure bulk extract scheduler
   Given the local bulk extract script path and the scheduling config path
   Then I run the bulk extract scheduler script
   Then I clear crontab
