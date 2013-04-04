@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Student Program Association with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child of entityType "studentProgramAssociation" with id "50dc8b12ef9184d88d8c304e635cf5a80d38bf79_idef393993fb4814a0d0b75ec2598cff3481df43f7_id" in the "Midgar" database
	Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
	|field                                                           |value                                                                                          |
	|studentProgramAssociation._id                                   |50dc8b12ef9184d88d8c304e635cf5a80d38bf79_idef393993fb4814a0d0b75ec2598cff3481df43f7_id         |
	And I save the collection counts in "Midgar" tenant
    And I post "BroadStudentProgramAssociationDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStudentProgramAssociationDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "studentProgramAssociation" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|program                                |         0|
	|studentProgramAssociation              |        -1|
	|recordHash                             |         0|
	And I should not see "50dc8b12ef9184d88d8c304e635cf5a80d38bf79_idef393993fb4814a0d0b75ec2598cff3481df43f7_id" in the "Midgar" database

	