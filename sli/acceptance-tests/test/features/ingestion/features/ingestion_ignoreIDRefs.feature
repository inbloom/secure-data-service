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
When zip file is scp to ingestion landing zone
  And a batch job for file "ingestion_IDReferences.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName               | count   |
     | assessment                   |   1     |
   And I check to find if record is in collection:
     | collectionName      | expectedRecordCount   | searchParameter                                | searchValue       |
     | assessment          | 0                     | body.assessmentItem.0.IdentificationCode       | AssessmentItem-1  |
     | assessment          | 0                     | body.assessmentItem.1.IdentificationCode       | AssessmentItem-2  |
     | assessment          | 0                     | body.assessmentItem.2.IdentificationCode       | AssessmentItem-3  |
     | assessment          | 0                     | body.assessmentItem.3.IdentificationCode       | AssessmentItem-4  |
     | assessment          | 1                     | body.objectiveAssessment.0.identificationCode  | ACT-English       |
     | assessment          | 1                     | body.objectiveAssessment.1.identificationCode  | ACT-Mathematics   |
     | assessment          | 1                     | body.objectiveAssessment.2.identificationCode  | ACT-Reading       |
     | assessment          | 1                     | body.objectiveAssessment.3.identificationCode  | ACT-Science       |
     | assessment          | 1                     | body.objectiveAssessment.4.identificationCode  | ACT-Writing       |
  And I should see "Processed 1 records." in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-ACT.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "All records processed successfully" in the resulting batch job file
  And I should not see an error log file created
