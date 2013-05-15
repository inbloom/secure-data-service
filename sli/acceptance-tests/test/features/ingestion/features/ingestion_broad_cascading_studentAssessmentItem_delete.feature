@RALLY_US5180 @RALLY_US5614
Feature: Ingestion delete tests for StudentAssessmentItem

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Safe delete of StudentAssessmentItem with Cascade = false, Force = false and LogViolations = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessmentItem"
        |field                                     |value                                                                                 |
        |studentAssessmentItem._id                 |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id2d8381eef493c43af15ffc6d2ad764b2ff4b998a_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentAssessmentItemDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentAssessmentItemDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "studentAssessmentItem" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentAssessmentItem                     |        -1|
        | recordHash                                |      	 -1|

Scenario: Safe delete of StudentAssessmentItem by ref with Cascade = false, Force = false and LogViolations = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessmentItem"
        |field                                     |value                                                                                 |
        |studentAssessmentItem._id                 |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id2d8381eef493c43af15ffc6d2ad764b2ff4b998a_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentAssessmentItemRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentAssessmentItemRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "studentAssessmentItem" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentAssessmentItem                     |        -1|
        | recordHash                                |      	 -1|


Scenario: Force delete of StudentAssessmentItem with Cascade = false, Force = true and LogViolations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessmentItem"
        |field                                     |value                                                                                 |
        |studentAssessmentItem._id                 |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id2d8381eef493c43af15ffc6d2ad764b2ff4b998a_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentAssessmentItemDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentAssessmentItemDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "studentAssessmentItem" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentAssessmentItem                     |        -1|
        | recordHash                                |      	 -1|

Scenario: Force delete of StudentAssessmentItem by ref with Cascade = false, Force = true and LogViolations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessmentItem"
        |field                                     |value                                                                                 |
        |studentAssessmentItem._id                 |02c2c55fa1ecd2f47091d3f4df61bd6b525e6524_id2d8381eef493c43af15ffc6d2ad764b2ff4b998a_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentAssessmentItemRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentAssessmentItemRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "studentAssessmentItem" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentAssessmentItem                     |        -1|
        | recordHash                                |      	 -1|


