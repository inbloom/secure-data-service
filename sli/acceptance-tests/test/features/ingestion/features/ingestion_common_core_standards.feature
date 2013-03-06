@RALLY_US2507
@RALLY_US3200
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
     | recordHash                  |
  When zip file is scp to ingestion landing zone
  And a batch job for file "grade12English.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 70    |
     | learningStandard            | 954   |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                              | searchValue                   |
     | learningObjective           | 3                   | body.objective                               | Reading Science/Technical     |
     | learningObjective           | 8                   | body.objectiveGradeLevel                     | Eleventh grade                |
     | learningStandard            | 113                 | body.gradeLevel                              | Eleventh grade                |
     | learningStandard            | 954                 | body.subjectArea                             | English                       |
     | learningStandard            | 954                 | body.contentStandard                         | State Standard                |

  And I should see "Processed 1024 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeAssessmentMetadata-CCS-English.xml records considered for processing: 1024" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-CCS-English.xml records ingested successfully: 1024" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-CCS-English.xml records failed processing: 0" in the resulting batch job file


Scenario: Post a zip file containing all configured High School Math CCS interchanges as a payload of the ingestion job: Clean Database
Given I post "CommonCoreStandards/grade12Math.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                     |
     | learningObjective                  |
     | learningStandard                   |
     | recordHash                         |
When zip file is scp to ingestion landing zone
  And a batch job for file "grade12Math.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 65     |
     | learningStandard            | 509    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue    |
     | learningObjective           | 9                   | body.objective                                 | Geometry       |
     | learningObjective           | 1                   | body.objective                                 | Circles |
     | learningObjective           | 1                   | body.objective                                 | Vector And Matrix Quantities|
     | learningObjective           | 22                   | body.objectiveGradeLevel                       | Ninth grade               |
     | learningStandard            | 1                   | body.description                               | Read, write, and compare decimals to thousandths. |
     | learningStandard            | 1                   | body.description                               | Prove that all circles are similar. |
     | learningStandard            | 1                   | body.learningStandardId.identificationCode     | BE6257FC08DA4AA896C87FC4A19F6520|
     | learningStandard            | 36                  | body.gradeLevel                                | Eighth grade                                |
     | learningStandard            | 509                  | body.subjectArea                               | Mathematics    |
     | learningStandard            | 509                  | body.contentStandard                           | State Standard    |

  And I should see "Processed 574 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeAssessmentMetadata-CCS-Math.xml records considered for processing: 574" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-CCS-Math.xml records ingested successfully: 574" in the resulting batch job file
  And I should see "InterchangeAssessmentMetadata-CCS-Math.xml records failed processing: 0" in the resulting batch job file

Scenario: Verify resolved references and ingestion to populated database
Given I post "CommonCoreStandards/grade12Math.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName                     |
     | learningObjective                  |
     | learningStandard                   |
     | recordHash                         |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 65     |
     | learningStandard            | 509    |
Given I post "CommonCoreStandards/grade12Math.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone with name "grade12Math2.zip"
  And a batch job for file "grade12Math2.zip" is completed in database
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 65     |
     | learningStandard            | 509    |
When I find a record in "learningObjective" where "body.objective" is "Circles"
Then the field "body.learningStandards" is an array of size 5
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "519064F0E97A489AA657BE1C4D81E64C"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "0B8104A4EC0441BBA569C75FDCD346A2"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "22CA4C028EDD4325ABF6007ADB252DA9"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "48AD7305975E4C38A38484926C2AC2B6"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "48AD7305975E4C38A38484926C2AC2B7"
#  And "body.parentLearningObjective" is a reference to "learningObjective" where "body.objective" is "Geometry"
When I find a record in "learningObjective" where "body.objective" is "Vector And Matrix Quantities"
Then the field "body.learningStandards" is an array of size 17
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "05BAE0DE74104B1AADC31E85AA1A6128"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "A9F2C3A58194456C9F31B5A8E6D761FE"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "A2C32D9A05114A7A9DF0F41BCD26AA8B"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "FA9C047FE1C547998A6CBB16C323FAD8"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "E71EAB46BC9E474C861A53C60CAB9D2C"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "984534D6C05B4CFB9E257E5BB29EF018"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "B5969B9BA727452CAB1EF05C4694974C"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "5B4228464AF24FC2A8B23CF733ABD5D8"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "FA032D8A02564B37A0BAF505A0296FB5"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "FA032D8A02564B37A0BAF505A0296FB4"
  And "body.learningStandards" contains a reference to a "learningStandard" where "body.learningStandardId.identificationCode" is "DB45830F7002427C8AF399E8819DA20E"
#  And "body.parentLearningObjective" is a reference to "learningObjective" where "body.objective" is "Geometry"


