@RALLY_US4006 @clearIndexer
Feature:  Search Indexer: Scheduler, Extract, Index

Background:
Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
Given I DELETE to clear the Indexer

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

