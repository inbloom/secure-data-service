@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

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


Scenario: Force Delete Graduation Plan with cascade = false, force = true and log violations = true
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
    And I post "ForceGraduationPlanDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceGraduationPlanDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentEnrollment.xml"
    And I re-execute saved query "graduationPlan" to get "0" records
	And I re-execute saved query "studentSchoolAssociation" to get "3" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        | graduationPlan            |   -1|
        | recordHash                |   -1|

Scenario: Force Delete Graduation Plan Ref with cascade = false, force = true and log violations = true
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
    And I post "ForceGraduationPlanRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceGraduationPlanRefDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentEnrollment.xml"
    And I re-execute saved query "graduationPlan" to get "0" records
	And I re-execute saved query "studentSchoolAssociation" to get "3" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        | graduationPlan            |   -1|
        | recordHash                |   -1|

Scenario: Safe Delete Orphan Graduation Plan
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    Then there exist "1" "graduationPlan" records like below in "Midgar" tenant. And I save this query as "graduationPlan"
        |field  |value                                      |
        |_id    |22411ee1066db57f4a8424f8285bc1d82fae1560_id|
    And I post "OrphanGraduationPlanDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "OrphanGraduationPlanDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
	And I re-execute saved query "graduationPlan" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |recordHash                 |   -1|
        |graduationPlan             |   -1|


Scenario: Safe Delete Orphan Graduation Plan Ref
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I save the collection counts in "Midgar" tenant
    Then there exist "1" "graduationPlan" records like below in "Midgar" tenant. And I save this query as "graduationPlan"
        |field  |value                                      |
        |_id    |22411ee1066db57f4a8424f8285bc1d82fae1560_id|
    And I post "OrphanGraduationPlanRefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "OrphanGraduationPlanRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
	And I re-execute saved query "graduationPlan" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |recordHash                 |   -1|
        |graduationPlan             |   -1|

