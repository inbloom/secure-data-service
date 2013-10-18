@RALLY_US5906
Feature: As an SLI application, I want to insert and update attendanceEvents differing only in AttendanceEventCategories

  Background: Nothing yet
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And format "application/vnd.slc+json"
    And I GET  attendance for student "0c2756fd-6a30-4010-af79-488d6ef2735a_id" in school "92d6d5a0-852c-45f4-907a-912752831772" for the schoolYear "2012-2013" on date "2012-10-10" and verify that it has "0" attendance events
    And I POST an attendance of "In Attendance, Early departure" for student "0c2756fd-6a30-4010-af79-488d6ef2735a_id" in school "92d6d5a0-852c-45f4-907a-912752831772" for the schoolYear "2012-2013" on date "2012-10-10"
    And I GET  attendance for student "0c2756fd-6a30-4010-af79-488d6ef2735a_id" in school "92d6d5a0-852c-45f4-907a-912752831772" for the schoolYear "2012-2013" on date "2012-10-10" and verify that it has "2" attendance events