@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Orphan LEA with cascade = false and force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	  Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	   |field                                                           |value                                                |
	   |_id                                                             |59bf087eb486eb989d2dd7d9e26a0ae1fa3bf7b2_id          |
    And I save the collection counts in "Midgar" tenant
	  And I post "OrphanLEADelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanLEADelete.zip" is completed in database
	  And I should see "records considered for processing: 1" in the resulting batch job file
	  And I should see "records ingested successfully: 0" in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
	  And I should see "records not considered for processing: 0" in the resulting batch job file
	  And I should see "All records processed successfully." in the resulting batch job file
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
	  And I re-execute saved query "educationOrganization" to get "0" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |educationOrganization             |        -1|
	   |recordHash                        |        -1|

Scenario: Delete Orphan LEA Ref with cascade = false and force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	  Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	   |field                                                           |value                                                |
	   |_id                                                             |59bf087eb486eb989d2dd7d9e26a0ae1fa3bf7b2_id          |
    And I save the collection counts in "Midgar" tenant
	  And I post "OrphanLEARefDelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanLEARefDelete.zip" is completed in database
	  And I should see "records considered for processing: 1" in the resulting batch job file
	  And I should see "records ingested successfully: 0" in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
	  And I should see "records not considered for processing: 0" in the resulting batch job file
	  And I should see "All records processed successfully." in the resulting batch job file
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
	  And I re-execute saved query "educationOrganization" to get "0" records
  	And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |educationOrganization             |        -1|
	   |recordHash                        |        -1|

	
Scenario: Delete LEA with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	   |field                                                           |value                                                |
	   |_id                                                             |1b223f577827204a1c7e9c851dba06bea6b031fe_id          |
	  Then there exist "3" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "parentEducationOrganizationAgencyReference"
	   |field                                                           |value                                                |
	   |body.parentEducationAgencyReference                             |1b223f577827204a1c7e9c851dba06bea6b031fe_id         |
	  Then there exist "14" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
	   |field                                                           |value                                               |
	   |body.gradingPeriodIdentity.schoolId                             |1b223f577827204a1c7e9c851dba06bea6b031fe_id         |
	  Then there exist "2" "session" records like below in "Midgar" tenant. And I save this query as "session"
	   |field                                                           |value                                                |
	   |body.schoolId          										 |1b223f577827204a1c7e9c851dba06bea6b031fe_id         |
	  Then there exist "3" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "schoolLineage"
	   |field                                                           |value                                                |
	   |metaData.edOrgs                                                 |1b223f577827204a1c7e9c851dba06bea6b031fe_id          |
    And I save the collection counts in "Midgar" tenant
	  And I post "ForceLEADelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "ForceLEADelete.zip" is completed in database
	  And I should see "records considered for processing: 1" in the resulting batch job file
	  And I should see "records ingested successfully: 0" in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
	  And I should see "records not considered for processing: 0" in the resulting batch job file
	  And I should see "All records processed successfully." in the resulting batch job file
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
  	And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
	  And I re-execute saved query "educationOrganization" to get "0" records
	  And I re-execute saved query "parentEducationOrganizationAgencyReference" to get "3" records
	  And I re-execute saved query "schoolLineage" to get "0" records
	  And I re-execute saved query "session" to get "2" records
    And I re-execute saved query "gradingPeriod" to get "14" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |educationOrganization             |        -1|
	   |studentProgramAssociation         |         0|
	   |session						              	|         0|
	   |gradingPeriod					          	|         0|
	   |recordHash                        |        -1|

    
    
Scenario: Delete LEA Ref with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
     |field                                                           |value                                                |
	   |_id                                                             |1b223f577827204a1c7e9c851dba06bea6b031fe_id          |
  	Then there exist "3" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "parentEducationOrganizationAgencyReference"
     |field                                                           |value                                                |
     |body.parentEducationAgencyReference                             |1b223f577827204a1c7e9c851dba06bea6b031fe_id         |
  	Then there exist "14" "gradingPeriod" records like below in "Midgar" tenant. And I save this query as "gradingPeriod"
	   |field                                                           |value                                               |
	   |body.gradingPeriodIdentity.schoolId                             |1b223f577827204a1c7e9c851dba06bea6b031fe_id         |
	Then there exist "2" "session" records like below in "Midgar" tenant. And I save this query as "session"
	   |field                                                           |value                                                |
	   |body.schoolId          										 |1b223f577827204a1c7e9c851dba06bea6b031fe_id         |
   	Then there exist "3" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "schoolLineage"
	   |field                                                           |value                                                |
	   |metaData.edOrgs                                                 |1b223f577827204a1c7e9c851dba06bea6b031fe_id          |
    And I save the collection counts in "Midgar" tenant
   	And I post "ForceLEARefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceLEARefDelete.zip" is completed in database
  	And I should see "records considered for processing: 1" in the resulting batch job file
	  And I should see "records ingested successfully: 0" in the resulting batch job file
	  And I should see "records deleted successfully: 1" in the resulting batch job file
	  And I should see "records failed processing: 0" in the resulting batch job file
	  And I should see "records not considered for processing: 0" in the resulting batch job file
	  And I should see "All records processed successfully." in the resulting batch job file
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I should see "CORE_0066" in the resulting warning log file for "InterchangeEducationOrganization.xml"
	  And I re-execute saved query "educationOrganization" to get "0" records
	  And I re-execute saved query "parentEducationOrganizationAgencyReference" to get "3" records
	  And I re-execute saved query "schoolLineage" to get "0" records
	  And I re-execute saved query "session" to get "2" records
    And I re-execute saved query "gradingPeriod" to get "14" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |educationOrganization             |        -1|
	   |studentProgramAssociation         |         0|
	   |session						              	|         0|
	   |gradingPeriod					          	|         0|
	   |recordHash                        |        -1|
	
    
        
    
