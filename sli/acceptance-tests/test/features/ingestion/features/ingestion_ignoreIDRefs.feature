@RALLY_US4456
Feature: Ingestion Ignores IDRefs Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing InterchangeAssessmentMetadata with ID References job: Clean Database
Given I post "ingestion_IDReferences.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName           |
     | assessment               |
     | assessmentFamily         |
When zip file is scp to ingestion landing zone
  And a batch job for file "ingestion_IDReferences.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | assessment                   |   1     |
     | assessmentFamily             |   1     |
   And I check to find if record is in collection:
     | collectionName      | expectedRecordCount   | searchParameter                                | searchValue       |
     | assessment          | 1                     | objectiveAssessment.body.identificationCode    | ACT-English       |
     | assessment          | 1                     | objectiveAssessment.body.identificationCode    | ACT-Mathematics   |
     | assessment          | 1                     | objectiveAssessment.body.identificationCode    | ACT-Reading       |
     | assessment          | 1                     | objectiveAssessment.body.identificationCode    | ACT-Science       |
     | assessment          | 1                     | objectiveAssessment.body.identificationCode    | ACT-Writing       |
  And I should see "Processed 18 records." in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ACT.xml records ingested successfully: 18" in the resulting batch job file
  And I should see "All records processed successfully" in the resulting batch job file
  And I should not see an error log file created
  And I should not see a warning log file created
