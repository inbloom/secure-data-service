@RALLY_US206
@RALLY_US1736
@RALLY_US2033
@RALLY_US4112
Feature: Tenant Purge Test

Background: I have a landing zone route configured
Given I am using local data store

 Scenario: Post a zip file containing student
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
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
Given I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
  And I post "TenantNoPurge.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
    | collectionName              |
    | student                     |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
 Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 2     |
   And I check to find if record is in collection:
     | collectionName   | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | student          | 1                   | body.studentUniqueStateId   | 530425896               | string               |
     | student          | 1                   | body.studentUniqueStateId   | 814202099               | string               |
And I should not see an error log file created
 And I should see "InterchangeStudent.xml records considered: 2" in the resulting batch job file
 And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file

 Scenario: Post a zip file containing purge configuration
 Given I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
 And I have checked the counts of the following collections:
     |collectionName|
     | application  |
     | realm        |
     | tenant       |
     | roles        |
     | securityEvent |
     | customRole   |
 And application "d0b2ded4-89a9-db4a-8f80-aaece6fda529" has "13" authorized edorgs
 And I post "TenantPurge.zip" file as the payload of the ingestion job
 When zip file is scp to ingestion landing zone
 And a batch job log has been created
 Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | student                     | 0     |
      | applicationAuthorization    | 0     |
   And the following collections counts are the same:
     |collectionName|
     | application  |
     | realm        |
     | tenant       |
     | roles        |
     | securityEvent |
     | customRole   |
   And application "d0b2ded4-89a9-db4a-8f80-aaece6fda529" has "10" authorized edorgs
   And I check to find if record is in batch job collection:
     | collectionName           | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | recordHash               | 0                   | t                           | Hyrule                  | string               |
 And I should not see an error log file created