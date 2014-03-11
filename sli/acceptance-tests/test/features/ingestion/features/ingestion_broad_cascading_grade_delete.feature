@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

    Background: I have a landing zone route configured
    Given I am using local data store

Scenario: Safe delete Grade without cascade, using just Action Type of Delete and without cascade flag to verify default
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
    And I post "SafeGradeDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeGradeDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
    And the only errors I want to see in the resulting error log file for "InterchangeStudentGrades.xml" are below
        | code    |
        | CORE_0066|
    And I should not see a warning log file created
    And I re-execute saved query "grade" to get "1" records
    And I re-execute saved query "reportCard" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |grade       |    0|

Scenario: Safe delete Grade by reference without cascade
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
    And I post "SafeGradeReferenceDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeGradeReferenceDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
    And the only errors I want to see in the resulting error log file for "InterchangeStudentGrades.xml" are below
        | code    |
        | CORE_0066|
    And I should not see a warning log file created
    And I re-execute saved query "grade" to get "1" records
    And I re-execute saved query "reportCard" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |grade       |    0|


Scenario: Delete Orphan Grade without cascade using body
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                     |value                                                                                 |
        |grade._id                                 |dca20d33a0ebc27f44a3575f03879925b886c12a_id7093de31d3cbd6b3d57745b7a8bb2d79672509d3_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanGradeDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanGradeDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "grade" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection            |delta|
        |yearlyTranscript       |   -1|
        |grade                  |   -1|
        |recordHash             |   -1|

Scenario: Delete Orphan Grade without cascade using reference
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                     |value                                                                                 |
        |grade._id                                 |dca20d33a0ebc27f44a3575f03879925b886c12a_id7093de31d3cbd6b3d57745b7a8bb2d79672509d3_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanGradeReferenceDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanGradeReferenceDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "grade" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection            |delta|
        |yearlyTranscript       |   -1|
        |grade                  |   -1|
        |recordHash             |   -1|


Scenario: Force delete Grade cascade=false, force=true
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
    And I post "ForceGradeDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceGradeDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And the only errors I want to see in the resulting warning log file for "InterchangeStudentGrades.xml" are below
        | code    |
        | CORE_0066|
    And I re-execute saved query "grade" to get "0" records
    And I re-execute saved query "reportCard" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |grade       |   -1|
        |recordHash  |   -1|

Scenario: Force delete Grade by reference cascade=false, force=true
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
    And I post "ForceGradeRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceGradeRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And the only errors I want to see in the resulting warning log file for "InterchangeStudentGrades.xml" are below
        | code    |
        | CORE_0066|
    And I re-execute saved query "grade" to get "0" records
    And I re-execute saved query "reportCard" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection |delta|
        |grade       |   -1|
        |recordHash  |   -1|

