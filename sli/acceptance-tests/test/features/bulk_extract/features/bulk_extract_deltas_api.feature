Feature: Retrived through the api a generated delta bulk extract file, and validate the file

Scenario: Initialize security trust store for Bulk Extract application and LEAs
    And The bulk extract app has been approved for "Midgar-DAYBREAK" with client id "<clientId>"
    And The X509 cert "cert" has been installed in the trust store and aliased

Scenario: Generate a bulk extract day 0 delta    
  When inBloom generates a bulk extract delta file
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

  When inBloom generates a bulk extract delta file
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

  When inBloom generates a bulk extract delta file
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

  When inBloom generates a bulk extract delta file
    Then there should be no deltas

Scenario: Create a new education organization through the API and perform delta
  Given I clean the bulk extract file system and database
    And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And format "application/vnd.slc+json"

  When I POST an entity of type "educationOrganization"
    Then I should receive a return code of 201   

  When inBloom generates a bulk extract delta file
   And I untar and decrypt the delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"
     Then I should see "1" bulk extract files
      And The "educationOrganization" delta was extracted in the same format as the api

Scenario: Update an existing education organization through the API and perform delta
Given I clean the bulk extract file system and database
  And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  And format "application/json"  
    When I GET the response body for a "school" in "<IL-DAYBREAK>"
      Then I should receive a return code of 200
  
  When I PUT the "postalCode" for a "school" entity to "11012"
    Then I should receive a return code of 204

  When inBloom generates a bulk extract delta file
   #And I request the latest bulk extract delta through the API
   And I untar and decrypt the delta tarfile for tenant "Midgar" and appId "19cca28d-7357-4044-8df9-caad4b1c8ee4"

  Then I should see "1" bulk extract files
   And The "educationOrganization" delta was extracted in the same format as the api

@wip
Scenario: Update an existing education organization with an invalid API call
Given format "application/json"
  When I "PUT" a "invalid" entity of type "educationOrganization"
    Then I should receive a return code of 404

  When inBloom generates a bulk extract delta file
   And I log in to the "SDK Sample" as "jstevenson" and get a token
   And I request the latest bulk extract delta
   And I untar and decrypt the tarfile with cert "<cert>"

  Then I should see "0" entities of type "educationOrganization" in the bulk extract deltas tarfile
   And a "educationOrganization" was extracted in the same format as the api
   And each extracted "educationOrganization" delta matches the mongo entry
