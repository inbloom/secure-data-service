@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Safe Delete LearningStandard with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |ac596eefed6f9bc62165e9c1dd21ad21071fd389_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                             |value                                                |
	|assessmentItem.body.learningStandards             |ac596eefed6f9bc62165e9c1dd21ad21071fd389_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "SafeLearningStandardDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeLearningStandardDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrade.xml"
   	And I should not see a warning log file created
 	And I re-execute saved query "learningStandard" to get "1" records
	And I see that collections counts have changed as follows in tenant "Midgar"
 	|collection                        |delta          |
	|learningStandard                       |         0|

	
Scenario: Safe Delete LearningStandard by Reference with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |ac596eefed6f9bc62165e9c1dd21ad21071fd389_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                             |value                                                |
	|assessmentItem.body.learningStandards             |ac596eefed6f9bc62165e9c1dd21ad21071fd389_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "SafeLearningStandardRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeLearningStandardRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrade.xml"
   	And I should not see a warning log file created
 	And I re-execute saved query "learningStandard" to get "1" records
	And I see that collections counts have changed as follows in tenant "Midgar"
 	|collection                        |delta          |
	|learningStandard                       |         0|

	
Scenario: Delete Orphan LearningStandard with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |c3eb32843ae9613b2ac8d6f9b8b21a4a2b91ac4f_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanLearningStandardDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanLearningStandardDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
  	And I re-execute saved query "learningStandard" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
 	|collection                        |delta          |
	|learningStandard                       |        -1|
	|recordHash                             |        -1|


Scenario: Delete Orphan LearningStandard Ref with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |c3eb32843ae9613b2ac8d6f9b8b21a4a2b91ac4f_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanLearningStandardRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanLearningStandardRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
  	And I re-execute saved query "learningStandard" to get "0" records
 	And I see that collections counts have changed as follows in tenant "Midgar"
 	|collection                        |delta          |
	|learningStandard                       |        -1|
	|recordHash                             |        -1|


Scenario: Delete LearningStandard with default settings (Confirm that by default cascade = false, force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |ac596eefed6f9bc62165e9c1dd21ad21071fd389_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                             |value                                                |
	|assessmentItem.body.learningStandards             |ac596eefed6f9bc62165e9c1dd21ad21071fd389_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "ForceLearningStandardDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceLearningStandardDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrade.xml"
  	And I re-execute saved query "learningStandard" to get "0" records
 	And I see that collections counts have changed as follows in tenant "Midgar"
 	|collection                        |delta          |
	|learningStandard                       |        -1|
	|recordHash                             |        -1|
	

Scenario: Delete LearningStandard Reference with default settings (Confirm that by default cascade = false, force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "learningStandard" records like below in "Midgar" tenant. And I save this query as "learningStandard"
	|field                                             |value                                                |
	|_id                                               |ac596eefed6f9bc62165e9c1dd21ad21071fd389_id          |
	Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
	|field                                             |value                                                |
	|assessmentItem.body.learningStandards             |ac596eefed6f9bc62165e9c1dd21ad21071fd389_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "ForceLearningStandardRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceLearningStandardRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrade.xml"
  	And I re-execute saved query "learningStandard" to get "0" records
 	And I see that collections counts have changed as follows in tenant "Midgar"
 	|collection                        |delta          |
	|learningStandard                       |        -1|
	|recordHash                             |        -1|
