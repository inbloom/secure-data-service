@RALLY_DE2347 @DE2678

Feature: Duplicate detection modes

#  As an ingestion user, I want to be able to control duplicate detection (record hashing) to ignore suspect data, disable it completely and test its performance
# assertions for sli db are to verify non-regression of DE2678

Background: I have a landing zone route configured
  Given I am using local data store

Scenario: Ingest and reingest with default mode
  Given I am using preconfigured Ingestion Landing Zone
  And the landing zone is reinitialized
  And the following collections are empty in datastore:
    | collectionName              |
    | recordHash                  |
  And the following collections are empty in sli datastore:
    | collectionName              |
    | recordHash                  |
  And I post "TinyDataSet.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job for file "TinyDataSet.zip" is completed in database
  And a batch job log has been created
  And I should not see a warning log file created
  And I should not see an error log file created
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count |
    | recordHash                  | 1     |
  Then I should see following map of entry counts in the corresponding sli db collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
When the landing zone is reinitialized
  And I post "TinyDataSet.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone with name "TinyDataSet2.zip"
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job for file "TinyDataSet2.zip" is completed in database
  And a batch job log has been created
  And I should not see a warning log file created
  And I should not see an error log file created
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count |
    | recordHash                  | 1     |
  Then I should see following map of entry counts in the corresponding sli db collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml stateEducationAgency 1 deltas" in the resulting batch job file

Scenario: Ingest then use mode reset
  Given I am using preconfigured Ingestion Landing Zone
  And the landing zone is reinitialized
  And the following collections are empty in datastore:
    | collectionName              |
    | recordHash                  |
  And the following collections are empty in sli datastore:
    | collectionName              |
    | recordHash                  |
  And I post "TinyDataSet.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job for file "TinyDataSet.zip" is completed in database
  And a batch job log has been created
  And I should not see a warning log file created
  And I should not see an error log file created
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count |
    | recordHash                  | 1     |
  Then I should see following map of entry counts in the corresponding sli db collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
  When the landing zone is reinitialized
  And I post "TinyDataSetDDreset.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job for file "TinyDataSetDDreset.zip" is completed in database
  And a batch job log has been created
  And I should not see a warning log file created
  And I should not see an error log file created
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count |
    | recordHash                  | 1     |
  Then I should see following map of entry counts in the corresponding sli db collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "duplicate-detection: reset" in the resulting batch job file

Scenario: Ingest then use mode disable
  Given I am using preconfigured Ingestion Landing Zone
  And the following collections are empty in datastore:
    | collectionName              |
    | recordHash                  |
  And the following collections are empty in sli datastore:
    | collectionName              |
    | recordHash                  |
  And the landing zone is reinitialized
  And I post "TinyDataSet.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job for file "TinyDataSet.zip" is completed in database
  And a batch job log has been created
  And I should not see a warning log file created
  And I should not see an error log file created
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count |
    | recordHash                  | 1     |
  Then I should see following map of entry counts in the corresponding sli db collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
  When the landing zone is reinitialized
  And I post "TinyDataSetDDdisable.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job for file "TinyDataSetDDdisable.zip" is completed in database
  And a batch job log has been created
  And I should not see a warning log file created
  And I should not see an error log file created
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  Then I should see following map of entry counts in the corresponding sli db collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "duplicate-detection: disable" in the resulting batch job file

Scenario: Ingest initially with debugdrop
  Given I am using preconfigured Ingestion Landing Zone
  And the following collections are empty in datastore:
    | collectionName              |
    | recordHash                  |
  And the following collections are empty in sli datastore:
    | collectionName              |
    | recordHash                  |
  And the landing zone is reinitialized
  And I post "TinyDataSetDDdebugdrop.zip" file as the payload of the ingestion job
  And zip file is scp to ingestion landing zone
  And I am willing to wait upto 60 seconds for ingestion to complete
  And a batch job for file "TinyDataSetDDdebugdrop.zip" is completed in database
  And a batch job log has been created
  And I should not see a warning log file created
  And I should not see an error log file created
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  Then I should see following map of entry counts in the corresponding sli db collections:
    | collectionName              | count |
    | recordHash                  | 0     |
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml stateEducationAgency 1 deltas" in the resulting batch job file
  And I should see "duplicate-detection: debugdrop" in the resulting batch job file
