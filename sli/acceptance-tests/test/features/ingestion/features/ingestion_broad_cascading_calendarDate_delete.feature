@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Calendar Date with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "calendarDate" records like below in "Midgar" tenant. And I save this query as "calendarDate"
	|field                                                           |value                                                |
	|_id                                                             |68afcad771ff07a4d988d8ff44434248a900fb5c_id          |
	Then there exist "1" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
	|field                                                           |value                                                |
	|body.calendarDateReference                                      |68afcad771ff07a4d988d8ff44434248a900fb5c_id          |
	And I save the collection counts in "Midgar" tenant
    And I post "BroadCalendarDateDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadCalendarDateDelete.zip" is completed in database
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
	And I re-execute saved query "gradingPeriod" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	|collection                        |delta          |
	|calendarDate                           |        -1|
	|gradingPeriod                          |         0|
	|recordHash                             |         0|
	And I should not see "68afcad771ff07a4d988d8ff44434248a900fb5c_id" in the "Midgar" database