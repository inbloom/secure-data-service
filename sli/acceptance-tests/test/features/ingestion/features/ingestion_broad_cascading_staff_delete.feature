@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Assessment with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child entities of entityType "staff" with id "63d4be8a233db1fd14676f1535fa21fe4c5dd466_id" in the "Midgar" database
    And I post "BroadStaffDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStaffDelete.zip" is completed in database
    And a batch job log has been created
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "63d4be8a233db1fd14676f1535fa21fe4c5dd466_id" in the "Midgar" database
	And I should not see any entity mandatorily referring to "63d4be8a233db1fd14676f1535fa21fe4c5dd466_id" in the "Midgar" database
	And I should see entities optionally referring to "63d4be8a233db1fd14676f1535fa21fe4c5dd466_id" be updated in the "Midgar" database