@RALLY_US5642

Feature: List a purge as a single event in the delta extract

  Background: Setup the deltas for purges
    Given I clean the bulk extract file system and database
    And I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"

  Scenario: Setup tenant first
    Given all collections are empty
    And I successfully ingest "StoriedDataSet_IL_Daybreak.zip"

  Scenario: The delta extract has a single event for a purge
    Given I successfully ingest "TenantPurgeKeepEdOrgs.zip"
    And I trigger a delta extract
    When I verify the last delta bulk extract by app "<app id>" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
      |  entityType                            |
      |  deleted                               |
    Then the delete file in the delta extract should have one purge entry

    Given I successfully ingest "TenantPurgeKeepEdOrgs.zip"
    And I successfully ingest "DeleteDaybreakElementary.zip"
    And the following collections are empty in batch job datastore:
      | collectionName |
      | newBatchJob    |
    And I successfully ingest "TenantPurgeKeepEdOrgs.zip"
    And I successfully ingest "DeleteDaybreakJuniorHigh.zip"
    And the extraction zone is empty
    And the bulk extract files in the database are scrubbed
    And I trigger a delta extract
    When I verify the last delta bulk extract by app "<app id>" for "<IL-DAYBREAK>" in "Midgar" contains a file for each of the following entities:
      |  entityType                            |
      |  deleted                               |
    Then the delete file in the delta extract should have one purge entry
    And I verify this "deleted" file should contain:
      | id                                               | condition                             |
      | 772a61c687ee7ecd8e6d9ad3369f7883409f803b_id      | entityType = educationOrganization    |
    And I verify this "deleted" file should not contain:
      | id                                               | condition                             |
      | 352e8570bd1116d11a72755b987902440045d346_id      |                                       |

  Scenario: Do a complete purge, reingest, authorize app, and delta extract should have a purge event
    Given I successfully ingest "TenantPurge.zip"
    And I successfully ingest "StoriedDataSet_IL_Sunset.zip"
    And all LEAs in "Midgar" are authorized for "SDK Sample"
    And I trigger a delta extract
    When I verify the last delta bulk extract by app "<app id>" for "<IL-SUNSET>" in "Midgar" contains a file for each of the following entities:
      |  entityType                            |
      |  student                               |
      |  studentSchoolAssociation              |
      |  studentSectionAssociation             |
      |  section                               |
      |  staff                                 |
      |  staffEducationOrganizationAssociation |
      |  teacher                               |
      |  teacherSchoolAssociation              |
      |  teacherSectionAssociation             |
      |  deleted                               |
    Then the delete file in the delta extract should have one purge entry