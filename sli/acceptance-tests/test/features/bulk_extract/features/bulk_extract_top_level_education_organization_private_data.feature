Feature: As an bulk extract user, I want to be able to get the state public entities

Scenario: As an bulk extract user, I want to initialize my database with test data.
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "TopLevelFullDataset.zip" file as the payload of the ingestion job
    And all collections are empty
    When zip file is scp to ingestion landing zone
    And a batch job for file "TopLevelFullDataset.zip" is completed in database
    And a batch job log has been created
    And all edorgs in "Midgar" are authorized for "SDK Sample"

 Scenario: I trigger a full bulk extract, and verify the top level edorg file has the correct private data
  Then I trigger a bulk extract
  When I fetch the path to and decrypt the edorg data extract file for the tenant "Midgar" and application with id "<app id>" and edorg with id "<IL>"
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

   Scenario: Valid bulk extract user at the top level edorg can retrieve the top level edorg full private extract via the API
     Given I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
     When I make a full bulk extract API call for edorg "<IL>"
     And the return code is 200 I get expected tar downloaded
     Then I check the http response headers
     When I decrypt and save the full extract
     And I verify that an extract tar file was created for the tenant "Midgar"
     Then the extract contains a file for each of the following entities:
       | entityType                            |
       | staff                                 |
       | staffCohortAssociation                |
       | staffEducationOrganizationAssociation |
       | staffProgramAssociation               |

  Scenario: Valid bulk extract user not at the top level edorg cannot retrieve the top level edorg full private extract
    Given I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a full bulk extract API call for edorg "<IL>"
    And I should receive a return code of 403

  Scenario: I trigger a full bulk extract on an education organization that loses its parent
    Given I clean the bulk extract file system and database
    When I remove the parent for the education organization "<IL-DAYBREAK>" for tenant "Midgar"
    Then I trigger a bulk extract
    When I fetch the path to and decrypt the edorg data extract file for the tenant "Midgar" and application with id "<app id>" and edorg with id "<IL-DAYBREAK>"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | staff                                 |
      | staffEducationOrganizationAssociation |
      | staffProgramAssociation               |
    And I check that the "staff" extract for "IL-DAYBREAK" has "4" records
    And I verify this "staff" file should contain:
      | id                                          | condition          |
      | 72ceaa42bae51d1c066141f4874567fccc7e8fdc_id | entityType = staff |
    And I verify this "staff" file should not contain:
      | id                                          |
      #cgray - Teacher at Daybreak Central High
      | b49545f9d443dfbf93358851c903a9923f6af4dd_id |
    And I check that the "staffEducationOrganizationAssociation" extract for "IL-DAYBREAK" has "4" records
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition          |
      | 7b824815fb67933529d518324141549c36da9602_id | entityType = staffEducationOrganizationAssociation |
    And I verify this "staffEducationOrganizationAssociation" file should not contain:
      | id                                          |
      #cgray - Teacher at Daybreak Central High
      | c38c41cbb2a2f6b28f951ea7ba0fe054185fbdb1_id |

  Scenario: Trigger a full bulk extract on an edorg that gains a parent, it contains its childrens data
    When I set the parent for the education organization "<IL-DAYBREAK>" to "<IL>" for tenant "Midgar"
    Given I clean the bulk extract file system and database
    Then I trigger a bulk extract
    When I fetch the path to and decrypt the edorg data extract file for the tenant "Midgar" and application with id "<app id>" and edorg with id "<IL-DAYBREAK>"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | attendance                            |
      | grade                                 |
      | reportCard                            |
      | student                               |
      | studentAcademicRecord                 |
      | studentAssessment                     |
      | studentCohortAssociation              |
      | studentDisciplineIncidentAssociation  |
      | studentGradebookEntry                 |
      | studentParentAssociation              |
      | studentProgramAssociation             |
      | studentSchoolAssociation              |
      | studentSectionAssociation             |
      | courseTranscript                      |
      | gradebookEntry                        |
      | disciplineIncident                    |
      | parent                                |
      | studentCompetency                     |
      | staff                                 |
      | staffEducationOrganizationAssociation |
      | teacher                               |
      | staffProgramAssociation               |
      | teacherSectionAssociation             |
      | teacherSchoolAssociation              |

  Scenario: I trigger a full bulk extract on a school that loses its parent (thereby becoming an orphan education organization)
    Given I clean the bulk extract file system and database
    When I remove the parent for the education organization "<South Daybreak Elementary>" for tenant "Midgar"
    Then I trigger a bulk extract
    When I fetch the path to and decrypt the edorg data extract file for the tenant "Midgar" and application with id "<app id>" and edorg with id "<South Daybreak Elementary>"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | attendance                            |
      | grade                                 |
      | reportCard                            |
      | student                               |
      | studentAcademicRecord                 |
      | studentAssessment                     |
      | studentCohortAssociation              |
      | studentGradebookEntry                 |
      | studentParentAssociation              |
      | studentProgramAssociation             |
      | studentSchoolAssociation              |
      | studentSectionAssociation             |
      | gradebookEntry                        |
      | disciplineAction                      |
      | parent                                |
      | studentCompetency                     |
      | staff                                 |
      | staffEducationOrganizationAssociation |
      | teacher                               |
      | staffCohortAssociation                |
      | teacherSectionAssociation             |
      | teacherSchoolAssociation              |
    And I verify this "student" file should contain:
      | id                                          | condition            |
      | c53b9737c4a9ff3cedaf8cae5555c4979b305ce8_id | entityType = student |
      #Past student
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id | entityType = student |
    And I verify this "student" file should not contain:
      | id                                          |
      | 9b38ee8562b14f3201aff4995bac9bbafc3336a0_id |
    And I verify this "studentCohortAssociation" file should contain:
      | id                                          | condition          |
      | c53b9737c4a9ff3cedaf8cae5555c4979b305ce8_id965bfacba57c3c23e23748dbf3494327fb9a58d3_id | entityType = studentCohortAssociation |
    And I verify this "studentCohortAssociation" file should not contain:
      | id                                          |
      | 9b38ee8562b14f3201aff4995bac9bbafc3336a0_id72ad306c9e6f8001818cda5ab49be0898a942caf_id |
    And I verify this "attendance" file should contain:
      | id                                          | condition            |
      | 6bebcf18d42e6ba84854ece88bf0152dc1dbd9e8_id | entityType = attendance |
    And I verify this "attendance" file should not contain:
      | id                                          |
      #Future student data
      | edf97a93c49127da4bad837450229966f500f2d2_id |
      | 94048d530feebc50195ce1f9685107a92d715f22_id |
    And I verify this "studentSchoolAssociation" file should contain:
      | id                                          | condition            |
      #Past data
      | 2addec0409825e6bdd0853b7aef34cb15d8dd45a_id | entityType = studentSchoolAssociation |

  Scenario: As an bulk extract user, I want to initialize my database with test data.
    Given I clean the bulk extract file system and database
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "TopLevelDeltaDataset.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "TopLevelDeltaDataset.zip" is completed in database
    And a batch job log has been created
    And I post "DeleteMattSollarsMom.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "DeleteMattSollarsMom.zip" is completed in database
    And a batch job log has been created

  Scenario: I trigger a delta bulk extract, and verify the top level edorg file has the correct private data
    Then I trigger a delta extract
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "<app id>" for "<IL>"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | staff                                 |
      | staffCohortAssociation                |
      | staffEducationOrganizationAssociation |
      | staffProgramAssociation               |
      | deleted                               |
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
    And I check that the "deleted" extract for "IL" has "1" records
    And I verify this "deleted" file should contain:
      | id                                          | condition          |
      | 067198fd6da91e1aa8d67e28e850f224d6851713_id6435d34a6aeaec26b49e2f31d79674b43ce1e140_id | entityType = studentParentAssociation |

  Scenario: Valid bulk extract user at the top level edorg can retrieve the top level edorg delta private extract via the API
    Given I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I request latest delta via API for tenant "Midgar", lea "<IL>" with appId "<app id>" clientId "<client id>"
    And I should receive a return code of 200
    And I download and decrypt the delta
    Then I verify the last delta bulk extract by app "<app id>" for "<IL>" in "Midgar" contains a file for each of the following entities:
      | entityType                            |
      | staff                                 |
      | staffCohortAssociation                |
      | staffEducationOrganizationAssociation |
      | staffProgramAssociation               |
      | deleted                               |

  Scenario: Valid bulk extract user not at the top level edorg cannot retrieve the top level edorg delta private extract
    Given I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I request latest delta via API for tenant "Midgar", lea "<IL>" with appId "<app id>" clientId "<client id>"
    And I should receive a return code of 403

  Scenario: As an bulk extract user, I want to initialize my database with test data.
    Given I clean the bulk extract file system and database
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "TopLevelDeltaDataset.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "TopLevelDeltaDataset.zip" is completed in database
    And a batch job log has been created

  Scenario: I trigger a delta bulk extract on an education organization that loses its parent
    When I remove the parent for the education organization "<IL-DAYBREAK>" for tenant "Midgar"
    Then I trigger a delta extract
  #Given I am a valid 'service' user with an authorized long-lived token "-------"
    And I request the latest bulk extract delta using the api
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "<app id>" for "<IL-DAYBREAK>"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | staff                                 |
      | staffEducationOrganizationAssociation |
      | staffProgramAssociation               |
    And I check that the "staff" extract for "IL-DAYBREAK" has "4" records
    And I verify this "staff" file should contain:
      | id                                          | condition          |
      #sbantu - Staff at IL-DAYBREAK
      | 72ceaa42bae51d1c066141f4874567fccc7e8fdc_id | entityType = staff |
    And I verify this "staff" file should not contain:
      | id                                          |
      #cgray - Teacher at Daybreak Central High
      | b49545f9d443dfbf93358851c903a9923f6af4dd_id |
      #rbraverman - Teacher at South Daybreak Elementary (now orphaned)
      | 8107c5ce31cec58d4ac0b647e91b786b03091f02_id |
    And I check that the "staffEducationOrganizationAssociation" extract for "IL-DAYBREAK" has "4" records
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition          |
      #sbantu - Staff at IL-DAYBREAK
      | 7b824815fb67933529d518324141549c36da9602_id | entityType = staffEducationOrganizationAssociation |
    And I verify this "staffEducationOrganizationAssociation" file should not contain:
      | id                                          |
      #cgray - Teacher at Daybreak Central High
      | c38c41cbb2a2f6b28f951ea7ba0fe054185fbdb1_id |
      #rbraverman - Teacher at South Daybreak Elementary (now orphaned)
      | f026e95f4c316533ce7945971cdda0bbb1c41386_id |

  Scenario: Trigger a full bulk extract on an edorg that gains a parent, it contains its childrens data
    When I set the parent for the education organization "<IL-DAYBREAK>" to "<IL>" for tenant "Midgar"
    Given I clean the bulk extract file system and database
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "TopLevelDeltaDataset.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "TopLevelDeltaDataset.zip" is completed in database
    And a batch job log has been created
    Then I trigger a delta extract
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "<app id>" for "<IL-DAYBREAK>"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | staff                                 |
      | staffEducationOrganizationAssociation |
      | staffProgramAssociation               |
      | teacher                               |
      | studentCohortAssociation              |
      | teacherSchoolAssociation              |
      | teacherSectionAssociation             |
    And I verify this "staff" file should contain:
      | id                                          | condition          |
      #sbantu - Staff at IL-DAYBREAK
      | 72ceaa42bae51d1c066141f4874567fccc7e8fdc_id | entityType = staff |
      #cgray - Teacher at Daybreak Central High
      | b49545f9d443dfbf93358851c903a9923f6af4dd_id | entityType = staff |
    And I verify this "staff" file should not contain:
      | id                                          |
      #rbraverman - Teacher at South Daybreak Elementary (now orphaned)
      | 8107c5ce31cec58d4ac0b647e91b786b03091f02_id |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition          |
      #sbantu - Staff at IL-DAYBREAK
      | 7b824815fb67933529d518324141549c36da9602_id | entityType = staffEducationOrganizationAssociation |
      #cgray - Teacher at Daybreak Central High
      | c38c41cbb2a2f6b28f951ea7ba0fe054185fbdb1_id | entityType = staffEducationOrganizationAssociation |
    And I verify this "staffEducationOrganizationAssociation" file should not contain:
      | id                                          |
      #rbraverman - Teacher at South Daybreak Elementary (now orphaned)
      | f026e95f4c316533ce7945971cdda0bbb1c41386_id |

    #Orphaned School should only have data for itself
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "<app id>" for "<South Daybreak Elementary>"
    And I verify that an extract tar file was created for the tenant "Midgar"
    Then the extract contains a file for each of the following entities:
      | entityType                            |
      | staff                                 |
      | staffCohortAssociation                |
      | staffEducationOrganizationAssociation |
      | studentCohortAssociation              |
      | teacher                               |
      | teacherSchoolAssociation              |
      | teacherSectionAssociation             |
    And I verify this "staff" file should contain:
      | id                                          | condition           |
      #rbraverman - Teacher at South Daybreak Elementary (now orphaned)
      | 8107c5ce31cec58d4ac0b647e91b786b03091f02_id | entityType = staff  |
    And I verify this "staff" file should not contain:
      | id                                          |
      #sbantu - Staff at IL-DAYBREAK
      | 72ceaa42bae51d1c066141f4874567fccc7e8fdc_id |
      #cgray - Teacher at Daybreak Central High
      | b49545f9d443dfbf93358851c903a9923f6af4dd_id |
    And I verify this "staffEducationOrganizationAssociation" file should contain:
      | id                                          | condition                                           |
      #rbraverman - Teacher at South Daybreak Elementary (now orphaned)
      | f026e95f4c316533ce7945971cdda0bbb1c41386_id | entityType = staffEducationOrganizationAssociation  |
    And I verify this "staffEducationOrganizationAssociation" file should not contain:
      | id                                          |
      #sbantu - Staff at IL-DAYBREAK
      | 7b824815fb67933529d518324141549c36da9602_id |
      #cgray - Teacher at Daybreak Central High
      | c38c41cbb2a2f6b28f951ea7ba0fe054185fbdb1_id |

