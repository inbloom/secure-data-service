@RALLY_US5180
Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store
Scenario: Delete Orphan Teacher Section Association with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "teacherSectionAssociation"
        |field                          |value                                                                                  |
        |teacherSectionAssociation._id  |e003fc1479112d3e953a0220a2d0ddd31077d6d9_id26556e7c6a6f7ef10fa46850c9c68d5cfc0c2d4d_id |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanTeacherSectionAssociationDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanTeacherSectionAssociationDelete.zip" is completed in database
	And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "teacherSectionAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                 |delta|
        |teacherSectionAssociation   |   -1|
        |recordHash                  |   -1|

Scenario: Delete Orphan Teacher Section Association by reference with cascade = false
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "teacherSectionAssociation"
        |field                          |value                                                                                  |
        |teacherSectionAssociation._id  |e003fc1479112d3e953a0220a2d0ddd31077d6d9_id26556e7c6a6f7ef10fa46850c9c68d5cfc0c2d4d_id |
    And I save the collection counts in "Midgar" tenant
    And I post "OrphanTeacherSectionAssociationRefDelete.zip" file as the payload of the ingestion job
  	When zip file is scp to ingestion landing zone
    And a batch job for file "OrphanTeacherSectionAssociationRefDelete.zip" is completed in database
	And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
	And I should not see a warning log file created
	And I re-execute saved query "teacherSectionAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
        | collection                 |delta|
        |teacherSectionAssociation   |   -1|
        |recordHash                  |   -1|


  Scenario: Delete Teacher Section Association with cascade =false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "teacherSectionAssociation"
      |field                          |value                                                                                  |
      |teacherSectionAssociation._id  |e003fc1479112d3e953a0220a2d0ddd31077d6d9_id26556e7c6a6f7ef10fa46850c9c68d5cfc0c2d4d_id |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceTeacherSectionAssociationDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceTeacherSectionAssociationDelete.zip" is completed in database
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    #(According to schema, nothing refers to teacherSectionAssociation)
    And I should not see a warning log file created
    And I re-execute saved query "teacherSectionAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                 |delta|
      |teacherSectionAssociation   |   -1|
      |recordHash                  |   -1|

  Scenario: Delete Teacher Section Association Referencewith cascade =false, force = true
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
    Then there exist "1" "section" records like below in "Midgar" tenant. And I save this query as "teacherSectionAssociation"
      |field                          |value                                                                                  |
      |teacherSectionAssociation._id  |e003fc1479112d3e953a0220a2d0ddd31077d6d9_id26556e7c6a6f7ef10fa46850c9c68d5cfc0c2d4d_id |
    And I save the collection counts in "Midgar" tenant
    And I post "ForceTeacherSectionAssociationRefDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ForceTeacherSectionAssociationRefDelete.zip" is completed in database
    And I should see "records ingested successfully: 0" in the resulting batch job file
    And I should see "records deleted successfully: 1" in the resulting batch job file
    And I should see "records failed processing: 0" in the resulting batch job file
    And I should see "records not considered for processing: 0" in the resulting batch job file
    And I should see "All records processed successfully." in the resulting batch job file
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    #(According to schema, nothing refers to teacherSchoolAssociation)
    And I should not see a warning log file created
    And I re-execute saved query "teacherSectionAssociation" to get "0" records
    And I see that collections counts have changed as follows in tenant "Midgar"
      | collection                 |delta|
      |teacherSectionAssociation   |   -1|
      |recordHash                  |   -1|