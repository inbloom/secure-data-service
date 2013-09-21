@RALLY_US5594

Feature: As an API user, I want to be able to get a list of links available to the user.

  Background: An authorized bulk extract user logs in and gets the information for the extract from a HEAD call
	Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"

  Scenario: Get the URL I should use to get the latest full and delta bulk extract for a given LEA
    Given I clean the bulk extract file system and database
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database

    # Make IL-DAYBREAK a charter school to verify bulk extract will work
    Given I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And format "application/json"
    Then I PATCH the "organizationCategories" field of the entity specified by endpoint "educationOrganizations/1b223f577827204a1c7e9c851dba06bea6b031fe_id" to '[ "School", "Local Education Agency" ]'

    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    Then I trigger a bulk extract
    And I post "ExtendStaffEdorgAssociation.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "ExtendStaffEdorgAssociation.zip" is completed in database
  	And I check to find if record is in collection:
  	 | collectionName              			   | expectedRecordCount | searchParameter     | searchValue                                 | searchType           |
     | staffEducationOrganizationAssociation   | 3                   | body.staffReference | e4320d0bef725998faa8579a987ada80f254e7be_id | string               |
    Then I trigger a delta extract
  	When I make a call to the bulk extract end point "/v1.1/bulk/extract/list"
	When I get back a response code of "200"
	When the number of returned URLs is correct:
	|   fieldName  | count |
	|   fullEdOrgs   |  1    |
	|   deltaEdOrgs  |  1    |
	|   fullSea    |  1    |
	|   deltaSea   |  1    |
    When I set the header format to "application/x-tar"
	And I make a head request with each returned URL

Scenario: Login as a user not directly associated with the SEA, SEA extract should be in the list
  Then I log into "SDK Sample" with a token of "jstevenson", a "Noldor" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
  When I make a call to the bulk extract end point "/v1.1/bulk/extract/list"
  When I get back a response code of "200"
  When the number of returned URLs is correct:
  |   fieldName  | count |
  |   fullEdOrgs   |  1    |
  |   deltaEdOrgs  |  1    |
  |   fullSea    |  1    |
  |   deltaSea   |  1    |
  And I make a head request with each returned URL

  Scenario: Validate that the delta extracts are in time order, most recent first
    Given I post "new_edorg_in_daybreak.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "new_edorg_in_daybreak.zip" is completed in database
    Then I should not see an error log file created
    And I should not see a warning log file created
    And I trigger a delta extract
    When I make a call to the bulk extract end point "/bulk/extract/list"
    And I get back a response code of "200"
    Then there are 2 total number of delta links in the list
    And I verify that the delta extract URLs are in time order, most recent first

  Scenario: No URL is returned if I am not associated with the SEA or top LEA
    Given I am a valid 'service' user with an authorized long-lived token "438e472e-a888-46d1-8087-0195f4e37089"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/v1.1/bulk/extract/list"
	When I get back a response code of "403"

  Scenario: Try getting a list when there are no extracts to list
    Given I clean the bulk extract file system and database
    And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/bulk/extract/list"
    Then I get back a response code of "200"
    And the response list is empty

