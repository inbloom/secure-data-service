@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Program with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "localEducationAgency"
	|field                                                           |value                                                |
	|body.programReference                                                |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"
	|field                                                           |value                                                |
	|_id                                                             |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "104" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "staffProgramAssociation"
	|field                                                           |value                                                |
	|body.programId                                                  |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
	|field                                                           |value                                                |
	|studentProgramAssociation.body.programId                        |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	Then there exist "9" "student" records like below in "Midgar" tenant. And I save this query as "student"
	|field                                                           |value                                                |
	|program._id                                                     |0064dd5bb3bffd47e93b023585e6591c018ee697_id          |
	And I save the collection counts in "Midgar" tenant   
    And I post "BroadProgramDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadProgramDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "program" to get "0" records
	And I re-execute saved query "localEducationAgency" to get "0" records
	And I re-execute saved query "studentProgramAssociation" to get "0" records
	And I re-execute saved query "staffProgramAssociation" to get "0" records
	And I re-execute saved query "student" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|student                                |         0|
	|program                                |        -1|
	|staffProgramAssociation                |      -104|
	|recordHash                             |         0|
	|studentProgramAssociation              |       -14|
	And I should not see "0064dd5bb3bffd47e93b023585e6591c018ee697_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "0064dd5bb3bffd47e93b023585e6591c018ee697_id" in the "Midgar" database
	And I should see entities optionally referring to "0064dd5bb3bffd47e93b023585e6591c018ee697_id" be updated in the "Midgar" database
	
	#StateEducationAgency program programReference relationship is missing 
	#school program programReference relationship is missing 
	#section program programReference relationship is missing
	#cohort program body.programId relationship is missing