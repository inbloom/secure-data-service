@RALLY_US1736
@RALLY_US2033
Feature: Tenant Purge Test

Background: I have a landing zone route configured
Given I am using local data store

 Scenario: Post a zip file containing student
Given I am using preconfigured Ingestion Landing Zone for "IL-Daybreak"
  And I post "TenantNoPurgeDefault.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
    | collectionName              |
    | student                     |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 72    |
  And I should see "All records processed successfully" in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudentDefault.xml records considered: 72" in the resulting batch job file
  And I should see "InterchangeStudentDefault.xml records failed: 0" in the resulting batch job file

 Scenario: Post a zip file containing student from a different tenant
Given I am using preconfigured Ingestion Landing Zone for "NY-NYC"
  And I post "TenantNoPurge.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
 Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 74    |
   And I check to find if record is in collection:
     | collectionName   | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | student          | 2                   | metaData.tenantId           | NY                      | string               |
And I should not see an error log file created
 And I should see "InterchangeStudent.xml records considered: 2" in the resulting batch job file
 And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file

 Scenario: Post a zip file containing purge configuration
 Given I am using preconfigured Ingestion Landing Zone for "NY-NYC"
   And I post "TenantPurge.zip" file as the payload of the ingestion job
 When zip file is scp to ingestion landing zone
  And a batch job log has been created
 Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | student                     | 72    |
   And I check to find if record is in collection:
     | collectionName   | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | student          | 0                   | metaData.tenantId           | NY                      | string               |
 And I should not see an error log file created