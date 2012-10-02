@RALLY_US4006
Feature:  Search Indexer: Scheduler, Extract, Index

Background:
Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
Given I DELETE to clear the Indexer

#Indexing
Scenario:  Malformed TextFile in Directory
Given I drop Invalid Files to Inbox Directory
And I should see the file has not been processed
And Indexer should have "0" entities

Scenario:  Indexing Multiple Files
Given I drop Valid Files to Inbox Directory
Then I should see the file has been prcoessed
And Indexer should have "2" entities
And I drop another Valid File to the Inbox Directory
Then I should see the file has been prcoessed
And Indexer should have "4" entities

#extracting
@wip
Scenario:  Data Extraction
Given I import some student data
And I configure the file size cutoff to be "1Mb"
And I configure the job to extract entity "student" with the following fields "name,grade"
And I see an output file placed into the directory
And it should have extracted "10" entities
And each entity has its list of extracted fields

@wip
Scenario:  Cron Scheduling
Given I import some student data
And I schedule the job to run every "1" minute
And I configure the job to extract entity "student" with the following fields "name,grade"
And I see an output file placed into the directory
And it should have extracted "10" entities
And each entity has its list of extracted fields
