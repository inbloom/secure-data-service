@RALLY_US3450
Feature: Ingestion Error/Warning Count Limitation Test

Background: I have a landing zone route configured
  Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone
 
@wip
Scenario: The number of Errors should be no more than the numbers Specifed in sli.properties
  Given I post "Error_Count_Limitation.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
   | collectionName              |
   | student                     |
   
   | parent                      |
   | cohort                      |
   | studentcohortAssociation    |
   | studentParentAssociation    |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job log has been created
  And I should see the number of errors in error log is no more than the error count limitation 10000
 
@wip
 Scenario: The number of Warnings should be no more than the numbers Specifed in sli.properties
  Given I post "Warn_Count_Limitation.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
   | collectionName              |
   | student                     |
   | parent                      |
   | studentParentAssociation    |
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job log has been created
  And I should see the number of warnings in warn log is no more than the warning count limitation 10000