@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Entity Report with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I post "BroadDeleteReport.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadDeleteReport.zip" is completed in database
    And I should see "InterchangeStudentParent.xml records deleted successfully: 2" in the resulting batch job file
	And I should see "InterchangeStaffAssociation.xml records deleted successfully: 1" in the resulting batch job file
	And I should see "InterchangeStudentProgram.xml records deleted successfully: 1" in the resulting batch job file
	And I should see "Processed 4 records." in the resulting batch job file
	And I should not see an error log file created
    And I should not see a warning log file created
	
