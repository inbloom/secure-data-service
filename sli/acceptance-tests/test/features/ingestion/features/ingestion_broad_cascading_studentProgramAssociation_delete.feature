@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

  Scenario: Safe Delete Student Program Association with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
      |field                                                           |value                                                                                 |
      |studentProgramAssociation._id                                   |90688439afff3a5fd4e1c33bc95378b4a7bed435_iddd0950ca5673284a24264bc7d66253e9dcaa147e_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentProgramAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentProgramAssociationDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "studentProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentProgramAssociation              |        -1|
      |recordHash                             |        -1|


 Scenario: Safe Delete Student Program Association Reference with Cascade = false, Force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
      |field                                                           |value                                                                                 |
      |studentProgramAssociation._id                                   |90688439afff3a5fd4e1c33bc95378b4a7bed435_iddd0950ca5673284a24264bc7d66253e9dcaa147e_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentProgramAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentProgramAssociationRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "studentProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentProgramAssociation              |        -1|
      |recordHash                             |        -1|



  Scenario: Delete Student Program Association with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
      |field                                                           |value                                                                                 |
      |studentProgramAssociation._id                                   |90688439afff3a5fd4e1c33bc95378b4a7bed435_iddd0950ca5673284a24264bc7d66253e9dcaa147e_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentProgramAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentProgramAssociationDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "studentProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentProgramAssociation              |        -1|
      |recordHash                             |        -1|

  Scenario: Delete All Student Program Association with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
      |field                                                           |value                                                                                 |
      |studentProgramAssociation._id                                   |90688439afff3a5fd4e1c33bc95378b4a7bed435_iddd0950ca5673284a24264bc7d66253e9dcaa147e_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentProgramAssociationDeleteAll.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentProgramAssociationDeleteAll.zip" is completed in database
    And I should see "records considered for processing: 49" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 49" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 49 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "studentProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta      |
      |studentProgramAssociation              |        -49|
      |recordHash                             |        -49|
    And I should see following map of entry counts in the corresponding collections:
      |collectionName                         |count    |
      |studentProgramAssociation              |      0  |


  Scenario: Delete Student Program Association Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentProgramAssociation"
      |field                                                           |value                                                                                 |
      |studentProgramAssociation._id                                   |90688439afff3a5fd4e1c33bc95378b4a7bed435_iddd0950ca5673284a24264bc7d66253e9dcaa147e_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentProgramAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentProgramAssociationRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "studentProgramAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentProgramAssociation              |        -1|
      |recordHash                             |        -1|
