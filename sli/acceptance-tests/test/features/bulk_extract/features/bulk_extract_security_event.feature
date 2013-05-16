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

  Scenario: Security Event is log when I retrieve LEA data
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/1b223f577827204a1c7e9c851dba06bea6b031fe_id"
    When I get back a response code of "200"
    Then a security event matching "Received request to stream Edorg data" should be in the sli db
    Then a security event matching "Successful request for singlePartFileResponse" should be in the sli db

  Scenario: Security Event is log when I retrieve SEA data
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/b64ee2bcc92805cdd8ada6b7d8f9c643c9459831_id"
    When I get back a response code of "200"
    Then a security event matching "Received request to stream Edorg data" should be in the sli db
    Then a security event matching "Successful request for singlePartFileResponse" should be in the sli db

  Scenario: Security Event is log when I retrieve tenant data
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/tenant"
    When I get back a response code of "200"
    Then a security event matching "Received request to stream tenant bulk extract" should be in the sli db
    Then a security event matching "Successful request for singlePartFileResponse" should be in the sli db

  Scenario: Security Event is log when I retrieve LEA list
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I make a call to the bulk extract end point "/bulk/extract/list"
    When I get back a response code of "200"
    Then a security event matching "Received request for list of links for all LEAs for this user/app" should be in the sli db
    Then a security event matching "Successful request for list of links for all LEAs for this user/app" should be in the sli db

  Scenario: Security Event is log when I retrieve BE delta data
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    When I request latest delta via API for tenant "Midgar", lea "<IL-DAYBREAK>" with appId "<app id>" clientId "<client id>"
    When I get back a response code of "200"
    Then a security event matching "Received request to stream Edorg delta bulk extract data" should be in the sli db
    Then a security event matching "Successful request for singlePartFileResponse" should be in the sli db

  Scenario: Security Event is log when I retrieve partial tenant BE files
    Given in my list of rights I have BULK_EXTRACT
    When I log into "SDK Sample" with a token of "rrogers", a "Noldor" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And I delete the previous tar file if it exists
    When I make a call to the bulk extract end point "/bulk/extract/tenant"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request
    When I prepare the custom headers for multiple byte ranges "in the first multipart call"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    Then a security event matching "Received request to stream tenant bulk extract" should be in the sli db
    Then a security event matching "Successful request for multiPartsFileResponse" should be in the sli db

  Scenario: SecurityEvent is logged when a bulk extract after it's been triggered but is missing from the filesystem
    Given the extraction zone is empty
    And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/bulk/extract/tenant"
    Then I get back a response code of "404"
    Then a security event matching "No bulk extract support for tenant: Midgar" should be in the sli db

Scenario: SecurityEvent is logged when BE file is missing
    Given the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/bulk/extract/tenant"
    Then I get back a response code of "404"
    Then a security event matching "No bulk extract support for tenant: Midgar" should be in the sli db

Scenario: Security Event is logged when access denied
  Given I trigger a bulk extract
  And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
  When I make a call to the bulk extract end point "/bulk/extract/tenant" using the certificate for app "pavedz00ua"
  Then I get back a response code of "403"
  Then a security event matching "Access Denied!" should be in the sli db

Scenario: Security Event is logged client does not provide Cert
  Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
  When I make a call to the bulk extract end point "/bulk/extract/tenant" without a certificate
  Then I get back a response code of "400"
  Then a security event matching "App must provide client side X509 Certificate" should be in the sli db

Scenario: Security Event is logged when header preconditions are not met
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/tenant"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request
    When the If-Unmodified-Since header field is set to "BEFORE"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"
    Then a security event matching "Bulk Extract request header preconditions failed" should be in the sli db

Scenario: Security Event is logged when range headers are incorrect
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/tenant"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request
    When I prepare the custom headers for byte range from "past the end of the file" to "way past the end of the file"
    And I make a custom bulk extract API call
    Then I get back a response code of "416"
    Then a security event matching "If Range is not syntactically valid" should be in the sli db
