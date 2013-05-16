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
   And The "attendance" delta was extracted in the same format as the api
   And The "student" delta was extracted in the same format as the api
   And The "studentSchoolAssociation" delta was extracted in the same format as the api
   And The "studentAssessment" delta was extracted in the same format as the api
   And The "studentGradebookEntry" delta was extracted in the same format as the api
   And The "cohort" delta was extracted in the same format as the api
   And The "studentCohortAssociation" delta was extracted in the same format as the api
   And The "staffCohortAssociation" delta was extracted in the same format as the api
   And The "session" delta was extracted in the same format as the api
   And The "gradingPeriod" delta was extracted in the same format as the api
   And The "courseOffering" delta was extracted in the same format as the api
   And The "course" delta was extracted in the same format as the api
   And The "program" delta was extracted in the same format as the api
   And The "studentProgramAssociation" delta was extracted in the same format as the api
   And The "staffProgramAssociation" delta was extracted in the same format as the api
   And The "studentDisciplineIncidentAssociation" delta was extracted in the same format as the api
   And The "disciplineIncident" delta was extracted in the same format as the api
   And The "disciplineAction" delta was extracted in the same format as the api

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
       |  attendance                            |
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
       |  course                                |
       |  courseOffering                        |
       |  program                               |
       |  graduationPlan                        |
       |  deleted                               |
     And I verify this "deleted" file should contain:
       | id                                                                                     | condition                             |
       | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id                                            | entityType = student                  |
       | 54759a8d56aba10b1b300e66657cd6fcc3ca6ac9_id                                            | entityType = studentSchoolAssociation |
       | 1b4aa93f01d11ad51072f3992583861ed080f15c_id                                            | entityType = parent                   |
       | 908404e876dd56458385667fa383509035cd4312_idd14e4387521c768830def2c9dea95dd0bf7f8f9b_id | entityType = studentParentAssociation |
       | 95147c130335e0656b0d8e9ab79622a22c3a3fab_id                                            | entityType = section                  |

     # Teacher 03 and related entities should be in both DAYBREAk and HIGHWIND
     And I verify this "teacher" file should contain:
       | id                                          | condition                                |
       | cab9d548be3e51adf6ac00a4028e4f9f4f9e9cae_id | staffUniqueStateId = tech-0000000003     |
     And I verify this "teacherSchoolAssociation" file should contain:
       | id                                          | condition                                |
       | c063086ce77b13c4e593ff8261024a6ef30e0a8d_id | teacherId = cab9d548be3e51adf6ac00a4028e4f9f4f9e9cae_id |

     # Teacher 01 should NOT be in HIGHWIND
     And I verify this "teacher" file should not contain:
       | id                                          | condition                                |
       | fe472294f0e40fd428b1a67b9765360004562bab_id |                                          |

     # staff 04 should be in both DAYBREAK and HIGHWIND
     And I verify this "staff" file should contain:
       | id                                          | condition                                |
       | b7beb5d73c2189c680e16826e2e57d4d71970181_id | staffUniqueStateId = stff-0000000004     |

     # Student 1 was in this section, should receive delta for it
     And I verify this "section" file should contain:
       | id                                          | condition                                |
       | 95cc5d67f3b653eb3e2f0641c429cf2006dc2646_id | uniqueSectionCode = 2                    |

    And I verify this "attendance" file should contain:
      | id                                          | condition                                |
      | aefc3c964b1caf4754c8792be9886edaa2f84d4c_id | schoolYearAttendance.attendanceEvent.reason = change_2       |

    And I verify this "attendance" file should not contain:
      | id                                          | condition                                |
      | aefc3c964b1caf4754c8792be9886edaa2f84d4c_id | schoolYearAttendance.attendanceEvent.date = 2013-09-09        |

    # only has section 45 since it followed the section
    And I verify this "courseOffering" file should contain:
      | id                                          | condition                                |
      | 48779c5fb806b8325ffbe4ceb0448bde1f5d8313_id | localCourseTitle = Ninth grade Advanced English |
    # I belong to DAYBREAK, but I creepily followed the above courseOffering to HIGHWIND
    And I verify this "course" file should contain:
      | id                                          | condition                                |
      | 2dad46540a82bd0ad17b7dbcbb6cbdd4fce2125d_id | uniqueCourseId = DAYBREAK21              |
    # this course belongs to DAYBREAK, so should not show up here
    And I verify this "course" file should not contain:
      | id                                          | condition                                |
      | 160cbcc9e293d45a11053f4d3bf6f4be8b70bac4_id |                                          |

    # Since student 1 is valid for Highwind, these two should be included
    And I verify this "program" file should contain:
      | id                                          | condition                                |
      | 004351714bfe0f6a34eb3f09a26fcbaf81645d1f_id | programType = Gifted and Talented        |
      | 9cce6ea23864ee4870c8871e4c14ddecb6ab0fb0_id | programType = Gifted and Talented        |

    # This one is just for Daybreak
    And I verify this "program" file should not contain:
      | id                                          | condition                                |
      | 5449814bb2dbed641d914843fb17a87f6222ec82_id |                                          |

    And I verify this "graduationPlan" file should contain:
      | id                                          | condition                                |
      | ac907f298a74a5f200c78ecb372afb1e53cf15c3_id | graduationPlanType = Distinguished       |
    
     And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  attendance                            |
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
       |  courseOffering                        |
       |  course                                |
       |  program                               |
       |  graduationPlan                        |
       |  deleted                               |
   
     And I verify this "deleted" file should contain:
       | id                                          | condition                                |
       | 54759a8d56aba10b1b300e66657cd6fcc3ca6ac9_id | entityType = studentSchoolAssociation |
       | 1b4aa93f01d11ad51072f3992583861ed080f15c_id | entityType = parent                      |
       | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id | entityType = student                     |
       | 908404e876dd56458385667fa383509035cd4312_idd14e4387521c768830def2c9dea95dd0bf7f8f9b_id | entityType = studentParentAssociation    |
       | 95147c130335e0656b0d8e9ab79622a22c3a3fab_id                                            | entityType = section                     |

     And I verify this "student" file should contain: 
       #this is student 11, which has updated information
       | id                                          | condition                                |
       | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id | studentUniqueStateId = 11                | 
     And I verify this "student" file should not contain: 
       #this is student 12, which has updated information, but we cut his tie with any schools
       | id                                          | condition                                |
       | 609640f6af263faad3a0cbee2cbe718fb71b9ab2_id |                                          | 

     And I verify this "studentSchoolAssociation" file should contain:
       #updated association for student 11 
       | id                                          | condition                                |
       | 68c4855bf0bdcc850a883d88fdf953b9657fe255_id | exitWithdrawDate = 2014-05-31            |
     And I verify this "studentSchoolAssociation" file should not contain:
       #this is an expired association, should not show up
       | id                                          | condition                                |
       | a13489364c2eb015c219172d561c62350f0453f3_id |                                          |

     And I verify this "studentGradebookEntry" file should contain:
       | id                                          | condition                                |
       | 6620fcd37d1095005a67dc330e591279577aede7_id | letterGradeEarned = A                    |

     And I verify this "studentAssessment" file should contain:
       | id                                          | condition                                |
       | 065f155b876c2dc15b6b319fa6f23834d05115b7_id | scoreResults.result = 92                 |

     And I verify this "parent" file should contain:
       | id                                          | condition                                                    |
       | 833c746641212c9e6e0fe5831f03570882c7bba1_id | electronicMail.emailAddress = roosevelt_mcgowan@fakemail.com |

     And I verify this "studentParentAssociation" file should contain:
       | id                                          | condition                                |
       | 908404e876dd56458385667fa383509035cd4312_id6ac27714bca705efbd6fd0eb6c0fd2c7317062e6_id | contactPriority = 0 |
  
     And I verify this "section" file should contain:
       | id                                          | condition                                |
       | 95cc5d67f3b653eb3e2f0641c429cf2006dc2646_id | uniqueSectionCode = 2                    |
  
     # Both Teacher 01 and 03 should be in DAYBREAk
     And I verify this "teacher" file should contain:
       | id                                          | condition                                |
       | cab9d548be3e51adf6ac00a4028e4f9f4f9e9cae_id | staffUniqueStateId = tech-0000000003     |
       | fe472294f0e40fd428b1a67b9765360004562bab_id | staffUniqueStateId = tech-0000000001     |
     And I verify this "teacher" file should not contain:
       # teacher 02 should not show up as we expired his staffEdorgAssociations 
       | id                                          | condition                                |
       | 631d712727054d49d706d5a3a7eb8faaad0cbeba_id |                                          |
     And I verify this "teacherSchoolAssociation" file should contain:
       | id                                          | condition                                |
       | c063086ce77b13c4e593ff8261024a6ef30e0a8d_id | teacherId = cab9d548be3e51adf6ac00a4028e4f9f4f9e9cae_id |

     # staff 04 should be in both DAYBREAK and HIGHWIND
     And I verify this "staff" file should contain:
       | id                                          | condition                                |
       | b7beb5d73c2189c680e16826e2e57d4d71970181_id | staffUniqueStateId = stff-0000000004     |

    And I verify this "attendance" file should contain:
      | id                                          | condition                                |
      | 07185fb3e72af3e0c2f48cf64b474b1731c52b20_id | schoolYearAttendance.attendanceEvent.reason = change_1       |

    And I verify this "courseOffering" file should contain:
      | id                                          | condition                                |
      | 1c4d8ea0c38aab28c05b5b40e8cf71e79e455ea2_id | localCourseTitle = First grade modified course |
      | 48779c5fb806b8325ffbe4ceb0448bde1f5d8313_id | localCourseTitle = Ninth grade Advanced English |
    And I verify this "course" file should contain:
      | id                                          | condition                                |
      | 2dad46540a82bd0ad17b7dbcbb6cbdd4fce2125d_id | uniqueCourseId = DAYBREAK21              |
      | 160cbcc9e293d45a11053f4d3bf6f4be8b70bac4_id | uniqueCourseId = DAYBREAK1               |

    And I verify this "program" file should contain:
      | id                                          | condition                                |
      | 004351714bfe0f6a34eb3f09a26fcbaf81645d1f_id | programType = Gifted and Talented        |
      | 9cce6ea23864ee4870c8871e4c14ddecb6ab0fb0_id | programType = Gifted and Talented        |
      | 5449814bb2dbed641d914843fb17a87f6222ec82_id | programType = Gifted and Talented        |

    And I verify this "graduationPlan" file should contain:
      | id                                          | condition                                |
      | 1af13424ea3a179e716468ff760255878ce20ec7_id | graduationPlanType = Distinguished       |

   #this step is necesssary since there is no graduationPlan in day 0 delta, need to verify it's really the same
   #format as API would return
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then The "graduationPlan" delta was extracted in the same format as the api


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
   And I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
   And I should receive a return code of 200
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
      And the extract contains a file for each of the following entities:
        |  entityType                            |
        |  school                                |
        |  educationOrganization                 |
      And I verify this "school" file should contain:
          | id                                          | condition                             |
          | 54b4b51377cd941675958e6e81dce69df801bfe8_id | stateOrganizationId=Daybreak Podunk High |
      
      # should only see delete file for lea 2
      And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
      And I verify this "deleted" file should contain:
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
      And I verify this "deleted" file should contain:
          | id                                          | condition                             |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = educationOrganization    |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = school                   |

      And I verify the last delta bulk extract by app "22c2a28d-7327-4444-8ff9-caad4b1c7aa3" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
      And I verify this "deleted" file should contain:
          | id                                          | condition                             |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = educationOrganization    |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = school                   |

    Then I reingest the SEA so I can continue my other tests


Scenario: CREATE and verify deltas for private entities through API POST
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 # CREATE parent entity via POST
  When I POST and validate the following entities:
    |  entity                       |  type                      |  returnCode  |
    |  newEducationOrganization     |  educationOrganization     |  201         |
    |  newDaybreakStudent           |  staffStudent              |  201         |
    |  DbStudentSchoolAssociation   |  studentSchoolAssociation  |  201         |
    |  newParentFather              |  parent                    |  201         |
    |  newParentMother              |  parent                    |  201         |
    |  newStudentFatherAssociation  |  studentParentAssociation  |  201         |
    |  newStudentMotherAssociation  |  studentParentAssociation  |  201         |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar" 
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  Then The "student" delta was extracted in the same format as the api
   And The "studentSchoolAssociation" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api
   And The "educationOrganization" delta was extracted in the same format as the api

 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"
  # UPDATE/UPSERT parent entity via PUT
  When I PUT and validate the following entities:
     |  field            |  entity                       |  value                           |  returnCode  |
     |  loginId          |  newStudent                   |  super_student_you_rock@bazinga  |  204         |
     |  loginId          |  newParentMom                 |  super_mom_you_rock@bazinga.com  |  204         |
     |  loginId          |  newParentDad                 |  super_dad_good_job@bazinga.com  |  204         |
     |  contactPriority  |  newStudentParentAssociation  |  1                               |  204         |
     |  postalCode       |  school                       |  11012                           |  204         |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar" 
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And The "student" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api
   And The "educationOrganization" delta was extracted in the same format as the api

 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"

  # UPDATE parent and parentStudentAssociation fields via PATCH 
  When I PATCH and validate the following entities:
    |  field            |  entity                       |  value                                 |  returnCode  |
    |  postalCode       |  patchEdOrg                   |  11099                                 |  204         |
    |  studentLoginId   |  newStudent                   |  average_student_youre_ok@bazinga.com  |  204         |
    |  momLoginId       |  newParentMom                 |  average_mom_youre_ok@bazinga.com      |  204         |
    |  dadLoginId       |  newParentDad                 |  average_dad_youre_ok@bazinga.com      |  204         |
    |  contactPriority  |  newStudentParentAssociation  |  1                                     |  204         |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify "2" delta bulk extract files are generated for LEA "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for LEA "<IL-HIGHWIND>" in "Midgar" 
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And The "student" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api
   And The "educationOrganization" delta was extracted in the same format as the api
   And The "school" delta was extracted in the same format as the api

  # DELETE entities
 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"
  When I DELETE and validate the following entities:
    |  entity        |  id                                           |  returnCode  |
    |  newStudent    |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  newParentDad  |  41f42690a7c8eb5b99637fade00fc72f599dab07_id  |  204         |
    |  newParentMom  |  41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id  |  204         |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  deleted                               |
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I verify this "deleted" file should contain:
    |  id                                          | condition                             |
    |  9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                  |
    |  41f42690a7c8eb5b99637fade00fc72f599dab07_id | entityType = parent                   |
    |  41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id | entityType = parent                   |
    |  cbfe3a47491fdff0432d5d4abca339735da9461d_id | entityType = studentSchoolAssociation |
    |  9bf3036428c40861238fdc820568fde53e658d88_idc3a6a4ed285c14f562f0e0b63e1357e061e337c6_id | entityType = studentParentAssociation |
    |  9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | entityType = studentParentAssociation |


Scenario: Update an existing edorg through the API, perform delta, call list endpoint, call API to download and verify delta
 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"   
  When I PUT the "postalCode" for a "school" entity to "11012"
  Then I should receive a return code of 204
  When I trigger a delta extract

 Given I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  When I make a call to the bulk extract end point "/v1.1/bulk/extract/list" using the certificate for app "vavedra9ub"
  When I get back a response code of "200"
  When I store the URL for the latest delta for LEA "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
  When the number of returned URLs is correct:
  |   fieldName  | count |
  |   fullLeas   |  0    |
  |   deltaLeas  |  1    |
  When I request listed delta via API for "19cca28d-7357-4044-8df9-caad4b1c8ee4"
  Then I should receive a return code of 200
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


Scenario: Create Student, course offering and section as SEA Admin, users from different LEAs requesting Delta extracts
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"  
 When I POST and validate the following entities:
    |  entity                        |  type                       |  returnCode  |
    |  newDaybreakStudent            |  staffStudent               |  201         |
    |  DbStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
    |  newParentFather               |  parent                     |  201         |
    |  newParentMother               |  parent                     |  201         |
    |  newStudentFatherAssociation   |  studentParentAssociation   |  201         |
    |  newCourseOffering             |  courseOffering             |  201         |
    |  newSection                    |  section                    |  201         |
    |  newStudentSectionAssociation  |  studentSectionAssociation  |  201         |
    |  newHighwindStudent            |  staffStudent               |  201         |
    |  HwStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
    |  newStudentAssessment          |  studentAssessment          |  201         |
    |  newGradebookEntry             |  gradebookEntry             |  201         |
  #  |  newGrade                      |  grade                      |  201         |
  #  |  newReportCard                 |  reportCard                 |  201         |
  #  |  newStudentAcademicRecord      |  studentAcademicRecord      |  201         |

 When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  student                               |
        |  parent                                |
        |  studentParentAssociation              |
        |  studentSchoolAssociation              |
        #|  course                              |
        |  courseOffering                        |
        |  section                               |
        |  studentSectionAssociation             |
        |  studentAssessment                   |
        |  gradebookEntry                      |
        #|  staff                               |
        #|  teacher                             |
        #|  yearlyTranscript                    |
        #|  attendance                          |
        #|  cohort                              |
        #|  session                             |
        #|  gradingPeriod                       |
        #|  program                             |
        #|  graduationPlan                      |
        #|  grade                               |
        #|  reportCard                          |
        #|  studentAcademicRecord               |

  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I verify this "student" file should contain:
    | id                                          | condition                             |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | loginId = new-student-min@bazinga.org |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | studentUniqueStateId = nsmin-1        |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | sex = Male                            |
  And I verify this "parent" file should contain:
    | id                                          | condition                             |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | entityType = parent                   |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | loginId = new-dad@bazinga.org         |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | parentUniqueStateId = new-dad-1       |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | sex = Male                            |
  And I verify this "parent" file should not contain:
    | id                                          | condition                             |
    | 41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id |                                       |
  And I verify this "studentParentAssociation" file should contain:
    | id                                                                                     | condition                                                |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | entityType = studentParentAssociation                    |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | parentId = 41f42690a7c8eb5b99637fade00fc72f599dab07_id   |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | relation = Father                                        |
  And I verify this "section" file should contain:
    | id                                          | condition                                                      |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | entityType = section                                           |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | sessionId = bfeaf9315f04797a41dbf1663d18ead6b6fb1309_id        |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | courseOfferingId = 38edd8479722ccf576313b4640708212841a5406_id |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id         |
  And I verify this "studentSchoolAssociation" file should contain:
    | id                                          | condition                                                |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | entityType = studentSchoolAssociation                    |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | entryDate = 2013-08-27                                   |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | entryGradeLevel = Eleventh grade                         |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | exitWithdrawDate = 2014-05-22                            |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id   |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id  |
  And I verify this "studentSectionAssociation" file should contain:
    | id                                                                                     | condition                                                |
    | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | entityType = studentSectionAssociation                   |
    | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id  |
    | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | beginDate = 2013-08-27                                   |
  And I verify this "studentAssessment" file should contain:
    | id                                          | condition                                                   |
    | 06d9f34280a62a49fe2c4e0acc8e1472284e9047_id | entityType = studentAssessment                              |
    | 06d9f34280a62a49fe2c4e0acc8e1472284e9047_id | administrationDate = 2013-09-24                             |
    | 06d9f34280a62a49fe2c4e0acc8e1472284e9047_id | assessmentId = d1db0a2c9d30c0fabcbc4c7fc796701e0509b86b_id  |
    | 06d9f34280a62a49fe2c4e0acc8e1472284e9047_id | gradeLevelWhenAssessed = Eleventh grade                     |
    | 06d9f34280a62a49fe2c4e0acc8e1472284e9047_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id     |
  And I verify this "gradebookEntry" file should contain:
    | id                                                                                     | condition                                                      |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | entityType = gradebookEntry                                    |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | sectionId = 4030207003b03d055bba0b5019b31046164eff4e_id        |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | gradingPeriodId = 21b8ac38bf886e78a879cfdb973a9352f64d07b9_id  |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | gradebookEntryType = Homework                                  |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | dateAssigned = 2014-02-21                                      |
 Given the extract download directory is empty
  When I request the latest bulk extract delta via API for "<IL-HIGHWIND>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  student                               |
        |  studentSchoolAssociation              |

  And I log into "SDK Sample" with a token of "lstevenson", a "IT Administrator" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I verify this "student" file should contain:
    | id                                          | condition                             |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | entityType = student                  |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | loginId = new-hw-student1@bazinga.org |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | studentUniqueStateId = hwmin-1        |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | sex = Female                          |
    
  And I verify this "studentSchoolAssociation" file should contain:
    | id                                          | condition                                                |
    | d913396aef918602b8049027dbdce8826c054402_id | entityType = studentSchoolAssociation                    |
    | d913396aef918602b8049027dbdce8826c054402_id | studentId = b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id  |
    | d913396aef918602b8049027dbdce8826c054402_id | schoolId = 1b5de2516221069fd8f690349ef0cc1cffbb6dca_id   |
    | d913396aef918602b8049027dbdce8826c054402_id | exitWithdrawDate = 2014-05-22                            |
    | d913396aef918602b8049027dbdce8826c054402_id | entryDate = 2013-08-27                                   |


Scenario: Delete student and stuSchAssoc, re-post them, then delete just studentSchoolAssociations (leaving students), verify delete
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"  
 # Delete both students and stSchAssoc
 When I DELETE and validate the following entities:
    |  entity                      |  id                                           |  returnCode  |
    |  newStudent                  |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  newStudent                  |  b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id  |  204         |
 # Create one student (and studentSchoolAssociation) per edorg
 And I POST and validate the following entities:
    |  entity                        |  type                       |  returnCode  |
    |  newDaybreakStudent            |  staffStudent               |  201         |
    |  DbStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
    |  newHighwindStudent            |  staffStudent               |  201         |
    |  HwStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
 # Delete the studentSchoolAssociations leaving the orphaned students
  And I DELETE and validate the following entities:
    |  entity                      |  id                                           |  returnCode  |
    #|  newStudent                  |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    #|  newStudent                  |  b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id  |  204         |
    |  studentSchoolAssociation    |  cbfe3a47491fdff0432d5d4abca339735da9461d_id  |  204         |    
    |  studentSchoolAssociation    |  d913396aef918602b8049027dbdce8826c054402_id  |  204         |

 # Log in as jstevenson from Daybreak and request the delta via API for Daybreak
 # Should only see 1 student delta, no deletes
 When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  deleted                               |

  # Verify contents of delete file does not contain the student
  And I verify this "deleted" file should not contain:
    | id                                          | condition                             |
    | 9bf3036428c40861238fdc820568fde53e658d88_id |                                       |
  And I verify this "deleted" file should contain:
    | id                                          | condition                             |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | entityType = studentSchoolAssociation |

 # Now log in as lstevenson from Highwind and request the delta for Highwind
 # Should only see 1 student delta, no deletes
 Given the extract download directory is empty
  When I request the latest bulk extract delta via API for "<IL-HIGHWIND>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  deleted                               |

  # Verify contents of delete file
  And I log into "SDK Sample" with a token of "lstevenson", a "IT Administrator" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I verify this "deleted" file should not contain:
    | id                                          | condition                             |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id |                                       |
  And I verify this "deleted" file should contain:
    | id                                          | condition                             |
    | d913396aef918602b8049027dbdce8826c054402_id | entityType = studentSchoolAssociation |

@shortcut
Scenario: Create, delete, then re-create the same entity, verify 1 delta entry, no deletes
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"  
 # Create one student in each lea, and matching studentSchoolAssociations
 When I POST and validate the following entities:
    |  entity                        |  type                       |  returnCode  |
    #|  newDaybreakStudent            |  staffStudent               |  201         |
    |  DbStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
    #|  newHighwindStudent            |  staffStudent               |  201         |
    |  HwStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
 # Delete students and stSchoAssoc
  And I DELETE and validate the following entities:
    |  entity                   |  id                                           |  returnCode  |
    |  studentSchoolAssociation |  cbfe3a47491fdff0432d5d4abca339735da9461d_id  |  204         |
    |  studentSchoolAssociation |  d913396aef918602b8049027dbdce8826c054402_id  |  204         |
    |  newStudent               |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  newStudent               |  b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id  |  204         |

 # Create one student in each lea, and matching studentSchoolAssociations
 And I POST and validate the following entities:
    |  entity                        |  type                       |  returnCode  |
    |  newDaybreakStudent            |  staffStudent               |  201         |
    |  DbStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
    |  newHighwindStudent            |  staffStudent               |  201         |
    |  HwStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |

 # Log in as jstevenson from Daybreak and request the delta via API for Daybreak
 # Should only see 1 student, 1 studentSchoolAssoc delta, no deletes
 When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  student                               |
    |  studentSchoolAssociation              |
    |  deleted                               |
  And I verify this "studentSchoolAssociation" file should contain:
    | id                                          | condition                             |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | entityType = studentSchoolAssociation |  

  And I verify this "student" file should contain:
    | id                                          | condition                             |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                  |
  # We should see deltes from the OTHER edOrg (Highwind), since they are spammed to all edorgs in a tenant
  And I verify this "deleted" file should contain:
    | id                                          | condition                             |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | entityType = student                  |
    | d913396aef918602b8049027dbdce8826c054402_id | entityType = studentSchoolAssociation |

 # Now log in as lstevenson from Highwind and request the delta for Highwind
 # Should only see 1 student delta, no deletes
 Given the extract download directory is empty
   And I request the latest bulk extract delta via API for "<IL-HIGHWIND>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  student                               |
    |  studentSchoolAssociation              |
    |  deleted                               |
  And I verify this "student" file should contain:
    | id                                          | condition                             |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id | entityType = student                  |

  And I verify this "studentSchoolAssociation" file should contain:
    | id                                          | condition                                                |
    | d913396aef918602b8049027dbdce8826c054402_id | entityType = studentSchoolAssociation                    |
  # We should see the deltes from the OTHER edOrg, since they are spammed to all edorgs in a tenant
  And I verify this "deleted" file should contain:
    | id                                          | condition                             |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | entityType = studentSchoolAssociation |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                  |


Scenario: Test access to the api
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
  Then I should receive a return code of 200
  Given I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
  Then I should receive a return code of 403
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request an unsecured latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>"
  Then I should receive a return code of 400
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "pavedz00ua"
  Then I should receive a return code of 403
  Given I log into "Paved Z00" with a token of "lstevenson", a "Noldor" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id paved>" clientId "<client id paved>"
  Then I should receive a return code of 200
  Given I log into "Paved Z00" with a token of "lstevenson", a "Noldor" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-DAYBREAK>" with appId "<app id paved>" clientId "<client id paved>"
  Then I should receive a return code of 403

Scenario: Be a good neighbor and clean up before you leave
    Given I clean the bulk extract file system and database
