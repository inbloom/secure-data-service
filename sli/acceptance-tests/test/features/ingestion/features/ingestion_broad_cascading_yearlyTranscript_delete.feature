@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

  Scenario: Delete Student Program Association with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I should see following map of entry counts in the corresponding collections:
      |collectionName                         |count    |
      |grade                                 |        21|
      |reportCard                            |        21|
      |studentAcademicRecord                 |        21|
      |yearlyTranscript                      |        23|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceYearlyTranscriptDeleteAll.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceYearlyTranscriptDeleteAll.zip" is completed in database
    And I should see "records considered for processing: 63" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 63" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 63 records." in the resulting batch job file
    And I should not see an error log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |grade                                 |        -21|
      |reportCard                            |        -21|
      |studentAcademicRecord                 |        -21|
      |yearlyTranscript                      |        -23|
      |recordHash                            |        -63|
    And I should see following map of entry counts in the corresponding collections:
      |collectionName                         |count    |
      |grade                                 |        0|
      |reportCard                            |        0|
      |studentAcademicRecord                 |        0|
      |yearlyTranscript                      |        0|
