Feature: Retrived through the api a generated delta bulk extract file, and validate the file

Scenario: Initialize security trust store for Bulk Extract application and LEAs
  Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased

Scenario: Generate a bulk extract day 0 delta    
  When I trigger a delta extract
   And I request the latest bulk extract delta using the api
   And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"
     Then I should see "2" bulk extract files
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    Then The "educationOrganization" delta was extracted in the same format as the api

Scenario: Generate a bulk extract in a different LEAs
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
   And I request the latest bulk extract delta using the api
   And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "<app id>"
   Then I should see "1" bulk extract files
   And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    Then The "educationOrganization" delta was extracted in the same format as the api
     And The "educationOrganization" entity with id "<ed_org_to_lea2_id>" should belong to LEA with id "<lea2_id>" 

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
    And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"
      Then I should see "1" bulk extract files
    And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    Then The "educationOrganization" delta was extracted in the same format as the api
      And The "school" delta was extracted in the same format as the api

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
      And I verify "1" delta bulk extract files are generated for "<lea1_id>" in "Midgar" 
      And I verify "1" delta bulk extract files are generated for "<lea2_id>" in "Midgar" 
      And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<lea1_id>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
      And I verify this "deleted" file contains:
          | id                                          | condition                             |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = educationOrganization    |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = school                   |

      And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<lea2_id>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
      And I verify this "deleted" file contains:
          | id                                          | condition                             |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = educationOrganization    |
          | 884daa27d806c2d725bc469b273d840493f84b4d_id | entityType = school                   |

    Then I reingest the SEA so I can continue my other tests

# This scenario depends on previous scenario: Ingest education organization and perform delta
Scenario: move school across LEA boundary by delete and create through ingestion
    The expected behavior is that the old LEA that school used to belong to would receive an delete file, and
    the new LEA would only receive a update file since the delete event is not applicable to the new LEA
  Given I clean the bulk extract file system and database
  And I ingested "deltas_move_between_edorg.zip" dataset

    When I trigger a delta extract
      And I verify "1" delta bulk extract files are generated for "<lea1_id>" in "Midgar" 
      And I verify "1" delta bulk extract files are generated for "<lea2_id>" in "Midgar" 
      # should see no delete file for lea 1
      And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<lea1_id>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  school                                |
        |  educationOrganization                 |
      And I verify this "school" file contains:
          | id                                          | condition                             |
          | 54b4b51377cd941675958e6e81dce69df801bfe8_id | stateOrganizationId=Daybreak Podunk High |
      
      # should only see delete file for lea 2
      And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<lea2_id>" in "Midgar" contains a file for each of the following entities:
        |  entityType                            |
        |  deleted                               |
      And I verify this "deleted" file contains:
          | id                                          | condition                             |
          | 54b4b51377cd941675958e6e81dce69df801bfe8_id | entityType = school                   |

Scenario: Create a new education organization through the API and perform delta
  Given I clean the bulk extract file system and database
    And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And format "application/vnd.slc+json"

  When I POST an entity of type "educationOrganization"
    Then I should receive a return code of 201   

  When I trigger a delta extract
   And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"
     Then I should see "1" bulk extract files
      And The "educationOrganization" delta was extracted in the same format as the api

Scenario: Update an existing education organization through the API and perform delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"  
    When I GET the response body for a "school" in "<IL-DAYBREAK>"
      Then I should receive a return code of 200
  
  When I "PUT" the "postalCode" for a "school" entity to "11012"
    Then I should receive a return code of 204

  When I trigger a delta extract
   And I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
   And I request latest delta via API for tenant "Midgar", lea "<IL-DAYBREAK>" with appId "<app id>" clientId "<client id>"
   #And I untar and decrypt the "API" delta tarfile for tenant "Midgar" and appId "<app id>"
   And I should receive a return code of 200
   And I download and decrypt the file
    Then I should see "1" bulk extract files
     And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
     And The "educationOrganization" delta was extracted in the same format as the api

Scenario: Update an existing edOrg with invalid API call, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"     
  When I GET the response body for a "school" in "<IL-DAYBREAK>"
    Then I should receive a return code of 200 
  When I "PUT" the "invalidEntry" for a "school" entity to "WHOOPS"
    Then I should receive a return code of 404
  And deltas collection should have "0" records

Scenario: Create an invalid edOrg with the API, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/vnd.slc+json"

  When I POST an entity of type "invalidEducationOrganization"
    Then I should receive a return code of 403   
  And deltas collection should have "0" records

Scenario: Delete an existing school with API call, verify delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
  When I GET the response body for a "school" in "<IL-DAYBREAK>"
    Then I should receive a return code of 200
  When I "DELETE" the "orphanEdorg" for a "school" entity to "null"
    Then I should receive a return code of 204
  When I trigger a delta extract
    And I verify "1" delta bulk extract files are generated for "<lea1_id>" in "Midgar"
    And I verify "1" delta bulk extract files are generated for "<lea2_id>" in "Midgar"

@wip
Scenario: Patch an existing school with API call, verify delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"
  When I "PATCH" the "postalCode" for a "school" entity to "11012"
    Then I should receive a return code of 204

  When I trigger a delta extract
   #And I request the latest bulk extract delta through the API
   And I untar and decrypt the "inBloom" delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"

@wip
Scenario: PATCH the zip code of an edOrg, trigger delta, verify contents
  When I trigger a delta extract
   And I log in to the "SDK Sample" as "jstevenson" and get a token
   And I request the latest bulk extract delta
   And I untar and decrypt the tarfile with cert "<app id>"

  Then I should see "0" entities of type "educationOrganization" in the bulk extract deltas tarfile
   And a "educationOrganization" was extracted in the same format as the api
   And each extracted "educationOrganization" delta matches the mongo entry

Scenario: Be a good neighbor and clean up before you leave
        Given I clean the bulk extract file system and database

@wip
Scenario: deltas for student/studentSchoolAssociation/studentAssessment and studentGradebookEntry
  All entities belong to lea1 which is IL-DAYBREAK, we should only see a delta file for lea1
  and nothing is generated for lea2.
  Updated two students, 11 and 12, 12 lost contextual resolution to LEA1, so it should not appear
  in the extract file.  
  Given I clean the bulk extract file system and database
  And I ingested "student_high_cardinality_entities.zip" dataset

  When I trigger a delta extract
     And I verify "1" delta bulk extract files are generated for "<lea1_id>" in "Midgar" 
     And I verify "0" delta bulk extract files are generated for "<lea2_id>" in "Midgar" 
     And I verify the last delta bulk extract by app "19cca28d-7357-4044-8df9-caad4b1c8ee4" for "<lea1_id>" in "Midgar" contains a file for each of the following entities:
       |  entityType                            |
       |  student                               |
       |  studentSchoolAssociation              | 
       |  studentAssessment                     | 
       |  studentGradebookEntry                 |
  
     And I verify this "student" file contains:
         | id                                          | condition                                |
         | 9be61921ddf0bcd3d58fb99d4e9c454ef5707eb7_id | studentUniqueStateId = 11                |
     And I verify this "studentSchoolAssociation" file contains:
         | id                                          | condition                                |
         | 68c4855bf0bdcc850a883d88fdf953b9657fe255_id | exitWithdrawDate = 2014-05-31            |
  
     And The "student" delta was extracted in the same format as the api
     And The "studentSchoolAssociation" delta was extracted in the same format as the api
     And The "studentAssessment" delta was extracted in the same format as the api
     And The "studentGradebookEntry" delta was extracted in the same format as the api
