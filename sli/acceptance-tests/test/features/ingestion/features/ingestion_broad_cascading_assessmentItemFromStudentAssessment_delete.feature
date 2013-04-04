@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Assessment Item from Student Assessment with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child of entityType "assessmentItem" with id "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" in the "Midgar" database	
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                                                           |value                                                                                          |
	|assessmentItem._id                                              |58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessmentRefs"
	|field                                                           |value                                                                                          |
	|objectiveAssessment.body.assessmentItemRefs                     |58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id          |
	Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	|field                                                           |value                                                                                          |
	|studentAssessmentItem.body.assessmentItemId                     |58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "BroadAssessmentItemFromStudentAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadAssessmentItemFromStudentAssessmentDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|studentAssessment                      |         0|
	|studentAssessmentItem                  |        -2|
	|assessmentItem                         |        -1|
	|recordHash                             |         0|
	And I should not see "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" in the "Midgar" database
	And I should see entities optionally referring to "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" be updated in the "Midgar" database
