@RALLY_US4006
Feature:  Send bulk extract command to Elastic Search

@smoke @integration @rc
Scenario:  Send bulk extract command
Given I import into tenant collection
Given I send a command to start the extractor to extract now
And I check that Elastic Search is non-empty
And I flush the Indexer
#And I clear the tenants that I previously imported

@smoke @integration @rc @sandbox
Scenario:  Send bulk extract command for sandbox
Given I import into tenant collection
Given I send a command to start the extractor to extract now
And I check that Elastic Search is non-empty
And I flush the Indexer
And I clear the tenants that I previously imported
