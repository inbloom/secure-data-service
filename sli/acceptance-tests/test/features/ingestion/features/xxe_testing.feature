@RALLY_US2191
Feature: Negative XXE Injection Testing

Background: I have a landing zone route configured
    Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file with an external entity that attempts to access a local file
  Given I post "XXETest.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job log has been created
  And I should see "InterchangeEducationOrganization.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "InterchangeEducationOrganization.xml records failed: 1" in the resulting batch job file
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 1 records." in the resulting batch job file

  And I should see "ERROR: There has been a data validation error when saving an entity" in the resulting error log file
  And I should see "     Error      REQUIRED_FIELD_MISSING" in the resulting error log file
  And I should see "     Entity     educationOrganization" in the resulting error log file
  And I should see "     Instance   1" in the resulting error log file
  And I should see "     Field      stateOrganizationId" in the resulting error log file
  And I should see "     Value      null" in the resulting error log file
  And I should see "     Expected   []" in the resulting error log file

