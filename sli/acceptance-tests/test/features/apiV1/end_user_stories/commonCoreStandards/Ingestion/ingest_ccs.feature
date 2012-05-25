@Rally_US2507 @RALLY_US2195
@wip
Feature: Common Core Standard Ingestion

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured Learning Objective interchanges as a payload of the ingestion job: Clean Database
Given I post "grade12English.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | learningObjective                  |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 5     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue           |
     | learningObjective           | 1                   | body.learningObjective.description             | Reading: Informational Text  |
     | learningObjective           | 1                   | body.learningObjective.grade                   | Twelfth                               |
 
  And I should see "Processed 5 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "grade12English.xml records considered: 5" in the resulting batch job file
  And I should see "grade12English.xml records ingested successfully: 4" in the resulting batch job file
  And I should see "grade12English.xml records failed: 0" in the resulting batch job file
  
 Scenario: Post a zip file containing all configured Learning Standards interchanges as a payload of the ingestion job: Clean Database
Given I post "grade12English.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | learningStandard            |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningStandard            | 10     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue                                      |
     | learningStandard            | 1                   | body.learningStandard.description              | Analyze a complex set of ideas or sequence of events and explain how specific individuals, ideas, or events interact and develop over the course of the text.                                 |
     | learningStandard            | 10                  | body.learningStandard.grade                    | Twelfth                                      |

  And I should see "Processed 10 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "grade12English.xml records considered: 10" in the resulting batch job file
  And I should see "grade12English.xml records ingested successfully: 11" in the resulting batch job file
  And I should see "grade12English.xml records failed: 0" in the resulting batch job file 
  
  
 Scenario: Post a zip file containing all configured High School Math SRT CCS interchanges as a payload of the ingestion job: Clean Database
Given I post "grade12MathSRT.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | learningObjective                  |
     | learningStandard                   |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 2     |
     | learningStandard            | 11    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue |
     | learningObjective           | 1                   | body.learningObjective.objective               | Geometry |
     | learningStandard            | 1                   | body.learningStandard.description              | Explain and use the relationship between the sine and cosine of complementary angles. |
     | learningObjective           | 2                   | body.learningObjective.grade                   | Twelfth                               |
     | learningStandard            | 11                  | body.learningStandard.grade                    | Twelfth                                      |

  And I should see "Processed 13 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "grade12MathSRT.xml records considered: 13" in the resulting batch job file
  And I should see "grade12MathSRT.xml records ingested successfully: 12" in the resulting batch job file
  And I should see "grade12MathSRT.xml records failed: 0" in the resulting batch job file
  
  
    Scenario: Post a zip file containing all configured High School Math Circle CCS interchanges as a payload of the ingestion job: Clean Database
Given I post "grade12MathGC.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | learningObjective           |
     | learningStandard            |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | learningObjective           | 2     |
     | learningStandard            | 5     |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue |
     | learningObjective           | 2                   | body.learningObjective.objective               | Circles |
     | learningStandard            | 1                   | body.learningStandard.description              | Prove that all circles are similar. |
     | learningObjective           | 2                   | body.learningObjective.grade                   | Twelfth  |
     | learningStandard            | 5                   | body.learningStandard.grade                    | Twelfth  |

  And I should see "Processed 7 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "grade12MathGC.xml records considered: 7" in the resulting batch job file
  And I should see "grade12MathGC.xml records ingested successfully: 6" in the resulting batch job file
  And I should see "grade12MathGC.xml records failed: 0" in the resulting batch job file
  
  
  
  
