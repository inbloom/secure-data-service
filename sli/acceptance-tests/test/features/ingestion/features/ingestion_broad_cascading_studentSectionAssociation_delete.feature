@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Student Section Association with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child entities of entityType "studentSectionAssociation" with id "2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id" in the "Midgar" database
    And I post "BroadStudentSectionAssociationDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStudentSectionAssociationDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id" in the "Midgar" database
	And I should see entities optionally referring to "2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id" be updated in the "Midgar" database	
	