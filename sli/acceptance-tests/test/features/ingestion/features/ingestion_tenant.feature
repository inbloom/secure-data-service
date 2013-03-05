@RALLY_US2169
Feature: Tenant Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Create a tenant
Given I add a new tenant for "TENANT-EDORG"
  And I am using preconfigured Ingestion Landing Zone for "TENANT-EDORG"
  And I post "tenant.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | recordHash                  |
When zip file is scp to ingestion landing zone
  And I am willing to wait upto 120 seconds for ingestion to complete
  And a batch job for file "tenant.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue      | searchType           |
     | student                     | 1                   | body.studentUniqueStateId   | 530425896        | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should not see a warning log file created
  And I should see "tenant.xml records considered: 1" in the resulting batch job file
  And I should see "tenant.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "tenant.xml records failed: 0" in the resulting batch job file

Scenario: Add a landing zone to an existing tenant
Given I add a new landing zone for "Midgar-Newtowne.te_\#&5st@wgen.net"
  And I am using preconfigured Ingestion Landing Zone for "Midgar-Newtowne.te_\#&5st@wgen.net"
  And I post "tenant.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | recordHash                  |
When zip file is scp to ingestion landing zone with name "tenant2.zip"
  And I am willing to wait upto 120 seconds for ingestion to complete
  And a batch job for file "tenant2.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue      | searchType           |
     | student                     | 1                   | body.studentUniqueStateId   | 530425896        | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should not see a warning log file created
  And I should see "tenant.xml records considered: 1" in the resulting batch job file
  And I should see "tenant.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "tenant.xml records failed: 0" in the resulting batch job file