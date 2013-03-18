@RALLY_USUS3696
Feature: LearningObjective Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "learningObjective_Full.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | learningObjective           |
     | learningStandard            |
When zip file is scp to ingestion landing zone
  And a batch job for file "learningObjective_Full.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 5     |
     | learningStandard            | 3     |
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective3"
  And verify 1 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective2" and its field "body.parentLearningObjective" references this document
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective4"
  And verify 1 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective3" and its field "body.parentLearningObjective" references this document
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective5"
  And verify 1 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective4" and its field "body.parentLearningObjective" references this document
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective4"
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective5"
  And verify 0 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective2" and its field "body.parentLearningObjective" references this document
  And verify 0 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective2" and its field "body.parentLearningObjective" references this document
  And verify 0 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective3" and its field "body.parentLearningObjective" references this document
  And verify 0 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective4" and its field "body.parentLearningObjective" references this document

  And I should see "Processed 8 records." in the resulting batch job file
  And I should not see an error log file created


Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "learningObjective_Partial_Happy.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "learningObjective_Partial_Happy.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 10    |
     | learningStandard            | 5     |
  
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective11"
  And verify 1 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective13" and its field "body.parentLearningObjective" references this document
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective12"
  And verify 1 "learningObjective" record(s) where "body.learningObjectiveId.identificationCode" equals "Objective14" and its field "body.parentLearningObjective" references this document
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective13"
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective14"
  And I find a(n) "learningObjective" record where "body.learningObjectiveId.identificationCode" is equal to "Objective15"
  
  And I should see "Processed 7 records." in the resulting batch job file
  And I should not see an error log file created

