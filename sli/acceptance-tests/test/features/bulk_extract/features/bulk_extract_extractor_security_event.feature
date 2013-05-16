@RALLY_US5725

Feature: Security events are logged when bulk extract is run

  Background: Reset bulk extract files
    Given I clean the bulk extract file system and database
    And I am using local data store
    And I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"

  Scenario: Trigger a bulk extract on an empty database
    Given all collections are empty
    And the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I trigger a bulk extract
    #Add a count to the following step once we know how many security events will be logged
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName              | count |
      | securityEvent               |       |
    #Add counts, and more log messages as we figure them out
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   |                     | body.appId              | Bulk Extract                                                                 | string          |
      | securityEvent   |                     | body.logMessage         | Bulk Extract process started.                                                | string          |


  Scenario: Trigger a full extract and check security event
    And I post "StoriedDataSet_IL_Daybreak.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "StoriedDataSet_IL_Daybreak.zip" is completed in database
    Then I should not see an error log file created
    And I should not see a warning log file created

    Given the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I trigger a bulk extract
    #Add a count to the following step once we know how many security events will be logged
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName              | count |
      | securityEvent               |       |
    #Add counts, and more log messages as we figure them out
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   |                     | body.appId              | Bulk Extract                                                                 | string          |
      | securityEvent   |                     | body.logMessage         | Bulk Extract process started.                                                | string          |

  Scenario: Trigger a delta extract and check security events
    Given I post "new_edorg_in_daybreak.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "new_edorg_in_daybreak.zip" is completed in database
    Then I should not see an error log file created
    And I should not see a warning log file created

    Given the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I trigger a delta extract
    #Add a count to the following step once we know how many security events will be logged
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName              | count |
      | securityEvent               |       |
    #Add counts, and more log messages as we figure them out
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   |                     | body.appId              | Bulk Extract                                                                 | string          |
      | securityEvent   |                     | body.logMessage         | Bulk Extract process started.                                                | string          |

  Scenario: Trigger a bulk extract on a tenant that doesn't have any authorized bulk extract apps
    Given I am using preconfigured Ingestion Landing Zone for "Hyrule"
    And all collections are empty
    And the tenant "Hyrule" does not have any bulk extract apps for any of its education organizations
    And I post "StoriedDataSet_NY.zip" file as the payload of the ingestion job
    When zip file is scp to ingestion landing zone
    And a batch job for file "StoriedDataSet_NY.zip" is completed in database
    Then I should not see an error log file created
    And I should not see a warning log file created

    Given the following collections are empty in sli datastore:
      | collectionName              |
      | securityEvent               |
    And I trigger an extract for tenant "Hyrule"
    #Add a count to the following step once we know how many security events will be logged
    Then I should see following map of entry counts in the corresponding sli db collections:
      | collectionName              | count |
      | securityEvent               |       |
    #Add counts, and more log messages as we figure them out
    And I check to find if record is in sli db collection:
      | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                  | searchType      |
      | securityEvent   |                     | body.appId              | Bulk Extract                                                                 | string          |
      | securityEvent   |                     | body.logMessage         | Bulk Extract process started.                                                | string          |
