@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

  Scenario: Safe Delete Student Competency with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |_id                                         |315511d70c3e5e231aae544dd1c9b2dd980e8d8b_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yt"
      |field                                       |value                                                                                 |
      |reportCard.body.studentCompetencyId         |315511d70c3e5e231aae544dd1c9b2dd980e8d8b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentCompetencyDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentCompetencyDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
    And I should not see a warning log file created
    And I re-execute saved query "sc" to get "1" records
    And I re-execute saved query "yt" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentCompetency                      |         0|
      |recordHash                             |         0|

  Scenario: Safe Delete Student Competency by reference with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |_id                                         |315511d70c3e5e231aae544dd1c9b2dd980e8d8b_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yt"
      |field                                       |value                                                                                 |
      |reportCard.body.studentCompetencyId         |315511d70c3e5e231aae544dd1c9b2dd980e8d8b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentCompetencyRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentCompetencyRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
    And I should not see a warning log file created
    And I re-execute saved query "sc" to get "1" records
    And I re-execute saved query "yt" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentCompetency                      |         0|
      |recordHash                             |         0|


  Scenario: Safe Delete Orphan Student Competency with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |_id                                         |bcebae40f6f70153107e172e22db85245c6a077e_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentCompetencyDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudentCompetencyDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "sc" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentCompetency                      |        -1|
      |recordHash                             |        -1|

  Scenario: Safe Delete Orphan Student Competency by reference with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |_id                                         |bcebae40f6f70153107e172e22db85245c6a077e_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentCompetencyRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudentCompetencyRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "sc" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentCompetency                      |        -1|
      |recordHash                             |        -1|


  Scenario: Delete Student Competency with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |_id                                         |315511d70c3e5e231aae544dd1c9b2dd980e8d8b_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yt"
      |field                                       |value                                                                                 |
      |reportCard.body.studentCompetencyId         |315511d70c3e5e231aae544dd1c9b2dd980e8d8b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentCompetencyDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentCompetencyDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I re-execute saved query "sc" to get "0" records
    And I re-execute saved query "yt" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentCompetency                      |        -1|
      |recordHash                             |        -1|

  Scenario: Delete Student Competency Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "sc"
      |field                                       |value                                                                                 |
      |_id                                         |315511d70c3e5e231aae544dd1c9b2dd980e8d8b_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yt"
      |field                                       |value                                                                                 |
      |reportCard.body.studentCompetencyId         |315511d70c3e5e231aae544dd1c9b2dd980e8d8b_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentCompetencyRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentCompetencyRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I re-execute saved query "sc" to get "0" records
    And I re-execute saved query "yt" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentCompetency                      |        -1|
      |recordHash                             |        -1|
