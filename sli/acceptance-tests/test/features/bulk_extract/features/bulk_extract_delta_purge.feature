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
    Then the delete file in the newest delta extract should have one purge entry

    #Replace the zips in the following to actual files that deletes an edorg
    Given I successfully ingest "BroadSchoolDelete.zip"
    And I successfully ingest "TenantPurgeKeepEdOrgs.zip"
    And I successfully ingest "DeleteDaybreakJuniorHigh.zip"
    And I successfully ingest "TenantPurgeKeepEdOrgs.zip"
    And I trigger a delta extract
    Then the delete file in the newest delta extract should have one purge entry

  Scenario: Do a complete purge, reingest, authorize app, and delta extract should have a purge event
    Given I successfully ingest "TenantPurge.zip"
    And I successfully ingest "StoriedDataSet_IL_Sunset.zip"
    And all LEAs in "Midgar" are authorized for "SDK Sample"
    And I trigger a delta extract
    Then the delete file in the newest delta extract should have one purge entry