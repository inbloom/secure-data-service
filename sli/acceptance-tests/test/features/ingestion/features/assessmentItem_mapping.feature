@RALLY_US2233
Feature: Mapping AssessmentObjectives and AssessmentItems to CCS

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured Learning Objective interchanges as a payload of the ingestion job: Clean Database
Given I post "actAssessmentCCSMapping.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | assessment                  |
     | learningObjective           |
     | learningStandard            |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | assessment                  | 1     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                                                          | searchValue      |  
     | assessment                  | 1                   | body.assessmentItem.identificationCode                                                   | AssessmentItem-1 |
     | assessment                  | 1                   | body.assessmentItem.identificationCode                                                   | AssessmentItem-2 |    
     | assessment                  | 1                   | body.assessmentItem.identificationCode                                                   | AssessmentItem-3 |
     | assessment                  | 1                   | body.assessmentItem.identificationCode                                                   | AssessmentItem-4 |    
 
  And I should see "Processed 31 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "actAssessment_CCSMapping.xml records considered: 1" in the resulting batch job file
  And I should see "actAssessment_CCSMapping.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "actAssessment_CCSMapping.xml records failed: 0" in the resulting batch job file
 When I find a record in "assessment" under "body.assessmentItem" where "identificationCode" is "AssessmentItem-1"
  Then the field "learningStandards" is an array of size 2
  And "learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-C.4"
 When I find a record in "assessment" under "body.assessmentItem" where "identificationCode" is "AssessmentItem-2"
  Then the field "learningStandards" is an array of size 2
  And "learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.3"
 When I find a record in "assessment" under "body.assessmentItem" where "identificationCode" is "AssessmentItem-3"
  Then the field "learningStandards" is an array of size 1
  And "learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.5"
 When I find a record in "assessment" under "body.assessmentItem" where "identificationCode" is "AssessmentItem-4"
  Then the field "learningStandards" is an array of size 1
  And "learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.6"

