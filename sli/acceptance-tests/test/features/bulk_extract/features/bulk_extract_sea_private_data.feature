Feature: As an bulk extract user, I want to be able to get the state public entities

Scenario: As an bulk extract user, I want to initialize my database with test data.
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "SEAFullDataset.zip" file as the payload of the ingestion job
    And all collections are empty
    When zip file is scp to ingestion landing zone
    And a batch job for file "SEAFullDataset.zip" is completed in database
    And a batch job log has been created

 Scenario: I trigger a full bulk extract, and verify the SEA file has the correct private data
  Then I trigger a bulk extract
  #Given I am a valid 'service' user with an authorized long-lived token "-------"
  When I fetch the path to and decrypt the LEA data extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4" and edorg with id "b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id"
  And I verify that an extract tar file was created for the tenant "Midgar"
   Then the extract contains a file for each of the following entities:
     | entityType                            |
     | staff                                 |
     | staffCohortAssociation                |
     | staffEducationOrganizationAssociation |
     | staffProgramAssociation               |
   And I check that the "staff" extract for "IL" has "3" records
   And I verify this "staff" file should contain:
     | id                                          | condition          |
     | e4320d0bef725998faa8579a987ada80f254e7be_id | entityType = staff |
   And I verify this "staff" file should not contain:
     | id                                          |
     #cgray - Teacher at Daybreak Central High
     | b49545f9d443dfbf93358851c903a9923f6af4dd_id |
     #sbantu - Staff at IL-DAYBREAK
     | 72ceaa42bae51d1c066141f4874567fccc7e8fdc_id |
   And I check that the "staffEducationOrganizationAssociation" extract for "IL" has "3" records
   And I verify this "staffEducationOrganizationAssociation" file should contain:
     | id                                          | condition          |
     | 28da81592931f98be1708f72249e9c99a6a0157e_id | entityType = staffEducationOrganizationAssociation |
   And I verify this "staffEducationOrganizationAssociation" file should not contain:
     | id                                          |
     #cgray - Teacher at Daybreak Central High
     | c38c41cbb2a2f6b28f951ea7ba0fe054185fbdb1_id |
     #sbantu - Staff at IL-DAYBREAK
     | 7b824815fb67933529d518324141549c36da9602_id |
   And I check that the "staffProgramAssociation" extract for "IL" has "1" records
   And I verify this "staffProgramAssociation" file should contain:
     | id                                          | condition          |
     | 5c39f4b8dd9bff032a7e0e521f466a69e49ce692_id | entityType = staffProgramAssociation |
   And I verify this "staffProgramAssociation" file should not contain:
     | id                                          |
     #sbantu - Staff at IL-DAYBREAK
     | 5ba2fba497636aea8b67918e7094234ad3cc5113_id |
   And I check that the "staffCohortAssociation" extract for "IL" has "1" records
   And I verify this "staffCohortAssociation" file should contain:
     | id                                          | condition          |
     | e96584bde89532285403ac9c55e662ad2a69e0fb_id | entityType = staffCohortAssociation |
   And I verify this "staffProgramAssociation" file should not contain:
     | id                                          |
     #sbantu - Staff at IL-DAYBREAK
     | 77d027fa7ebb00aac5b2887c9ffc2f1a19b8d8cd_id |

  Scenario: As an bulk extract user, I want to initialize my database with test data.
    Given I clean the bulk extract file system and database
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "SEAFullDatasetDelta.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "SEAFullDatasetDelta.zip" is completed in database
    And a batch job log has been created

  Scenario: I trigger a full bulk extract, and verify the SEA file has the correct private data
    Then I trigger a delta extract

  #Given I am a valid 'service' user with an authorized long-lived token "-------"
    And I request the latest bulk extract delta using the api
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | staff                                 |
      | staffCohortAssociation                |
      | staffEducationOrganizationAssociation |
      | staffProgramAssociation               |
    And I check that the "staff" extract for "IL" has "3" records
    And I verify this "staff" file should contain:
      | id                                          | condition          |
      | e4320d0bef725998faa8579a987ada80f254e7be_id | entityType = staff |
    And I verify this "staff" file should not contain:
      | id                                          |
      #cgray - Teacher at Daybreak Central High
      | b49545f9d443dfbf93358851c903a9923f6af4dd_id |
      #sbantu - Staff at IL-DAYBREAK
      | 72ceaa42bae51d1c066141f4874567fccc7e8fdc_id |
    And I check that the "staffEducationOrganizationAssociation" extract for "IL" has "3" records
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition          |
      | 28da81592931f98be1708f72249e9c99a6a0157e_id | entityType = staffEducationOrganizationAssociation |
    And I verify this "staffEducationOrganizationAssociation" file should not contain:
      | id                                          |
      #cgray - Teacher at Daybreak Central High
      | c38c41cbb2a2f6b28f951ea7ba0fe054185fbdb1_id |
      #sbantu - Staff at IL-DAYBREAK
      | 7b824815fb67933529d518324141549c36da9602_id |
    And I check that the "staffProgramAssociation" extract for "IL" has "1" records
    And I verify this "staffProgramAssociation" file should contain:
      | id                                          | condition          |
      | 5c39f4b8dd9bff032a7e0e521f466a69e49ce692_id | entityType = staffProgramAssociation |
    And I verify this "staffProgramAssociation" file should not contain:
      | id                                          |
      #sbantu - Staff at IL-DAYBREAK
      | 5ba2fba497636aea8b67918e7094234ad3cc5113_id |
    And I check that the "staffCohortAssociation" extract for "IL" has "1" records
    And I verify this "staffCohortAssociation" file should contain:
      | id                                          | condition          |
      | e96584bde89532285403ac9c55e662ad2a69e0fb_id | entityType = staffCohortAssociation |
    And I verify this "staffProgramAssociation" file should not contain:
      | id                                          |
      #sbantu - Staff at IL-DAYBREAK
      | 77d027fa7ebb00aac5b2887c9ffc2f1a19b8d8cd_id |

