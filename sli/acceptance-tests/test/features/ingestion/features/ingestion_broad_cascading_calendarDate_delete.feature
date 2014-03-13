@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Calendar Date with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "calendarDate" records like below in "Midgar" tenant. And I save this query as "calendarDate"
	|field                                                           |value                                                |
	|_id                                                             |68afcad771ff07a4d988d8ff44434248a900fb5c_id          |
	Then there exist "2" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
	|field                                                           |value                                                |
	|body.calendarDateReference                                      |68afcad771ff07a4d988d8ff44434248a900fb5c_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "SafeCalendarDateDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCalendarDateDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrgCalendar.xml"
	And I should not see a warning log file created
	And I re-execute saved query "calendarDate" to get "1" records
	And I re-execute saved query "gradingPeriod" to get "2" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                             |delta     |
	|calendarDate                           |         0|
	|recordHash                             |         0|

Scenario: Delete Orphan Calendar Date with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "calendarDate" records like below in "Midgar" tenant. And I save this query as "calendarDate"
	|field                                                           |value                                                |
	|_id                                                             |396d29ea43f004cad22df3b9cf59d9a45f4e0741_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanCalendarDateDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanCalendarDateDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "calendarDate" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                             |delta     |
	|calendarDate                           |        -1|
	|recordHash                             |        -1|

Scenario: Delete Orphan Calendar Date Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "calendarDate" records like below in "Midgar" tenant. And I save this query as "calendarDate"
	|field                                                           |value                                                |
	|_id                                                             |396d29ea43f004cad22df3b9cf59d9a45f4e0741_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "OrphanCalendarDateRefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanCalendarDateRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "All records processed successfully." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "calendarDate" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                             |delta     |
	|calendarDate                           |        -1|
	|recordHash                             |        -1|

  Scenario: Delete Calendar Date with cascade = false, force = true and default settings (Confirm that by default logViolations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "calendarDate" records like below in "Midgar" tenant. And I save this query as "calendarDate"
      |field                                                           |value                                                |
      |_id                                                             |68afcad771ff07a4d988d8ff44434248a900fb5c_id          |
    Then there exist "2" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
      |field                                                           |value                                                |
      |body.calendarDateReference                                      |68afcad771ff07a4d988d8ff44434248a900fb5c_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceCalendarDateDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCalendarDateDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrgCalendar.xml"
    And I re-execute saved query "calendarDate" to get "0" records
    And I re-execute saved query "gradingPeriod" to get "2" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |  delta     |
      |calendarDate                           |   -1       |
      |recordHash                             |   -1       |

  Scenario: Delete Calendar Date Reference with force = true and default settings (Confirm that by default cascade = false and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "calendarDate" records like below in "Midgar" tenant. And I save this query as "calendarDate"
      |field                                                           |value                                                |
      |_id                                                             |68afcad771ff07a4d988d8ff44434248a900fb5c_id          |
    Then there exist "2" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
      |field                                                           |value                                                |
      |body.calendarDateReference                                      |68afcad771ff07a4d988d8ff44434248a900fb5c_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceCalendarDateRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCalendarDateRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrgCalendar.xml"
    And I re-execute saved query "calendarDate" to get "0" records
    And I re-execute saved query "gradingPeriod" to get "2" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |  delta     |
      |calendarDate                           |   -1       |
      |recordHash                             |   -1       |
