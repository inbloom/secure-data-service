@RALLY_US1387
@RALLY_US2033
Feature: TenantId Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Specified Tenant
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
  And I post "idNamespace.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
When zip file is scp to ingestion landing zone
  And a batch job for file "idNamespace.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue      | searchType           |
     | student                     | 1                   | body.studentUniqueStateId   | 530425896        | string               |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "idNamespace.xml records considered: 1" in the resulting batch job file
  And I should see "idNamespace.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "idNamespace.xml records failed: 0" in the resulting batch job file
  And I should not see a warning log file created

# There is no longer a default tenant, every landing zone has to be associated with a tenant. Can we remove this scenario?
@wip
Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Default Tenant
Given I post "idNamespaceDefault.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
When zip file is scp to ingestion landing zone
  And a batch job for file "idNamespaceDefault.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "idNamespace.xml records considered: 1" in the resulting batch job file
  And I should see "idNamespace.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "idNamespace.xml records failed: 0" in the resulting batch job file
  And I should not see a warning log file created
