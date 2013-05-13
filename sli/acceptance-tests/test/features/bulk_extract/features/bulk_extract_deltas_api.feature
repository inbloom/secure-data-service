Feature: Retrived through the api a generated delta bulk extract file, and validate the file

Scenario: Initialize security trust store for Bulk Extract application and LEAs
  Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased

Scenario: Generate a bulk extract delta after day 0 ingestion
  When I trigger a delta extract
   And I request the latest bulk extract delta using the api
   And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>"
  Then I should see "8" bulk extract files
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  Then The "educationOrganization" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api
   And The "section" delta was extracted in the same format as the api
   And The "gradebookEntry" delta was extracted in the same format as the api
   And The "studentSectionAssociation" delta was extracted in the same format as the api
   And The "staff" delta was extracted in the same format as the api
   And The "teacher" delta was extracted in the same format as the api
   And The "teacherSectionAssociation" delta was extracted in the same format as the api
   And The "teacherSchoolAssociation" delta was extracted in the same format as the api
   And The "staffEducationOrganizationAssociation" delta was extracted in the same format as the api
   And The "grade" delta was extracted in the same format as the api
   And The "reportCard" delta was extracted in the same format as the api
   And The "studentAcademicRecord" delta was extracted in the same format as the api
   And The "student" delta was extracted in the same format as the api
   And The "studentSchoolAssociation" delta was extracted in the same format as the api
   And The "studentAssessment" delta was extracted in the same format as the api
   And The "studentGradebookEntry" delta was extracted in the same format as the api
   And The "cohort" delta was extracted in the same format as the api
   And The "studentCohortAssociation" delta was extracted in the same format as the api
   And The "staffCohortAssociation" delta was extracted in the same format as the api

Scenario: Triggering deltas via ingestion
  All entities belong to lea1 which is IL-DAYBREAK, we should only see a delta file for lea1
  and only a delete file is generated for lea2.
  Updated two students, 1 and 12, 12 lost contextual resolution to LEA1, so it should not appear
  in the extract file.  
  1 is added to LEA2, so its stuff should go in there as well
Given I clean the bulk extract file system and database
  And I am using local data store
  And I ingest "bulk_extract_deltas.zip"
  When I trigger a delta extract
     And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar" 
     And I verify "2" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar" 
     When I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  student                               |
       |  studentSchoolAssociation              |
       |  studentAssessment                     |
       |  studentGradebookEntry                 |
       |  studentParentAssociation              |
       |  parent                                |
       |  school                                |
       |  section                               |
       |  educationOrganization                 |
       |  staff                                 |
       |  staffEducationOrganizationAssociation |
       |  teacher                               |
       |  teacherSchoolAssociation              |
       |  deleted                               |
     And I verify this "deleted" file should contains:
       | id                                                                                     | condition                             |
       | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id                                            | entityType = student                  |
       | 8a0a6c0791cd0656d07278b0813d113065c6426f_id                                            | entityType = studentSchoolAssociation |
       | 1b4aa93f01d11ad51072f3992583861ed080f15c_id                                            | entityType = parent                   |
       | 908404e876dd56458385667fa383509035cd4312_idd14e4387521c768830def2c9dea95dd0bf7f8f9b_id | entityType = studentParentAssociation |

     # Teacher 03 and related entities should be in both DAYBREAk and HIGHWIND
     And I verify this "teacher" file should contains:
       | id                                          | condition                                |
       | cab9d548be3e51adf6ac00a4028e4f9f4f9e9cae_id | staffUniqueStateId = tech-0000000003     |
     And I verify this "teacherSchoolAssociation" file should contains:
       | id                                          | condition                                |
       | c063086ce77b13c4e593ff8261024a6ef30e0a8d_id | teacherId = cab9d548be3e51adf6ac00a4028e4f9f4f9e9cae_id |

     # Teacher 01 should NOT be in HIGHWIND
     And I verify this "teacher" file should not contains:
       | id                                          | condition                                |
       | fe472294f0e40fd428b1a67b9765360004562bab_id |                                          |

     # staff 04 should be in both DAYBREAK and HIGHWIND
     And I verify this "staff" file should contains:
       | id                                          | condition                                |
       | b7beb5d73c2189c680e16826e2e57d4d71970181_id | staffUniqueStateId = stff-0000000004     |

     # Student 1 was in this section, should receive delta for it
     And I verify this "section" file should contains:
       | id                                          | condition                                |
       | 95cc5d67f3b653eb3e2f0641c429cf2006dc2646_id | uniqueSectionCode = 2                    |

     And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  parent                                |
       |  section                               |
       |  student                               |
       |  studentAssessment                     | 
       |  studentGradebookEntry                 |
       |  studentSchoolAssociation              | 
       |  studentParentAssociation              |
       |  staff                                 |
       |  staffEducationOrganizationAssociation |
       |  teacher                               |
       |  teacherSchoolAssociation              |
       |  teacherSectionAssociation             |
       |  deleted                               |
   
     And I verify this "deleted" file should contains:
       | id                                          | condition                                |
       | 8a0a6c0791cd0656d07278b0813d113065c6426f_id | entityType = studentSchoolAssociation |
       | 1b4aa93f01d11ad51072f3992583861ed080f15c_id | entityType = parent                      |
       | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id | entityType = student                     |
       | 908404e876dd56458385667fa383509035cd4312_idd14e4387521c768830def2c9dea95dd0bf7f8f9b_id | entityType = studentParentAssociation    |

     And I verify this "student" file should contains: 
       #this is student 11, which has updated information
       | id                                          | condition                                |
       | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id | studentUniqueStateId = 11                | 
     And I verify this "student" file should not contains: 
       #this is student 12, which has updated information, but we cut his tie with any schools
       | id                                          | condition                                |
       | 609640f6af263faad3a0cbee2cbe718fb71b9ab2_id |                                          | 

     And I verify this "studentSchoolAssociation" file should contains:
       #updated association for student 11 
       | id                                          | condition                                |
       | 68c4855bf0bdcc850a883d88fdf953b9657fe255_id | exitWithdrawDate = 2014-05-31            |
     And I verify this "studentSchoolAssociation" file should not contains:
       #this is an expired association, should not show up
       | id                                          | condition                                |
       | a13489364c2eb015c219172d561c62350f0453f3_id |                                          |

     And I verify this "studentGradebookEntry" file should contains:
       | id                                          | condition                                |
       | 6620fcd37d1095005a67dc330e591279577aede7_id | letterGradeEarned = A                    |

     And I verify this "studentAssessment" file should contains:
       | id                                          | condition                                |
       | 065f155b876c2dc15b6b319fa6f23834d05115b7_id | scoreResults.result = 92                 |

     And I verify this "parent" file should contains:
       | id                                          | condition                                                    |
       | 833c746641212c9e6e0fe5831f03570882c7bba1_id | electronicMail.emailAddress = roosevelt_mcgowan@fakemail.com |

     And I verify this "studentParentAssociation" file should contains:
       | id                                          | condition                                |
       | 908404e876dd56458385667fa383509035cd4312_id6ac27714bca705efbd6fd0eb6c0fd2c7317062e6_id | contactPriority = 0 |
  
     And I verify this "section" file should contains:
       | id                                          | condition                                |
       | 95cc5d67f3b653eb3e2f0641c429cf2006dc2646_id | uniqueSectionCode = 2                    |
  
     # Both Teacher 01 and 03 should be in DAYBREAk
     And I verify this "teacher" file should contains:
       | id                                          | condition                                |
       | cab9d548be3e51adf6ac00a4028e4f9f4f9e9cae_id | staffUniqueStateId = tech-0000000003     |
       | fe472294f0e40fd428b1a67b9765360004562bab_id | staffUniqueStateId = tech-0000000001     |
     And I verify this "teacherSchoolAssociation" file should contains:
       | id                                          | condition                                |
       | c063086ce77b13c4e593ff8261024a6ef30e0a8d_id | teacherId = cab9d548be3e51adf6ac00a4028e4f9f4f9e9cae_id |

     # staff 04 should be in both DAYBREAK and HIGHWIND
     And I verify this "staff" file should contains:
       | id                                          | condition                                |
       | b7beb5d73c2189c680e16826e2e57d4d71970181_id | staffUniqueStateId = stff-0000000004     |

Scenario: Generate a bulk extract in a different LEA
  Given I clean the bulk extract file system and database
    And I am using local data store
    And I post "deltas_new_edorg_wrong_lea.zip" file as the payload of the ingestion job

  When the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
   And zip file is scp to ingestion landing zone
   And a batch job for file "deltas_new_edorg_wrong_lea.zip" is completed in database
   And a batch job log has been created 
  Then I should not see an error log file created
   And I should not see a warning log file created

  When I trigger a delta extract
   #And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
   And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
   And I should receive a return code of 200
   #And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "<app id>" for "<IL-HIGHWIND>"
   And I download and decrypt the delta
   Then I should see "2" bulk extract files
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And The "educationOrganization" delta was extracted in the same format as the api
   And The "educationOrganization" entity with id "<ed_org_to_lea2_id>" should belong to LEA with id "<IL-HIGHWIND>" 

Scenario: Ingest education organization and perform delta   
  Given I clean the bulk extract file system and database
    And I am using local data store
    And I post "deltas_new_edorg.zip" file as the payload of the ingestion job

  When the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
   And zip file is scp to ingestion landing zone
   And a batch job for file "deltas_new_edorg.zip" is completed in database
   And a batch job log has been created 
  Then I should not see an error log file created
   And I should not see a warning log file created

  When I trigger a delta extract
   And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>"
   Then I should see "2" bulk extract files
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  Then The "educationOrganization" delta was extracted in the same format as the api
   And The "school" delta was extracted in the same format as the api

  #Combined moving school across LEA boundary scenario to test genetate a delta after another delta and 
  #delta should only pick up latest change without clean bulk extract file system and database step
 
  #The expected behavior is that the old LEA that school used to belong to would receive an delete file, and
  #the new LEA would only receive a update file since the delete event is not applicable to the new LEA
  And I ingested "deltas_move_between_edorg.zip" dataset
    When I trigger a delta extract
      And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "22c2a28d-7327-4444-8ff9-caad4b1c7aa3" for "<IL-HIGHWIND>"
      And I verify "4" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for LEA "<10 School District>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for LEA "<11 School District>" in "Midgar"
      # should see no delete file for lea 1
      #And I verify the last delta bulk extract by app "22c2a28d-7327-4444-8ff9-caad4b1c7aa3" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
      And the extract contains a file for each of the following entities:
        |  entityType                            |
        |  school                                |
        |  educationOrganization                 |
      And I verify this "school" file should contains:
          | id                                          | condition                             |
          | 54b4b51377cd941675958e6e81dce69df801bfe8_id | stateOrganizationId=Daybreak Podunk High |
      
      # should only see delete file for lea 2
      And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
      And I verify this "deleted" file should contains:
          | id                                          | condition                             |
          | 54b4b51377cd941675958e6e81dce69df801bfe8_id | entityType = school                   |

Scenario: Ingest SEA update and verify no deltas generated
  Given I clean the bulk extract file system and database
    And I am using local data store
    And I post "deltas_update_sea.zip" file as the payload of the ingestion job

  When the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
   And zip file is scp to ingestion landing zone
   And a batch job for file "deltas_update_sea.zip" is completed in database
   And a batch job log has been created 
  Then I should not see an error log file created
   And I should not see a warning log file created

  When I trigger a delta extract
  Then there should be no deltas in mongo

Scenario: Ingest SEA delete and verify both LEAs received the delete
  Given I clean the bulk extract file system and database
    And I ingested "deltas_delete_sea.zip" dataset
  # We will see a warning file on cascading delete -- there are a lot of children of this SEA
    When I trigger a delta extract
      And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar" 
      And I verify "2" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar" 
      And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
      And I verify this "deleted" file should contains:
          | id                                          | condition                             |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = educationOrganization    |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = school                   |

      And I verify the last delta bulk extract by app "22c2a28d-7327-4444-8ff9-caad4b1c7aa3" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
      And I verify this "deleted" file should contains:
          | id                                          | condition                             |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = educationOrganization    |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = school                   |

    Then I reingest the SEA so I can continue my other tests

Scenario: Create a new education organization through the API and perform delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/vnd.slc+json"
 When I POST a "newEducationOrganization" of type "educationOrganization"
 Then I should receive a return code of 201   
 When I trigger a delta extract
  And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>"
 Then I should see "2" bulk extract files
  And The "educationOrganization" delta was extracted in the same format as the api

Scenario: Update an existing education organization through the API and perform delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"   
 When I PUT the "postalCode" for a "school" entity to "11012"
 Then I should receive a return code of 204
 When I trigger a delta extract
  And I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-DAYBREAK>" with appId "<app id>" clientId "<client id>"
  And I should receive a return code of 200
  And I download and decrypt the delta
 Then I should see "2" bulk extract files
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And The "educationOrganization" delta was extracted in the same format as the api

Scenario: Update an existing education organization through the API and perform delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"   
 When I PUT the "postalCode" for a "school" entity to "11012"
 Then I should receive a return code of 204
 When I trigger a delta extract

Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
  And in my list of rights I have BULK_EXTRACT
  When I make a call to the bulk extract end point "/v1.1/bulk/extract/list"
  When I get back a response code of "200"
  When I store the URL for the latest delta for LEA "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
  When the number of returned URLs is correct:
  |   fieldName  | count |
  |   fullLeas   |  0    |
  |   deltaLeas  |  1    |
  When I request listed delta via API for "19cca28d-7357-4044-8df9-caad4b1c8ee4"
  Then I should receive a return code of 200
  #And I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I download and decrypt the delta
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
  |  entityType                            |
  |  educationOrganization                 |
  |  school                                |
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And The "educationOrganization" delta was extracted in the same format as the api
  And The "school" delta was extracted in the same format as the api

Scenario: Update an existing edOrg with invalid API call, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"     
 When I PUT the "missingEntity" for a "school" entity to "WHOOPS"
 Then I should receive a return code of 404
  And deltas collection should have "0" records

Scenario: Create an invalid edOrg with the API, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/vnd.slc+json"
 When I POST a "invalidEducationOrganization" of type "educationOrganization"
 Then I should receive a return code of 403   
  And deltas collection should have "0" records

Scenario: As SEA Admin, delete an existing school with API call, verify delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 When I DELETE an "orphanEdorg" of id "54b4b51377cd941675958e6e81dce69df801bfe8_id" 
 Then I should receive a return code of 204
 When I trigger a delta extract
  And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar"
  And I verify "2" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar"

Scenario: Patch an existing school with API call, verify delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"  
 When I PATCH the "postalCode" for a "patchEdOrg" entity to "11099"
 Then I should receive a return code of 204
 When I trigger a delta extract
  And I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-DAYBREAK>" with appId "<app id>" clientId "<client id>"
  And I should receive a return code of 200
  And I download and decrypt the delta
 Then I should see "2" bulk extract files
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And The "educationOrganization" delta was extracted in the same format as the api

Scenario: CREATE and verify deltas for private entities through API POST
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 # CREATE parent entity via POST
 When I POST a "newMinStudent" of type "staffStudent"
 Then I should receive a return code of 201
 When I POST a "newStudentSchoolAssociation" of type "studentSchoolAssociation"
 Then I should receive a return code of 201
 When I POST a "newParentFather" of type "parent"
 Then I should receive a return code of 201
 When I POST a "newStudentFatherAssociation" of type "studentParentAssociation"
 Then I should receive a return code of 201
 When I POST a "newParentMother" of type "parent"
 Then I should receive a return code of 201
 When I POST a "newStudentMotherAssociation" of type "studentParentAssociation"
 Then I should receive a return code of 201
 When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar"
  And I verify "0" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar" 
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
 Then The "student" delta was extracted in the same format as the api
  And The "studentSchoolAssociation" delta was extracted in the same format as the api
  And The "parent" delta was extracted in the same format as the api
  And The "studentParentAssociation" delta was extracted in the same format as the api

 Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 # UPDATE/UPSERT parent entity via PUT
 When I PUT the "loginId" for a "newStudent" entity to "super_student_you_rock@bazinga.com"
 Then I should receive a return code of 204
 When I PUT the "loginId" for a "newParentMom" entity to "super_mom_you_rock@bazinga.com"
 Then I should receive a return code of 204
 When I PUT the "loginId" for a "newParentDad" entity to "super_dad_good_job@bazinga.com"
 Then I should receive a return code of 204
 When I PUT the "contactPriority" for a "newStudentParentAssociation" entity to "1"
 Then I should receive a return code of 204
 When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar"
  And I verify "0" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar" 
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And The "student" delta was extracted in the same format as the api
  And The "parent" delta was extracted in the same format as the api
  And The "studentParentAssociation" delta was extracted in the same format as the api

 # UPDATE parent and parentStudentAssociation fields via PATCH 
 Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 When I PATCH the "studentLoginId" for a "newStudent" entity to "average_student_youre_ok@bazinga.com"
 Then I should receive a return code of 204
 When I PATCH the "momLoginId" for a "newParentMom" entity to "average_mom_youre_ok@bazinga.com"
 Then I should receive a return code of 204
 When I PATCH the "dadLoginId" for a "newParentDad" entity to "average_dad_youre_ok@bazinga.com"
 Then I should receive a return code of 204
 When I PATCH the "contactPriority" for a "newStudentParentAssociation" entity to "1"
 Then I should receive a return code of 204
 When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar"
  And I verify "0" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar" 
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And The "student" delta was extracted in the same format as the api
  And The "parent" delta was extracted in the same format as the api
  And The "studentParentAssociation" delta was extracted in the same format as the api

 # DELETE entities
 Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 When I DELETE an "newStudent" of id "9bf3036428c40861238fdc820568fde53e658d88_id"
 Then I should receive a return code of 204
 When I DELETE an "newParentDad" of id "41f42690a7c8eb5b99637fade00fc72f599dab07_id"
 Then I should receive a return code of 204
 When I DELETE an "newParentMom" of id "41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id" 
 Then I should receive a return code of 204
 When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I verify this "deleted" file should contains:
        | id                                          | condition                             |
        | 9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                  |
        | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | entityType = parent                   |
        | 41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id | entityType = parent                   |
        | bc3588737cb477e9f721421104b783179887fbdb_id | entityType = studentSchoolAssociation |
        | 9bf3036428c40861238fdc820568fde53e658d88_idc3a6a4ed285c14f562f0e0b63e1357e061e337c6_id | entityType = studentParentAssociation |
        | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | entityType = studentParentAssociation |

Scenario: Be a good neighbor and clean up before you leave
    Given I clean the bulk extract file system and database
