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
When zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue      | searchType           |
     | student                     | 1                   | metaData.tenantId           | TENANT           | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "tenant.xml records considered: 1" in the resulting batch job file
  And I should see "tenant.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "tenant.xml records failed: 0" in the resulting batch job file
  
  
Scenario: Add a landing zone to an existing tenant
Given I add a new landing zone for "IL-Newtowne"
  And I am using preconfigured Ingestion Landing Zone for "IL-Newtowne"
  And I post "tenant.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
When zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue      | searchType           |
     | student                     | 1                   | metaData.tenantId           | IL               | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "tenant.xml records considered: 1" in the resulting batch job file
  And I should see "tenant.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "tenant.xml records failed: 0" in the resulting batch job file