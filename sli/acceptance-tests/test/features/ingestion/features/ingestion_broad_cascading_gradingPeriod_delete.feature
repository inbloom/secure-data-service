@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Grading Period with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
	|field                                                           |value                                                |
	|_id                                                             |0d88d7123ffea30a9bb12d557152518e560a65d5_id          |
	Then there exist "1" "session" records like below in "Midgar" tenant. And I save this query as "session"
	|field                                                           |value                                                |
	|body.gradingPeriodReference                                     |0d88d7123ffea30a9bb12d557152518e560a65d5_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "SafeGradingPeriodDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeGradingPeriodDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrgCalendar.xml"
	And I should not see a warning log file created
    And I re-execute saved query "gradingPeriod" to get "1" records
    And I re-execute saved query "session" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |gradingPeriod|   0|


 Scenario: Delete Orphan GradingPeriod with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
	|field  |value                                                |
	|_id    |4f3309cd97c95b466a7348605287ab327e492608_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanGradingPeriodDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanGradingPeriodDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "gradingPeriod" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |gradingPeriod|  -1|
        |recordHash   |  -1|


Scenario: Delete Orphan GradingPeriod Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
	|field                                                           |value                                                |
	|_id                                                             |4f3309cd97c95b466a7348605287ab327e492608_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanGradingPeriodRefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanGradingPeriodRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "gradingPeriod" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |gradingPeriod|  -1|
        |recordHash   |  -1|

  Scenario: Delete Grading Period with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
      |field                                                           |value                                                |
      |_id                                                             |0d88d7123ffea30a9bb12d557152518e560a65d5_id          |
    Then there exist "1" "session" records like below in "Midgar" tenant. And I save this query as "session"
      |field                                                           |value                                                |
      |body.gradingPeriodReference                                     |0d88d7123ffea30a9bb12d557152518e560a65d5_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceGradingPeriodDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceGradingPeriodDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
	And I should not see a warning log file created
    And I should not see a warning log file created
    And I re-execute saved query "gradingPeriod" to get "0" records
    And I re-execute saved query "session" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection  |delta|
      |gradingPeriod|  -1 |
      |recordHash   |  -1 |

  Scenario: Delete Grading Period Reference with logViolations = true and default settings (Confirm that by default cascade = false, force = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
      |field                                                           |value                                                |
      |_id                                                             |0d88d7123ffea30a9bb12d557152518e560a65d5_id          |
    Then there exist "1" "session" records like below in "Midgar" tenant. And I save this query as "session"
      |field                                                           |value                                                |
      |body.gradingPeriodReference                                     |0d88d7123ffea30a9bb12d557152518e560a65d5_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceGradingPeriodRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceGradingPeriodRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrgCalendar.xml"
    And I re-execute saved query "gradingPeriod" to get "0" records
    And I re-execute saved query "session" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection  |delta|
      |gradingPeriod|  -1 |
      | recordHash  |  -1 |
