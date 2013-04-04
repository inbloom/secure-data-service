@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Cohort with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
	|field                                                           |value                                                                                          |
	|_id                                                             |3ec8e3eb5388b559890be7df3cf189902fc2735d_id          |
	Then there exist "30" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "staffCohortAssociation"
	|field                                                           |value                                                                                          |
	|body.cohortId                                                   |3ec8e3eb5388b559890be7df3cf189902fc2735d_id          | 
	Then there exist "4" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
    |field                                                           |value                                                                                          |
    |cohort._id                                                      |3ec8e3eb5388b559890be7df3cf189902fc2735d_id          |
	And I save the collection counts in "Midgar" tenant	
    And I post "BroadCohortDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadCohortDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "cohort" to get "0" records
	And I re-execute saved query "staffCohortAssociation" to get "0" records
	And I re-execute saved query "studentCohortAssociation" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|cohort                                 |        -1|
	|staffCohortAssociation                 |       -30|
	|studentCohortAssociation               |        -4|
	|recordHash                             |         0|
	And I should not see "3ec8e3eb5388b559890be7df3cf189902fc2735d_id" in the "Midgar" database
	And I should not see any entity mandatorily referring to "3ec8e3eb5388b559890be7df3cf189902fc2735d_id" in the "Midgar" database
	And I should see entities optionally referring to "3ec8e3eb5388b559890be7df3cf189902fc2735d_id" be updated in the "Midgar" database