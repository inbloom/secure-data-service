@RALLY_US206
@RALLY_US1736
@RALLY_US2033
@RALLY_US4112
@DE2294
Feature: Tenant Purge Test

Background: I have a landing zone route configured
Given I am using local data store

 Scenario: Purge a tenant, keeping the application edorgs.
Given I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
  And I post "TenantNoPurge.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
    | collectionName              |
    | student                     |
When zip file is scp to ingestion landing zone
  And a batch job for file "TenantNoPurge.zip" is completed in database
  And a batch job log has been created
 Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 2     |
     | educationOrganization       | 8     |
     | applicationAuthorization    | 14    |
   And I check to find if record is in collection:
     | collectionName   | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | student          | 1                   | body.studentUniqueStateId   | 530425896               | string               |
     | student          | 1                   | body.studentUniqueStateId   | 814202099               | string               |
And I should not see an error log file created
And I should not see a warning log file created
 And I should see "InterchangeStudent.xml records considered: 2" in the resulting batch job file
 And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file

When I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
 And I have checked the counts of the following collections:
     | collectionName           |
     | application              |
     | realm                    |
     | tenant                   |
     | roles                    |
     | securityEvent            |
     | customRole               |
 And application "d0b2ded4-89a9-db4a-8f80-aaece6fda529" has "13" authorized edorgs
 And I post "TenantPurgeKeepEdOrgs.zip" file as the payload of the ingestion job
 When zip file is scp to ingestion landing zone
 And a batch job for file "TenantPurgeKeepEdOrgs.zip" is completed in database
 And a batch job log has been created
 Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | student                     | 0     |
      | educationOrganization       | 8     |
      | applicationAuthorization    | 14    |
   And the following collections counts are the same:
     | collectionName           |
     | application              |
     | realm                    |
     | tenant                   |
     | roles                    |
     | securityEvent            |
     | customRole               |
   And application "d0b2ded4-89a9-db4a-8f80-aaece6fda529" has "13" authorized edorgs
 And I should not see an error log file created

 Scenario: Purge a tenant, cleaning out the application edorgs.
Given I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
  And I post "TenantNoPurge.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
    | collectionName              |
    | student                     |
When zip file is scp to ingestion landing zone
  And a batch job for file "TenantNoPurge.zip" is completed in database
  And a batch job log has been created
 Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 2     |
     | educationOrganization       | 8     |
     | applicationAuthorization    | 14    |
   And I check to find if record is in collection:
     | collectionName   | expectedRecordCount | searchParameter             | searchValue             | searchType           |
     | student          | 1                   | body.studentUniqueStateId   | 530425896               | string               |
     | student          | 1                   | body.studentUniqueStateId   | 814202099               | string               |
And I should not see an error log file created
And I should not see a warning log file created
 And I should see "InterchangeStudent.xml records considered: 2" in the resulting batch job file
 And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file

When I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
 And I have checked the counts of the following collections:
     | collectionName           |
     | application              |
     | realm                    |
     | tenant                   |
     | roles                    |
     | securityEvent            |
     | customRole               |
 And application "d0b2ded4-89a9-db4a-8f80-aaece6fda529" has "13" authorized edorgs
 And I post "TenantPurge.zip" file as the payload of the ingestion job
 When zip file is scp to ingestion landing zone
 And a batch job for file "TenantPurge.zip" is completed in database
 And a batch job log has been created
 Then I should see following map of entry counts in the corresponding collections:
      | collectionName              | count |
      | student                     | 0     |
      | applicationAuthorization    | 0     |
      | educationOrganization       | 0     |
   And the following collections counts are the same:
     | collectionName           |
     | application              |
     | realm                    |
     | tenant                   |
     | roles                    |
     | securityEvent            |
     | customRole               |
   And application "d0b2ded4-89a9-db4a-8f80-aaece6fda529" has "10" authorized edorgs
 And I should not see an error log file created
 And I should not see a warning log file created

