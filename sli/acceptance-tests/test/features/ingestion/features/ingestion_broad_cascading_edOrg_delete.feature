@RALLY_US5818
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

@wip
Scenario: Delete generic EdOrg with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And I post "BroadEdOrgDelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "BroadEdOrgDelete.zip" is completed in database
	  And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	  And I should not see a warning log file created
          And I should not see "d7152b7086bb1714912fd697d531d310f6ff74b0_id" in the "Midgar" database
          And I should not see "37e7e68eca793ca208c02ade0eb92691870c1ef1_id" in the "Midgar" database
          And I should not see "74d8081e1f97822cb07636b13d61dafa9ff0e45d_id" in the "Midgar" database

Scenario: Delete EdOrg with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	   |field                                                           |value                                                |
	   |_id                                                             |d7152b7086bb1714912fd697d531d310f6ff74b0_id          |
	  Then there exist "2" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "parentEducationOrganizationAgencyReference"
	   |field                                                           |value                                                |
	   |body.parentEducationAgencyReference                             |d7152b7086bb1714912fd697d531d310f6ff74b0_id          |
    And I save the collection counts in "Midgar" tenant
	  And I post "ForceEdOrgDelete.zip" file as the payload of the ingestion job
	  When zip file is scp to ingestion landing zone
    And a batch job for file "ForceEdOrgDelete.zip" is completed in database
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
	  And I re-execute saved query "parentEducationOrganizationAgencyReference" to get "2" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |educationOrganization             |        -1|
	   |recordHash                        |        -1|

Scenario: Delete EdOrg Ref with cascade = false and force = true, log violations = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "educationOrganization"
	   |field                                                           |value                                                |
	   |_id                                                             |d7152b7086bb1714912fd697d531d310f6ff74b0_id          |
	  Then there exist "2" "educationOrganization" records like below in "Midgar" tenant. And I save this query as "parentEducationOrganizationAgencyReference"
	   |field                                                           |value                                                |
	   |body.parentEducationAgencyReference                             |d7152b7086bb1714912fd697d531d310f6ff74b0_id          |
    And I save the collection counts in "Midgar" tenant
   	And I post "ForceEdOrgRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "ForceEdOrgRefDelete.zip" is completed in database
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
	  And I re-execute saved query "parentEducationOrganizationAgencyReference" to get "2" records
	  And I see that collections counts have changed as follows in tenant "Midgar"
	   |collection                        |delta     |
	   |educationOrganization             |        -1|
	   |recordHash                        |        -1|
