@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Session with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "session" records like below in "Midgar" tenant. And I save this query as "session"
        |field                  |value                                      |
        |_id                    |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "22" "section" records like below in "Midgar" tenant. And I save this query as "sections"
        |field                  |value                                      |
        |body.sessionId         |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "34" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOfferings"
        |field                  |value                                      |
        |body.sessionId         |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "10" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "studentAcademicRecords"
        |field                                          |value                                      |
        |studentAcademicRecord.body.sessionId           |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "3" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript1"
        |field                                  |value                                                                                 |
        |body.studentAcademicRecordId           |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript2"
        |field                                  |value                                                                                 |
        |body.studentAcademicRecordId           |e987e3842e0c7a5410b5a06d5d5a0cffa5d155f9_id89e7bb5b512c620aed26b1d1f1e48d6d30b701c4_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript3"
        |field                                  |value                                                                                 |
        |body.studentAcademicRecordId           |45555fb040d24a65147273c18c8d123ff25dc426_idb24981b22d0c821938310f239b7aac33db1ecbf3_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript4"
        |field                                  |value                                                                                 |
        |body.studentAcademicRecordId           |d4c3e1e6ce36d4325c27b8e00cd97c5288dbac31_id5a0de7044586e2747c0b94d77a03424f9c2fd054_id|
    Then there exist "2" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript5"
        |field                                  |value                                                                                 |
        |body.studentAcademicRecordId           |1485058ea5f4cd8053c590af0a9bbbbfe59ae6ba_id4caf30a7fce4e9cbb7a4aa4f9e6816418213ff90_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript6"
        |field                                  |value                                                                                 |
        |body.studentAcademicRecordId           |33a0924ac7cba36f57165dba5dde9d6980990adb_id6065652c2fa25e1048e14434bae28e49f8adc6aa_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeSessionDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeSessionDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
    #And I should see "records ingested successfully: 0" in the resulting batch job file
    #And I should see "records deleted successfully: 1" in the resulting batch job file
    #And I should see "records failed processing: 0" in the resulting batch job file
    #And I should see "records not considered for processing: 0" in the resulting batch job file
    #And I should see "All records processed successfully." in the resulting batch job file
    #And I should see "Processed 1 records." in the resulting batch job file
    #And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "session" to get "1" records
    And I re-execute saved query "sections" to get "22" records
    And I re-execute saved query "courseOfferings" to get "34" records
    And I re-execute saved query "studentAcademicRecords" to get "10" records
    And I re-execute saved query "courseTranscript1" to get "3" records
    And I re-execute saved query "courseTranscript2" to get "1" records
    And I re-execute saved query "courseTranscript3" to get "1" records
    And I re-execute saved query "courseTranscript4" to get "1" records
    And I re-execute saved query "courseTranscript5" to get "2" records
    And I re-execute saved query "courseTranscript6" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |session                    |    0|

Scenario: Delete Orphan Session with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "session" records like below in "Midgar" tenant. And I save this query as "session"
        |field                  |value                                      |
        |_id                    |0be5d4c743c2e0a4a7d6931fb46cf7df80d5fe1d_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanSessionDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanSessionDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "session" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |session                    |   -1|
        |recordHash                 |   -1|

Scenario: Delete Orphan Session Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "session" records like below in "Midgar" tenant. And I save this query as "session"
        |field                  |value                                      |
        |_id                    |0be5d4c743c2e0a4a7d6931fb46cf7df80d5fe1d_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanSessionRefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanSessionRefDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "session" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |session                    |   -1|
        |recordHash                 |   -1|

Scenario: Delete Session with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "session" records like below in "Midgar" tenant. And I save this query as "session"
      |field                  |value                                      |
      |_id                    |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "22" "section" records like below in "Midgar" tenant. And I save this query as "sections"
      |field                  |value                                      |
      |body.sessionId         |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "34" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOfferings"
      |field                  |value                                      |
      |body.sessionId         |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "10" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "studentAcademicRecords"
      |field                                          |value                                      |
      |studentAcademicRecord.body.sessionId           |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "3" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript1"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript2"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |e987e3842e0c7a5410b5a06d5d5a0cffa5d155f9_id89e7bb5b512c620aed26b1d1f1e48d6d30b701c4_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript3"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |45555fb040d24a65147273c18c8d123ff25dc426_idb24981b22d0c821938310f239b7aac33db1ecbf3_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript4"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |d4c3e1e6ce36d4325c27b8e00cd97c5288dbac31_id5a0de7044586e2747c0b94d77a03424f9c2fd054_id|
    Then there exist "2" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript5"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |1485058ea5f4cd8053c590af0a9bbbbfe59ae6ba_id4caf30a7fce4e9cbb7a4aa4f9e6816418213ff90_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript6"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |33a0924ac7cba36f57165dba5dde9d6980990adb_id6065652c2fa25e1048e14434bae28e49f8adc6aa_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceSessionDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceSessionDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrgCalendar.xml"
    And I re-execute saved query "session" to get "0" records
    And I re-execute saved query "sections" to get "22" records
    And I re-execute saved query "courseOfferings" to get "34" records
    And I re-execute saved query "studentAcademicRecords" to get "10" records
    And I re-execute saved query "courseTranscript1" to get "3" records
    And I re-execute saved query "courseTranscript2" to get "1" records
    And I re-execute saved query "courseTranscript3" to get "1" records
    And I re-execute saved query "courseTranscript4" to get "1" records
    And I re-execute saved query "courseTranscript5" to get "2" records
    And I re-execute saved query "courseTranscript6" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                |delta|
      |session                    |   -1|
      |recordHash                 |   -1|

  Scenario: Delete Session Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "session" records like below in "Midgar" tenant. And I save this query as "session"
      |field                  |value                                      |
      |_id                    |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "22" "section" records like below in "Midgar" tenant. And I save this query as "sections"
      |field                  |value                                      |
      |body.sessionId         |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "34" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOfferings"
      |field                  |value                                      |
      |body.sessionId         |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "10" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "studentAcademicRecords"
      |field                                          |value                                      |
      |studentAcademicRecord.body.sessionId           |fd5005534bf74e30de034c0e82662309f4df630f_id|
    Then there exist "3" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript1"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript2"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |e987e3842e0c7a5410b5a06d5d5a0cffa5d155f9_id89e7bb5b512c620aed26b1d1f1e48d6d30b701c4_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript3"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |45555fb040d24a65147273c18c8d123ff25dc426_idb24981b22d0c821938310f239b7aac33db1ecbf3_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript4"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |d4c3e1e6ce36d4325c27b8e00cd97c5288dbac31_id5a0de7044586e2747c0b94d77a03424f9c2fd054_id|
    Then there exist "2" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript5"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |1485058ea5f4cd8053c590af0a9bbbbfe59ae6ba_id4caf30a7fce4e9cbb7a4aa4f9e6816418213ff90_id|
    Then there exist "1" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript6"
      |field                                  |value                                                                                 |
      |body.studentAcademicRecordId           |33a0924ac7cba36f57165dba5dde9d6980990adb_id6065652c2fa25e1048e14434bae28e49f8adc6aa_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceSessionRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceSessionRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrgCalendar.xml"
    And I re-execute saved query "session" to get "0" records
    And I re-execute saved query "sections" to get "22" records
    And I re-execute saved query "courseOfferings" to get "34" records
    And I re-execute saved query "studentAcademicRecords" to get "10" records
    And I re-execute saved query "courseTranscript1" to get "3" records
    And I re-execute saved query "courseTranscript2" to get "1" records
    And I re-execute saved query "courseTranscript3" to get "1" records
    And I re-execute saved query "courseTranscript4" to get "1" records
    And I re-execute saved query "courseTranscript5" to get "2" records
    And I re-execute saved query "courseTranscript6" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                |delta|
      |session                    |   -1|
      |recordHash                 |   -1|
