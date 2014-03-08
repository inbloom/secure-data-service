@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete School with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "school"
        |field                                     |value                                                                                 |
        |_id                                       |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "3" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |body.educationOrgId                       |352e8570bd1116d11a72755b987902440045d346_id                                           |       
    Then there exist "34" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "12" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "4" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
        |field                                     |value                                                                                 |
        |body.responsibilitySchoolId               |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "4" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction2"
        |field                                     |value                                                                                 |
        |body.assignmentSchoolId                   |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "23" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |    
    Then there exist "2" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
        |field                                     |value                                                                                 |
        |body.educationOrganizationId              |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "24" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |                           
    And I save the collection counts in "Midgar" tenant
    And I post "SafeSchoolDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SafeSchoolDelete.zip" is completed in database
    And I should see "Processed 1 records." in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
    And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrganization.xml"
   	And I should not see a warning log file created
    And I re-execute saved query "school" to get "1" records
    And I re-execute saved query "attendance" to get "11" records
    And I re-execute saved query "cohort" to get "3" records
    And I re-execute saved query "course" to get "34" records
    And I re-execute saved query "courseOffering" to get "12" records
    And I re-execute saved query "disciplineAction" to get "4" records
    And I re-execute saved query "disciplineAction2" to get "4" records
    And I re-execute saved query "disciplineIncident" to get "23" records
    And I re-execute saved query "section" to get "11" records
    And I re-execute saved query "studentCompetencyObjective" to get "2" records
    And I re-execute saved query "studentSchoolAssociation" to get "11" records
    And I re-execute saved query "teacherSchoolAssociation" to get "24" records   
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |         0|         

Scenario: Delete Orphan School with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "school"
        |field                                     |value                                      |
        |_id                                       |bb6db4e34541d0c3a42bd9aa11f5da6b0f51cf38_id|
    Then there exist "3" "custom_entities" records like below in "Midgar" tenant. And I save this query as "custom_entities"
            |field                                     |value                                      |
            |metaData.entityId                            |bb6db4e34541d0c3a42bd9aa11f5da6b0f51cf38_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanSchoolDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanSchoolDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
	And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "school" to get "0" records
    And I re-execute saved query "custom_entities" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |        -1|
        | recordHash                                |        -1|
        | custom_entities                           |        -3|

Scenario: Delete Orphan School Reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "school"
        |field                                     |value                                      |
        |_id                                       |bb6db4e34541d0c3a42bd9aa11f5da6b0f51cf38_id|
    Then there exist "3" "custom_entities" records like below in "Midgar" tenant. And I save this query as "custom_entities"
        |field                                     |value                                      |
        |metaData.entityId                            |bb6db4e34541d0c3a42bd9aa11f5da6b0f51cf38_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanSchoolRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanSchoolRefDelete.zip" is completed in database
	And I should see "Processed 1 records." in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I re-execute saved query "school" to get "0" records
    And I re-execute saved query "custom_entities" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |        -1|
        | recordHash                                |        -1|
        | custom_entities                           |        -3|

Scenario: Delete School with cascade = false, logViolations = true and default settings (Confirm that by default force = true)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "school"
        |field                                     |value                                                                                 |
        |_id                                       |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "3" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |body.educationOrgId                       |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "34" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "12" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "4" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
        |field                                     |value                                                                                 |
        |body.responsibilitySchoolId               |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "4" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction2"
        |field                                     |value                                                                                 |
        |body.assignmentSchoolId                   |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "23" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "2" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
        |field                                     |value                                                                                 |
        |body.educationOrganizationId              |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "24" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "2" "custom_entities" records like below in "Midgar" tenant. And I save this query as "custom_entities"		  
        |field                                     |value                                      	  	   				   |
        |metaData.entityId                            |352e8570bd1116d11a72755b987902440045d346_id                                         |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceSchoolDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceSchoolDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
    And I re-execute saved query "school" to get "0" records
    And I re-execute saved query "attendance" to get "11" records
    And I re-execute saved query "cohort" to get "3" records
    And I re-execute saved query "course" to get "34" records
    And I re-execute saved query "courseOffering" to get "12" records
    And I re-execute saved query "disciplineAction" to get "4" records
    And I re-execute saved query "disciplineAction2" to get "4" records
    And I re-execute saved query "disciplineIncident" to get "23" records
    And I re-execute saved query "section" to get "11" records
    And I re-execute saved query "studentCompetencyObjective" to get "2" records
    And I re-execute saved query "studentSchoolAssociation" to get "11" records
    And I re-execute saved query "teacherSchoolAssociation" to get "24" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |        -1|
        | recordHash                                |        -1|
	| custom_entities			    | 	     -2|

Scenario: Delete School with force = true, logViolations = true and default settings (Confirm that by default cascade = false)
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "school"
        |field                                     |value                                                                                 |
        |_id                                       |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "3" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |body.educationOrgId                       |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "34" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "12" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "4" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
        |field                                     |value                                                                                 |
        |body.responsibilitySchoolId               |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "4" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction2"
        |field                                     |value                                                                                 |
        |body.assignmentSchoolId                   |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "23" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "2" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
        |field                                     |value                                                                                 |
        |body.educationOrganizationId              |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "24" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "2" "custom_entities" records like below in "Midgar" tenant. And I save this query as "custom_entities"		  
        |field                                     |value                                      	  	   				   |
        |metaData.entityId                            |352e8570bd1116d11a72755b987902440045d346_id                                         |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceSchoolRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceSchoolRefDelete.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
    And I re-execute saved query "school" to get "0" records
    And I re-execute saved query "attendance" to get "11" records
    And I re-execute saved query "cohort" to get "3" records
    And I re-execute saved query "course" to get "34" records
    And I re-execute saved query "courseOffering" to get "12" records
    And I re-execute saved query "disciplineAction" to get "4" records
    And I re-execute saved query "disciplineAction2" to get "4" records
    And I re-execute saved query "disciplineIncident" to get "23" records
    And I re-execute saved query "section" to get "11" records
    And I re-execute saved query "studentCompetencyObjective" to get "2" records
    And I re-execute saved query "studentSchoolAssociation" to get "11" records
    And I re-execute saved query "teacherSchoolAssociation" to get "24" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |        -1|
        | recordHash                                |        -1|
	| custom_entities			    |        -2|

Scenario: Delete School with cascade = false and force = true, log violations = false, non-entity specific test
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "school"
        |field                                     |value                                                                                 |
        |_id                                       |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "attendance" records like below in "Midgar" tenant. And I save this query as "attendance"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "3" "cohort" records like below in "Midgar" tenant. And I save this query as "cohort"
        |field                                     |value                                                                                 |
        |body.educationOrgId                       |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "34" "course" records like below in "Midgar" tenant. And I save this query as "course"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "12" "courseOffering" records like below in "Midgar" tenant. And I save this query as "courseOffering"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "4" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction"
        |field                                     |value                                                                                 |
        |body.responsibilitySchoolId               |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "4" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "disciplineAction2"
        |field                                     |value                                                                                 |
        |body.assignmentSchoolId                   |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "23" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "disciplineIncident"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "section" records like below in "Midgar" tenant. And I save this query as "section"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "2" "studentCompetencyObjective" records like below in "Midgar" tenant. And I save this query as "studentCompetencyObjective"
        |field                                     |value                                                                                 |
        |body.educationOrganizationId              |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "11" "studentSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "studentSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "24" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociation"
        |field                                     |value                                                                                 |
        |body.schoolId                             |352e8570bd1116d11a72755b987902440045d346_id                                           |
    Then there exist "2" "custom_entities" records like below in "Midgar" tenant. And I save this query as "custom_entities"		  
        |field                                     |value                                      	  	   				   |
        |metaData.entityId                            |352e8570bd1116d11a72755b987902440045d346_id                                         |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceSchoolRefDeleteWithoutLogViolations.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceSchoolRefDeleteWithoutLogViolations.zip" is completed in database
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see an error log file created
    And I re-execute saved query "school" to get "0" records
    And I re-execute saved query "attendance" to get "11" records
    And I re-execute saved query "cohort" to get "3" records
    And I re-execute saved query "course" to get "34" records
    And I re-execute saved query "courseOffering" to get "12" records
    And I re-execute saved query "disciplineAction" to get "4" records
    And I re-execute saved query "disciplineAction2" to get "4" records
    And I re-execute saved query "disciplineIncident" to get "23" records
    And I re-execute saved query "section" to get "11" records
    And I re-execute saved query "studentCompetencyObjective" to get "2" records
    And I re-execute saved query "studentSchoolAssociation" to get "11" records
    And I re-execute saved query "teacherSchoolAssociation" to get "24" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                |     delta|
        | educationOrganization                     |        -1|
        | recordHash                                |        -1|
	| custom_entities			    | 	     -2|
