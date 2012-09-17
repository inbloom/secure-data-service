@RALLY_US3478
Feature: Ingestion Index Validation Test

@wip
Scenario: Verify if Indexes are present

Given the log directory contains "ingestion.log" file
Then I should see either "Indexes verified" or "The following indexes are missing" following IndexValidator in "ingestion.log" file


