@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Orphan Teacher School Association cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociation"
        |field                  |value                                      |
        |_id                    |185f8333b893edd803f880463a2a193d60715743_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanTeacherSchoolAssociationDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanTeacherSchoolAssociationDelete.zip" is completed in database
	And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "teacherSchoolAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |teacherSchoolAssociation   |   -1|
        |recordHash                 |   -1|

Scenario: Delete Orphan Teacher School Association by reference cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
	Then there exist "1" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociation"
        |field                  |value                                      |
        |_id                    |185f8333b893edd803f880463a2a193d60715743_id|
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanTeacherSchoolAssociationRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanTeacherSchoolAssociationRefDelete.zip" is completed in database
	And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "teacherSchoolAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                |delta|
        |teacherSchoolAssociation   |   -1|
        |recordHash                 |   -1|

  Scenario: Delete Teacher School Association with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociation"
      |field                  |value                                      |
      |_id                    |185f8333b893edd803f880463a2a193d60715743_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceTeacherSchoolAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceTeacherSchoolAssociationDelete.zip" is completed in database
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    # (According to schema, nothing refers to teacherSchoolAssociation)
    And I should not see a warning log file created
    And I re-execute saved query "teacherSchoolAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                |delta|
      |teacherSchoolAssociation   |   -1|
      |recordHash                 |   -1|

  Scenario: Delete Teacher School Association Reference with cascade = false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "teacherSchoolAssociation" records like below in "Midgar" tenant. And I save this query as "teacherSchoolAssociation"
      |field                  |value                                      |
      |_id                    |185f8333b893edd803f880463a2a193d60715743_id|
    And I save the collection counts in "Midgar" tenant
    And I post "ForceTeacherSchoolAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceTeacherSchoolAssociationRefDelete.zip" is completed in database
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    #(According to schema, nothing refers to teacherSchoolAssociation)
    And I should not see a warning log file created
    And I re-execute saved query "teacherSchoolAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                |delta|
      |teacherSchoolAssociation   |   -1|
      |recordHash                 |   -1|