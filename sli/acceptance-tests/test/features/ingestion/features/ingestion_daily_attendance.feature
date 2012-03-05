Feature: Daily Attendance Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
	And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DailyAttendance.zip" file as the payload of the ingestion job
	And the following collections are empty in datastore:
	   | collectionName              |
	   | student                     |
	   | attendance                  |
When zip file is scp to ingestion landing zone
	And "90" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | student                     | 52    |
	   | attendance                  | 11544 |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
	   | attendance                  | 11544               | body.educationalEnvironment   | Classroom       |
	   | attendance                  | 1187                | body.attendanceEventCategory  | Excused Absence |
	   | attendance                  | 10357               | body.attendanceEventCategory  | In Attendance   |
	   | attendance                  | 52                  | body.eventDate                | 2011-09-01      |
	   | attendance                  | 0                   | body.eventDate                | 2011-09-03      |
	   | attendance                  | 0                   | body.eventDate                | 2012-07-01      |
	   | attendance                  | 52                  | body.eventDate                | 2012-07-02      |
	   
	And I should see "Processed 11596 records." in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceAppend.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "30" seconds have elapsed
Then I should see following map of entry counts in the corresponding collections:
	   | collectionName              | count |
	   | attendance                  | 11596 |
	 And I check to find if record is in collection:
	   | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
	   | attendance                  | 11596               | body.educationalEnvironment   | Classroom       |
	   | attendance                  | 1191                | body.attendanceEventCategory  | Excused Absence |
	   | attendance                  | 10405               | body.attendanceEventCategory  | In Attendance   |
	   | attendance                  | 52                  | body.eventDate                | 2012-07-09      |
	   
	 And I should see "Processed 52 records." in the resulting batch job file
	 
Scenario: Post a zip file containing duplicate configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceDuplicate.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "5" seconds have elapsed
#	And I should see "Entity (attendanceEvent) reports failure: E11000 duplicate key error" in the resulting error log file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 24 records." in the resulting batch job file

Scenario: Post a zip file containing attendance event interchange with non-existent student as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceNoStudent.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
	And "5" seconds have elapsed
#	And I should see "<<<insert could not find [student] in mongo repository error message>>>" in the resulting error log file
	And I should see "Not all records were processed completely due to errors." in the resulting batch job file
	And I should see "Processed 1 records." in the resulting batch job file
