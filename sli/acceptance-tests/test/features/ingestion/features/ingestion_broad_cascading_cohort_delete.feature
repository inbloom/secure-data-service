@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

	Scenario: Delete Cohort with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |_id                                       |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
	  Then there exist "30" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "staffCohortAssociation"
	      |field                                     |value                                                                                 |
	      |body.cohortId                             |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
	  Then there exist "4" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
	      |field                                     |value                                                                                 |
	      |studentCohortAssociation.body.cohortId    |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "SafeCohortDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeCohortDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
   	And I should not see a warning log file created
    And I re-execute saved query "cohort" to get "1" records
    And I re-execute saved query "staffCohortAssociation" to get "30" records
    And I re-execute saved query "studentCohortAssociation" to get "4" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | cohort                                    |         0| 
        | recordHash                                |      	  0|


	Scenario: Delete Orphan Cohort with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |_id                                       |8f57ec3c5f88653da356fe19b758cd2de5a7b8ef_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanCohortDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanCohortDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "cohort" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | cohort                                    |        -1| 
        | recordHash                                |      	 -1|

	Scenario: Delete Orphan Cohort Ref with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |_id                                       |8f57ec3c5f88653da356fe19b758cd2de5a7b8ef_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanCohortRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanCohortRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
   	And I should not see a warning log file created
    And I re-execute saved query "cohort" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | cohort                                    |        -1| 
        | recordHash                                |      	 -1|


	Scenario: Delete Cohort with cascade = false, force = true, logviolations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |_id                                       |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
	  Then there exist "30" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "staffCohortAssociation"
	      |field                                     |value                                                                                 |
	      |body.cohortId                             |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
	  Then there exist "4" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
	      |field                                     |value                                                                                 |
	      |studentCohortAssociation.body.cohortId    |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceCohortDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCohortDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentCohort.xml"    
    And I re-execute saved query "cohort" to get "0" records
    And I re-execute saved query "staffCohortAssociation" to get "30" records
    And I re-execute saved query "studentCohortAssociation" to get "4" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | cohort                                    |        -1| 
        | recordHash                                |      	 -1|
        
        
	Scenario: Delete Cohort Reference with cascade = false and default settings (Confirm that by default force = true and log violations = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |_id                                       |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
	  Then there exist "30" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "staffCohortAssociation"
	      |field                                     |value                                                                                 |
	      |body.cohortId                             |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
	  Then there exist "4" "student" records like below in "Midgar" tenant. And I save this query as "studentCohortAssociation"
	      |field                                     |value                                                                                 |
	      |studentCohortAssociation.body.cohortId    |3ec8e3eb5388b559890be7df3cf189902fc2735d_id                                           |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceCohortRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceCohortRefDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeStudentCohort.xml"    
    And I re-execute saved query "cohort" to get "0" records
    And I re-execute saved query "staffCohortAssociation" to get "30" records
    And I re-execute saved query "studentCohortAssociation" to get "4" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|    
        | cohort                                    |        -1| 
        | recordHash                                |      	 -1|
