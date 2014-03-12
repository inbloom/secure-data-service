@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

  Scenario: Safe Delete Student Academic Record with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yearlyTranscript"
      |field                                       |value                                                                                 |
      |studentAcademicRecord._id                   |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    Then there exist "3" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
      |field                                       |value                                                                                 |
      |body.studentAcademicRecordId                |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentAcademicRecordDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentAcademicRecordDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
  	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "yearlyTranscript" to get "1" records
    And I re-execute saved query "courseTranscript" to get "3" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentAcademicRecord                  |         0|
      |recordHash                             |         0|

  Scenario: Safe Delete Student Academic Record by reference with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yearlyTranscript"
      |field                                       |value                                                                                 |
      |studentAcademicRecord._id                   |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    Then there exist "3" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
      |field                                       |value                                                                                 |
      |body.studentAcademicRecordId                |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentAcademicRecordRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentAcademicRecordRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 0" in the resulting batch job file
    And I should see "records failed processing: 1" in the resulting batch job file
  	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentGrades.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "yearlyTranscript" to get "1" records
    And I re-execute saved query "courseTranscript" to get "3" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentAcademicRecord                  |         0|
      |recordHash                             |         0|


  Scenario: Delete Orphan Student Academic Record with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yearlyTranscript"
      |field                                       |value                                                                                 |
      |studentAcademicRecord._id                   |c40d5ced1809cd64b18976139f5e4c9ea64fc245_idf22241e7e5a4e613c9bf7ca780773d38675d6254_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentAcademicRecordDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudentAcademicRecordDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "yearlyTranscript" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |yearlyTranscript                       |        -1|
      |studentAcademicRecord                  |        -1|
      |recordHash                             |        -1|

  Scenario: Delete Orphan Student Academic Record by reference with cascade = false, force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yearlyTranscript"
      |field                                       |value                                                                                 |
      |studentAcademicRecord._id                   |c40d5ced1809cd64b18976139f5e4c9ea64fc245_idf22241e7e5a4e613c9bf7ca780773d38675d6254_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentAcademicRecordRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudentAcademicRecordRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I re-execute saved query "yearlyTranscript" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |yearlyTranscript                       |        -1|
      |studentAcademicRecord                  |        -1|
      |recordHash                             |        -1|


  Scenario: Delete Student Academic Record with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yearlyTranscript"
      |field                                       |value                                                                                 |
      |studentAcademicRecord._id                   |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    Then there exist "3" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
      |field                                       |value                                                                                 |
      |body.studentAcademicRecordId                |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentAcademicRecordDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentAcademicRecordDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    #And I should not see a warning log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I re-execute saved query "yearlyTranscript" to get "0" records
    And I re-execute saved query "courseTranscript" to get "3" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentAcademicRecord                  |        -1|
      |recordHash                             |        -1|

  Scenario: Delete Student Academic Record Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "yearlyTranscript"
      |field                                       |value                                                                                 |
      |studentAcademicRecord._id                   |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    Then there exist "3" "courseTranscript" records like below in "Midgar" tenant. And I save this query as "courseTranscript"
      |field                                       |value                                                                                 |
      |body.studentAcademicRecordId                |861efe5627b2c10ac01441b9afd26903398585bc_id8c0587f25143443bd060b75244834f4058daabac_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentAcademicRecordRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentAcademicRecordRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    #And I should not see a warning log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentGrades.xml"
    And I re-execute saved query "yearlyTranscript" to get "0" records
    And I re-execute saved query "courseTranscript" to get "3" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta     |
      |studentAcademicRecord                  |        -1|
      |recordHash                             |        -1|
