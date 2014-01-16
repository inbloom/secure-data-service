@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Assessment Item from Assessment Metadata with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
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
    And I post "BroadAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "assessmentItem" to get "0" records
	And I re-execute saved query "objectiveAssessmentRefs" to get "0" records
	And I re-execute saved query "studentAssessment" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|studentAssessment                      |         0|
	|studentAssessmentItem                  |        -2|
	|assessmentItem                         |        -1|
	|recordHash                             |         0|
	And I should not see "58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id" in the "Midgar" database
	
	#objectiveAssessment assessmentItem missing

Scenario: Safe Delete Assessment Item from Assessment Metadata with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
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
    And I post "SafeAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeAssessmentMetadata.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "assessmentItem" to get "1" records
    And I re-execute saved query "objectiveAssessmentRefs" to get "1" records
    And I re-execute saved query "studentAssessment" to get "2" records   
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentItem                         |         0|

Scenario: Safe Delete Assessment Item by Reference from Assessment Metadata with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
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
    And I post "SafeAssessmentItemDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeAssessmentItemDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeAssessmentMetadata.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "assessmentItem" to get "1" records
    And I re-execute saved query "objectiveAssessmentRefs" to get "1" records
    And I re-execute saved query "studentAssessment" to get "2" records   
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentItem                         |         0|


Scenario: Delete Orphan Assessment Item from Assessment Metadata with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                                                           |value                                                                                          |
	|assessmentItem._id                                              |773eb0d94a36b66faaeea1d680899b481a385397_id81a3071ab2c71a7a55397a995e678ac4f1c4be85_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "assessmentItem" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentItem                         |        -1|
	|assessment                             |        -1|  
	|assessment<hollow>                     |        -1|
#the body assessment is empty after deletion of assessmentItem, so it is deleted too
    |recordHash                             |       -1|
	

Scenario: Delete Orphan Assessment Item Reference from Assessment Metadata with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                                                           |value                                                                                          |
	|assessmentItem._id                                              |773eb0d94a36b66faaeea1d680899b481a385397_id81a3071ab2c71a7a55397a995e678ac4f1c4be85_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanAssessmentItemRefFromAssessmentMetadataInterchangeDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAssessmentItemRefFromAssessmentMetadataInterchangeDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "assessmentItem" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentItem                         |        -1|
	|assessment                             |        -1|
	|assessment<hollow>                     |        -1|
	|recordHash                             |       -1|
	

Scenario: Delete Assessment Item from Assessment Metadata with default settings (Confirm that by default cascade = false, force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                                                           |value                                                                                          |
	|assessmentItem._id                                              |58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id         |
  Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItemNoResponse"
    |field                                                           |value                                                                                          |
    |assessmentItem._id                                              |96bf74faf70dffa00128dd1eeffde9e2b544632a_id13b2b0ff391548f6b12cbbda6fab64eb6cd00d6c_id         |
  Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessmentRefs"
	|field                                                           |value                                                                                          |
	|objectiveAssessment.body.assessmentItemRefs                     |58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id          |
	Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	|field                                                           |value                                                                                          |
	|studentAssessmentItem.body.assessmentItemId                     |58346902a070426a109f451129eeeb1268daed21_id406e5f1c9ff1339aaf93fc8f3fe21ff6fead0439_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "ForceAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAssessmentItemFromAssessmentMetadataInterchangeDelete.zip" is completed in database
    And I should see "Processed 2 records." in the resulting batch job file
    And I should see "records deleted successfully: 2" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
    And I re-execute saved query "assessmentItem" to get "0" records
    And I re-execute saved query "assessmentItemNoResponse" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentItem                         |        -2|
	|recordHash                             |        -2|


Scenario: Delete Assessment Item Reference from Assessment Metadata with default settings (Confirm that by default cascade = false, force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
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
    And I post "ForceAssessmentItemDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAssessmentItemDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
    And I re-execute saved query "assessmentItem" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentItem                         |        -1|
	|recordHash                             |        -1|
