@RALLY_US4006
Feature:  Send bulk extract command to Elastic Search

Background:
Given I am logged in using "jstevenson" "jstevenson1234" to realm "IL"
And I import into tenant collection

@smoke
Scenario:  Send bulk extract command
Given I send a command to start the extractor to extract now
And I check that Elastic Search is non-empty
And I flush the Indexer