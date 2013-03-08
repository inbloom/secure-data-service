@RALLY_DE1277
Feature: Order Agnostic Lists in KeyFields

Background: I have done backgroundy things
Given I am using local data store
And I am using preconfigured Ingestion Landing Zone

Scenario: I ingest an update to a pre-existing object, which has an unordered list as one of its keyfields
Given I post "orderAgnosticList.zip" file as the payload of the ingestion job
 And I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | staffProgramAssociation                  |                 1|
 And I check to find if record is in collection:
     | collectionName          | expectedRecordCount | searchParameter | searchValue | searchType |
     | staffProgramAssociation | 0                   | body.endDate    | 2012-05-27  | string     |
 When zip file is scp to ingestion landing zone
#When "90" seconds have elapsed
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job for file "orderAgnosticList.zip" is completed in database
  Then I should see "Processed 1 records." in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records considered for processing: 1" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records ingested successfully: 1" in the resulting batch job file
    And I should see "InterchangeStaffAssociation.xml records failed processing: 0" in the resulting batch job file
    And I should not see an error log file created
    And I should see following map of entry counts in the corresponding collections:
     | collectionName                           |              count|
     | staffProgramAssociation                  |                 1|
And I check to find if record is in collection:
     | collectionName          | expectedRecordCount | searchParameter | searchValue | searchType |
     | staffProgramAssociation | 1                   | body.endDate    | 2012-05-27  | string     |
