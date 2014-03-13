@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete AssessmentFamily with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
   Then there exist "1" "assessmentFamily" records like below in "Midgar" tenant. And I save this query as "assessmentFamily"
        |field                                     |value                                                                                 |
        |_id                                       |1b37e1eb4516a453fece7f01b5af7a3fb86741a8_id                                           |
   Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
        |field                                     |value                                                                                 |
        |body.assessmentFamilyReference            |1b37e1eb4516a453fece7f01b5af7a3fb86741a8_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeAssessmentFamilyDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeAssessmentFamilyDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeAssessmentMetadata.xml"
	And I should not see a warning log file created
	And I re-execute saved query "assessmentFamily" to get "1" records
    And I re-execute saved query "assessment" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessment                        |              0|
	|assessmentFamily                  |              0|

Scenario: Delete Orphan AssessmentFamily with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
   Then there exist "1" "assessmentFamily" records like below in "Midgar" tenant. And I save this query as "assessmentFamily"
        |field                                     |value                                                                                 |
        |_id                                       |aa5a9e2972e7217413dc593ba02b2761779f7d3d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanAssessmentFamilyDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAssessmentFamilyDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "assessmentFamily" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentFamily                  |             -1|
	|recordHash                        |             -1|

Scenario: Delete Orphan AssessmentFamily Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
   Then there exist "1" "assessmentFamily" records like below in "Midgar" tenant. And I save this query as "assessmentFamily"
        |field                                     |value                                                                                 |
        |_id                                       |aa5a9e2972e7217413dc593ba02b2761779f7d3d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanAssessmentFamilyRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAssessmentFamilyRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "assessmentFamily" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentFamily                  |             -1|
	|recordHash                        |             -1|


Scenario: Delete AssessmentFamily with cascade = false, force = true, logviolations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
   Then there exist "1" "assessmentFamily" records like below in "Midgar" tenant. And I save this query as "assessmentFamily"
        |field                                     |value                                                                                 |
        |_id                                       |1b37e1eb4516a453fece7f01b5af7a3fb86741a8_id                                           |
   Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
        |field                                     |value                                                                                 |
        |body.assessmentFamilyReference            |1b37e1eb4516a453fece7f01b5af7a3fb86741a8_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceAssessmentFamilyDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAssessmentFamilyDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
	And I should not see an error log file created
	And I re-execute saved query "assessmentFamily" to get "0" records
    And I re-execute saved query "assessment" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentFamily                  |             -1|
	|recordHash                        |             -1|

	
Scenario: Delete AssessmentFamily Reference with cascade = false, force = true, logviolations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
   Then there exist "1" "assessmentFamily" records like below in "Midgar" tenant. And I save this query as "assessmentFamily"
        |field                                     |value                                                                                 |
        |_id                                       |1b37e1eb4516a453fece7f01b5af7a3fb86741a8_id                                           |
   Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "assessment"
        |field                                     |value                                                                                 |
        |body.assessmentFamilyReference            |1b37e1eb4516a453fece7f01b5af7a3fb86741a8_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceAssessmentFamilyRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAssessmentFamilyRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
	And I should not see an error log file created
	And I re-execute saved query "assessmentFamily" to get "0" records
    And I re-execute saved query "assessment" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|assessmentFamily                  |             -1|
	|recordHash                        |             -1|

