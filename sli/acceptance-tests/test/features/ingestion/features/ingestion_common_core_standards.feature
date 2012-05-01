Feature: Common Core Standard Ingestion

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone


Scenario: Post a zip file containing all configured Learning Objective interchanges as a payload of the ingestion job: Clean Database
Given I post "CommonCoreStandards/grade12English.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | learningObjective           |
     | learningStandard            |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 1     |
     | learningStandard            | 10    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                              | searchValue           		|
     | learningObjective           | 1                   | body.objective					            | Reading: Informational Text  	|
     | learningObjective           | 1                   | body.objectiveGradeLevel                   	| Twelfth grade                 |
     | learningStandard            | 10                  | body.gradeLevel                              | Twelfth grade               |
     | learningStandard            | 10                  | body.subjectArea                             | ELA    |
     | learningStandard            | 10                  | body.contentStandard                         | State Standard    |

  And I should see "Processed 11 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Grade_12_English_CCS_RI_11_12.xml records considered: 1" in the resulting batch job file
  And I should see "Grade_12_English_CCS_RI_11_12.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "Grade_12_English_CCS_RI_11_12.xml records failed: 0" in the resulting batch job file



Scenario: Post a zip file containing all configured High School Math CCS interchanges as a payload of the ingestion job: Clean Database
Given I post "CommonCoreStandards/grade12Math.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                     |
     | learningObjective                  |
     | learningStandard                   |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 3     |
     | learningStandard            | 16    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue    |
     | learningObjective           | 1                   | body.objective                                 | Geometry       |
     | learningObjective           | 1                   | body.objective                                 | Circles | 
     | learningObjective           | 1                   | body.objective                                 | Similarity, Right Triangle, and Trigonometry |
     | learningObjective           | 3                   | body.objectiveGradeLevel                       | Twelfth grade                                |
     | learningStandard            | 1                   | body.description                               | Explain and use the relationship between the sine and cosine of complementary angles. |
     | learningStandard            | 1                   | body.description                               | Prove that all circles are similar. |
     | learningStandard            | 1                   | body.learningStandardId.identificationCode     | G-SRT.7        |
     | learningStandard            | 16                  | body.gradeLevel                                | Twelfth grade                                |
     | learningStandard            | 16                  | body.subjectArea                               | Mathematics    |             
     | learningStandard            | 16                  | body.contentStandard                           | National Standard    |

  And I should see "Processed 19 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "Grade_12_Math_CCS_G_C.xml records considered: 19" in the resulting batch job file
  And I should see "Grade_12_Math_CCS_G_C.xml records ingested successfully: 19" in the resulting batch job file
  And I should see "Grade_12_Math_CCS_G_C.xml records failed: 0" in the resulting batch job file


Scenario: Verify resolved references and ingestion to populated database
Given I post "CommonCoreStandards/grade12Math.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                     |
     | learningObjective                  |
     | learningStandard                   |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 3     |
     | learningStandard            | 16    |
Given I post "CommonCoreStandards/grade12Math.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 3     |
     | learningStandard            | 16    |
When I find a record in "learningObjective" where "body.objective" is "Circles"
Then the field "body.learningStandards" is an array of size 5
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-C.1"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-C.2"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-C.3"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-C.4"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-C.5"
  And "body.parentLearningObjective" is a reference to "learningObjective" where "body.objective" is "Geometry"
When I find a record in "learningObjective" where "body.objective" is "Similarity, Right Triangle, and Trigonometry"
Then the field "body.learningStandards" is an array of size 11
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.1"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.2"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.3"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.4"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.5"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.6"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.7"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.8"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.9"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.10"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "G-SRT.11"
  And "body.parentLearningObjective" is a reference to "learningObjective" where "body.objective" is "Geometry"


