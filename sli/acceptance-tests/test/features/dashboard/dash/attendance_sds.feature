@RALLY_US1288
@RALLY_US1289
@RALLY_US1291
@RALLY_US1292
Feature: Attendance in the dashboard

As a SEA/LEA user, I want to be able to see various
attendance data for a list of students.

Background:
    Given I have an open web browser
And that dashboard has been authorized for all ed orgs

@integration  @RALLY_US200
Scenario: Monster test until LOS is faster
    When I navigate to the Dashboard home page
    When I select "Illinois Daybreak School District 4529" and click go
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "East Daybreak Junior High"
        And I select <course> "8th Grade English"
        And I select <section> "8th Grade English - Sec 6"
      And I select view "Middle School ELA View"
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "attendances.tardyCount" for student "Rudy Bedoya" is "0"
      And the class for id "attendances.tardyCount" for student "Rudy Bedoya" is "color-widget-darkgreen"
      And I should see a table heading "Tardy Rate %"
      And the count for id "attendances.tardyRate" for student "Samantha Scorzelli" is "0"
      And the class for id "attendances.tardyRate" for student "Samantha Scorzelli" is "color-widget-darkgreen"
      And I should see a table heading "Attendance Rate %"
      And the count for id "attendances.attendanceRate" for student "Dominic Brisendine" is "100"
      And the class for id "attendances.attendanceRate" for student "Dominic Brisendine" is "color-widget-darkgreen"
      And the count for id "attendances.attendanceRate" for student "Lashawn Aldama" is "99"
      And the class for id "attendances.attendanceRate" for student "Lashawn Aldama" is "color-widget-darkgreen"
      And the count for id "attendances.attendanceRate" for student "Karrie Rudesill" is "100"
      And the class for id "attendances.attendanceRate" for student "Karrie Rudesill" is "color-widget-darkgreen"
      And the count for id "attendances.attendanceRate" for student "Lashawn Taite" is "86"
      And the class for id "attendances.attendanceRate" for student "Lashawn Taite" is "color-widget-red"
      And I should see a table heading "Absence Count"
      And the count for id "attendances.absenceCount" for student "Dominic Brisendine" is "0"
      And the class for id "attendances.absenceCount" for student "Dominic Brisendine" is "color-widget-darkgreen"
      And the count for id "attendances.absenceCount" for student "Felipe Cianciolo" is "6"
      And the class for id "attendances.absenceCount" for student "Felipe Cianciolo" is "color-widget-yellow"
       And the count for id "attendances.absenceCount" for student "Merry Mccanse" is "5"
      And the class for id "attendances.absenceCount" for student "Merry Mccanse" is "color-widget-green"


