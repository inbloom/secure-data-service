@RALLY_US5180

Feature: Error When Attempting Unsupported Deletes

    Background: I have a landing zone route configured
    Given I am using local data store

@wip
Scenario: Error When Attempting Cascade Delete, using grade as an example
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                     |value                                                                                 |
        |grade._id                                 |861efe5627b2c10ac01441b9afd26903398585bc_id091159fdc8200451fc5482189b29f7b9749ba775_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "reportCard"
        |field                                     |value                                                                                 |
        |reportCard.body.grades                    |861efe5627b2c10ac01441b9afd26903398585bc_id091159fdc8200451fc5482189b29f7b9749ba775_id|
    And I save the collection counts in "Midgar" tenant
    And I post "BroadGradeDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadGradeDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "child records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_00xx" in the resulting error log file for "InterchangeStudentGrades.xml"
	And I should not see a warning log file created
    And I re-execute saved query "grade" to get "1" records
    And I re-execute saved query "reportCard" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |grade       |    0|

@wip
Scenario: Error when attempting safe delete using reference for unsupported entity
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanGradeReferenceDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanGradeReferenceDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_00xx" in the resulting error log file for "InterchangeAttendance.xml"
	And I should not see a warning log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | attendanceEvent                           |         0|

@wip
Scenario: Error when attempting safe delete using full body for unsupported entity
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    And I post "SafeSEADelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeSEADelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_0071" in the resulting error log file for "InterchangeEducationOrganization.xml"
	And I should not see a warning log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |         0|

