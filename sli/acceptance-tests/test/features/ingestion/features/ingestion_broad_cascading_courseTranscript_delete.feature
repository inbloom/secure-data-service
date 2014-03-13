@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

  Scenario: Safe Delete Course Transcript (leaf) with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
      |field                                       |value                                      |
      |_id                                         |8c0cdbad1e99681a017ecd2549d7ebd972649035_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeCourseTranscriptDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCourseTranscriptDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "courseTranscript" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |courseTranscript                       |        -1|
      |recordHash                             |        -1|

  Scenario: Safe Delete Course Transcript (leaf) by reference with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
      |field                                       |value                                      |
      |_id                                         |8c0cdbad1e99681a017ecd2549d7ebd972649035_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeCourseTranscriptRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCourseTranscriptRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "courseTranscript" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |courseTranscript                       |        -1|
      |recordHash                             |        -1|


  Scenario: Delete Course Transcript with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
      |field                                       |value                                      |
      |_id                                         |8c0cdbad1e99681a017ecd2549d7ebd972649035_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceCourseTranscriptDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCourseTranscriptDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "courseTranscript" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |courseTranscript                       |        -1|
      |recordHash                             |        -1|

  Scenario: Delete Course Transcript Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
      |field                                       |value                                      |
      |_id                                         |8c0cdbad1e99681a017ecd2549d7ebd972649035_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceCourseTranscriptRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCourseTranscriptRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "courseTranscript" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |courseTranscript                       |        -1|
      |recordHash                             |        -1|
