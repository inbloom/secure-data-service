Feature: Retrived through the api a generated delta bulk extract file, and validate the file

Scenario: Initialize security trust store for Bulk Extract application and LEAs
    Given the extraction zone is empty
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased

Scenario: Generate a bulk extract day 0 delta    
  When I trigger a delta extract
   And I request the latest bulk extract delta using the api
   And I untar and decrypt the delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"
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
   And I untar and decrypt the delta tarfile for tenant "Midgar" and appId "<cert>"
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
    And I untar and decrypt the delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"
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
    And I am using local data store
    And I post "deltas_delete_sea.zip" file as the payload of the ingestion job


  When the landing zone for tenant "Midgar" edOrg "Daybreak" is reinitialized
   And zip file is scp to ingestion landing zone
   And a batch job for file "deltas_delete_sea.zip" is completed in database
   And a batch job log has been created 
   Then I should not see an error log file created
    # We will see a warning file on cascading delete -- there are a lot of children of this SEA
    #And I should not see a warning log file created

    When inBloom generates a bulk extract delta file
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

@wip
Scenario: Create a new education organization through the API and perform delta
  Given I clean the bulk extract file system and database
    And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And format "application/vnd.slc+json"

  When I POST an entity of type "educationOrganization"
    Then I should receive a return code of 201   

  When I trigger a delta extract
   And I untar and decrypt the delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"
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
   #And I request the latest bulk extract delta through the API
   And I untar and decrypt the delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"

  Then I should see "1" bulk extract files
   And The "educationOrganization" delta was extracted in the same format as the api

Scenario: Update an existing edOrg with invalid API call, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"     
  When I GET the response body for a "school" in "<IL-DAYBREAK>"
    Then I should receive a return code of 200 
  When I "PUT" the "invalidEntry" for a "school" entity to "WHOOPS"
    Then I should receive a return code of 404
  When I trigger a delta extract
    Then there should be no deltas in mongo

Scenario: Create an invalid edOrg with the API, verify no delta created
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/vnd.slc+json"

  When I POST an entity of type "invalidEducationOrganization"
    Then I should receive a return code of 403   
  When I trigger a delta extract
    Then there should be no deltas in mongo

@wip
Scenario: Delete an existing school with API call, verify delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"     
  When I GET the response body for a "school" in "<IL-DAYBREAK>"
    Then I should receive a return code of 200
  When I "DELETE" the "orphanEdorg" for a "school" entity to "null"
    Then I should receive a return code of 204
  When I trigger a delta extract
    Then I should see "2" bulk extract files
     #And The "id" and "entityType" should match the deleted record

@wip
Scenario: Something
  When I trigger a delta extract
   And I log in to the "SDK Sample" as "jstevenson" and get a token
   And I request the latest bulk extract delta
   And I untar and decrypt the tarfile with cert "<cert>"

  Then I should see "0" entities of type "educationOrganization" in the bulk extract deltas tarfile
   And a "educationOrganization" was extracted in the same format as the api
   And each extracted "educationOrganization" delta matches the mongo entry
