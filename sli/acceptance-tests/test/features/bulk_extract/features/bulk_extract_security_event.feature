@RALLY_US5609

Feature: BEEP will log security events for each request

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

   Scenario: Security Event is log when I retrieve Edorg data
