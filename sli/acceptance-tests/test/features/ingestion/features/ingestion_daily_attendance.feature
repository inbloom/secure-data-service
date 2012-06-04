@RALLY_US0615
Feature: Daily Attendance Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DailyAttendance.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | student                     |
     | studentSchoolAssociation    |
     | course                      |
     | educationOrganization       |
     | school                      |
     | section                     |
     | session                     |
     | studentSectionAssociation   |
     | attendance                  |
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 94    |
     | studentSchoolAssociation    | 123   |
     | course                      | 15    |
     | educationOrganization       | 8     |
     | school                      | 0     |
     | section                     | 25    |
     | session                     | 8     |
     | studentSectionAssociation   | 210   |
     | attendance                  | 38    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                 | searchValue   |
     | attendance                  | 38                  | body.schoolYearAttendance.schoolYear            | 2011-2012     |
     | attendance                  | 35                  | body.schoolYearAttendance.attendanceEvent.event | Tardy         |
     | attendance                  | 38                  | body.schoolYearAttendance.attendanceEvent.event | In Attendance |
     | attendance                  | 0                   | body.schoolYearAttendance.attendanceEvent.date  | 2011-09-01    |
     | attendance                  | 38                  | body.schoolYearAttendance.attendanceEvent.date  | 2011-11-10    |

  And I should see "Processed 526 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 94" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 94" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records considered: 38" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records ingested successfully: 38" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records failed: 0" in the resulting batch job file


@wip
Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceAppend.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | attendance                  | 38    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue     |
     | attendance                  | 16056               | body.educationalEnvironment   | Classroom       |
     | attendance                  | 14422               | body.attendanceEventCategory  | In Attendance   |
     | attendance                  | 850                 | body.attendanceEventCategory  | Excused Absence |
     | attendance                  | 784                 | body.attendanceEventCategory  | Tardy           |
     | attendance                  | 72                  | body.schoolYearAttendance.attendanceEvent.date | 2012-07-09      |
   And I should see "Processed 72 records." in the resulting batch job file
   And I should not see an error log file created
   And I should see "StudentAttendanceAppend.xml records considered: 72" in the resulting batch job file
   And I should see "StudentAttendanceAppend.xml records ingested successfully: 72" in the resulting batch job file
   And I should see "StudentAttendanceAppend.xml records failed: 0" in the resulting batch job file

@wip
Scenario: Post a zip file containing duplicate configured interchanges as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceDuplicate.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
#	And I should see "Entity (attendanceEvent) reports failure: E11000 duplicate key error" in the resulting error log file
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 72 records." in the resulting batch job file
  And I should see "StudentAttendanceDuplicate.xml records considered: 72" in the resulting batch job file
  And I should see "StudentAttendanceDuplicate.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "StudentAttendanceDuplicate.xml records failed: 72" in the resulting batch job file

Scenario: Post a zip file containing attendance event interchange with non-existent student as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceNoStudent.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job log has been created
#  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 0 records." in the resulting batch job file
  And I should see "StudentAttendanceNoStudent.xml records considered: 0" in the resulting batch job file
  And I should see "StudentAttendanceNoStudent.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "StudentAttendanceNoStudent.xml records failed: 0" in the resulting batch job file
