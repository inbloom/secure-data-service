@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

 Scenario: Safe Delete Student Competency Objective (negative test)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
      |field                                       |value                                                                                 |
      |_id                                         |d529fd5fd0c1fca9df51fc202fbc2a7bdf1fd7fb_id|
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
      |field                                        |value                                      |
      |body.objectiveId.studentCompetencyObjectiveId|d529fd5fd0c1fca9df51fc202fbc2a7bdf1fd7fb_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentCompetencyObjectiveDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentCompetencyObjectiveDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
    And I should not see a warning log file created
    And I re-execute saved query "studentCompetencyObjective" to get "1" records
    And I re-execute saved query "studentCompetency" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |recordHash                             |         0|

 Scenario: Safe Delete Student Competency Objective Ref (negative test)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
      |field                                       |value                                                                                 |
      |_id                                         |d529fd5fd0c1fca9df51fc202fbc2a7bdf1fd7fb_id|
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
      |field                                        |value                                      |
      |body.objectiveId.studentCompetencyObjectiveId|d529fd5fd0c1fca9df51fc202fbc2a7bdf1fd7fb_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentCompetencyObjectiveRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentCompetencyObjectiveRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
    And I should not see a warning log file created
    And I re-execute saved query "studentCompetencyObjective" to get "1" records
    And I re-execute saved query "studentCompetency" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |recordHash                             |         0|

 Scenario: Orphan Safe Delete Student Competency Objective
     Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
     And the "Midgar" tenant db is empty
     When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
     Then there exist "1" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
       |field                                       |value                                                                                 |
       |_id                                         |028d7f8e25584d3353c9691e6aab89156029dde8_id|
     And I save the collection counts in "Midgar" tenant
     And I post "OrphanStudentCompetencyObjectiveDelete.zip" file as the payload of the ingestion job
     When zip file is scp to ingestion landing zone
     And a batch job for file "OrphanStudentCompetencyObjectiveDelete.zip" is completed in database
     And I should see "records considered for processing: 1" in the resulting batch job file
     And I should see "records ingested successfully: 0" in the resulting batch job file
     And I should see "records deleted successfully: 1" in the resulting batch job file
     And I should see "records failed processing: 0" in the resulting batch job file
     And I should see "records not considered for processing: 0" in the resulting batch job file
     And I should see "All records processed successfully." in the resulting batch job file
     And I should see "Processed 1 records." in the resulting batch job file
     And I should not see an error log file created
     And I re-execute saved query "studentCompetencyObjective" to get "0" records
     And I see that collections counts have changed as follows in tenant "Midgar"
       |collection                             |delta     |
       |studentCompetencyObjective             |        -1|
       |recordHash                             |        -1|

 Scenario: Orphan Safe Delete Student Competency Objective Ref
     Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
     And the "Midgar" tenant db is empty
     When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
     Then there exist "1" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
       |field                                       |value                                                                                 |
       |_id                                         |028d7f8e25584d3353c9691e6aab89156029dde8_id|
     And I save the collection counts in "Midgar" tenant
     And I post "OrphanStudentCompetencyObjectiveRefDelete.zip" file as the payload of the ingestion job
     When zip file is scp to ingestion landing zone
     And a batch job for file "OrphanStudentCompetencyObjectiveRefDelete.zip" is completed in database
     And I should see "records considered for processing: 1" in the resulting batch job file
     And I should see "records ingested successfully: 0" in the resulting batch job file
     And I should see "records deleted successfully: 1" in the resulting batch job file
     And I should see "records failed processing: 0" in the resulting batch job file
     And I should see "records not considered for processing: 0" in the resulting batch job file
     And I should see "All records processed successfully." in the resulting batch job file
     And I should see "Processed 1 records." in the resulting batch job file
     And I should not see an error log file created
     And I re-execute saved query "studentCompetencyObjective" to get "0" records
     And I see that collections counts have changed as follows in tenant "Midgar"
       |collection                             |delta     |
       |studentCompetencyObjective             |        -1|
       |recordHash                             |        -1|

 Scenario: Force Delete Student Competency Objective
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
        |field                                       |value                                                                                 |
        |_id                                         |d529fd5fd0c1fca9df51fc202fbc2a7bdf1fd7fb_id|
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                        |value                                      |
        |body.objectiveId.studentCompetencyObjectiveId|d529fd5fd0c1fca9df51fc202fbc2a7bdf1fd7fb_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentCompetencyObjectiveDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentCompetencyObjectiveDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I should not see an error log file created
    And I re-execute saved query "studentCompetencyObjective" to get "0" records
    And I re-execute saved query "studentCompetency" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
       |collection                             |delta     |
       |studentCompetencyObjective             |        -1|
       |recordHash                             |        -1|

 Scenario: Force Delete Student Competency Objective Ref
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
        |field                                       |value                                                                                 |
        |_id                                         |d529fd5fd0c1fca9df51fc202fbc2a7bdf1fd7fb_id|
    Then there exist "1" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                        |value                                      |
        |body.objectiveId.studentCompetencyObjectiveId|d529fd5fd0c1fca9df51fc202fbc2a7bdf1fd7fb_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentCompetencyObjectiveRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentCompetencyObjectiveRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I should not see an error log file created
    And I re-execute saved query "studentCompetencyObjective" to get "0" records
    And I re-execute saved query "studentCompetency" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
       |collection                             |delta     |
       |studentCompetencyObjective             |        -1|
       |recordHash                             |        -1|
