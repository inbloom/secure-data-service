@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Safe Delete Student Section Association (negative test)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                          |value                                                                                 |
        |studentSectionAssociation._id  |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                   |value                                                                                 |
        |grade.body.studentSectionAssociationId  |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "28" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "studentGradebookEntry"
        |field                                   |value                                                                                 |
        |body.studentSectionAssociationId        |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "6" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                   |value                                                                                 |
        |body.studentSectionAssociationId        |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    And I post "SafeStudentSectionAssociationDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "SafeStudentSectionAssociationDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentEnrollment.xml"
	And I should not see a warning log file created
	And I re-execute saved query "section" to get "1" records
	And I re-execute saved query "grade" to get "1" records
	And I re-execute saved query "studentGradebookEntry" to get "28" records
	And I re-execute saved query "studentCompetency" to get "6" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |recordHash                 |    0|

Scenario: Safe Delete Student Section Association Reference(negative test)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                          |value                                                                                 |
        |studentSectionAssociation._id  |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                   |value                                                                                 |
        |grade.body.studentSectionAssociationId  |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "28" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "studentGradebookEntry"
        |field                                   |value                                                                                 |
        |body.studentSectionAssociationId        |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "6" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                   |value                                                                                 |
        |body.studentSectionAssociationId        |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    And I post "SafeStudentSectionAssociationRefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "SafeStudentSectionAssociationRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentEnrollment.xml"
	And I should not see a warning log file created
	And I re-execute saved query "section" to get "1" records
	And I re-execute saved query "grade" to get "1" records
	And I re-execute saved query "studentGradebookEntry" to get "28" records
	And I re-execute saved query "studentCompetency" to get "6" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |recordHash                 |    0|

Scenario: Force Delete Student Section Association with cascade = false, force = true and log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                          |value                                                                                 |
        |studentSectionAssociation._id  |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                   |value                                                                                 |
        |grade.body.studentSectionAssociationId  |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "28" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "studentGradebookEntry"
        |field                                   |value                                                                                 |
        |body.studentSectionAssociationId        |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "6" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                   |value                                                                                 |
        |body.studentSectionAssociationId        |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentDenormalizedData1"
        |field                            |value                                                                                 |
        |_id                              |908404e876dd56458385667fa383509035cd4312_id|
        |section.$._id                    |2c77a1e5896b8ea9504e91e324c199e95130878d_id|
        |section.$.beginDate              |2001-09-03|
    Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "studentDenormalizedData2"
        |field                            |value                                                                                 |
        |_id                              |908404e876dd56458385667fa383509035cd4312_id|
        |section.$._id                    |2c77a1e5896b8ea9504e91e324c199e95130878d_id|
        |section.$.beginDate              |2101-09-03|
    And I post "ForceStudentSectionAssociationDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "ForceStudentSectionAssociationDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentEnrollment.xml"
	And I re-execute saved query "section" to get "0" records
	And I re-execute saved query "grade" to get "1" records
	And I re-execute saved query "studentGradebookEntry" to get "28" records
	And I re-execute saved query "studentCompetency" to get "6" records
	And I re-execute saved query "studentDenormalizedData1" to get "0" records
	And I re-execute saved query "studentDenormalizedData2" to get "1" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |student.section            |   -1|
        |recordHash                 |   -1|
        |studentSectionAssociation  |   -1|

Scenario: Force Delete Student Section Association Reference with cascade = false, force = true and log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                          |value                                                                                 |
        |studentSectionAssociation._id  |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "1" "yearlyTranscript" records like below in "Midgar" tenant. And I save this query as "grade"
        |field                                   |value                                                                                 |
        |grade.body.studentSectionAssociationId  |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "28" "studentGradebookEntry" records like below in "Midgar" tenant. And I save this query as "studentGradebookEntry"
        |field                                   |value                                                                                 |
        |body.studentSectionAssociationId        |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    Then there exist "6" "studentCompetency" records like below in "Midgar" tenant. And I save this query as "studentCompetency"
        |field                                   |value                                                                                 |
        |body.studentSectionAssociationId        |2c77a1e5896b8ea9504e91e324c199e95130878d_id5cb2d0a4813a0633260942351bc83b00be7d8f1e_id|
    And I post "ForceStudentSectionAssociationRefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "ForceStudentSectionAssociationRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentEnrollment.xml"
	And I re-execute saved query "section" to get "0" records
	And I re-execute saved query "grade" to get "1" records
	And I re-execute saved query "studentGradebookEntry" to get "28" records
	And I re-execute saved query "studentCompetency" to get "6" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |student.section            |   -1|
        |recordHash                 |   -1|
        |studentSectionAssociation  |   -1|
