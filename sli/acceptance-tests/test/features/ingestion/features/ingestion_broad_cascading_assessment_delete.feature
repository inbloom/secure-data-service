@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

#assessment	studentAssessment	assessmentId	1	1	studentAssessment		
#assessment	section	assessmentReferences	0	N	section	missing!


Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Assessment with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                               |value                                                                                          |
	|_id                                 |5f650f8a65dcfee035b74787ca47a6738239c614_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                               |value                                                                                          |
	|assessmentItem.body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                               |value                                                                                          |
	|objectiveAssessment.body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	|field                               |value                                                                                          |
	|body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	And I save the collection counts in "Midgar" tenant
    And I post "SafeAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeAssessmentDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeAssessmentMetadata.xml"
	And I should not see a warning log file created
	And I re-execute saved query "assessment" to get "1" records
	And I re-execute saved query "assessmentItem" to get "1" records
	And I re-execute saved query "objectiveAssessment" to get "1" records
	And I re-execute saved query "studentAssessment" to get "2" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta|
	|assessment                        |    0|

Scenario: Delete Orphan Assessment with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                               |value                                      |
	|_id                                 |d186370c4079064d23fb796fd861358621cb2016_id|
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
   And a batch job for file "OrphanAssessmentDelete.zip" is completed in database
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
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta|
	|assessment                        |   -1|
	|recordHash                        |   -1|

Scenario: Delete Orphan Assessment Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                               |value                                      |
	|_id                                 |d186370c4079064d23fb796fd861358621cb2016_id|
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanAssessmentRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAssessmentRefDelete.zip" is completed in database
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
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta|
	|assessment                        |   -1|
	|recordHash                        |   -1|

Scenario: Delete Assessment with cascade = false, force = true, logViolations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                               |value                                                                                          |
	|_id                                 |5f650f8a65dcfee035b74787ca47a6738239c614_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                               |value                                                                                          |
	|assessmentItem.body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                               |value                                                                                          |
	|objectiveAssessment.body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	|field                               |value                                                                                          |
	|body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	And I save the collection counts in "Midgar" tenant
    And I post "ForceAssessmentDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAssessmentDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
  And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
  And I should not see an error log file created
	And I re-execute saved query "assessment" to get "1" records
	And I re-execute saved query "assessmentItem" to get "1" records
	And I re-execute saved query "objectiveAssessment" to get "1" records
	And I re-execute saved query "studentAssessment" to get "2" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta|
	|assessment                        |   0 |
	|assessmentItem                    |   0 |
	|objectiveAssessment               |   0 |
	|assessment<hollow>                |   1 |
	|recordHash                        |  -1 |


Scenario: Delete Assessment Reference with cascade = false, force = true, logviolations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                               |value                                                                                          |
	|_id                                 |5f650f8a65dcfee035b74787ca47a6738239c614_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessmentItem"
	|field                               |value                                                                                          |
	|assessmentItem.body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "objectiveAssessment"
	|field                               |value                                                                                          |
	|objectiveAssessment.body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	Then there exist "2" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
	|field                               |value                                                                                          |
	|body.assessmentId                   |5f650f8a65dcfee035b74787ca47a6738239c614_id          | 
	And I save the collection counts in "Midgar" tenant
    And I post "ForceAssessmentRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAssessmentRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
  And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
  And I should not see an error log file created
	And I re-execute saved query "assessment" to get "1" records
	And I re-execute saved query "assessmentItem" to get "1" records
	And I re-execute saved query "objectiveAssessment" to get "1" records
	And I re-execute saved query "studentAssessment" to get "2" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta|
	|assessment                        |   0|
	|assessmentItem                    |   0|
	|objectiveAssessment               |   0|
	|assessment<hollow>                |   1|
	|recordHash                        |  -1|
