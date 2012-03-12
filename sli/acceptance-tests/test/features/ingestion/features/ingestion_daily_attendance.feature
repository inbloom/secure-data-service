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
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 72    |
     | attendance                  | 15984 |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
     | attendance                  | 15984               | body.educationalEnvironment   | Classroom       |
     | attendance                  | 14374               | body.attendanceEventCategory  | In Attendance   |
     | attendance                  | 837                 | body.attendanceEventCategory  | Excused Absence |
     | attendance                  | 773                 | body.attendanceEventCategory  | Tardy           |
     | attendance                  | 72                  | body.eventDate                | 2011-09-01      |
     | attendance                  | 0                   | body.eventDate                | 2011-09-03      |
     | attendance                  | 0                   | body.eventDate                | 2012-07-01      |
     | attendance                  | 72                  | body.eventDate                | 2012-07-02      |

  And I should see "Processed 16056 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 72" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 72" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records considered: 15984" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records ingested successfully: 15984" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceAppend.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | attendance                  | 16056 |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter               | searchValue     |
     | attendance                  | 16056               | body.educationalEnvironment   | Classroom       |
     | attendance                  | 14438               | body.attendanceEventCategory  | In Attendance   |
     | attendance                  | 841                 | body.attendanceEventCategory  | Excused Absence |
     | attendance                  | 777                 | body.attendanceEventCategory  | Tardy           |
     | attendance                  | 72                  | body.eventDate                | 2012-07-09      |

   And I should see "Processed 72 records." in the resulting batch job file
   And I should not see an error log file created
   And I should see "StudentAttendanceAppend.xml records considered: 72" in the resulting batch job file
   And I should see "StudentAttendanceAppend.xml records ingested successfully: 72" in the resulting batch job file
   And I should see "StudentAttendanceAppend.xml records failed: 0" in the resulting batch job file
#
#Scenario: Post a zip file containing duplicate configured interchanges as a payload of the ingestion job: Populated Database
#Given I post "DailyAttendanceDuplicate.zip" file as the payload of the ingestion job
#When zip file is scp to ingestion landing zone
#  And a batch job log has been created
##	And I should see "Entity (attendanceEvent) reports failure: E11000 duplicate key error" in the resulting error log file
#  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
#  And I should see "Processed 72 records." in the resulting batch job file
#     And I should see "StudentAttendanceDuplicate.xml records considered: 72" in the resulting batch job file
#   And I should see "StudentAttendanceDuplicate.xml records ingested successfully: 0" in the resulting batch job file
#   And I should see "StudentAttendanceDuplicate.xml records failed: 72" in the resulting batch job file
#
#Scenario: Post a zip file containing attendance event interchange with non-existent student as a payload of the ingestion job: Populated Database
#Given I post "DailyAttendanceNoStudent.zip" file as the payload of the ingestion job
#When zip file is scp to ingestion landing zone
#  And a batch job log has been created
##	And I should see "<<<insert could not find [student] in mongo repository error message>>>" in the resulting error log file
#  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
#  And I should see "Processed 1 records." in the resulting batch job file
#  And I should see "StudentAttendanceNoStudent.xml records considered: 1" in the resulting batch job file
#  And I should see "StudentAttendanceNoStudent.xml records ingested successfully: 0" in the resulting batch job file
#  And I should see "StudentAttendanceNoStudent.xml records failed: 1" in the resulting batch job file
