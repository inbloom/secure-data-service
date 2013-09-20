Feature: Retrieved through the api a generated delta bulk extract file, and validate the file

Scenario: Initialize security trust store for Bulk Extract application and LEAs
  Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased    
    # Make IL-DAYBREAK a charter school to verify bulk extract will work
    Given I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And format "application/json"
    Then I PATCH the "organizationCategories" field of the entity specified by endpoint "educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id" to '[ "School", "Local Education Agency" ]'

Scenario: Generate a bulk extract delta after day 1 ingestion
  When I ingest "EdorgAppend.zip"
  When I trigger a delta extract
   And I request the latest bulk extract delta using the api
   And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>"
  Then I should see "24" bulk extract files
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
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
   And The "courseTranscript" delta was extracted in the same format as the api
   And The "studentProgramAssociation" delta was extracted in the same format as the api
   And The "staffProgramAssociation" delta was extracted in the same format as the api
   And The "studentDisciplineIncidentAssociation" delta was extracted in the same format as the api
   And The "disciplineIncident" delta was extracted in the same format as the api
   And The "disciplineAction" delta was extracted in the same format as the api
   And The "studentCompetency" delta was extracted in the same format as the api
   And The "calendarDate" delta was extracted in the same format as the api

  Given I trigger a bulk extract
   When I set the header format to "application/x-tar"
   Then I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   When I make lea bulk extract API call for lea "1b223f577827204a1c7e9c851dba06bea6b031fe_id"
    And the return code is 200 I get expected tar downloaded
   Then I check the http response headers
   When I decrypt and save the full extract
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
    #Then each record in the full extract is present and matches the delta extract
   #And I save some IDs from all the extract files to "delete_candidate" so I can delete them later

  And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<Daybreak Central High>"
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
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
  And The "courseOffering" delta was extracted in the same format as the api
  And The "course" delta was extracted in the same format as the api
  And The "courseTranscript" delta was extracted in the same format as the api
  And The "studentProgramAssociation" delta was extracted in the same format as the api
  And The "staffProgramAssociation" delta was extracted in the same format as the api
  And The "studentDisciplineIncidentAssociation" delta was extracted in the same format as the api
  And The "disciplineIncident" delta was extracted in the same format as the api
  And The "disciplineAction" delta was extracted in the same format as the api
  And The "studentCompetency" delta was extracted in the same format as the api

  And The "session" delta was extracted in the same format as the api
  And The "graduationPlan" delta was extracted in the same format as the api
  And The "gradingPeriod" delta was extracted in the same format as the api
  And The "calendarDate" delta was extracted in the same format as the api

  When I set the header format to "application/x-tar"
  Then I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  When I make lea bulk extract API call for lea "<Daybreak Central High>"
  And the return code is 200 I get expected tar downloaded
  Then I check the http response headers
  When I decrypt and save the full extract
  And I verify that an extract tar file was created for the tenant "Midgar"
  And there is a metadata file in the extract
  #Then each record in the full extract is present and matches the delta extract


  Scenario: Generate a SEA bulk extract delta after day 1 ingestion
    When I untar and decrypt the "inBloom" public delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>"
     And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    Then The "educationOrganization" delta was extracted in the same format as the api
     And The "learningObjective" delta was extracted in the same format as the api
     And The "learningStandard" delta was extracted in the same format as the api
     And The "competencyLevelDescriptor" delta was extracted in the same format as the api
     And The "studentCompetencyObjective" delta was extracted in the same format as the api
     And The "program" delta was extracted in the same format as the api

  Given I trigger a bulk extract
   When I set the header format to "application/x-tar"
   Then I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   When I make lea bulk extract API call for lea "884daa27d806c2d725bc469b273d840493f84b4d_id"
    And the return code is 200 I get expected tar downloaded
   Then I check the http response headers
   When I decrypt and save the full extract
    And I verify that an extract tar file was created for the tenant "Midgar"
    And there is a metadata file in the extract
   Then each record in the full extract is present and matches the delta extract


Scenario: SEA - Ingest additional entities in preparation for subsequent update and delete tests
   Given the extraction zone is empty
	When I ingest "SEAAppend.zip"
     And I trigger a delta extract
    Then I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
      |  entityType                            |
      |  assessment                            |
      |  session                               |
      |  calendarDate                          |
      |  course                                |
      |  courseOffering                        |
      |  gradingPeriod                         |
      |  graduationPlan                        |
      |  deleted                               |
    And Only the following extracts exists for edOrg "<STANDARD-SEA>" in tenant "Midgar"
      |  assessment                            |
      |  session                               |
      |  calendarDate                          |
      |  course                                |
      |  courseOffering                        |
      |  gradingPeriod                         |
      |  graduationPlan                        |
      |  deleted                               |
    And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And The "calendarDate" delta was extracted in the same format as the api
    And There should not be any of the following extracts for edOrg "<IL-DAYBREAK>" in tenant "Midgar"
    # Note course exists here due to a reference to a courseOffering related to this LEA
      |  assessment                            |
      |  session                               |
      |  calendarDate                          |
      |  courseOffering                        |
      |  gradingPeriod                         |
      |  graduationPlan                        |
    And There should not be any of the following extracts for edOrg "<IL-HIGHWIND>" in tenant "Midgar"
    # Note course exists here due to a reference to a courseOffering related to this LEA
      |  assessment                            |
      |  session                               |
      |  calendarDate                          |
      |  courseOffering                        |
      |  gradingPeriod                         |
      |  graduationPlan                        |
    And There should not be any of the following extracts for edOrg "<IL-SUNSET>" in tenant "Midgar"
      |  assessment                            |
      |  session                               |
      |  calendarDate                          |
      |  course                                |
      |  courseOffering                        |
      |  gradingPeriod                         |
      |  graduationPlan                        |
    And I verify this "assessment" file should contain:
      | id                                          | condition                                | description                                  |
	  | bb99132d75ccc4f92db1b8923a15bda8b40a3826_id | assessmentTitle = 2013-Kindergarten Assessment 2 Item | insert                          |
	  | 5fe86f2c3a2da1fbe9eaa386b40d0f0fbe265456_id | assessmentTitle = 2013-Kindergarten Assessment 1 Item | insert                          |
      | a59997eeaafc53047a7b972f435fd4fc0b6458f1_id | assessmentTitle = Delete AF              | insert                                       |
      | 87cc09359cfc6686841aa018b25d965a75153e36_id | assessmentTitle = Update AF              | insert                                       |
      | 69191a2b23a3395aba8183edb6088889f44cbd8a_id | assessmentTitle = Delete A and Update AF | insert                                       |
      | 6fbf0016bba0a599878ddcc1873669a62a4b5958_id | assessmentTitle = Update A and Delete AF | insert                                       |
      | ceaad98bfd854959a6cbdfd621808cd6d35997aa_id | assessmentTitle = Delete A and Delete AF | insert                                       |
      | dd6c15967a9807cc061a9796f985d2f6dbe40ec0_id | assessmentTitle = Update AF then Delete A| insert                                       |
      | 0635fa68fbd43b610f281566241b809274adfd52_id | assessmentTitle = Delete A then Update AF| insert                                       |
      | f8a8f68c8aed779c2e8c3f9174e5b05e880e9a9d_id | assessmentTitle = READ 2.0 Grade 1 BOY   | insert                                       |
    And I verify this "session" file should contain:
      | id                                          | condition                                | description                                  |
      | 3d809925e89e28202cbaa76ddfaca40f52124dd3_id | totalInstructionalDays = 40              | insert                                       |
    And I verify this "course" file should contain:
      | id                                          | condition                                | description                                  |
      | a71ea7489a86103bddd7459c25c83b7e7c5da875_id | courseTitle = Sixth grade English        | insert                                       |
    And I verify this "courseOffering" file should contain:
      | id                                          | condition                                | description                                  |
      | eba54e12a1a8ce4c09a4ce2863fe080ee05a42e0_id | localCourseTitle = Sixth grade English   | insert                                       |
    And I verify this "graduationPlan" file should contain:
      | id                                          | condition                                | description                                  |
      | 22411ee1066db57f4a8424f8285bc1d82fae1560_id | graduationPlanType = Distinguished       | insert                                       |


Scenario: SEA - Update entities
   Given the extraction zone is empty
	When I ingest "SEAUpdate.zip"
     And I trigger a delta extract
    Then I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  assessment                            |
       |  calendarDate                          |
       |  course                                |
       |  courseOffering                        |
       |  gradingPeriod                         |
       |  graduationPlan                        |
       |  session                               |
	 And I verify this "assessment" file only contains:
       | id                                          | condition                                 | description                                |
	   | bb99132d75ccc4f92db1b8923a15bda8b40a3826_id | assessmentTitle = 2013-Kindergarten Assessment 2 Item  | updated by AssessmentItem     |
       | dd6c15967a9807cc061a9796f985d2f6dbe40ec0_id | assessmentTitle = Update AF then Delete A | inserted by AssessmentFamily update        |
     And I verify this "session" file only contains:
       | id                                          | condition                                 | description                                |
       | 3d809925e89e28202cbaa76ddfaca40f52124dd3_id | totalInstructionalDays = 45               | updated                                    |
     And I verify this "course" file only contains:
       | id                                          | condition                                 | description                                |
       | a71ea7489a86103bddd7459c25c83b7e7c5da875_id | courseTitle = Seventh grade English       | updated                                    |
     And I verify this "courseOffering" file only contains:
       | id                                          | condition                                 | description                                |
       | eba54e12a1a8ce4c09a4ce2863fe080ee05a42e0_id | localCourseTitle = Seventh grade English  | updated                                    |
     And I verify this "gradingPeriod" file only contains:
       | id                                          | condition                                 | description                                |
       | aec59707feac8e68d9d4b780bef5547e934297dc_id | totalInstructionalDays = 190              | updated                                    |
     And I verify this "graduationPlan" file only contains:
       | id                                          | condition                                 | description                                |
       | 7f6e03f2a01f0f74258a1b0d8796be5eaf289f0a_id | graduationPlanType = Standard             | updated                                    |

Scenario: SEA Assessment + Objective Deltas Interactions (Picked Objective Assessments but same behaviour expected for Assessment Item)
    Given the extraction zone is empty
    When I ingest "SEAAssessment.zip"
    And I trigger a delta extract
    Then I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
      |  entityType                            |
      |  assessment                            |
    And I verify this "assessment" file only contains:
      | id                                         | condition                                                      | description                                                                                         |
	  |bcf0cadde56a961dd73efee8c15a6ca86c511ce8_id | assessmentTitle = Scenario 1                                   | delete the Assessment and update the Objective Assessment in the same ingestion                     |
	  |0b3a35aeec13efc4547f19fc20b55b141992c795_id | assessmentTitle = Scenario 2                                   | update the Assessment and delete the Objective Assessment in the same ingestion                     |
      |f5feb4f8940c0fda119ce82f1b8d1d3162dbabc4_id | assessmentTitle = Scenario 3                                   | delete the Assessment                                                                               |
	  |483b11e7a9ea9b0b337242bdc47fa758469f370e_id | assessmentTitle = Scenario 4                                   | delete the Objective Assessment                                                                     |
	  |d3580c38701831271557256b7eaa8b3c1dea1087_id | assessmentTitle = Scenario 5                                   | update the Objective Assessment                                                                     |
	  |60adaf0b07d87d8f76f22df2c38717ab36935ae0_id | assessmentTitle = Scenario 6                                   | update the Objective Assessment in one ingestion and delete the Assessment in another               |
	  |2a0d8d2f8049d113e2310bece5ba4aa9c5e34f59_id | assessmentTitle = Scenario 7                                   | delete the Assessment in one ingestion and update the Objective Assessment in another               |
      |86a6e5a5ec24b416c8db672515c72caa5bcaa7a8_id | assessmentTitle = delete Assessment and then update            | delete the Assessment in one ingestion and re-ingest and update the Assessment in another ingestion |
      |90282da8fa5d3e6fb433d961b129f19fc0a48b09_id | assessmentTitle = 2013-Eleventh grade Assessment 2 A+OB+Update | update the Assessment                                                                               |

     Given the extraction zone is empty
	 When I ingest "SEAAssessmentDeleteAndUpdate.zip"
	 #second ingestion to control order of events
	 And I ingest "SEAAssessmentDeleteAndUpdate2.zip"
     And I trigger a delta extract
     Then I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  assessment                            |
       |  deleted                               |
    #getting an additional and incorrect objective assessment deletion
    #And I verify this "deleted" file only contains:
      #| id                                           | condition               | description                                                                                         |
      #| bcf0cadde56a961dd73efee8c15a6ca86c511ce8_id  | entityType = assessment | delete the Assessment and update the Objective Assessment in the same ingestion                     |
      #| f5feb4f8940c0fda119ce82f1b8d1d3162dbabc4_id  | entityType = assessment | delete the Assessment                                                                               |
      #| 60adaf0b07d87d8f76f22df2c38717ab36935ae0_id  | entityType = assessment | update the Objective Assessment in one ingestion and delete the Assessment in another               |
      #| 2a0d8d2f8049d113e2310bece5ba4aa9c5e34f59_id  | entityType = assessment | delete the Assessment in one ingestion and update the Objective Assessment in another               |
     And I verify this "assessment" file only contains:
      | id                                         | condition                                                      | description                                                                                         |
	  |90282da8fa5d3e6fb433d961b129f19fc0a48b09_id | assessmentTitle = 2013-Eleventh grade Assessment 2 A+OB+Update | update the Assessment                                                                               |
      |86a6e5a5ec24b416c8db672515c72caa5bcaa7a8_id | assessmentTitle = delete Assessment and then update            | delete the Assessment in one ingestion and re-ingest and update the Assessment in another ingestion |
      |0b3a35aeec13efc4547f19fc20b55b141992c795_id | assessmentTitle = Scenario 2                                   | update the Assessment and delete the Objective Assessment in the same ingestion                     |
      |483b11e7a9ea9b0b337242bdc47fa758469f370e_id | assessmentTitle = Scenario 4                                   | delete the Objective Assessment                                                                     |
      |d3580c38701831271557256b7eaa8b3c1dea1087_id | assessmentTitle = Scenario 5                                   | update the Objective Assessment                                                                     |


  Scenario: Ingesting SEA (Non Odin) entities - AssessmentFamily Updates and Deletes
   Given the extraction zone is empty
    When I ingest "AssessmentFamilyDeltaUpdated.zip"
    #second ingestion to control order of events
    Then I ingest "AssessmentFamilyDeltaDeleted.zip"
     And I trigger a delta extract
    And I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
      |  entityType                            |
      |  assessment                            |
      |  deleted                               |
    And I verify this "assessment" file only contains:
      | id                                          | condition                                 | description                                |
      | a59997eeaafc53047a7b972f435fd4fc0b6458f1_id | assessmentTitle = Delete AF               | updated by AssessmentFamily                |
      | 87cc09359cfc6686841aa018b25d965a75153e36_id | assessmentTitle = Update AF               | updated by AssessmentFamily                |
      | 6fbf0016bba0a599878ddcc1873669a62a4b5958_id | assessmentTitle = Update A and Delete AF  | updated by Assessment                      |
      | f8a8f68c8aed779c2e8c3f9174e5b05e880e9a9d_id | assessmentTitle = READ 2.0 Grade 1 BOY    | updated by AssessmentFamily                |
    And I verify this "deleted" file only contains:
      | id                                          | condition                |
      | 69191a2b23a3395aba8183edb6088889f44cbd8a_id | entityType = assessment  |
      | ceaad98bfd854959a6cbdfd621808cd6d35997aa_id | entityType = assessment  |
      | dd6c15967a9807cc061a9796f985d2f6dbe40ec0_id | entityType = assessment  |
      | 0635fa68fbd43b610f281566241b809274adfd52_id | entityType = assessment  |


  Scenario: SEA Assessment + AssessmentItem Delta Updates and Deletes
   Given the extraction zone is empty
	 When I ingest "AssessmentItemDeltaDeleted.zip"
     And I trigger a delta extract
       Then I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  assessment                            |
       |  deleted                               |
	    And I verify this "assessment" file only contains:
       | id                                          | condition                                              | description                   |
	   | bb99132d75ccc4f92db1b8923a15bda8b40a3826_id | assessmentTitle = 2013-Kindergarten Assessment 2 Item  | updated by assessmentItem     |
	   | 5fe86f2c3a2da1fbe9eaa386b40d0f0fbe265456_id | assessmentTitle = 2013-Kindergarten Assessment 1 Item  | deleted                       |
     And I verify this "deleted" file only contains:
       | id                                          | condition                                              |
       | f8a8f68c8aed779c2e8c3f9174e5b05e880e9a9d_id | entityType = assessment                                |


  Scenario: Ingest an update to AssessmentPeriodDescriptor and output only affected assessments
   Given I clean the bulk extract file system and database
    And I trigger a delta extract
   When I ingest "AssessmentPeriodDescriptorUpdate.zip"
    And I trigger a delta extract
   When I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
      |  entityType                            |
      |  assessment                            |
    And I verify this "assessment" file should contain:
     | id                                          | condition                |
     | a59997eeaafc53047a7b972f435fd4fc0b6458f1_id | entityType = assessment  |
    And I verify this "assessment" file should not contain:
     | id                                          | condition                |
     | 124057675fa0903e905f0377bbc0450aacc7edab_id |                          |


Scenario: Triggering deltas via ingestion
  All entities belong to lea1 which is IL-DAYBREAK, we should only see a delta file for lea1
  and only a delete file is generated for lea2.
  Updated two students, 1 and 12, 12 lost contextual resolution to LEA1, so it should not appear
  in the extract file.
  1 is added to LEA2, so its stuff should go in there as well
  SEA - one of each of the 5 simple entities are deleted
     Given I clean the bulk extract file system and database
	  And I ingest "bulk_extract_SEA_calendarDates.zip"
      And I ingest "bulk_extract_deltas.zip"
      And I ingest "SEAGradingPeriodDelete.zip"
     When I trigger a delta extract
      And I verify "2" delta bulk extract files are generated for Edorg "<Daybreak Central High>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for Edorg "<STANDARD-SEA>" in "Midgar"
     When I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  assessment                            |
       |  learningObjective                     |
       |  learningStandard                      |
       |  competencyLevelDescriptor             |
       |  studentCompetencyObjective            |
       |  program                               |
       |  educationOrganization                 |
       |  calendarDate                          |
       |  deleted                               |
     And I verify this "deleted" file should contain:
       | id                                           | condition                               |
       | 49059b5a8c8c3e11649995bb4ca4275b0afe5f58_id  | entityType = assessment                 |
       | 8621a1a8d32dde3cf200697f22368a0f92f0fb92_id  | entityType = learningObjective          |
       | 4a6402c02ea016736280ac88d202d71a81058171_id  | entityType = learningStandard           |
       | d82250f49dbe4facb59af2f88fe746f70948405d_id  | entityType = competencyLevelDescriptor  |
       | edcd730acd29f74a5adcb4123b183001a3513853_id  | entityType = studentCompetencyObjective |
       | ebfc74d85dfcdcb7e5ddc93a6af2952801f9436e_id  | entityType = program                    |
       | aec59707feac8e68d9d4b780bef5547e934297dc_id  | entityType = gradingPeriod              |
       | 78f5ed2b6ce039539f34ef1889af712816aec6f7_id  | entityType = calendarDate               |

      And I verify this "assessment" file should contain:
       | id                                          | condition                                                    |
       # update assessmentFamily(parent)
       | 2777fe8b68767df7b7ab36768938daa576b5765b_id | assessmentTitle = 2013-Eighth grade Assessment 1             |

       # create assessmentFamily and assessment
       |41741ad11fa4c777e397faa0bf36546f27cdb5a8_id | assessmentFamilyHierarchyName = 2015 Standard.2015 First grade Standard  |
       |41741ad11fa4c777e397faa0bf36546f27cdb5a8_id | assessmentIdentificationCode.ID = 2015-First Grade Assessment 2 BKC      |
       #update assessment (child)
	   |8b7e6ce92009e03e3760e798a5f6a3d7c5e134ae_id | assessmentIdentificationCode.ID = 2013-Kindergarten Assessment 2 BKU     |

	   #update objectiveAssessment
	   |2c53daf31299947bc83fa5637ea502f16b715a60_id | objectiveAssessment.objectiveAssessments.nomenclature= Nomenclature BKU  |
	   #create objectiveAssessment
	   |d6be71fd4ede46095c1efd7281e9f96cd75b1798_id | assessmentTitle = 2016-Kindergarten Assessment 1                         |

	   #update AssessessmentPeriodDescriptor
	   | e8c930772a34becb630760ea019491294bd900b4_id | assessmentPeriodDescriptor.description = Beginning of Year 2013-2014 for Seventh grade BKU|

	   #created Assessessment + AssessmentFamily 
	   | 789660a15ff1f7588050018d581a77e0002e8120_id | assessmentTitle = 2017-First grade Assessment 2 BKC|

	   #check fields after deleting AssessessmentPeriodDescriptor and assessmentFamily 
	   And the "assessment" file should not contain a field
	     | id                                          | field                          |
         | f0ffa2e21cf1fc400527ac2ba63c20e4a620815c_id | assessmentPeriodDescriptor     |
         | b3a9994c8006a7e4c086b02e59e034146f053f77_id | assessmentPeriodDescriptor     |
         | 124057675fa0903e905f0377bbc0450aacc7edab_id  | assessmentFamilyReference     |
	    
        #delete objectiveAssessment
       And I verify this "assessment" file should not contain:
        | id                                          | condition                                |
  	    | a60af241e154436d3a996e544fb886381edc490a_id | objectiveAssessment.identificationCode = 2013-Fourth grade Assessment 2.OA-0    |

       And I verify this "calendarDate" file should contain:
        | id                                          | condition                                |
        | f34a74910eca77b0d344e1611f89156d84d0a40d_id | calendarEvent = Holiday                  |
        | c8d46187efd4476ccbf1442fd11abb4fc990b269_id | calendarEvent = Holiday                  |

     And I verify this "learningObjective" file should contain:
       | id                                          | condition                                |
       | a2b6a9f6ec88b4524c13064b335c0e078395e658_id | description = This an added description to test Deltas |
       | 984cd6d4d235b3d1f306042a69bbc8a67075bf9f_id | description = This an added description to test Deltas after day N bulk extract |
     And I verify this "learningStandard" file should contain:
       | id                                          | condition                                                                                |
       | bf9877afdcb1bb388334047c66b46daf83252534_id | description = This is an updated description for learning standard 2-18-9 to test Deltas |
       | c36b6ac46187cd637208ea8d48dc5efdd6d8ecb9_id | description = This is an updated description for learning standard 3-28-22 to test Deltas after day N bulk extract |
     And I verify this "competencyLevelDescriptor" file should contain:
       | id                                          | condition                                                        |
       | c91ae4718903d20289607c3c4335759e652ad569_id | description = Student understands almost everything in subject   |
       | dfa61631e5a2198d090c6276effebf3bd83cffce_id | description = Student does not understand anything               |
     And I verify this "studentCompetencyObjective" file should contain:
       | id                                          | condition                                     |
       | 5a564cbaa1059014fafd97c24971c5088c1fbffb_id | description = Added description to test Delta |
       | b37239e32fdda69caae3bd0bd30dbe95d5edef89_id | description = Added description to test Delta after day N bulk extract |
     And I verify this "program" file should contain:
       | id                                          | condition                                |
       | 9cce6ea23864ee4870c8871e4c14ddecb6ab0fb0_id | programType = Adult/Continuing Education |
       | b3cb28bf5c7e1e43a0901d51c2ec0967e5e079a7_id | programType = Gifted and Talented |
     And I verify this "educationOrganization" file should contain:
       | id                                          | condition                      |
       | 884daa27d806c2d725bc469b273d840493f84b4d_id | webSite = www.STANDARD-SEA.net |
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
       |  graduationPlan                        |
       |  disciplineIncident                    |
       |  studentDisciplineIncidentAssociation  |
       |  disciplineAction                      |
       |  studentCompetency                     |
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

    And I verify this "graduationPlan" file should contain:
      | id                                          | condition                                |
      | ac907f298a74a5f200c78ecb372afb1e53cf15c3_id | graduationPlanType = Distinguished       |

    # This incident involved student 1
    And I verify this "disciplineIncident" file should contain:
      | id                                          | condition                                |
      | ededd91e0b8069fb040227ec0fdeb20ff1a257bc_id | reporterName = Upstanding Citizen        |

    # These did not
    And I verify this "disciplineIncident" file should not contain:
      | id                                          | condition  |
      | 8270a081d30b82a9ac40a324bde644aaee933c20_id |            |
      | e6a01c4ee7768924c9e260c7ef5cea8d75088b89_id |            |

    # This incident involved student 1
    And I verify this "studentDisciplineIncidentAssociation" file should contain:
      | id                                                                                     | condition                          |
      | 908404e876dd56458385667fa383509035cd4312_id1ff4ecec0c9b4ef0e5dde0c3287af9871d519971_id | studentParticipationCode = Witness |

    # These did not
    And I verify this "studentDisciplineIncidentAssociation" file should not contain:
      | id                                                                                     | condition  |
      | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id7360a4e247645d9bd44de7fba62fc4094e8f5dc6_id |            |

    And I verify this "disciplineAction" file should contain:
      | id                                          | condition                          |
      # This incident involved student 1
      | 58295e247c01aae77d6f494d28c6a0b4808d4248_id | actualDisciplineActionLength = 3   |
      # This incident involved one of our schools
      | c78d1f951362ce558cb379cabc7491c6da339e58_id | actualDisciplineActionLength = 3   |

    # This did not
    And I verify this "disciplineAction" file should not contain:
      | id                                          | condition  |
      | d00dfdc3821fb8ea4f97147716afc2b153ceb5ba_id |            |

    # This assessment was for student 1
    And I verify this "studentAssessment" file should contain:
      | id                                          | condition                          |
      | 86154dd301695c9219d0525569a922a0144b8d17_id | scoreResults.result = 92           |

    # This was not
    And I verify this "studentAssessment" file should not contain:
      | id                                          | condition  |
      | 779b30733dbfacbaed769fae944dfec3fa5196e0_id |            |

    # this studentCompetency followed student 1
    And I verify this "studentCompetency" file should contain:
      | id                                          | condition  |
      | 568836d2bc382136c46356c2dbfeb51758ead1ff_id | diagnosticStatement = Student has Advanced understanding of subject. |

     # DAYBREAK stuff now
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
       |  studentCompetency                     |
       |  staff                                 |
       |  staffEducationOrganizationAssociation |
       |  teacher                               |
       |  teacherSchoolAssociation              |
       |  teacherSectionAssociation             |
       |  courseOffering                        |
       |  courseTranscript                      |
       |  course                                |
       |  graduationPlan                        |
       |  disciplineIncident                    |
       |  studentDisciplineIncidentAssociation  |
       |  disciplineAction                      |
       |  calendarDate                          |
       |  deleted                               |
     And I save some IDs from all the extract files to "delete_candidate" so I can delete them later

     And I verify this "deleted" file should contain:
       | id                                          | condition                                |
       | b43a7313ed0eaf7a6e71389b5cc64eb9e0ca0f2a_id | entityType = calendarDate                |
       | 54759a8d56aba10b1b300e66657cd6fcc3ca6ac9_id | entityType = studentSchoolAssociation    |
       | 1b4aa93f01d11ad51072f3992583861ed080f15c_id | entityType = parent                      |
       | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id | entityType = student                     |
       | 908404e876dd56458385667fa383509035cd4312_idd14e4387521c768830def2c9dea95dd0bf7f8f9b_id | entityType = studentParentAssociation    |
       | 95147c130335e0656b0d8e9ab79622a22c3a3fab_id                                            | entityType = section                     |

       And I verify this "calendarDate" file should contain:
        | id                                          | condition                                |
        | eab8ffa631837241c27316f4cd034b8e014821a3_id | calendarEvent = Instructional day   |
        | 356a441384ea905a4e01d5acebb25f7c42b7e0bd_id | date = 2022-06-04                   |

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
       #this is an association on a expired student 12, should not show up
       | id                                          | condition                                |
       | a13489364c2eb015c219172d561c62350f0453f3_id |                                          |

     And I verify this "studentGradebookEntry" file should contain:
       | id                                          | condition                                |
       | 6620fcd37d1095005a67dc330e591279577aede7_id | letterGradeEarned = A                    |

     And I verify this "studentAssessment" file should contain:
       | id                                          | condition                                 |
       | 86154dd301695c9219d0525569a922a0144b8d17_id | scoreResults.result = 92                  |
       | 779b30733dbfacbaed769fae944dfec3fa5196e0_id | studentAssessmentItems.rawScoreResult = 7 |

     And I verify this "parent" file should contain:
       | id                                          | condition                                                    |
       | 833c746641212c9e6e0fe5831f03570882c7bba1_id | electronicMail.emailAddress = roosevelt_mcgowan@fakemail.com |

     And I verify this "studentParentAssociation" file should contain:
       | id                                          | condition                                |
       | 908404e876dd56458385667fa383509035cd4312_id6ac27714bca705efbd6fd0eb6c0fd2c7317062e6_id | contactPriority = 0 |

     And I verify this "section" file should contain:
       | id                                          | condition                                |
       | 95cc5d67f3b653eb3e2f0641c429cf2006dc2646_id | uniqueSectionCode = 2                    |

     # Both Teacher 01 and 03 should be in DAYBREAK
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

    # This course transcript has a direct edorg reference to IL-HIGHWIND, but belongs to a student
    # only in IL-DAYBREAK, so it only shows up in IL-DAYBREAK
    And I verify this "courseTranscript" file should contain:
      | id                                          | condition                                |
      | adbd098e947690550c7c7bda7bd04d0e76f3d715_id | studentId = 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id |

    And I verify this "graduationPlan" file should contain:
      | id                                          | condition                                |
      | 1af13424ea3a179e716468ff760255878ce20ec7_id | graduationPlanType = Distinguished       |

    And I verify this "disciplineIncident" file should contain:
      | id                                          | condition                                |
      | 8270a081d30b82a9ac40a324bde644aaee933c20_id | reporterName = Squealer                  |
      | ededd91e0b8069fb040227ec0fdeb20ff1a257bc_id | reporterName = Upstanding Citizen        |
      | e6a01c4ee7768924c9e260c7ef5cea8d75088b89_id | incidentIdentifier = orphan1             |

    And I verify this "studentDisciplineIncidentAssociation" file should contain:
      | id                                                                                     | condition                          |
      | 908404e876dd56458385667fa383509035cd4312_id1ff4ecec0c9b4ef0e5dde0c3287af9871d519971_id | studentParticipationCode = Witness |
      | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id7360a4e247645d9bd44de7fba62fc4094e8f5dc6_id | studentParticipationCode = Witness |

    And I verify this "disciplineAction" file should contain:
      | id                                          | condition                            |
      | 58295e247c01aae77d6f494d28c6a0b4808d4248_id | actualDisciplineActionLength = 3     |
      | d00dfdc3821fb8ea4f97147716afc2b153ceb5ba_id | actualDisciplineActionLength = 2     |
      | c78d1f951362ce558cb379cabc7491c6da339e58_id | actualDisciplineActionLength = 3     |

    And I verify this "studentCompetency" file should contain:
      | id                                          | condition  |
      | 568836d2bc382136c46356c2dbfeb51758ead1ff_id | diagnosticStatement = Student has Advanced understanding of subject. |

  #this step is necesssary since there is no graduationPlan in day 0 delta, need to verify it's really the same
   #format as API would return
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   Then The "graduationPlan" delta was extracted in the same format as the api

# DAYBREAK stuff now
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<Daybreak Central High>" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  attendance                            |
    |  parent                                |
    |  section                               |
    |  student                               |
    |  studentAssessment                     |
    |  studentGradebookEntry                 |
    |  studentSchoolAssociation              |
    |  studentParentAssociation              |
    |  studentCompetency                     |
    |  staff                                 |
    |  staffEducationOrganizationAssociation |
    |  teacher                               |
    |  teacherSchoolAssociation              |
    |  teacherSectionAssociation             |
    |  courseOffering                        |
    |  courseTranscript                      |
    |  course                                |
    #|  graduationPlan                        |
    |  disciplineIncident                    |
    |  studentDisciplineIncidentAssociation  |
    |  disciplineAction                      |
    #|  calendarDate                          |
    |  deleted                               |
  And I save some IDs from all the extract files to "delete_candidate" so I can delete them later

  And I verify this "deleted" file should contain:
    | id                                          | condition                                |
    | b43a7313ed0eaf7a6e71389b5cc64eb9e0ca0f2a_id | entityType = calendarDate                |
    | 54759a8d56aba10b1b300e66657cd6fcc3ca6ac9_id | entityType = studentSchoolAssociation    |
    | 1b4aa93f01d11ad51072f3992583861ed080f15c_id | entityType = parent                      |
    | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id | entityType = student                     |
    | 908404e876dd56458385667fa383509035cd4312_idd14e4387521c768830def2c9dea95dd0bf7f8f9b_id | entityType = studentParentAssociation    |
    | 95147c130335e0656b0d8e9ab79622a22c3a3fab_id                                            | entityType = section                     |

  #And I verify this "calendarDate" file should contain:
  #  | id                                          | condition                                |
  #  | eab8ffa631837241c27316f4cd034b8e014821a3_id | calendarEvent = Instructional day   |
  #  | 356a441384ea905a4e01d5acebb25f7c42b7e0bd_id | date = 2022-06-04                   |

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
  #this is an association on a expired student 12, should not show up
    | id                                          | condition                                |
    | a13489364c2eb015c219172d561c62350f0453f3_id |                                          |

  And I verify this "studentGradebookEntry" file should contain:
    | id                                          | condition                                |
    | 6620fcd37d1095005a67dc330e591279577aede7_id | letterGradeEarned = A                    |

  And I verify this "studentAssessment" file should contain:
    | id                                          | condition                                 |
    | 86154dd301695c9219d0525569a922a0144b8d17_id | scoreResults.result = 92                  |
    | 779b30733dbfacbaed769fae944dfec3fa5196e0_id | studentAssessmentItems.rawScoreResult = 7 |

  And I verify this "parent" file should contain:
    | id                                          | condition                                                    |
    | 833c746641212c9e6e0fe5831f03570882c7bba1_id | electronicMail.emailAddress = roosevelt_mcgowan@fakemail.com |

  And I verify this "studentParentAssociation" file should contain:
    | id                                          | condition                                |
    | 908404e876dd56458385667fa383509035cd4312_id6ac27714bca705efbd6fd0eb6c0fd2c7317062e6_id | contactPriority = 0 |

  And I verify this "section" file should contain:
    | id                                          | condition                                |
    | 95cc5d67f3b653eb3e2f0641c429cf2006dc2646_id | uniqueSectionCode = 2                    |

# Both Teacher 01 and 03 should be in DAYBREAK
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
    | fe472294f0e40fd428b1a67b9765360004562bab_id | staffUniqueStateId = tech-0000000001     |

  And I verify this "attendance" file should contain:
    | id                                          | condition                                |
    | 07185fb3e72af3e0c2f48cf64b474b1731c52b20_id | schoolYearAttendance.attendanceEvent.reason = change_1       |

  And I verify this "courseOffering" file should contain:
    | id                                          | condition                                |
    | 48779c5fb806b8325ffbe4ceb0448bde1f5d8313_id | localCourseTitle = Ninth grade Advanced English |
  And I verify this "course" file should contain:
    | id                                          | condition                                |
    | 2dad46540a82bd0ad17b7dbcbb6cbdd4fce2125d_id | uniqueCourseId = DAYBREAK21              |

# This course transcript has a direct edorg reference to IL-HIGHWIND, but belongs to a student
# only in IL-DAYBREAK, so it only shows up in IL-DAYBREAK
  And I verify this "courseTranscript" file should contain:
    | id                                          | condition                                |
    | adbd098e947690550c7c7bda7bd04d0e76f3d715_id | studentId = 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id |

  #And I verify this "graduationPlan" file should contain:
  #  | id                                          | condition                                |
  #  | 1af13424ea3a179e716468ff760255878ce20ec7_id | graduationPlanType = Distinguished       |

  And I verify this "disciplineIncident" file should contain:
    | id                                          | condition                                |
    | 8270a081d30b82a9ac40a324bde644aaee933c20_id | reporterName = Squealer                  |
    | ededd91e0b8069fb040227ec0fdeb20ff1a257bc_id | reporterName = Upstanding Citizen        |
    | e6a01c4ee7768924c9e260c7ef5cea8d75088b89_id | incidentIdentifier = orphan1             |

  And I verify this "studentDisciplineIncidentAssociation" file should contain:
    | id                                                                                     | condition                          |
    | 908404e876dd56458385667fa383509035cd4312_id1ff4ecec0c9b4ef0e5dde0c3287af9871d519971_id | studentParticipationCode = Witness |
    | db9a7477390fb5de9d58350d1ce3c45ef8fcb0c6_id7360a4e247645d9bd44de7fba62fc4094e8f5dc6_id | studentParticipationCode = Witness |

  And I verify this "disciplineAction" file should contain:
    | id                                          | condition                            |
    | 58295e247c01aae77d6f494d28c6a0b4808d4248_id | actualDisciplineActionLength = 3     |
    | d00dfdc3821fb8ea4f97147716afc2b153ceb5ba_id | actualDisciplineActionLength = 2     |
    | c78d1f951362ce558cb379cabc7491c6da339e58_id | actualDisciplineActionLength = 3     |

  And I verify this "studentCompetency" file should contain:
    | id                                          | condition  |
    | 568836d2bc382136c46356c2dbfeb51758ead1ff_id | diagnosticStatement = Student has Advanced understanding of subject. |

#this step is necesssary since there is no graduationPlan in day 0 delta, need to verify it's really the same
#format as API would return
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  Then The "attendance" delta was extracted in the same format as the api

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
   And I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
   And I should receive a return code of 200
   And I download and decrypt the delta
   Then I should see "2" bulk extract files
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
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
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  Then The "educationOrganization" delta was extracted in the same format as the api
   And The "school" delta was extracted in the same format as the api

  #Combined moving school across LEA boundary scenario to test genetate a delta after another delta and
  #delta should only pick up latest change without clean bulk extract file system and database step

  #The expected behavior is that the old LEA that school used to belong to would receive an delete file, and
  #the new LEA would only receive a update file since the delete event is not applicable to the new LEA
  And I ingested "deltas_move_between_edorg.zip" dataset
    When I trigger a delta extract
      And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "22c2a28d-7327-4444-8ff9-caad4b1c7aa3" for "<IL-HIGHWIND>"
      And I verify "4" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for Edorg "<10 School District>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for Edorg "<11 School District>" in "Midgar"
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

Scenario: Ingest SEA delete and verify both LEAs received the delete
  Given I clean the bulk extract file system and database
    And I ingested "deltas_delete_sea.zip" dataset
  # We will see a warning file on cascading delete -- there are a lot of children of this SEA
    When I trigger a delta extract
      And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
      And I verify "2" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
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

@shortcut
Scenario: CREATE and verify deltas for private entities through API POST
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 # CREATE parent entity via POST
  When I POST and validate the following entities:
    |  entityName                   |  entityType                |  returnCode  |
    |  newEducationOrganization     |  educationOrganization     |  201         |
    |  newDaybreakStudent           |  student                   |  201         |
    |  DbStudentSchoolAssociation   |  studentSchoolAssociation  |  201         |
    |  newParentFather              |  parent                    |  201         |
    |  newParentMother              |  parent                    |  201         |
    |  newStudentFatherAssociation  |  studentParentAssociation  |  201         |
    |  newStudentMotherAssociation  |  studentParentAssociation  |  201         |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  Then The "student" delta was extracted in the same format as the api
   And The "studentSchoolAssociation" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api
   And The "educationOrganization" delta was extracted in the same format as the api

 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"
  # UPDATE/UPSERT parent entity via PUT
  When I PUT and validate the following entities:
     |  field            |  entityName                   |  value                           |  returnCode  | endpoint                                             |
     |  loginId          |  newDaybreakStudent           |  super_student_you_rock@bazinga  |  204         | students/9bf3036428c40861238fdc820568fde53e658d88_id |
     |  loginId          |  newParentMother              |  super_mom_you_rock@bazinga.com  |  204         | parents/41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id  |
     |  loginId          |  newParentFather              |  super_dad_good_job@bazinga.com  |  204         | parents/41f42690a7c8eb5b99637fade00fc72f599dab07_id  |
     |  contactPriority  |  newStudentMotherAssociation  |  1                               |  204         | studentParentAssociations/9bf3036428c40861238fdc820568fde53e658d88_idc3a6a4ed285c14f562f0e0b63e1357e061e337c6_id |
     |  postalCode       |  newEducationOrganization     |  11012                           |  204         | educationOrganizations/a96ce0a91830333ce68e235a6ad4dc26b414eb9e_id |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And The "student" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api
   And The "educationOrganization" delta was extracted in the same format as the api

 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"

  # UPDATE parent and parentStudentAssociation fields via PATCH
  When I PATCH and validate the following entities:
    |  fieldName        |  entityType               | value                                 |  returnCode  | endpoint                                                           |
    |  postalCode       |  educationOrganization    | 11099                                 |  204         | educationOrganizations/a96ce0a91830333ce68e235a6ad4dc26b414eb9e_id |
    |  studentLoginId   |  student                  | average_student_youre_ok@bazinga.com  |  204         | students/9bf3036428c40861238fdc820568fde53e658d88_id               |
    |  momLoginId       |  parent                   | average_mom_youre_ok@bazinga.com      |  204         | parents/41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id                |
    |  dadLoginId       |  parent                   | average_dad_youre_ok@bazinga.com      |  204         | parents/41f42690a7c8eb5b99637fade00fc72f599dab07_id                |
    |  contactPriority  |  studentParentAssociation | 1                                     |  204         | studentParentAssociations/9bf3036428c40861238fdc820568fde53e658d88_idc3a6a4ed285c14f562f0e0b63e1357e061e337c6_id |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
   And I verify "0" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And The "student" delta was extracted in the same format as the api
   And The "parent" delta was extracted in the same format as the api
   And The "studentParentAssociation" delta was extracted in the same format as the api
   And The "educationOrganization" delta was extracted in the same format as the api
   And The "school" delta was extracted in the same format as the api

  # DELETE entities
 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"
  When I DELETE and validate the following entities:
    |  entity        |  id                                           |  returnCode  |
    |  student       |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  parent        |  41f42690a7c8eb5b99637fade00fc72f599dab07_id  |  204         |
    |  parent        |  41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id  |  204         |

  When I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
    |  entityType                            |
    |  deleted                               |
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I verify this "deleted" file should contain:
    |  id                                          | condition                             |
    |  9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                  |
    |  41f42690a7c8eb5b99637fade00fc72f599dab07_id | entityType = parent                   |
    |  41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id | entityType = parent                   |
    |  cbfe3a47491fdff0432d5d4abca339735da9461d_id | entityType = studentSchoolAssociation |
    |  9bf3036428c40861238fdc820568fde53e658d88_idc3a6a4ed285c14f562f0e0b63e1357e061e337c6_id | entityType = studentParentAssociation |
    |  9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | entityType = studentParentAssociation |


Scenario: Update an existing edorg through the API, perform delta, call list endpoint, call API to download and verify delta
    # Make IL-DAYBREAK a charter school to verify bulk extract will work
    Given I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And format "application/json"
    Then I PATCH the "organizationCategories" field of the entity specified by endpoint "educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id" to '[ "School", "Local Education Agency" ]'

 Given I clean the bulk extract file system and database
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And format "application/json"
  When I PUT the "postalCode" for a "newEducationOrganization" entity to "11013" at "educationOrganizations/a96ce0a91830333ce68e235a6ad4dc26b414eb9e_id "
  Then I should receive a return code of 204
  When I trigger a delta extract

 Given I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
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
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And The "educationOrganization" delta was extracted in the same format as the api
  And The "school" delta was extracted in the same format as the api

Scenario: Update an existing edOrg with invalid API call, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 When I PUT the "missingEntity" for a "newEducationOrganization" entity to "WHOOPS" at "educationOrganizations/doesNotExistb015c219172d561c62350f0453f3_id "
 Then I should receive a return code of 404
  And deltas collection should have "0" records

Scenario: Create an invalid edOrg with the API, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/vnd.slc+json"
 When I POST a "invalidEducationOrganization" of type "educationOrganization"
 Then I should receive a return code of 403
  And deltas collection should have "0" records

Scenario: As SEA Admin, delete an existing school with API call, verify delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 When I DELETE an "educationOrganization" of id "17eaa66c1fc53cc1ec7d4aa25459d3924525832f_id"
 Then I should receive a return code of 204
 When I trigger a delta extract
  And I verify "2" delta bulk extract files are generated for Edorg "<IL-DAYBREAK>" in "Midgar"
  And I verify "2" delta bulk extract files are generated for Edorg "<IL-HIGHWIND>" in "Midgar"

@shortcut
@RALLY_US5741
Scenario: Create Student, course offering and section as SEA Admin, users from different LEAs requesting Delta extracts
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 When I POST and validate the following entities:
 # An entry for "entity" must be defined in bulk_extract.rb:prepareBody:field_data["POST"]
 # An entry for "type" must be defined in bulk_extract.rb:getEntityEndpoint:entity_to_endpoint_map
 # Note that "entity" is passed as "field", and "type" passed as "entity" when the underlying POST step is called for each table entry
 # Note if you get a 409 after adding an entity, it may have duplicate natural keys of a pre-existing entity
    | entityName                     |  entityType                            |  returnCode  |
    | newDaybreakStudent             |  student                               |  201         |
    | DbStudentSchoolAssociation     |  studentSchoolAssociation              |  201         |
    | newParentFather                |  parent                                |  201         |
    | newParentMother                |  parent                                |  201         |
    | newStudentFatherAssociation    |  studentParentAssociation              |  201         |
    | newDaybreakCourse              |  course                                |  201         |
    | newCourseOffering              |  courseOffering                        |  201         |
    | newSection                     |  section                               |  201         |
    | newStudentSectionAssociation   |  studentSectionAssociation             |  201         |
    | newHighwindStudent             |  student                               |  201         |
    | HwStudentSchoolAssociation     |  studentSchoolAssociation              |  201         |
    | newStudentAssessment           |  studentAssessment                     |  201         |
    | newGradebookEntry              |  gradebookEntry                        |  201         |
    | newStaff                       |  staff                                 |  201         |
    | newStaffDaybreakAssociation    |  staffEducationOrganizationAssociation |  201         |
    | newStaffHighwindAssociation    |  staffEducationOrganizationAssociation |  201         |
    | newTeacher                     |  teacher                               |  201         |
    | newTeacherEdorgAssociation     |  staffEducationOrganizationAssociation |  201         |
    | newTeacherSchoolAssociation    |  teacherSchoolAssociation              |  201         |
    | newGrade                       |  grade                                 |  201         |
    | newReportCard                  |  reportCard                            |  201         |
    | newStudentAcademicRecord       |  studentAcademicRecord                 |  201         |
    | newAttendanceEvent             |  attendance                            |  201         |
    | newCohort                      |  cohort                                |  201         |
    | newStaffCohortAssociation      |  staffCohortAssociation                |  201         |
    | newStudentCohortAssociation    |  studentCohortAssociation              |  201         |
    | DbGradingPeriod                |  gradingPeriod                         |  201         |
    | DbSession                      |  session                               |  201         |
    | newProgram                     |  program                               |  201         |
    | newStudentProgramAssociation   |  studentProgramAssociation             |  201         |
   #| newStaffProgramAssociation     |  staffProgramAssociation               |  201         |
    | newStudentCompetency           |  studentCompetency                     |  201         |
   #| newDisciplineIncident          |  disciplineIncident                    |  201         |
   #| newDisciplineAction            |  disciplineAction                      |  201         |
   #| newStudentDiscIncidentAssoc    |  studentDisciplineIncidentAssociation  |  201         |
    | newGraduationPlan              |  graduationPlan                        |  201         |
    | newGradingPeriod               |  gradingPeriod                         |  201         |
    | newLearningObjective           |  learningObjective                     |  201         |
    | newLearningStandard            |  learningStandard                      |  201         |
    | newCompetencyLevelDescriptor   |  competencyLevelDescriptor             |  201         |
    | newStudentCompetencyObjective  |  studentCompetencyObjective            |  201         |
    | newSession                     |  session                               |  201         |
    | newSEACourse                   |  course                                |  201         |
    | newSEACourseOffering           |  courseOffering                        |  201         |
    | newAssessment                  |  assessment                            |  201         |
    | newBECalendarDate              |  calendarDate                          |  201         |

 When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I generate and retrieve the bulk extract delta via API for "<STANDARD-SEA>"
  And I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  program                               |
        |  gradingPeriod                         |
        |  learningObjective                     |
        |  learningStandard                      |
        |  competencyLevelDescriptor             |
        |  studentCompetencyObjective            |
        |  session                               |
        |  course                                |
        |  courseOffering                        |
        |  assessment                            |
        |  graduationPlan                        |
        |  calendarDate                          |
  And I verify this "program" file should contain:
        | id                                          | condition                                |
        | 0ee2b448980b720b722706ec29a1492d95560798_id | programType = Regular Education          |
        | 0ee2b448980b720b722706ec29a1492d95560798_id | programId = 12345                        |
  And I verify this "gradingPeriod" file should contain:
        | id                                          | condition                                |
        | 8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id | endDate = 2014-05-22                     |
  And I verify this "learningObjective" file should contain:
        | id                                          | condition                                |
        | bc2dd61ff2234eb25835dbebe22d674c8a10e963_id | description = Description                |
  And I verify this "learningStandard" file should contain:
        | id                                          | condition                                |
        | 1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id | description = Description                |
  And I verify this "competencyLevelDescriptor" file should contain:
        | id                                          | condition                                |
        | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | description = Description                |
  And I verify this "studentCompetencyObjective" file should contain:
        | id                                          | condition                                |
        | ef680988e7c411cdb5438ded373512cd59cbfa7b_id | description = Description                |
  And I verify this "session" file should contain:
        | id                                          | condition                                |
        | fe6e1a162e6f6825830d78d72cb55498afaedcd3_id | endDate = 2015-06-11                     |
  And I verify this "course" file should contain:
        | id                                          | condition                                |
        | 494d4c8281ec78c7d8634afb683d39f6afdc5b85_id | courseDescription = new SEA course |
  And I verify this "courseOffering" file should contain:
        | id                                          | condition                                |
        | 0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id | courseId = 877e4934a96612529535581d2e0f909c5288131a_id |
  And I verify this "assessment" file should contain:
        | id                                          | condition                                |
        | 8d58352d180e00da82998cf29048593927a25c8e_id | contentStandard = State Standard |
  And I verify this "graduationPlan" file should contain:
        | id                                          | condition                                |
        | a77cdbececc81173aa76a34c05f9aeb44126a64d_id | individualPlan = false |
  And I verify this "calendarDate" file should contain:
        | id                                          | condition                                |
        | c7af73b8f98390a6d695a9e458529d6a149f0a21_id | date = 2015-04-02                        |
        | c7af73b8f98390a6d695a9e458529d6a149f0a21_id | calendarEvent = Instructional day        |

 When I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 Then I PATCH and validate the following entities:
 # "field" values must be defined in bulk_extract.rb:prepareBody:field_data["PATCH"]
 # "entity" values must be defined in bulk_extract.rb:getEntityBodyFromApi:entity_to_uri_map and in bulk_extract.rb:getEntityEndpoint:entity_to_endpoint_map
 # Note if "value" is empty in this table, the patched field will be set to the string "value"
    |  fieldName            |  entityType                 | value                                       |  returnCode  | endpoint                                                                |
    |  patchProgramType     |  program                    | Adult/Continuing Education                  |  204         | programs/0ee2b448980b720b722706ec29a1492d95560798_id                    |
    |  patchEndDate         |  gradingPeriod              | 2015-07-01                                  |  204         | gradingPeriods/8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id              |
    |  patchDescription     |  learningObjective          | Patched description                         |  204         | learningObjectives/bc2dd61ff2234eb25835dbebe22d674c8a10e963_id          |
    |  patchDescription     |  learningStandard           | Patched description                         |  204         | learningStandards/1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id           |
    |  patchDescription     |  competencyLevelDescriptor  | Patched description                         |  204         | competencyLevelDescriptor/ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id   |
    |  patchDescription     |  studentCompetencyObjective | Patched description                         |  204         | studentCompetencyObjectives/ef680988e7c411cdb5438ded373512cd59cbfa7b_id |
    |  patchEndDate         |  session                    | 2015-06-12                                  |  204         | sessions/fe6e1a162e6f6825830d78d72cb55498afaedcd3_id                    |
    |  patchCourseDesc      |  course                     | Patched description                         |  204         | courses/494d4c8281ec78c7d8634afb683d39f6afdc5b85_id                     |
    |  patchCourseId        |  courseOffering             | 06ccb498c620fdab155a6d70bcc4123b021fa60d_id |  204         | courseOfferings/0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id             |
    |  patchContentStd      |  assessment                 | National Standard                           |  204         | assessments/8d58352d180e00da82998cf29048593927a25c8e_id                 |
    |  patchIndividualPlan  |  graduationPlan             | true                                        |  204         | graduationPlans/a77cdbececc81173aa76a34c05f9aeb44126a64d_id             |
    |  calendarEvent        |  calendarDate               | Holiday                                     |  204         | calendarDates/c7af73b8f98390a6d695a9e458529d6a149f0a21_id             |

 Given the unpack directory is empty
 When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I generate and retrieve the bulk extract delta via API for "<STANDARD-SEA>"
   And I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  program                               |
        |  gradingPeriod                         |
        |  learningObjective                     |
        |  learningStandard                      |
        |  competencyLevelDescriptor             |
        |  studentCompetencyObjective            |
        |  session                               |
        |  course                                |
        |  courseOffering                        |
        |  assessment                            |
        |  graduationPlan                        |
        |  calendarDate                          |
   And I verify this "program" file should contain:
        | id                                          | condition                                |
        | 0ee2b448980b720b722706ec29a1492d95560798_id | programType = Adult/Continuing Education |
        | 0ee2b448980b720b722706ec29a1492d95560798_id | programId = 12345                        |
  And I verify this "gradingPeriod" file should contain:
        | id                                          | condition                                |
        | 8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id | endDate = 2015-07-01                     |
  And I verify this "learningObjective" file should contain:
        | id                                          | condition                                |
        | bc2dd61ff2234eb25835dbebe22d674c8a10e963_id | description = Patched description        |
  And I verify this "learningStandard" file should contain:
        | id                                          | condition                                |
        | 1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id | description = Patched description        |
  And I verify this "competencyLevelDescriptor" file should contain:
        | id                                          | condition                                |
        | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | description = Patched description        |
  And I verify this "studentCompetencyObjective" file should contain:
        | id                                          | condition                                |
        | ef680988e7c411cdb5438ded373512cd59cbfa7b_id | description = Patched description        |
  And I verify this "session" file should contain:
        | id                                          | condition                                |
        | fe6e1a162e6f6825830d78d72cb55498afaedcd3_id | endDate = 2015-06-12                     |
  And I verify this "course" file should contain:
        | id                                          | condition                                |
        | 494d4c8281ec78c7d8634afb683d39f6afdc5b85_id | courseDescription = Patched description  |
  And I verify this "courseOffering" file should contain:
        | id                                          | condition                                              |
        | 0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id | courseId = 06ccb498c620fdab155a6d70bcc4123b021fa60d_id |
  And I verify this "assessment" file should contain:
        | id                                          | condition                                              |
        | 8d58352d180e00da82998cf29048593927a25c8e_id | contentStandard = National Standard                |
  And I verify this "graduationPlan" file should contain:
        | id                                          | condition                                |
        | a77cdbececc81173aa76a34c05f9aeb44126a64d_id | individualPlan = true                    |
  And I verify this "calendarDate" file should contain:
        | id                                          | condition                                |
        | c7af73b8f98390a6d695a9e458529d6a149f0a21_id | date = 2015-04-02                        |
        | c7af73b8f98390a6d695a9e458529d6a149f0a21_id | calendarEvent = Holiday                  |

 When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
  And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  attendance                            |
        |  cohort                                |
        |  course                                |
        |  courseOffering                        |
        |  grade                                 |
        |  gradebookEntry                        |
        |  gradingPeriod                         |
        |  parent                                |
        |  reportCard                            |
        |  section                               |
        |  session                               |
        |  staff                                 |
        |  staffCohortAssociation                |
        |  staffEducationOrganizationAssociation |
        |  student                               |
        |  studentAcademicRecord                 |
        |  studentAssessment                     |
        |  studentCohortAssociation              |
        |  studentParentAssociation              |
        |  studentSchoolAssociation              |
        |  studentSectionAssociation             |
        |  teacher                               |
        |  teacherSchoolAssociation              |
        |  studentProgramAssociation             |
        #| newStaffProgramAssociation             |
        |  studentCompetency                     |
        #| newDisciplineIncident                  |
        #| newDisciplineAction                    |
        #| newStudentDiscIncidentAssoc            |
        #|  graduationPlan                        |

  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
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
  And I verify this "gradingPeriod" file should contain:
    | id                                          | condition                                                      |
    | 1dae9e8450e2e77dd0b06dee3fd928c1bfda4d49_id | entityType = gradingPeriod                                     |
  And I verify this "session" file should contain:
    | id                                          | condition                                                      |
    | 227097db8525f4631d873837754633daf8bfcb22_id | entityType = session                                           |
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
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | entityType = studentAssessment                              |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | administrationDate = 2013-09-24                             |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | assessmentId = 8e6fceafe05daef1da589a1709ee278ba51d337a_id  |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | gradeLevelWhenAssessed = Eleventh grade                     |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id     |
  And I verify this "gradebookEntry" file should contain:
    | id                                                                                     | condition                                                      |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | entityType = gradebookEntry                                    |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | sectionId = 4030207003b03d055bba0b5019b31046164eff4e_id        |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | gradingPeriodId = 21b8ac38bf886e78a879cfdb973a9352f64d07b9_id  |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | gradebookEntryType = Homework                                  |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | dateAssigned = 2014-02-21                                      |
  And I verify this "staff" file should contain:
    | id                                          | condition                                                   |
    | e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id | entityType = staff                                          |
    | 2472b775b1607b66941d9fb6177863f144c5ceae_id | entityType = staff                                          |
  And I verify this "staffEducationOrganizationAssociation" file should contain:
    | id                                          | condition                                                                    |
    | 9a5f2cc89f85609b5c188362e4ff767e02bc4483_id | entityType = staffEducationOrganizationAssociation                           |
    | 9a5f2cc89f85609b5c188362e4ff767e02bc4483_id | staffReference = 2472b775b1607b66941d9fb6177863f144c5ceae_id                 |
    | 9a5f2cc89f85609b5c188362e4ff767e02bc4483_id | educationOrganizationReference = a13489364c2eb015c219172d561c62350f0453f3_id |
    | afef1537920d10e093a8d301efbb463e364f8079_id | staffReference = e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id                 |
    | afef1537920d10e093a8d301efbb463e364f8079_id | educationOrganizationReference = 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
    | f44b0a272ba009b9668151070806e132f9e38364_id | staffReference = e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id                 |
    | f44b0a272ba009b9668151070806e132f9e38364_id | educationOrganizationReference = 99d527622dcb51c465c515c0636d17e085302d5e_id |
  And I verify this "teacher" file should contain:
    | id                                          | condition                                                   |
    | 2472b775b1607b66941d9fb6177863f144c5ceae_id | entityType = teacher                                        |
    | 2472b775b1607b66941d9fb6177863f144c5ceae_id | loginId = new-teacher-1@fakemail.com                        |
  And I verify this "teacherSchoolAssociation" file should contain:
    | id                                          | condition                                                   |
    | 7a2d5a958cfda9905812c3a9f38c07ac4e8899b0_id | entityType = teacherSchoolAssociation                       |
  And I verify this "course" file should contain:
    | id                                          | condition                                                   |
    | 877e4934a96612529535581d2e0f909c5288131a_id | entityType = course                                         |
  And I verify this "grade" file should contain:
    | id                                          | condition                                                   |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id | entityType = grade |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id | sectionId = 4030207003b03d055bba0b5019b31046164eff4e_id |
  And I verify this "reportCard" file should contain:
    | id                                                                                     | condition               |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id | entityType = reportCard |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id |
  And I verify this "studentAcademicRecord" file should contain:
    | id                                                                                     | condition                          |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id | entityType = studentAcademicRecord |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id |
  And I verify this "attendance" file should contain:
    | id                                          | condition                                                |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | entityType = attendance                                  |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id  |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | schoolId = a13489364c2eb015c219172d561c62350f0453f3_id   |
  And I verify this "cohort" file should contain:
    | id                                          | condition                                                |
    | cb99a7df36fadf8885b62003c442add9504b3cbd_id | entityType = cohort                                      |
    | cb99a7df36fadf8885b62003c442add9504b3cbd_id | cohortIdentifier = new-cohort-1                          |
  And I verify this "staffCohortAssociation" file should contain:
    | id                                          | condition                                                |
    | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | entityType = staffCohortAssociation                      |
    | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | staffId = 2472b775b1607b66941d9fb6177863f144c5ceae_id    |
    | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | cohortId = cb99a7df36fadf8885b62003c442add9504b3cbd_id   |
  And I verify this "studentCohortAssociation" file should contain:
    | id                                          | condition                                                |
    | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | entityType = studentCohortAssociation                   |
    | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | cohortId = cb99a7df36fadf8885b62003c442add9504b3cbd_id  |
    | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | studentId = 9bf3036428c40861238fdc820568fde53e658d88_id |
  And I verify this "studentProgramAssociation" file should contain:
    | id                                          | condition                                                |
    | 9bf3036428c40861238fdc820568fde53e658d88_id38025c314f0972d09cd982ffe58c7d8d2b59d23d_id | entityType = studentProgramAssociation                  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id38025c314f0972d09cd982ffe58c7d8d2b59d23d_id | educationOrganizationId = 1b223f577827204a1c7e9c851dba06bea6b031fe_id |

 Given the extract download directory is empty
  When I request the latest bulk extract delta via API for "<IL-HIGHWIND>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-HIGHWIND>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  staff                                 |
        |  staffEducationOrganizationAssociation |
        |  student                               |
        |  studentSchoolAssociation              |

  And I log into "SDK Sample" with a token of "lstevenson", a "IT Administrator" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
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
  And I verify this "staff" file should contain:
    | id                                          | condition                                                |
    | e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id | entityType = staff                                       |
  And I verify this "staffEducationOrganizationAssociation" file should contain:
    | id                                          | condition                                                                    |
    | afef1537920d10e093a8d301efbb463e364f8079_id | staffReference = e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id                 |
    | afef1537920d10e093a8d301efbb463e364f8079_id | educationOrganizationReference = 1b223f577827204a1c7e9c851dba06bea6b031fe_id |
    | f44b0a272ba009b9668151070806e132f9e38364_id | staffReference = e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id                 |
    | f44b0a272ba009b9668151070806e132f9e38364_id | educationOrganizationReference = 99d527622dcb51c465c515c0636d17e085302d5e_id |

  # Now delete the recently added entities and check the delete file
  Given I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  When I DELETE and validate the following entities:
    |  entity                     |  id                                           |  returnCode  |
    |  attendance                 |  95b973e29368712e2090fcad34d90fffb20aa9c4_id  |  204         |
    |  studentAssessment          |  d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id  |  204         |
    |  staffCohortAssociation     |  5e7d5f12cefbcb749069f2e5db63c1003df3c917_id  |  204         |
    |  gradebookEntry             |  4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id  |  204         |
    |  grade                      |  1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id  |  204         |
    |  reportCard                 |  1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id  |  204         |
    |  studentAcademicRecord      |  1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id  |  204         |
    |  studentCohortAssociation   |  9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id  |  204         |
    |  studentProgramAssociation  |  9bf3036428c40861238fdc820568fde53e658d88_id38025c314f0972d09cd982ffe58c7d8d2b59d23d_id  |  204         |
    |  studentSectionAssociation  |  4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id  |  204         |
    |  cohort                     |  cb99a7df36fadf8885b62003c442add9504b3cbd_id  |  204         |
    |  section                    |  4030207003b03d055bba0b5019b31046164eff4e_id  |  204         |
    |  courseOffering             |  38edd8479722ccf576313b4640708212841a5406_id  |  204         |
    |  course                     |  877e4934a96612529535581d2e0f909c5288131a_id  |  204         |
    |  staff                      |  e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id  |  204         |
    |  teacherSchoolAssociation   |  7a2d5a958cfda9905812c3a9f38c07ac4e8899b0_id  |  204         |
    |  teacher                    |  2472b775b1607b66941d9fb6177863f144c5ceae_id  |  204         |
    |  parent                     |  41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id  |  204         |
    |  parent                     |  41f42690a7c8eb5b99637fade00fc72f599dab07_id  |  204         |
    |  studentSchoolAssociation   |  cbfe3a47491fdff0432d5d4abca339735da9461d_id  |  204         |
    |  student                    |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  session                    |  227097db8525f4631d873837754633daf8bfcb22_id  |  204         |
    |  gradingPeriod              |  1dae9e8450e2e77dd0b06dee3fd928c1bfda4d49_id  |  204         |
    |  program                    |  0ee2b448980b720b722706ec29a1492d95560798_id  |  204         |
    |  gradingPeriod              |  8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id  |  204         |
    |  learningObjective          |  bc2dd61ff2234eb25835dbebe22d674c8a10e963_id  |  204         |
    |  learningStandard           |  1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id  |  204         |
    |  competencyLevelDescriptor  |  ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id  |  204         |
    |  studentCompetencyObjective |  ef680988e7c411cdb5438ded373512cd59cbfa7b_id  |  204         |
    |  session                    |  fe6e1a162e6f6825830d78d72cb55498afaedcd3_id  |  204         |
    |  course                     |  494d4c8281ec78c7d8634afb683d39f6afdc5b85_id  |  204         |
    |  courseOffering             |  0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id  |  204         |
    |  assessment                 |  8d58352d180e00da82998cf29048593927a25c8e_id  |  204         |
    |  graduationPlan             |  a77cdbececc81173aa76a34c05f9aeb44126a64d_id  |  204         |
    |  calendarDate               |  c7af73b8f98390a6d695a9e458529d6a149f0a21_id  |  204         |

 Given the extraction zone is empty
  When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I generate and retrieve the bulk extract delta via API for "<IL-DAYBREAK>"
   And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
  And I verify this "deleted" file should contain:
    | id                                          | condition                                |
    | 95b973e29368712e2090fcad34d90fffb20aa9c4_id | entityType = attendance                  |
    | cb99a7df36fadf8885b62003c442add9504b3cbd_id | entityType = cohort                      |
    | 877e4934a96612529535581d2e0f909c5288131a_id | entityType = course                      |
    | 38edd8479722ccf576313b4640708212841a5406_id | entityType = courseOffering              |
    | 1dae9e8450e2e77dd0b06dee3fd928c1bfda4d49_id | entityType = gradingPeriod               |
    | 41f42690a7c8eb5b99637fade00fc72f599dab07_id | entityType = parent                      |
    | 41edbb6cbe522b73fa8ab70590a5ffba1bbd51a3_id | entityType = parent                      |
    | 4030207003b03d055bba0b5019b31046164eff4e_id | entityType = section                     |
    | 227097db8525f4631d873837754633daf8bfcb22_id | entityType = session                     |
    | 5e7d5f12cefbcb749069f2e5db63c1003df3c917_id | entityType = staffCohortAssociation      |
    | afef1537920d10e093a8d301efbb463e364f8079_id | entityType = staffEducationOrganizationAssociation |
    | e9f3401e0a034e20bb17663dd7d18ece6c4166b5_id | entityType = staff                       |
    | 9bf3036428c40861238fdc820568fde53e658d88_id | entityType = student                     |
    | d4a8b25254af09fe3dd772a2149aa6b45fa6b170_id | entityType = studentAssessment           |
    | 6a74f51699a7554f89865e004695913aee277117_id | entityType = studentCompetency           |
    | cbfe3a47491fdff0432d5d4abca339735da9461d_id | entityType = studentSchoolAssociation    |
    | 2472b775b1607b66941d9fb6177863f144c5ceae_id | entityType = teacher                     |
    | 7a2d5a958cfda9905812c3a9f38c07ac4e8899b0_id | entityType = teacherSchoolAssociation    |
    | 4030207003b03d055bba0b5019b31046164eff4e_id383ee846e68a3f539a0a64a651ab2078dedbb6f3_id | entityType = gradebookEntry            |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idcd83575df61656c7d8aebb690ae0bb3ff129a857_id | entityType = grade                     |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_id77bc827b90835ef0df42154428ac3153f0ddc746_id | entityType = reportCard                |
    | 1417cec726dc51d43172568a9c332ee1712d73d4_idb2b773084845209865762830ceb1721ebb1101ef_id | entityType = studentAcademicRecord     |
    | 9bf3036428c40861238fdc820568fde53e658d88_idfa64547520fbfcbc8646a7a0bb3a52f76e4f4d21_id | entityType = studentCohortAssociation  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id28af8b70a2f2e695fc25da04e0f8625115002556_id | entityType = studentParentAssociation  |
    | 9bf3036428c40861238fdc820568fde53e658d88_id38025c314f0972d09cd982ffe58c7d8d2b59d23d_id | entityType = studentProgramAssociation |
    | 4030207003b03d055bba0b5019b31046164eff4e_id78468628f357b29599510341f08dfd3277d9471e_id | entityType = studentSectionAssociation |
    | c7af73b8f98390a6d695a9e458529d6a149f0a21_id                                            | entityType = calendarDate              |

  When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I generate and retrieve the bulk extract delta via API for "<STANDARD-SEA>"
  Then I verify the last public delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<STANDARD-SEA>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  deleted                               |
  And I verify this "deleted" file should contain:
       | id                                          | condition                                |
       | 0ee2b448980b720b722706ec29a1492d95560798_id | entityType = program                     |
       | 8feb483ade5d7b3b45c1e4b4a50d00302cba4548_id | entityType = gradingPeriod               |
       | bc2dd61ff2234eb25835dbebe22d674c8a10e963_id | entityType = learningObjective           |
       | 1bd6fea0e8b8ac6a8fe87a8530effbced0df9318_id | entityType = learningStandard            |
       | ceddd8ec0ee71c1f4f64218e00581e9b27c0fffb_id | entityType = competencyLevelDescriptor   |
       | ef680988e7c411cdb5438ded373512cd59cbfa7b_id | entityType = studentCompetencyObjective  |
       | fe6e1a162e6f6825830d78d72cb55498afaedcd3_id | entityType = session                     |
       | 494d4c8281ec78c7d8634afb683d39f6afdc5b85_id | entityType = course                      |
       | 0fee7a7aba9a96388ef628b7e3e5e5ea60a142a7_id | entityType = courseOffering              |
       | 8d58352d180e00da82998cf29048593927a25c8e_id | entityType = assessment                  |
       | a77cdbececc81173aa76a34c05f9aeb44126a64d_id | entityType = graduationPlan              |

Scenario: Delete student and stuSchAssoc, re-post them, then delete just studentSchoolAssociations (leaving students), verify delete
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 # Create one student (and studentSchoolAssociation) per edorg
 And I POST and validate the following entities:
    |  entityName                    |  entityType                 |  returnCode  |
    |  newDaybreakStudent            |  student                    |  201         |
    |  DbStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
 # Delete both students and stSchAssoc
 When I DELETE and validate the following entities:
    |  entity                      |  id                                           |  returnCode  |
    |  student                     |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  student                  |  b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id  |  204         |
 # Create one student (and studentSchoolAssociation) per edorg
 And I POST and validate the following entities:
    |  entityName                    |  entityType                 |  returnCode  |
    |  newDaybreakStudent            |  student                    |  201         |
    |  DbStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
    |  newHighwindStudent            |  student                    |  201         |
    |  HwStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
 # Delete the studentSchoolAssociations leaving the orphaned students
  And I DELETE and validate the following entities:
    |  entity                      |  id                                           |  returnCode  |
    |  studentSchoolAssociation    |  cbfe3a47491fdff0432d5d4abca339735da9461d_id  |  204         |
    |  studentSchoolAssociation    |  d913396aef918602b8049027dbdce8826c054402_id  |  204         |

 # Log in as jstevenson from Daybreak and request the delta via API for Daybreak
 # Should only see 1 student delta, no deletes
 When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
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
  And I log into "SDK Sample" with a token of "lstevenson", a "IT Administrator" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I verify this "deleted" file should not contain:
    | id                                          | condition                             |
    | b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id |                                       |
  And I verify this "deleted" file should contain:
    | id                                          | condition                             |
    | d913396aef918602b8049027dbdce8826c054402_id | entityType = studentSchoolAssociation |


Scenario: Create, delete, then re-create the same entity, verify 1 delta entry, no deletes
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "rrogers", a "IT Administrator" for "STANDARD-SEA" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
 # Create one student in each lea, and matching studentSchoolAssociations
 When I POST and validate the following entities:
    |  entityName                    |  entityType                 |  returnCode  |
    |  DbStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
    |  HwStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
 # Delete students and stSchoAssoc
  And I DELETE and validate the following entities:
    |  entity                   |  id                                           |  returnCode  |
    |  studentSchoolAssociation |  cbfe3a47491fdff0432d5d4abca339735da9461d_id  |  204         |
    |  studentSchoolAssociation |  d913396aef918602b8049027dbdce8826c054402_id  |  204         |
    |  student                  |  9bf3036428c40861238fdc820568fde53e658d88_id  |  204         |
    |  student                  |  b8b0a8d439591b9e073e8f1115ff1cf1fd4125d6_id  |  204         |

 # Create one student in each lea, and matching studentSchoolAssociations
 And I POST and validate the following entities:
    |  entityName                    |  entityType                 |  returnCode  |
    |  newDaybreakStudent            |  student                    |  201         |
    |  DbStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |
    |  newHighwindStudent            |  student                    |  201         |
    |  HwStudentSchoolAssociation    |  studentSchoolAssociation   |  201         |

 # Log in as jstevenson from Daybreak and request the delta via API for Daybreak
 # Should only see 1 student, 1 studentSchoolAssoc delta, no deletes
 When I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
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
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
  Then I should receive a return code of 200
  Given I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And the sli securityEvent collection is empty 
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "<client id>"
  Then I should receive a return code of 403
  And I should see a count of "2" in the security event collection 
  And I check to find if record is in sli db collection:
 | collectionName | expectedRecordCount | searchParameter      | searchValue                                                 | searchType |
 | securityEvent  | 2                   | body.tenantId        | Midgar                                                      | string     |
 | securityEvent  | 2                   | body.appId           | vavedRa9uB                                                  | string     |
 | securityEvent  | 2                   | body.className       | org.slc.sli.api.resources.BulkExtract                       | string     |
 | securityEvent  | 2                   | body.userEdOrg       | IL-DAYBREAK                                                 | string     |
 | securityEvent  | 1                   | body.targetEdOrgList | IL-HIGHWIND                                                 | string     |
 | securityEvent  | 1                   | body.logMessage      | Access Denied:User is not authorized to access this extract | string     |
  And "2" security event with field "body.actionUri" matching "http.*/api/rest/v1..*/bulk/extract/99d527622dcb51c465c515c0636d17e085302d5e_id/delta/.*" should be in the sli db
  
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request an unsecured latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>"
  Then I should receive a return code of 400
  Given I log into "SDK Sample" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id>" clientId "pavedz00ua"
  Then I should receive a return code of 403
  Given I log into "Paved Z00" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-HIGHWIND>" with appId "<app id paved>" clientId "<client id paved>"
  Then I should receive a return code of 200
  Given I log into "Paved Z00" with a token of "lstevenson", a "Noldor" for "IL-HIGHWIND" for "IL-Highwind" in tenant "Midgar", that lasts for "300" seconds
  And I request latest delta via API for tenant "Midgar", lea "<IL-DAYBREAK>" with appId "<app id paved>" clientId "<client id paved>"
  Then I should receive a return code of 403

Scenario: Trigger a SEA delta extract and check security events
   Given I ingest "bulk_extract_sea_delta_security_event.zip"
   And the following collections are empty in sli datastore:
     | collectionName              |
     | securityEvent               |
   And I trigger a delta extract
   Then I should see following map of entry counts in the corresponding sli db collections:
     | collectionName             | count |
     | securityEvent              | 5     |
  And I check to find if record is in sli db collection:
     | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
     | securityEvent   | 1                   | body.logMessage         | Beginning bulk extract execution                                             | string          |
     | securityEvent   | 2                   | body.className          | org.slc.sli.bulk.extract.extractor.DeltaExtractor                            | string          |
     | securityEvent   | 1                   | body.actionUri          | Bulk extract execution                                                       | string          |
     | securityEvent   | 1                   | body.actionUri          | Delta Extract Initiation                                                     | string          |
     | securityEvent   | 1                   | body.actionUri          | Delta Extract Finished                                                       | string          |
     | securityEvent   | 2                   | body.actionUri          | Writing extract file to the file system                                      | string          |
     | securityEvent   | 1                   | body.logMessage         | Generating archive for app 19cca28d-7357-4044-8df9-caad4b1c8ee4              | string          |
     | securityEvent   | 1                   | body.logMessage         | Generating archive for app 22c2a28d-7327-4444-8ff9-caad4b1c7aa3              | string          |
     | securityEvent   | 2                   | body.targetEdOrg        | 884daa27d806c2d725bc469b273d840493f84b4d_id                                  | string          |

Scenario: Be a good neighbor and clean up before you leave
    Given I clean the bulk extract file system and database
