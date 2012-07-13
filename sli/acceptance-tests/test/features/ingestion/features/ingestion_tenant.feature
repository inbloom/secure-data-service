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
  
  #This invalid case should not affect other tenants
@wip
Scenario: Add a invalid landing zone
Given I add a new named landing zone for "Midgar-STATE-DAYBREAK.test@gmail_.c?omtest!@:#$|<>;"
  And I am using preconfigured Ingestion Landing Zone for "Midgar-STATE-DAYBREAK.test@gmail_.c?omtest!@:#$|<>;"
  And I post "tenant.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
When zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
    And a batch job log has not been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 0     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue          | searchType           |
     | student                     | 0                   | metaData.tenantId           | Midgar               | string               |
  And I should not see an error log file created
  
Scenario: Add a landing zone to an existing tenant
Given I add a new landing zone for "Midgar-Newtowne.te_#&5st@wgen.net"
  And I am using preconfigured Ingestion Landing Zone for "Midgar-Newtowne.te_#&5st@wgen.net"
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
     | student                     | 1                   | metaData.tenantId           | Midgar           | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "tenant.xml records considered: 1" in the resulting batch job file
  And I should see "tenant.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "tenant.xml records failed: 0" in the resulting batch job file