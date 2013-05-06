Feature: Negative XXE Injection Testing

Background: I have a landing zone route configured
    Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file with bad control file 
  Given I post "BadCtlFile.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And a batch job for file "BadCtlFile.zip" is completed in database
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file

