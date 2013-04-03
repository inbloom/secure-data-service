@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

#SLC-StudentAssessmentReferenceType	SLC-StudentAssessmentItem	                StudentAssessmentReference	1	1
#SLC-StudentAssessmentReferenceType	SLC-StudentObjectiveAssessment	            StudentAssessmentReference	1	1
#SLC-StudentAssessmentReferenceType	SLC-StudentObjectiveAssessmentIdentityType	StudentAssessmentReference	1	1

  Scenario: Delete Program with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty

  #    And I post "BroadSetOfTypes.zip" file as the payload of the ingestion job
  #    When zip file is scp to ingestion landing zone
  #    Then a batch job for file "BroadSetOfTypes.zip" is completed in database
  #    And I should not see an error log file created
  #    And I should not see a warning log file created

  When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
  And I should see child entities of entityType "studentAssessment" with id "30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id" in the "Midgar" database

  Then there exist "1" "student" records like below in "Midgar" tenant. And I save this query as "SA1"
    |field                                                  |value                                                |
    |studentAssessment._id                                  |30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id          |
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
    And I post "BroadStudentAssessmentDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStudentAssessmentDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
	And I should not see "30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id" in the "Midgar" database
	And I should not see any entity mandatorily referring to "30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id" in the "Midgar" database
	And I should see entities optionally referring to "30fdc0baa2af224046e8b7ba3407a5741fb37ac3_id" be updated in the "Midgar" database
    And I re-execute saved query "SA1" to get "0" records
    And I re-execute saved query "SA2" to get "0" records
    And I re-execute saved query "SA3" to get "0" records
    And I re-execute saved query "SA4" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                       |delta|
      | studentAssessment                |   -1|
      | studentAssessmentItem            |   -1|
      | studentObjectiveAssessment       |   -1|
#studentAssessmentItem.studentObjectiveAssessment.studentAssessment This relationship is missing from Odin data
