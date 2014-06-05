@wip
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

Scenario: Monster test until LOS is faster
    When I navigate to the Dashboard home page
    When I select "Illinois Sunset School District 4526" and click go
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
 #    And I select view "College Ready ELA View"
 #   Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "attendances.tardyCount" for student "Charde Lowery" is "0"
      And the class for id "attendances.tardyCount" for student "Charde Lowery" is "color-widget-darkgreen"
      And the count for id "attendances.tardyCount" for student "Patricia Harper" is "3"
      And the class for id "attendances.tardyCount" for student "Patricia Harper" is "color-widget-green"
      And the count for id "attendances.tardyCount" for student "Arsenio Durham" is "22"
      And the class for id "attendances.tardyCount" for student "Arsenio Durham" is "color-widget-red"
      #And I should see a table heading "Tardy Rate %"
      #And the count for id "attendances.tardyRate" for student "Charde Lowery" is "0"
      #And the class for id "attendances.tardyRate" for student "Charde Lowery" is "color-widget-darkgreen"
      #And the count for id "attendances.tardyRate" for student "Jolene Ashley" is "4"
      #And the class for id "attendances.tardyRate" for student "Jolene Ashley" is "color-widget-green"
      #And the count for id "attendances.tardyRate" for student "Delilah Sims" is "6"
      #And the class for id "attendances.tardyRate" for student "Delilah Sims" is "color-widget-yellow"
      #And the count for id "attendances.tardyRate" for student "Arsenio Durham" is "14"
     # And the class for id "attendances.tardyRate" for student "Arsenio Durham" is "color-widget-red"
     # And I should see a table heading "Attendance Rate %"
     # And the count for id "attendances.attendanceRate" for student "Arsenio Durham" is "96"
     # And the class for id "attendances.attendanceRate" for student "Arsenio Durham" is "color-widget-green"
     # And the count for id "attendances.attendanceRate" for student "Alec Swanson" is "96"
     # And the class for id "attendances.attendanceRate" for student "Alec Swanson" is "color-widget-green"
     # And the count for id "attendances.attendanceRate" for student "Nomlanga Mccormick" is "96"
     # And the class for id "attendances.attendanceRate" for student "Nomlanga Mccormick" is "color-widget-green"
     # And the count for id "attendances.attendanceRate" for student "Johnny Patel" is "87"
     # And the class for id "attendances.attendanceRate" for student "Johnny Patel" is "color-widget-red"
      And I should see a table heading "Absence Count"
      And the count for id "attendances.absenceCount" for student "Charde Lowery" is "10"

