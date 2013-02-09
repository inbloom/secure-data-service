@RALLY_US3478
Feature: Ingestion Index Validation Test

Scenario: Verify if Indexes are present

Given the log directory contains "ingestion.log" file
Then I should see either "CORE_0018" or "CORE_0038" following IndexValidatorExecutor in "ingestion.log" file


