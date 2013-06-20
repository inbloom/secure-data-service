Feature: Corrupt Zip Validation

Background: I have a landing zone route configured
    Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file with an external entity that attempts to access a local file
  Given I post "corrupt.zip" zipped file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And a batch job for file "corrupt.zip" is completed in database
  And I should see "Not all records were processed completely due to errors" in the resulting batch job file
  And I should see "BASE_0021" in the resulting error log file
  And I should not see a warning log file created

