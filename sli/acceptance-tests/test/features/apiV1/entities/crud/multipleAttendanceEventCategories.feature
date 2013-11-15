@RALLY_US5906
Feature: As an SLI application, I want to insert and update attendanceEvents differing only in AttendanceEventCategories

  Scenario: Attendance api GETs and POSTs

    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
      And I GET attendance for student "0c2756fd-6a30-4010-af79-488d6ef2735a_id" in school "92d6d5a0-852c-45f4-907a-912752831772" for the schoolYear "2012-2013" on date "2012-10-10" and verify that it has "0" attendance events
    When I POST an attendance of "In Attendance, Early departure" for student "0c2756fd-6a30-4010-af79-488d6ef2735a_id" in school "92d6d5a0-852c-45f4-907a-912752831772" for the schoolYear "2012-2013" on date "2012-10-10"
    Then I GET attendance for student "0c2756fd-6a30-4010-af79-488d6ef2735a_id" in school "92d6d5a0-852c-45f4-907a-912752831772" for the schoolYear "2012-2013" on date "2012-10-10" and verify that it has "2" attendance events
      And I GET attendance for student "0c2756fd-6a30-4010-af79-488d6ef2735a_id" in school "92d6d5a0-852c-45f4-907a-912752831772" for the schoolYear "2012-2013" on date "2012-10-10" and verify that its attendance events are at the section level
      And I DELETE attendance for student "0c2756fd-6a30-4010-af79-488d6ef2735a_id" in school "92d6d5a0-852c-45f4-907a-912752831772" for the schoolYear "2012-2013" on date "2012-10-10" and verify that there are no attendance events remaining