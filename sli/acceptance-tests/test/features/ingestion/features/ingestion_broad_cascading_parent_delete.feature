@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Parent with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "parent" records like below in "Midgar" tenant. And I save this query as "parent"
        |field                                     |value                                                                                 |
        |_id                                       |635634cebafb9b700a40d75430f28d00db896553_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation.body.parentId    |635634cebafb9b700a40d75430f28d00db896553_id                                           |                          
    And I save the collection counts in "Midgar" tenant
    And I post "SafeParentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeParentDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file
   	And I should not see a warning log file created
    And I re-execute saved query "parent" to get "1" records 
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |         0|

Scenario: Delete Parent with cascade = false with ref
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "parent" records like below in "Midgar" tenant. And I save this query as "parent"
        |field                                     |value                                                                                 |
        |_id                                       |635634cebafb9b700a40d75430f28d00db896553_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation.body.parentId    |635634cebafb9b700a40d75430f28d00db896553_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeParentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeParentRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file
   	And I should not see a warning log file created
    And I re-execute saved query "parent" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |         0|
      
Scenario: Delete Orphan Parent with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "parent" records like below in "Midgar" tenant. And I save this query as "parent"
        |field                                     |value                                                                                 |
        |_id                                       |1a6c5e20162dc782e85e7752ad9d4f7f9cf0fe8c_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanParentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanParentDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
 	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "parent" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | parent                                    |        -1|       
        | recordHash                                |        -1|


Scenario: Delete Orphan Parent Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "parent" records like below in "Midgar" tenant. And I save this query as "parent"
        |field                                     |value                                                                                 |
        |_id                                       |1a6c5e20162dc782e85e7752ad9d4f7f9cf0fe8c_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanParentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanParentRefDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "parent" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | parent                                    |        -1|       
        | recordHash                                |        -1|
      
 Scenario: Delete Parent with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "parent" records like below in "Midgar" tenant. And I save this query as "parent"
        |field                                     |value                                                                                 |
        |_id                                       |635634cebafb9b700a40d75430f28d00db896553_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation.body.parentId    |635634cebafb9b700a40d75430f28d00db896553_id                                           |                          
    And I save the collection counts in "Midgar" tenant
    And I post "ForceParentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceParentDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentParent.xml"
   	And I should not see an error log file created
    And I re-execute saved query "parent" to get "0" records 
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | parent                                    |        -1|       
        | recordHash                                |        -1|
        
   Scenario: Delete Parent with Ref cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "parent" records like below in "Midgar" tenant. And I save this query as "parent"
        |field                                     |value                                                                                 |
        |_id                                       |635634cebafb9b700a40d75430f28d00db896553_id                                           |
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation.body.parentId    |635634cebafb9b700a40d75430f28d00db896553_id                                           |                          
    And I save the collection counts in "Midgar" tenant
    And I post "ForceParentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceParentRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentParent.xml"
   	And I should not see an error log file created
    And I re-execute saved query "parent" to get "0" records 
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | parent                                    |        -1|       
        | recordHash                                |        -1|   

	
