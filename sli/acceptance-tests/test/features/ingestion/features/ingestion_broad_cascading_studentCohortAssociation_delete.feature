@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

	Scenario: Safe Delete StudentCohortAssociation with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
        |field                                     |value                                                                                 |
        |studentCohortAssociation._id              |908404e876dd56458385667fa383509035cd4312_ide3e74d25c695f1e27d2272bcbe12351cd02a78c1_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentCohortAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentCohortAssociationDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "studentCohortAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | studentCohortAssociation                  |        -1| 
        | recordHash                                |        -1|


  Scenario: Safe Delete StudentCohortAssociation Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
        |field                                     |value                                                                                 |
        |studentCohortAssociation._id              |908404e876dd56458385667fa383509035cd4312_ide3e74d25c695f1e27d2272bcbe12351cd02a78c1_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentCohortAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentCohortAssociationRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I re-execute saved query "studentCohortAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | studentCohortAssociation                  |        -1| 
        | recordHash                                |        -1|

  Scenario: Delete StudentCohortAssociation with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
      |field                                     |value                                                                                 |
      |studentCohortAssociation._id              |908404e876dd56458385667fa383509035cd4312_ide3e74d25c695f1e27d2272bcbe12351cd02a78c1_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentCohortAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentCohortAssociationDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "studentCohortAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                                |     delta|
      | studentCohortAssociation                  |        -1|
      | recordHash                                |        -1|


  Scenario: Delete StudentCohortAssociation Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
      |field                                     |value                                                                                 |
      |studentCohortAssociation._id              |908404e876dd56458385667fa383509035cd4312_ide3e74d25c695f1e27d2272bcbe12351cd02a78c1_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentCohortAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentCohortAssociationRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "studentCohortAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                                |     delta|
      | studentCohortAssociation                  |        -1|
      | recordHash                                |        -1|
      