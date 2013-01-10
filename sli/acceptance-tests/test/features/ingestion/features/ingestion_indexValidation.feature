@RALLY_US3478
Feature: Ingestion Index Validation Test

Scenario: Verify if Indexes are present

Given the log directory contains "ingestion.log" file
Then I should see either "Index verified" or "Index missing" following IngestionRouteBuilder in "ingestion.log" file


