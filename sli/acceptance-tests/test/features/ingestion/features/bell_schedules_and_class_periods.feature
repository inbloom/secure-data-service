Feature: Ingestion SuperDoc Deletion

Scenario: Ingestion of Bell Schedules and Class Periods

#initial ingestion
Given the "Midgar" tenant db is empty
Then I ingest "BellSchedulesAndClassPeriods.zip"
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | bellSchedule                             |                1 |
     | classPeriod                              |                1 |
     | section                                  |                1 |
     | recordHash                               |                3 |

