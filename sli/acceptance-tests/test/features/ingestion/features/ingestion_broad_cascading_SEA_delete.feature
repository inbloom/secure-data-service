@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

#Type	                Child Type	    Field	                        minOccurs	maxOccurs	Child Collection	
#SEA	                LEA          	parentEducationAgencyReference	1	        1	         educationOrganization		

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete SEA with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|_id                                                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "2" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|body.parentEducationAgencyReference                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "3" "graduationPlan" records like below in "Midgar" tenant. And I save this query as "graduationPlan"
	|field                                                           |value                                               |
	|body.educationOrganizationId                                    |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "1" "program" records like below in "Midgar" tenant. And I save this query as "program"
	|field                                                           |value                                                |
	|studentProgramAssociation.body.educationOrganizationId          |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "136" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "staffEducationOrganizationAssociation"
	|field                                                           |value                                                |
	|body.educationOrganizationReference                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "3" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "school"
	|field                                                           |value                                                |
	|metaData.edOrgs                                                 |884daa27d806c2d725bc469b273d840493f84b4d_id          |
    And I save the collection counts in "Midgar" tenant
	And I post "BroadSEADelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "BroadSEADelete.zip" is completed in database
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
	And I re-execute saved query "school" to get "0" records
	And I re-execute saved query "staffEducationOrganizationAssociation" to get "0" records
    And I re-execute saved query "program" to get "0" records
	And I re-execute saved query "graduationPlan" to get "0" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	 |collection                        |delta     |
	 |educationOrganization             |        -1|
	 |staffEducationOrganizationAssociation |  -136|
	 |studentProgramAssociation         |       -14|
	 |recordHash                        |         0|
	And I should not see "884daa27d806c2d725bc469b273d840493f84b4d_id" in the "Midgar" database

	
Scenario: Delete SEA with cascade = false and force = false, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|_id                                                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "4" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "parentEducationOrganizationAgencyReference"
	|field                                                           |value                                                |
	|body.parentEducationAgencyReference                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "3" "graduationPlan" records like below in "Midgar" tenant. And I save this query as "graduationPlan"
	|field                                                           |value                                               |
	|body.educationOrganizationId                                    |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "136" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "staffEducationOrganizationAssociation"
	|field                                                           |value                                                |
	|body.educationOrganizationReference                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "5" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "schoolLineage"
	|field                                                           |value                                                |
	|metaData.edOrgs                                                 |884daa27d806c2d725bc469b273d840493f84b4d_id          |
    And I save the collection counts in "Midgar" tenant
	And I post "SafeSEADelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "SafeSEADelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
	And I should see "records ingested successfully: 0" in the resulting batch job file
	And I should see "records deleted successfully: 0" in the resulting batch job file
	And I should see "records failed processing: 1" in the resulting batch job file
	And I should see "records not considered for processing: 0" in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
    And I should not see a warning log file created
	And I should see "CORE_0066" in the resulting error log file for "InterchangeEducationOrganization.xml"
	And I re-execute saved query "educationOrganization" to get "1" records
	And I re-execute saved query "parentEducationOrganizationAgencyReference" to get "4" records
	And I re-execute saved query "schoolLineage" to get "5" records
	And I re-execute saved query "staffEducationOrganizationAssociation" to get "136" records
    And I re-execute saved query "graduationPlan" to get "3" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	 |collection                        |delta     |
	 |educationOrganization             |         0|


Scenario: Delete Orphan SEA with cascade = false and force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|_id                                                             |79789f01d8debe0db2691ac3c726b7a6f91ea160_id          |
  And I save the collection counts in "Midgar" tenant
	And I post "OrphanSEADelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanSEADelete.zip" is completed in database
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


Scenario: Delete Orphan SEA Ref with cascade = false and force = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|_id                                                             |79789f01d8debe0db2691ac3c726b7a6f91ea160_id          |
    And I save the collection counts in "Midgar" tenant
	And I post "OrphanSEARefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanSEARefDelete.zip" is completed in database
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
		
	
Scenario: Delete SEA with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|_id                                                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "4" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "parentEducationOrganizationAgencyReference"
	|field                                                           |value                                                |
	|body.parentEducationAgencyReference                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "3" "graduationPlan" records like below in "Midgar" tenant. And I save this query as "graduationPlan"
	|field                                                           |value                                               |
	|body.educationOrganizationId                                    |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "136" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "staffEducationOrganizationAssociation"
	|field                                                           |value                                                |
	|body.educationOrganizationReference                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "5" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "schoolLineage"
	|field                                                           |value                                                |
	|metaData.edOrgs                                                 |884daa27d806c2d725bc469b273d840493f84b4d_id          |
    And I save the collection counts in "Midgar" tenant
	And I post "ForceSEADelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceSEADelete.zip" is completed in database
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
	And I re-execute saved query "parentEducationOrganizationAgencyReference" to get "4" records
	And I re-execute saved query "schoolLineage" to get "0" records
	And I re-execute saved query "staffEducationOrganizationAssociation" to get "136" records
    And I re-execute saved query "graduationPlan" to get "3" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	 |collection                        |delta     |
	 |educationOrganization             |        -1|
	 |staffEducationOrganizationAssociation |     0|
	 |studentProgramAssociation         |         0|
	 |recordHash                        |        -1|
	
	
Scenario: Delete SEA Ref with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	|field                                                           |value                                                |
	|_id                                                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "4" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "parentEducationOrganizationAgencyReference"
	|field                                                           |value                                                |
	|body.parentEducationAgencyReference                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "3" "graduationPlan" records like below in "Midgar" tenant. And I save this query as "graduationPlan"
	|field                                                           |value                                               |
	|body.educationOrganizationId                                    |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "136" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "staffEducationOrganizationAssociation"
	|field                                                           |value                                                |
	|body.educationOrganizationReference                             |884daa27d806c2d725bc469b273d840493f84b4d_id          |
	Then there exist "5" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "schoolLineage"
	|field                                                           |value                                                |
	|metaData.edOrgs                                                 |884daa27d806c2d725bc469b273d840493f84b4d_id          |
    And I save the collection counts in "Midgar" tenant
	And I post "ForceSEARefDelete.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceSEARefDelete.zip" is completed in database
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
	And I re-execute saved query "parentEducationOrganizationAgencyReference" to get "4" records
	And I re-execute saved query "schoolLineage" to get "0" records
	And I re-execute saved query "staffEducationOrganizationAssociation" to get "136" records
    And I re-execute saved query "graduationPlan" to get "3" records
	And I see that collections counts have changed as follows in tenant "Midgar"
	 |collection                        |delta     |
	 |educationOrganization             |        -1|
	 |staffEducationOrganizationAssociation |     0|
	 |studentProgramAssociation         |         0|
	 |recordHash                        |        -1|
	
	
	
	
