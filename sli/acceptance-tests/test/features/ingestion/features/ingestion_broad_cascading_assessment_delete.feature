@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion
#assessment	studentAssessment	assessmentId	1	1	studentAssessment		
#assessment	section	assessmentReferences	0	N	section		

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Assessment with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                               |value                                                                                          |
	|_id                                 |5f650f8a65dcfee035b74787ca47a6738239c614_id          |
	Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	|field                               |value                                                                                          |
	|body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "BroadAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadAssessmentDelete.zip" is completed in database
    And a batch job log has been created
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "assessment" to get "0" records
	And I re-execute saved query "assessment" to get "0" records
	And I re-execute saved query "studentAssessment" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessment                             |        -1|
	|studentAssessment                      |        -2|
	|studentAssessmentItem                  |        -2|
	|assessmentItem                         |        -1|
	|objectiveAssessment                    |        -4|
	|studentObjectiveAssessment             |        -2|
	|recordHash                             |         0|
	And I should not see "5f650f8a65dcfee035b74787ca47a6738239c614_id" in the "Midgar" database
	