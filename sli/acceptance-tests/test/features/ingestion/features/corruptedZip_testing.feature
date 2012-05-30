Feature: Negative XXE Injection Testing

Background: I have a landing zone route configured
    Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a corrupted zip file to verify we report an error 
  Given I post "CorruptedZip.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job log has been created
  And I should see "Processed 0 records." in the resulting batch job file

  And I should see "ERROR  Could not read .zip archive CorruptedZip.zip." in the resulting error log file

