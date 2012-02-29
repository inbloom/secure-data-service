Feature: Daily Attendance Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DailyAttendance1.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName              |
	   | student                     |
	   | attendanceEvent             |
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | student                     | 2     |
	   | attendanceEvent             | 24    |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter               | searchValue    |
	   | attendanceEvent             | 24                  | body.educationalEnvironment   | Classroom      |
	   | attendanceEvent             | 22                  | body.attendanceEventCategory  | In Attendance  |
	   | attendanceEvent             | 2                   | body.eventDate                | 2011-08-31     |

	And I should see "Processed 26 records." in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DailyAttendance2.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | attendanceEvent             | 48    |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
	   | attendanceEvent             | 48                  | body.educationalEnvironment   | Classroom       |
	   | attendanceEvent             | 3                   | body.attendanceEventCategory  | Tardy           |
	   | attendanceEvent             | 7                   | body.attendanceEventCategory  | Excused Absence |
	   | attendanceEvent             | 2                   | body.eventDate                | 2011-10-03      |

	   	And I should see "Processed 24 records." in the resulting batch job file

Scenario: Post a zip file containing duplicate configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceDuplicate.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "10" seconds have elapsed
#	And I should see "Entity (attendanceEvent) reports failure: E11000 duplicate key error" in the resulting error log file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 24 records." in the resulting batch job file
	