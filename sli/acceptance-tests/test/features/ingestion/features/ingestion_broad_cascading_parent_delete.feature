@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Parent with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	And I should see child entities of entityType "parent" with id "1b4aa93f01d11ad51072f3992583861ed080f15c_id" in the "Midgar" database	
    And I post "BroadParentDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadParentDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "1b4aa93f01d11ad51072f3992583861ed080f15c_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "1b4aa93f01d11ad51072f3992583861ed080f15c_id" in the "Midgar" database
	And I should see entities optionally referring to "1b4aa93f01d11ad51072f3992583861ed080f15c_id" be updated in the "Midgar" database
	
	
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
    And a batch job log has been created
    And I should see "Processed 1 records." in the resulting batch job file
	  And I should see "records deleted successfully: 0" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
#    And I should not see an error log file created
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
#        | recordHash                                |        -1|   

@wip
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
#        | recordHash                                |        -1|   

	