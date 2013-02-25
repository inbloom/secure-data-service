@RALLY_US0615
@RALLY_US927

Feature: Daily Attendance Ingestion Test

Background: I have a landing zone route configured
Given I am using local data store
  And I am using preconfigured Ingestion Landing Zone

Scenario: Post a zip file containing all configured interchanges as a payload of the ingestion job: Clean Database
Given I post "DailyAttendance.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | recordHash                  |
     | student                     |
     | studentSchoolAssociation    |
     | educationOrganization       |
     | school                      |
     | session                     |
     | attendance                  |
When zip file is scp to ingestion landing zone
  And a batch job for file "DailyAttendance.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 94    |
     | studentSchoolAssociation    | 123   |
     | educationOrganization       | 8     |
     | school                      | 0     |
     | session                     | 8     |
     | attendance                  | 38    |
   And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                 | searchValue   |
    | attendance                  | 38                  | body.schoolYearAttendance.schoolYear            | 2011-2012     |
    | attendance                  | 3                  | body.schoolYearAttendance.attendanceEvent.event | Tardy         |
    | attendance                  | 33                  | body.schoolYearAttendance.attendanceEvent.event | In Attendance |
     | attendance                  | 0                   | body.schoolYearAttendance.attendanceEvent.date  | 2011-09-01    |
     | attendance                  | 38                  | body.schoolYearAttendance.attendanceEvent.date  | 2011-11-10    |
     | studentSchoolAssociation     | 7                   | body.classOf                                     | 2011-2012    |

  And I should see "Processed 281 records." in the resulting batch job file
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
  And a batch job for file "DailyAttendanceAppend.zip" is completed in database
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
  And a batch job for file "DailyAttendanceDuplicate.zip" is completed in database
  And a batch job log has been created
  And I should see "Not all records were processed completely due to errors." in the resulting batch job file
  And I should see "Processed 72 records." in the resulting batch job file
  And I should see "StudentAttendanceDuplicate.xml records considered: 72" in the resulting batch job file
  And I should see "StudentAttendanceDuplicate.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "StudentAttendanceDuplicate.xml records failed: 72" in the resulting batch job file

Scenario: Post a zip file containing attendance event interchange with non-existent student as a payload of the ingestion job: Populated Database
Given I post "DailyAttendanceNoStudent.zip" file as the payload of the ingestion job
When zip file is scp to ingestion landing zone
  And a batch job for file "DailyAttendanceNoStudent.zip" is completed in database
  And a batch job log has been created
  And I should see "Processed 0 records." in the resulting batch job file
  And I should see "StudentAttendanceNoStudent.xml records considered: 0" in the resulting batch job file
  And I should see "StudentAttendanceNoStudent.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "StudentAttendanceNoStudent.xml records failed: 0" in the resulting batch job file

Scenario: Post a zip file where an attendanceEvent occurs in a school's parent LEA session: Clean Database
Given I post "DailyAttendanceInheritedSession.zip" file as the payload of the ingestion job
  And the following collections are empty in datastore:
     | collectionName              |
     | recordHash                  |
     | student                     |
     | educationOrganization       |
     | session                     |
     | attendance                  |
	| recordHash                  |
When zip file is scp to ingestion landing zone
  And a batch job for file "DailyAttendanceInheritedSession.zip" is completed in database
Then I should see following map of entry counts in the corresponding collections:
     | collectionName              | count |
     | student                     | 1     |
     | educationOrganization       | 3     |
     | session                     | 1     |
     | attendance                  | 1     |
 And I check to find if record is in collection:
     | collectionName              | expectedRecordCount | searchParameter                                | searchValue     |
     | attendance                  | 1                   | body.schoolYearAttendance.attendanceEvent.date | 2011-09-06      |
  And I should see "Processed 8 records." in the resulting batch job file
  And I should not see an error log file created
  And I should see "InterchangeStudent.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "InterchangeStudent.xml records failed: 0" in the resulting batch job file
  And I should see "InterchangeAttendance.xml records considered: 1" in the resulting batch job file
  And I should see "InterchangeAttendance.xml records ingested successfully: 1" in the resulting batch job file
  And I should see "InterchangeAttendance.xml records failed: 0" in the resulting batch job file

  Scenario: Ingest a zip file and ensure the attendance entity contains the expected format.
    Given I post "DailyAttendance.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
      | attendance                  |
    When zip file is scp to ingestion landing zone
    And a batch job for file "DailyAttendance.zip" is completed in database
    Then all attendance entities should should have the expected structure.
