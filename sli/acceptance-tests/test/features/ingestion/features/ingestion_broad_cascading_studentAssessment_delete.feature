@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

#SLC-StudentAssessmentReferenceType	SLC-StudentAssessmentItem	                StudentAssessmentReference	1	1
#SLC-StudentAssessmentReferenceType	SLC-StudentObjectiveAssessment	            StudentAssessmentReference	1	1
#SLC-StudentAssessmentReferenceType	SLC-StudentObjectiveAssessmentIdentityType	StudentAssessmentReference	1	1

Scenario: Delete StudentAssessment with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported

    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
        |field                                     |value                                                                                 |
        |_id                                       |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id                                           |
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessmentItem"
        |field                                     |value                                                                                 |
        |studentAssessmentItem.body.studentAssessmentId |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id                                      |        
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentObjectiveAssessment1"
        |field                                     |value                                                                                 |
        |studentObjectiveAssessment.body.studentAssessmentId |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id                                 |        
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentObjectiveAssessment2"
        |field                                     |value                                                                                 |
        |studentObjectiveAssessment.body.studentAssessmentId |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id                                 |        
    And I save the collection counts in "Midgar" tenant
    And I post "SafeStudentAssessmentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeStudentAssessmentDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
 		And I should see "records deleted successfully: 0" in the resulting batch job file
	  And I should see "records failed processing: 1" in the resulting batch job file
	  And I should not see a warning log file created
    And I re-execute saved query "studentAssessment" to get "1" records
    And I re-execute saved query "studentAssessmentItem" to get "1" records    
    And I re-execute saved query "studentObjectiveAssessment1" to get "1" records
    And I re-execute saved query "studentObjectiveAssessment2" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentAssessment                         |         0|        

Scenario: Delete Orphan StudentAssessment with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
        |field                                     |value                                                                                 |
        |_id                                       |43be4c7fe5848aa8df60477f735793309fa19cd2_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentAssessmentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudentAssessmentDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
 		And I should see "records deleted successfully: 1" in the resulting batch job file
 	  And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
    And I re-execute saved query "studentAssessment" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentAssessment                         |        -1|
        | recordHash                                |        -1|

Scenario: Delete Orphan StudentAssessment Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "studentAssessment"
        |field                                     |value                                                                                 |
        |_id                                       |43be4c7fe5848aa8df60477f735793309fa19cd2_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanStudentAssessmentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanStudentAssessmentRefDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
		And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
    And I re-execute saved query "studentAssessment" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | studentAssessment                         |        -1|
        | recordHash                                |        -1|

Scenario: Delete StudentAssessment with cascade = false, force = true and log violations = true
  Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And the "Midgar" tenant db is empty
  When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
  Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "SA2"
    |field                                                  |value                                                |
    |_id                                                    |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id          |
  Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "SA3"
    |field                                                  |value                                                |
    |studentAssessmentItem.body.studentAssessmentId         |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id          |
  Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "SA4"
    |field                                                  |value                                                |
    |studentObjectiveAssessment.body.studentAssessmentId    |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentAssessmentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentAssessmentDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentAssessment.xml"
    And I re-execute saved query "SA2" to get "1" records
    And I re-execute saved query "SA3" to get "1" records
    And I re-execute saved query "SA4" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                       |delta|
      | studentAssessment                |    0|
   	  | studentAssessment<hollow>        |    1|
      | studentAssessmentItem            |    0|
      | studentObjectiveAssessment       |    0|
      | recordHash                       |   -1|


Scenario: Delete StudentAssessment Ref with cascade = false, force = true and log violations = true
  Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And the "Midgar" tenant db is empty
  When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
  Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "SA2"
    |field                                                  |value                                                |
    |_id                                                    |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id          |
  Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "SA3"
    |field                                                  |value                                                |
    |studentAssessmentItem.body.studentAssessmentId         |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id          |
  Then there exist "1" "studentAssessment" records like below in "Midgar" tenant. And I save this query as "SA4"
    |field                                                  |value                                                |
    |studentObjectiveAssessment.body.studentAssessmentId    |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id          |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceStudentAssessmentRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceStudentAssessmentRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentAssessment.xml"
    And I re-execute saved query "SA2" to get "1" records
    And I re-execute saved query "SA3" to get "1" records
    And I re-execute saved query "SA4" to get "1" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                       |delta|
      | studentAssessment                |    0|
   	  | studentAssessment<hollow>        |    1|
      | studentAssessmentItem            |    0|
      | studentObjectiveAssessment       |    0|
      | recordHash                       |   -1|
