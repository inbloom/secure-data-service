Feature: Session Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing empty control File as a payload of the ingestion job: Clean Database
  Given I post "EmptyCtlFile.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And I am willing to wait upto 30 seconds for ingestion to complete
  And a batch job log has been created
  And I should see "Processed 0 records." in the resulting batch job file
  And I should see "ERROR  No files specified in control file." in the resulting error log file
