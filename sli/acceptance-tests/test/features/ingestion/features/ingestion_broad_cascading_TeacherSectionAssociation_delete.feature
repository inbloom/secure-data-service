@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Assessment with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child entities of entityType "teacherSectionAssociation" with id "e003fc1479112d3e953a0220a2d0ddd31077d6d9_id26556e7c6a6f7ef10fa46850c9c68d5cfc0c2d4d_id" in the "Midgar" database
    And I post "BroadTeacherSectionAssociationDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadTeacherSectionAssociationDelete.zip" is completed in database
    And a batch job log has been created
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "e003fc1479112d3e953a0220a2d0ddd31077d6d9_id26556e7c6a6f7ef10fa46850c9c68d5cfc0c2d4d_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "e003fc1479112d3e953a0220a2d0ddd31077d6d9_id26556e7c6a6f7ef10fa46850c9c68d5cfc0c2d4d_id" in the "Midgar" database
	And I should see entities optionally referring to "e003fc1479112d3e953a0220a2d0ddd31077d6d9_id26556e7c6a6f7ef10fa46850c9c68d5cfc0c2d4d_id" be updated in the "Midgar" database	
	