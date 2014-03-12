@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

	Scenario: Delete StaffProgramAssociation with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "staffProgramAssociation"
    	|field                                                           |value                                                                                          |
    	|_id                                                             |b968978b829eb02e385bcd7fc586597de8bf1701_id         |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStaffProgramAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStaffProgramAssociationDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
   	And I should not see a warning log file created
   	And I should not see an error log file created
    And I re-execute saved query "staffProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staffProgramAssociation                |   -1  |
      |recordHash                             |   -1  |

	Scenario: Safe Delete StaffProgramAssociation by reference with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "staffProgramAssociation"
    	|field                                                           |value                                                                                          |
    	|_id                                                             |b968978b829eb02e385bcd7fc586597de8bf1701_id         |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStaffProgramAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStaffProgramAssociationRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
   	And I should not see a warning log file created
   	And I should not see an error log file created
    And I re-execute saved query "staffProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staffProgramAssociation                |   -1  |
      |recordHash                             |   -1  |


	Scenario: Delete StaffProgramAssociation with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "staffProgramAssociation"
    	|field                                                           |value                                                                                          |
    	|_id                                                             |b968978b829eb02e385bcd7fc586597de8bf1701_id         |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStaffProgramAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStaffProgramAssociationDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
   	And I should not see a warning log file created
   	And I should not see an error log file created
    And I re-execute saved query "staffProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staffProgramAssociation                |   -1  |
      |recordHash                             |   -1  |  	


	Scenario: Delete StaffProgramAssociation Reference with cascade = false, force = true, logviolations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "staffProgramAssociation"
    	|field                                                           |value                                                                                          |
    	|_id                                                             |b968978b829eb02e385bcd7fc586597de8bf1701_id         |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStaffProgramAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStaffProgramAssociationRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
   	And I should not see a warning log file created
   	And I should not see an error log file created
    And I re-execute saved query "staffProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staffProgramAssociation                |   -1  |
      |recordHash                             |   -1  |  	
