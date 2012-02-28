@wip
Feature: Daily Attendance Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DailyAttendance1.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName              |
	   | student                     |
	   | attendance                  |
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | attendance                  | 2     |
	   | attendance                  | 24    |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter               | searchValue    |
	   | attendance                  | 12                  | body.studentUniqueStateId     | 530425896      |
	   | attendance                  | 22                  | body.attendanceEventCategory  | In Attendance  |
	   | attendance                  | 2                   | body.eventDate                | 2011-08-31     |

	And I should see "Processed 3 records." in the resulting batch job file
@wip
Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DailyAttendance2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | attendance                  | 1600  |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter                             | searchValue             |
	   | attendance                  | 200                 | body.studentId                              |289c933b-ca69-448c-9afd-2c5879b7d221|
	   | attendance                  | 28                  | body.attendanceEventCategory                |Tardy                           |
	   | attendance                  | 3                   | body.eventDate                              |2012-12-21                           |

	   	And I should see "Processed 8 records." in the resulting batch job file
