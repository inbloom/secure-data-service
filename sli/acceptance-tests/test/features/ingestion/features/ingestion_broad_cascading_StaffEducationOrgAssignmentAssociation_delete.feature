@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Assessment with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And there exist "1" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "staffEducationOrganizationAssociation"
        |field                  |value                                      |
        |_id                    |e8ce901a42119bcca2516c2e0c6e606f0fd5343c_id|
    And I save the collection counts in "Midgar" tenant
    And I post "BroadStaffEducationOrgAssignmentAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStaffEducationOrgAssignmentAssociationDelete.zip" is completed in database
	And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                  |delta|
        |staffEducationOrganizationAssociation        |   -1|
        #|recordHash                                  |    -1
	And I re-execute saved query "staffEducationOrganizationAssociation" to get "0" records
	And I should not see "e8ce901a42119bcca2516c2e0c6e606f0fd5343c_id" in the "Midgar" database

Scenario: Delete Assessment without cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    And there exist "1" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "staffEducationOrganizationAssociation"
        |field                  |value                                      |
        |_id                    |e8ce901a42119bcca2516c2e0c6e606f0fd5343c_id|
    And I save the collection counts in "Midgar" tenant
    And I post "LeafStaffEducationOrgAssignmentAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "LeafStaffEducationOrgAssignmentAssociationDelete.zip" is completed in database
    And I should see "records considered for processing: 1" in the resulting batch job file
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                                  |delta|
        |staffEducationOrganizationAssociation        |   -1|
        #|recordHash                                  |    -1
    And I re-execute saved query "staffEducationOrganizationAssociation" to get "0" records
    And I should not see "e8ce901a42119bcca2516c2e0c6e606f0fd5343c_id" in the "Midgar" database