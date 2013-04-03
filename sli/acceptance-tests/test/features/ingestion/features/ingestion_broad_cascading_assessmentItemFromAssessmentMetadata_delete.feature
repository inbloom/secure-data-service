@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Assessment Item from Assessment Metadata with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child entities of entityType "assessmentItem" with id "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" in the "Midgar" database	
    And I post "BroadAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" in the "Midgar" database
	And I should see entities optionally referring to "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" be updated in the "Midgar" database
	