@wip
Feature: Mapping AssessmentObjectives and AssessmentItems to CCS

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured Learning Objective interchanges as a payload of the ingestion job: Clean Database
Given I post "actAssessmentCCSMapping.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | assessment                  |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | assessment                  | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                                        | searchValue                                      |
     | assessment                  | 1                   | body.assessment.objectiveAssessment.assessmentItem.identificationCode  | AssessmentItem-2 |
     | assessment                  | 1                   | body.assessment.objectiveAssessment.assessmentItem.identificationCode  | AssessmentItem-1 |
     | assessment                  | 1                   | body.assessment.objectiveAssessment.assessmentItem.learningStandardReference.learningStandardId  | G.SRT.2 |
     | assessment                  | 3                   | body.assessment.objectiveAssessment.assessmentItem.learningStandardReference.learningStandardId  | Reading: Informational Text |
 
  And I should see "Processed 16 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "actAssessmentCCSMapping.xml records considered: 16" in the resulting batch job file
  And I should see "actAssessmentCCSMapping.xml records ingested successfully: 16" in the resulting batch job file
  And I should see "actAssessmentCCSMapping.xml records failed: 0" in the resulting batch job file
  
