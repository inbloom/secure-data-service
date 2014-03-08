@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Safe Delete AssessmentPeriodDescriptor with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "assessmentPeriodDescriptor" records like below in "Midgar" tenant. And I save this query as "APD1"
    |field                                                  |value                                                |
    |_id                                                    |58cf2613a101803aba61279f7090f7dd990eebc1_id          | 
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "APD2"
    |field                                                  |value                                                |
    |body.assessmentPeriodDescriptorId                      |58cf2613a101803aba61279f7090f7dd990eebc1_id          |   
    And I save the collection counts in "Midgar" tenant 
    And I post "SafeAssessmentPeriodDescriptorDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeAssessmentPeriodDescriptorDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeAssessmentMetadata.xml"
   	And I should not see a warning log file created
	And I re-execute saved query "APD1" to get "1" records
	And I re-execute saved query "APD2" to get "1" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        |collection                     |delta|
        |assessmentPeriodDescriptor      |   0|

Scenario: Safe Delete AssessmentPeriodDescriptor Reference with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "assessmentPeriodDescriptor" records like below in "Midgar" tenant. And I save this query as "APD1"
    |field                                                  |value                                                |
    |_id                                                    |58cf2613a101803aba61279f7090f7dd990eebc1_id          |
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "APD2"
    |field                                                  |value                                                |
    |body.assessmentPeriodDescriptorId                      |58cf2613a101803aba61279f7090f7dd990eebc1_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeAssessmentPeriodDescriptorRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeAssessmentPeriodDescriptorRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeAssessmentMetadata.xml"
   	And I should not see a warning log file created
	And I re-execute saved query "APD1" to get "1" records
	And I re-execute saved query "APD2" to get "1" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        |collection |delta|
        |assessmentPeriodDescriptor      |   0|

Scenario: Delete Orphan AssessmentPeriodDescriptor with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "assessmentPeriodDescriptor" records like below in "Midgar" tenant. And I save this query as "APD1"
    |field                                                  |value                                                |
    |_id                                                    |03aaf1d17a93228d5798f9d4680a7ce0b9aaea2d_id          | 
    And I save the collection counts in "Midgar" tenant 
    And I post "OrphanAssessmentPeriodDescriptorDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAssessmentPeriodDescriptorDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
	And I re-execute saved query "APD1" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        |collection                      |delta|
        |assessmentPeriodDescriptor      |   -1|
        |recordHash                      |   -1|

Scenario: Delete Orphan AssessmentPeriodDescriptor Ref with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "assessmentPeriodDescriptor" records like below in "Midgar" tenant. And I save this query as "APD1"
    |field                                                  |value                                                |
    |_id                                                    |03aaf1d17a93228d5798f9d4680a7ce0b9aaea2d_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanAssessmentPeriodDescriptorRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanAssessmentPeriodDescriptorRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
	And I re-execute saved query "APD1" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        |collection                      |delta|
        |assessmentPeriodDescriptor      |   -1|
        |recordHash                      |   -1|

Scenario: Delete AssessmentPeriodDescriptor with cascade = false, force = true and log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "assessmentPeriodDescriptor" records like below in "Midgar" tenant. And I save this query as "APD1"
    |field                                                  |value                                                |
    |_id                                                    |58cf2613a101803aba61279f7090f7dd990eebc1_id          | 
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "APD2"
    |field                                                  |value                                                |
    |body.assessmentPeriodDescriptorId                      |58cf2613a101803aba61279f7090f7dd990eebc1_id          |   
    And I save the collection counts in "Midgar" tenant 
    And I post "ForceAssessmentPeriodDescriptorDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAssessmentPeriodDescriptorDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
	And I re-execute saved query "APD1" to get "0" records
	And I re-execute saved query "APD2" to get "1" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        |collection |delta|
        |assessmentPeriodDescriptor      |   -1|
        |recordHash                      |   -1|

Scenario: Delete AssessmentPeriodDescriptor Ref with cascade = false, force = true and log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "assessmentPeriodDescriptor" records like below in "Midgar" tenant. And I save this query as "APD1"
    |field                                                  |value                                                |
    |_id                                                    |58cf2613a101803aba61279f7090f7dd990eebc1_id          |
    Then there exist "1" "assessment" records like below in "Midgar" tenant. And I save this query as "APD2"
    |field                                                  |value                                                |
    |body.assessmentPeriodDescriptorId                      |58cf2613a101803aba61279f7090f7dd990eebc1_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceAssessmentPeriodDescriptorRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceAssessmentPeriodDescriptorRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeAssessmentMetadata.xml"
	And I re-execute saved query "APD1" to get "0" records
	And I re-execute saved query "APD2" to get "1" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        |collection |delta|
        |assessmentPeriodDescriptor      |   -1|
        |recordHash                      |   -1|
	
