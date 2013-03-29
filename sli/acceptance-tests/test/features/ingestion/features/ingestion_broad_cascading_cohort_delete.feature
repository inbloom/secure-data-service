@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Cohort with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I should see child entities of entityType "cohort" with id "3ec8e3eb5388b559890be7df3cf189902fc2735d_id" in the "Midgar" database	
	
    And I post "BroadCohortDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadCohortDelete.zip" is completed in database
    And a batch job log has been created
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "3ec8e3eb5388b559890be7df3cf189902fc2735d_id" in the "Midgar" database
	And I should not see any entity mandatorily referring to "3ec8e3eb5388b559890be7df3cf189902fc2735d_id" in the "Midgar" database
	And I should see entities optionally referring to "3ec8e3eb5388b559890be7df3cf189902fc2735d_id" be updated in the "Midgar" database