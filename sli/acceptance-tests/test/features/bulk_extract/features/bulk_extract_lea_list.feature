@RALLY_US5594

Feature: As an API user, I want to be able to get a list authorized LEAs.

Background: An authorized bulk extract user logs in and gets the information for the extract from a HEAD call
	Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "ExtendStaffEdorgAssociation.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
  	And a batch job for file "ExtendStaffEdorgAssociation.zip" is completed in database
  	And I check to find if record is in collection:
  	 | collectionName              			   | expectedRecordCount | searchParameter     | searchValue                                 | searchType           |
     | staffEducationOrganizationAssociation   | 3                   | body.staffReference | e4320d0bef725998faa8579a987ada80f254e7be_id | string               |

    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    Then I trigger a delta extract

Scenario: Get the URL I should use to get the latest full bulk extract for a given LEA
	When I make a call to the bulk extract end point "/v1.1/bulk/extract/list"
	When I get back a response code of "200"
	When the number of returned URLs is correct:
	|   fieldName  | count |
	|   fullLeas   |  1    |
	|   deltaLeas  |  1    |
#	|   fullSea    |  1    |
#	|   deltaSea   |  1    |
	Then I make a head request with each returned URL

Scenario: No URL is returned if I am not associated with the SEA or top LEA
    Given I am a valid 'service' user with an authorized long-lived token "438e472e-a888-46d1-8087-0195f4e37089"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/v1.1/bulk/extract/list"
	When I get back a response code of "403"
