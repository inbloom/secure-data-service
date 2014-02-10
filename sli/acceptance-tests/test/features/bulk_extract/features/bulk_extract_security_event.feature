@RALLY_US5609

Feature: BEEP will log security events for each request

Background:
    Given the sli securityEvent collection is empty

Scenario: An authorized bulk extract user logs in and gets the information for the extract from a HEAD call
    Given I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    Given I clean the bulk extract file system and database
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database
    And in my list of rights I have BULK_EXTRACT
    Then I trigger a bulk extract
    And I post "ExtendStaffEdorgAssociation.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ExtendStaffEdorgAssociation.zip" is completed in database
    And I check to find if record is in collection:
      | collectionName              			   | expectedRecordCount | searchParameter     | searchValue                                 | searchType           |
      | staffEducationOrganizationAssociation   | 3                   | body.staffReference | e4320d0bef725998faa8579a987ada80f254e7be_id | string               |
    Then I trigger a delta extract

  Scenario: Security Event is logged when I retrieve LEA data
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    When I get back a response code of "200"
    Then a security event matching "Received request to stream Edorg data" should be in the sli db
    Then a security event matching "Successful request for singlePartFileResponse" should be in the sli db

  Scenario: Security Event is logged when I attempt to retrieve a bulk extract for a non-bulk extract application
    Given I update the "application" with ID "19cca28d-7357-4044-8df9-caad4b1c8ee4" field "body.isBulkExtract" to "false" on the sli database
    And in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    Given the sli securityEvent collection is empty
    When I make a call to the bulk extract end point "/bulk/extract/list"
    Then I get back a response code of "403"
       And I should see a count of "2" in the security event collection
       And I check to find if record is in sli db collection:
         | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                | searchType |
         | securityEvent   | 2                   | body.appId              | vavedRa9uB                                                 | string     |
         | securityEvent   | 2                   | body.tenantId           | Midgar                                                     | string     |
         | securityEvent   | 2                   | body.userEdOrg          | IL-DAYBREAK                                                | string     |
         | securityEvent   | 2                   | body.targetEdOrgList    | IL-DAYBREAK                                                | string     |
         | securityEvent   | 1                   | body.logMessage         | Access Denied:Application is not approved for bulk extract | string     |
         | securityEvent   | 1                   | body.className          | org.slc.sli.api.security.context.resolver.AppAuthHelper    | string     |
       And "2" security event with field "body.actionUri" matching "http.*/api/rest/v1.5/bulk/extract/list" should be in the sli db
    Given I update the "application" with ID "19cca28d-7357-4044-8df9-caad4b1c8ee4" field "body.isBulkExtract" to "true" on the sli database

  Scenario: Security Event is logged when I retrieve public data
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/public"
    When I get back a response code of "200"
    Then a security event matching "Received request to stream public data" should be in the sli db
    Then a security event matching "Successful request for singlePartFileResponse" should be in the sli db

  Scenario: Security Event is logged when I retrieve SEA/LEA list
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/list"
    When I get back a response code of "200"
    Then a security event matching "Received request for list of links for all edOrgs and public data for this user/app" should be in the sli db
    Then a security event matching "Successful request for list of links for all edOrgs and public data for this user/app" should be in the sli db

  Scenario: Security Event is logged when I retrieve BE delta data
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I request latest delta via API for tenant "Midgar", lea "<IL-DAYBREAK>" with appId "<app id>" clientId "<client id>"
    When I get back a response code of "200"
    Then a security event matching "Received request to stream Edorg delta bulk extract data" should be in the sli db
    Then a security event matching "Successful request for singlePartFileResponse" should be in the sli db

  Scenario: Security Event is logged when I retrieve partial BE files
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And I delete the previous tar file if it exists
    When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request
    When I prepare the custom headers for multiple byte ranges "in the first multipart call"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    Then a security event matching "Received request to stream Edorg data" should be in the sli db
    Then a security event matching "Successful request for multiPartsFileResponse" should be in the sli db

  Scenario: SecurityEvent is logged when a bulk extract after it's been triggered but is missing from the filesystem
    Given the extraction zone is empty
    And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "404"
    Then a security event matching "No bulk extract file found for : LEA_DAYBREAK_ID" should be in the sli db

Scenario: SecurityEvent is logged when BE file is missing
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "404"
    Then a security event matching "No bulk extract support for : LEA_DAYBREAK_ID" should be in the sli db

Scenario: Security Event is logged when access denied
  Given I trigger a bulk extract
  And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
  When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID" using the certificate for app "pavedz00ua"
  Then I get back a response code of "403"

Scenario: Security Event is logged client does not provide Cert
  Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
  When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID" without a certificate
  Then I get back a response code of "400"
  Then a security event matching "App must provide client side X509 Certificate" should be in the sli db

Scenario: Security Event is logged when header preconditions are not met
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request
    When the If-Unmodified-Since header field is set to "BEFORE"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"
    Then a security event matching "Bulk Extract request header preconditions failed" should be in the sli db

Scenario: Security Event is logged when range headers are incorrect
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request
    When I prepare the custom headers for byte range from "past the end of the file" to "way past the end of the file"
    And I make a custom bulk extract API call
    Then I get back a response code of "416"
    Then a security event matching "If Range is not syntactically valid" should be in the sli db
