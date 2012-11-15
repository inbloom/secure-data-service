@RALLY_US2160
Feature: StudentObjectiveAssessment Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "StudentObjectiveAssessment1.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | StudentAssessment                     |
When zip file is scp to ingestion landing zone
  And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | StudentObjectiveAssessment  | 6    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter             | searchValue             |
     | StudentObjectiveAssessment  | 2                   | body.Result                                 |9                           |
     | StudentObjectiveAssessment  | 2                   | body.Result                                 |7                           |
     | StudentObjectiveAssessment  | 1                   | body.Result                                 |8                           |
     | StudentObjectiveAssessment  | 1                   | body.Result                                 |6                           |
     | StudentObjectiveAssessment  | 5                   | body.ObjectiveAssessmentIdentificationCode  |TAKSReading9-1              |

  And I should see "Processed 6 records." in the resulting batch job file
  And I should not see an error log file created

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "StudentObjectiveAssessment2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | session                     | 50    |
   And I check to find if record is in collection:

     | collectionName              | expectedRecordCount | searchParameter                             | searchValue                         |
     | StudentObjectiveAssessment  | 4                   | body.Result                                 | 9                                   |
     | StudentObjectiveAssessment  | 1                   | body.Result                                 | 8                                   |
     | StudentObjectiveAssessment  | 1                   | body.Result                                 | 6                                   |
     | StudentObjectiveAssessment  | 5                   | body.ObjectiveAssessmentIdentificationCode  | TAKSReading9-1                      |
     | StudentObjectiveAssessment  | 1                   | body.StudentTestAssessmentReference         | STA-TAKS-Reading-9-2010-0410-604968 |

       And I should see "Processed 6 records." in the resulting batch job file
       And I should not see an error log file created
