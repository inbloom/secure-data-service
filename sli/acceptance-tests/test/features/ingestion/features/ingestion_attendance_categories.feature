@RALLY_US5906
Feature: Ingest AttendanceEvents differing only in AttendanceEventCategory

Background: I have a landing zone route configured
Given I am using local data store

Scenario: Post an Attendance Interchange containing 2 AttendanceEvents differing only in AttendanceEventCategory
Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
    And I post "AttendanceEventCategories.zip" file as the payload of the ingestion job
    And the following collections are empty in datastore:
        | collectionName                        |
        | recordHash                            |
        | attendance                            |
  When zip file is scp to ingestion landing zone
  And a batch job for file "AttendanceEventCategories.zip" is completed in database
  And I should not see an error log file created
  And I should not see a warning log file created
  And I should see "Processed 245 records." in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records considered for processing: 2" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records ingested successfully: 2" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records failed processing: 0" in the resulting batch job file
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count               |
    | attendance                  | 1                   |
  And I check to find if record is in collection:
    | collectionName              | expectedRecordCount | searchParameter            | searchValue       | searchType           |
    | attendance                  | 1                   | body.attendanceEvent.event | In Attendance     | string               |
    | attendance                  | 1                   | body.attendanceEvent.event | Early departure   | string               |

  #Reingest same AttendanceEvents. 2 Deltas should be detected
  And I post "AttendanceEventCategories_AttendanceOnly.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And a batch job for file "AttendanceEventCategories_AttendanceOnly.zip" is completed in database
  And I should not see an error log file created
  And I should not see a warning log file created
  And I should see "StudentAttendanceEvents.xml records considered for processing: 0" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records ingested successfully: 0" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records failed processing: 0" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml attendanceEvent 2 deltas!" in the resulting batch job file

  #Delete one AttendanceEvent. Second AttendanceEvent should not be deleted.
  And I post "AttendanceEventCategories_Delete.zip" file as the payload of the ingestion job
  When zip file is scp to ingestion landing zone
  And a batch job for file "AttendanceEventCategories_Delete.zip" is completed in database
  And I should not see an error log file created
  And I should not see a warning log file created
  And I should see "StudentAttendanceEvents.xml records considered for processing: 1" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records deleted successfully: 1" in the resulting batch job file
  And I should see "StudentAttendanceEvents.xml records failed processing: 0" in the resulting batch job file
  Then I should see following map of entry counts in the corresponding collections:
    | collectionName              | count               |
    | attendance                  | 1                   |
  And I check to find if record is in collection:
    | collectionName              | expectedRecordCount | searchParameter            | searchValue       | searchType           |
    | attendance                  | 0                   | body.attendanceEvent.event | In Attendance     | string               |
    | attendance                  | 1                   | body.attendanceEvent.event | Early departure   | string               |
