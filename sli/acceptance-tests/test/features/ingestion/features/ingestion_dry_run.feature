@RALLY_US361
Feature: Dry Run Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone


Scenario: Post a zip file with no @dry-run in the control file as payload of the ingestion job: Clean Database
Given I post "NoDryRun.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
     | collectionName                          | count     |
     | student                                 | 72        |
  And I should see "Processed 72 records." in the resulting batch job file

Scenario: Post a zip file with the ctl file containing @dry-run as payload of the ingestion job: Clean Database
Given I post "DryRun.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
  Then I should see following map of entry counts in the corresponding collections:
     | collectionName                          | count     |
     | student                                 | 0         |
  And I should see "Processed 0 records." in the resulting batch job file