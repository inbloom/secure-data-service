@RALLY_US5180

Feature: Safe Deletion and Cascading Deletion

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Delete Staff with cascade
    Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And the "Midgar" tenant db is empty
    When the data from "test/features/ingestion/test_data/delete_fixture_data/" is imported
#    And I post "BroadSetOfTypes.zip" file as the payload of the ingestion job
#    When zip file is scp to ingestion landing zone
#    Then a batch job for file "BroadSetOfTypes.zip" is completed in database
#    And a batch job log has been created
#    And I should not see an error log file created
#    And I should not see a warning log file created
  And I should see child entities of entityType "staff" with id "3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id" in the "Midgar" database
    Then there exist "1" "staff" records like below in "Midgar" tenant. And I save this query as "StaffQ"
      |field                                        |value                                         |
      |_id                                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineAction" records like below in "Midgar" tenant. And I save this query as "DisciplineActionQ"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "1" "disciplineIncident" records like below in "Midgar" tenant. And I save this query as "DisciplineIncidentQ"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "21" "staffCohortAssociation" records like below in "Midgar" tenant. And I save this query as "StaffCohortAssociationQ"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    Then there exist "13" "staffProgramAssociation" records like below in "Midgar" tenant. And I save this query as "StaffProgramAssociationQ"
      |field                                        |value                                         |
      |body.staffId                                 |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
     Then there exist "1" "staffEducationOrganizationAssociation" records like below in "Midgar" tenant. And I save this query as "StaffEducationOrganizationAssociationQ"
      |field                                        |value                                         |
      |body.staffReference                          |3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id   |
    And I save the collection counts in "Midgar" tenant
    And I post "BroadStaffDelete.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "BroadStaffDelete.zip" is completed in database
    And a batch job log has been created
    And I should see "Processed 1 records." in the resulting batch job file
    And I should not see an error log file created
    And I should not see a warning log file created
    And I should not see "3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id" in the "Midgar" database
    And I re-execute saved query "StaffQ" to get "0" records
    And I re-execute saved query "DisciplineActionQ" to get "0" records
    And I re-execute saved query "DisciplineIncidentQ" to get "0" records
    And I re-execute saved query "StaffCohortAssociationQ" to get "0" records
    And I re-execute saved query "StaffProgramAssociationQ" to get "0" records
    And I re-execute saved query "StaffEducationOrganizationAssociationQ" to get "0" records
  #staff
  #disciplineAction                       maxOccurs="unbounded" minOccurs="0"
  #disciplineIncident                     minOccurs="0"
  #staffCohortAssociation                 maxOccurs="unbounded"
  #staffProgramAssociation                maxOccurs="unbounded"
  #staffEducationOrganizationAssociation
    And I see that collections counts have changed as follows in tenant "Midgar"
      |collection                             |delta  |
      |staff                                  |   -1  |
      |disciplineAction                       |    0  |
      |disciplineIncident                     |    0  |
      |staffCohortAssociation                 |   -21 |
      |staffProgramAssociation                |   -13 |
      |staffEducationOrganizationAssociation  |   -1  |
  #  |recordHash               |   -36 | 1 + 21 + 13 + 1
    And I should not see "3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id" in the "Midgar" database
    And I should not see any entity mandatorily referring to "3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id" in the "Midgar" database
    And I should see entities optionally referring to "3a780cebc8f98982f9b7a5d548fecff42ed8f2f1_id" be updated in the "Midgar" database
