@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete StudentParentAssociation with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported

    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation._id              |908404e876dd56458385667fa383509035cd4312_id6ac27714bca705efbd6fd0eb6c0fd2c7317062e6_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentParentAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentParentAssociationDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
		And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
    And I re-execute saved query "studentParentAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentParentAssociation                  |        -1|
        | recordHash                                |        -1|

Scenario: Safe Delete StudentParentAssociation Ref with Cascade="false" Force="false"
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation._id              |908404e876dd56458385667fa383509035cd4312_id6ac27714bca705efbd6fd0eb6c0fd2c7317062e6_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentParentAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentParentAssociationRefDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "studentParentAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentParentAssociation                  |        -1|
        | recordHash                                |        -1|
        
        
	Scenario: Delete StudentParentAssociation Ref with cascade = false and force = true, log violations = true
	Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentParentAssociation"
        |field                                     |value                                                                                 |
        |studentParentAssociation._id              |908404e876dd56458385667fa383509035cd4312_id6ac27714bca705efbd6fd0eb6c0fd2c7317062e6_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentParentAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentParentAssociationRefDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "studentParentAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentParentAssociation                  |        -1|
        | recordHash                                |        -1|
        
	  
