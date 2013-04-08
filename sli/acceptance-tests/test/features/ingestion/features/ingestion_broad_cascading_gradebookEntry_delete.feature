@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Gradebook Entry with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child entities of entityType "gradebookEntry" with id "e003fc1479112d3e953a0220a2d0ddd31077d6d9_idbb469f1ebc859c9a90383807b7a5aa027524e5f0_id" in the "Midgar" database	
    And I post "BroadGradebookEntryDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadGradebookEntryDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "e003fc1479112d3e953a0220a2d0ddd31077d6d9_idbb469f1ebc859c9a90383807b7a5aa027524e5f0_id" in the "Midgar" database
	And I should not see any entity mandatorily referring to "e003fc1479112d3e953a0220a2d0ddd31077d6d9_idbb469f1ebc859c9a90383807b7a5aa027524e5f0_id" in the "Midgar" database
	And I should see entities optionally referring to "e003fc1479112d3e953a0220a2d0ddd31077d6d9_idbb469f1ebc859c9a90383807b7a5aa027524e5f0_id" be updated in the "Midgar" database
	