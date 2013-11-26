Feature: Ingestion SuperDoc Deletion

Scenario: Ingestion of Section Level Attendance Events

#initial ingestion
Given the "Midgar" tenant db is empty
Then I ingest "SectionLevelAttendanceEventsHappyPath.zip"
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | attendance                               |                1 |
     | recordHash                               |                3 |
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "In Attendance", date "2011-11-10" and section id "7e35bf82c85d1703eea35aa570b0b8ea8f9612c7_id", containing event reason "Old Reason 2" and educational environment "Classroom"
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "Tardy", date "2011-11-10" and section id "7e35bf82c85d1703eea35aa570b0b8ea8f9612c7_id", containing event reason "Old Reason 3" and educational environment "Classroom"
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "In Attendance", date "2011-11-10" without a section id, containing event reason "Old Reason 1" and educational environment "Classroom"
#duplicate ingestion
When I post "SectionLevelAttendanceEventsHappyPath.zip" file as the payload of the ingestion job
And zip file is scp to ingestion landing zone with name "SectionLevelAttendanceEventsHappyPath2.zip"
Then a batch job for file "SectionLevelAttendanceEventsHappyPath2.zip" is completed in database
And I should not see an error log file created
And I should not see a warning log file created
And I should see "StudentAttendanceEvents.xml attendanceEvent 3 deltas!" in the resulting batch job file
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | attendance                               |                1 |
     | recordHash                               |                3 |
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "In Attendance", date "2011-11-10" and section id "7e35bf82c85d1703eea35aa570b0b8ea8f9612c7_id", containing event reason "Old Reason 2" and educational environment "Classroom"
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "Tardy", date "2011-11-10" and section id "7e35bf82c85d1703eea35aa570b0b8ea8f9612c7_id", containing event reason "Old Reason 3" and educational environment "Classroom"
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "In Attendance", date "2011-11-10" without a section id, containing event reason "Old Reason 1" and educational environment "Classroom"
#updates through ingestion
Then I ingest "SectionLevelAttendanceEventsHappyPathUpdates.zip"
Then I should see following map of entry counts in the corresponding collections:
     | collectionName                           |            count |
     | attendance                               |                1 |
     | recordHash                               |                4 |
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "In Attendance", date "2011-11-10" and section id "7e35bf82c85d1703eea35aa570b0b8ea8f9612c7_id", containing event reason "Old Reason 2" and educational environment "Resource room"
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "Tardy", date "2011-11-10" and section id "7e35bf82c85d1703eea35aa570b0b8ea8f9612c7_id", containing event reason "New Reason 5" and educational environment "Classroom"
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "In Attendance", date "2011-11-10" without a section id, containing event reason "New Reason 4" and educational environment "Resource room"
#Append case
And in tenant "Midgar", I should see an attendance record with id "f77d258165513ea45687ca9c096484819c413b18_id" containing "1" attendance events matching event type "Early departure", date "2011-11-10" and section id "7e35bf82c85d1703eea35aa570b0b8ea8f9612c7_id", without event reason or educational environment
#Deletes through ingestion covered in ingestion_broad_cascading_attendanceEvent_delete.feature
