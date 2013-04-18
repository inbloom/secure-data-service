@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete Graduation Plan with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I post "BroadGraduationPlanDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadGraduationPlanDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I should not see "438cc6756e65d65da2eabb0968387ad25a3e0b93_id" in the "Midgar" database

@wip
Scenario: Safe Delete Graduation Plan (negative test)
  Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    Then there exist "1" "graduationPlan" records like below in "Midgar" tenant. And I save this query as "graduationPlan"
        |field  |value                                      |
        |_id    |438cc6756e65d65da2eabb0968387ad25a3e0b93_id|
    Then there exist "3" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                      |value                                      |
        |body.graduationPlanId      |438cc6756e65d65da2eabb0968387ad25a3e0b93_id|
    And I post "SafeGraduationPlanDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "SafeGraduationPlanDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentEnrollment.xml"
	And I should not see a warning log file created
	And I re-execute saved query "graduationPlan" to get "1" records
	And I re-execute saved query "studentSchoolAssociation" to get "3" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |recordHash                 |    0|

@wip
Scenario: Safe Delete Graduation Plan Reference (negative test)
  Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    Then there exist "1" "graduationPlan" records like below in "Midgar" tenant. And I save this query as "graduationPlan"
        |field  |value                                      |
        |_id    |438cc6756e65d65da2eabb0968387ad25a3e0b93_id|
    Then there exist "3" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                      |value                                      |
        |body.graduationPlanId      |438cc6756e65d65da2eabb0968387ad25a3e0b93_id|
    And I post "SafeGraduationPlanRefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "SafeGraduationPlanRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
 	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
 	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeStudentEnrollment.xml"
	And I should not see a warning log file created
	And I re-execute saved query "graduationPlan" to get "1" records
	And I re-execute saved query "studentSchoolAssociation" to get "3" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |recordHash                 |    0|
