@RALLY_US5180, @RALLY_US5627

Feature: Error When Attempting Unsupported Deletes

    Background: I have a landing zone route configured
    Given I am using local data store

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
	And I should see "records considered for processing: 0" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 1" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0072" in the resulting error log file for "InterchangeStudentGrades.xml"
	And I should not see a warning log file created
    And I re-execute saved query "grade" to get "1" records
    And I re-execute saved query "reportCard" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |grade       |    0|

Scenario: Error When Attempting Cascade Delete with Force and LogViolations flags on, using student as an example
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "student"
        |field                                     |value                                                                                 |
        |_id                                       |908404e876dd56458385667fa383509035cd4312_id                                           |
        Then there exist "2" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    Then there exist "7" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
        |field                                     |value                                                                                 |
        |body.studentId                            |908404e876dd56458385667fa383509035cd4312_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "StudentUnsupported.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "StudentUnsupported.zip" is completed in database
	And I should see "records considered for processing: 0" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
	And I should see "records not considered for processing: 1" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0072" in the resulting error log file for "InterchangeStudentParent.xml"
	And I should not see a warning log file created
    And I re-execute saved query "student" to get "1" records
    And I re-execute saved query "attendance" to get "2" records
    And I re-execute saved query "courseTranscript" to get "7" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection   |delta|
        | student      |    0|
        | recordHash   |    0|

Scenario: Error when attempting force delete of not found entity
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    And I post "NotFoundLEARefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "NotFoundLEARefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0071" in the resulting error log file for "InterchangeEducationOrganization.xml"
	And I should not see a warning log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |         0|
