Feature: Attendance in the dashboard

As a SEA/LEA user, I want to be able to see various
attendance data for a list of students.

Background:
    Given I have an open web browser
    Given the server is in "live" mode
	
@wip
Scenario: Teacher sees Absence Count in K-3 list of students view
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_K-3"
		  And I wait for "5" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Absence Count"
			And the count for id "ATTENDANCE.AbsenceCount" for student "Charde Lowery" is "25" 
@wip
Scenario: Teacher sees Absence Count in 3-8 list of students view
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
	  	  And I select view "IL_3-8 ELA"
		  And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Absence Count"
			And the count for id "ATTENDANCE.AbsenceCount" for student "Charde Lowery" is "25"
 
 @wip
Scenario: Teacher sees Absence Count in 9-12 list of students view
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Absence Count"
			And the count for id "ATTENDANCE.AbsenceCount" for student "Charde Lowery" is "13"

@wip
Scenario: Teacher sees Attendance Rate in 9-12 list of students view - red
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Attendance Rate %"
			And the count for id "ATTENDANCE.AttendanceRate" for student "Johnny Patel" is "87"
      			And the class for id "ATTENDANCE.AttendanceRate" for student "Johnny Patel" is "perfLevel1"

@wip
Scenario: Teacher sees Attendance Rate in 9-12 list of students view - yellow 
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Attendance Rate %"
			And the count for id "ATTENDANCE.AttendanceRate" for student "Nomlanga Mccormick" is "94"
      			And the class for id "ATTENDANCE.AttendanceRate" for student "Nomlanga Mccormick" is "perfLevel3"

@wip
Scenario: Teacher sees Attendance Rate in 9-12 list of students view - light green
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Attendance Rate %"
			And the count for id "ATTENDANCE.AttendanceRate" for student "Alec Swanson" is "95"
      			And the class for id "ATTENDANCE.AttendanceRate" for student "Alec Swanson" is "perfLevel4"

@wip
Scenario: Teacher sees Attendance Rate in 9-12 list of students view - green
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Attendance Rate %"
			And the count for id "ATTENDANCE.AttendanceRate" for student "Marvin Miller" is "99"
      			And the class for id "ATTENDANCE.AttendanceRate" for student "Marvin Miller" is "perfLevel5"

@wip
Scenario: Teacher sees Tardy Rate in 9-12 list of students view - red
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Tardy Rate %"
			And the count for id "ATTENDANCE.TardyRate" for student "Arsenio Durham" is "13"
      			And the class for id "ATTENDANCE.TardyRate" for student "Arsenio Durham" is "perfLevel1"
@wip
Scenario: Teacher sees Tardy Rate in 9-12 list of students view - yellow
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Tardy Rate %"
			And the count for id "ATTENDANCE.TardyRate" for student "Delilah Sims" is "6"
      			And the class for id "ATTENDANCE.TardyRate" for student "Delilah Sims" is "perfLevel3"

@wip
Scenario: Teacher sees Tardy Rate in 9-12 list of students view - light green 
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Tardy Rate %"
			And the count for id "ATTENDANCE.TardyRate" for student "Jolene Ashley" is "4"
      			And the class for id "ATTENDANCE.TardyRate" for student "Jolene Ashley" is "perfLevel4"

@wip
Scenario: Teacher sees Tardy Rate in 9-12 list of students view - green 
		When I navigate to the Dashboard home page
		When I select "Sunset School District 4526" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "16" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Tardy Rate %"
			And the count for id "ATTENDANCE.TardyRate" for student "Charde Lowery" is "0"
      			And the class for id "ATTENDANCE.TardyRate" for student "Charde Lowery" is "perfLevel5"

@wip
 Scenario: Teacher sees Tardy Count in 9-12 list of students view - third color
    When I navigate to the Dashboard home page
    When I select "Sunset School District 4526" and click go
    When I login as "cgray" "cgray1234"
    When I go to "/studentList"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
      And I select view "IL_9-12"
        And I wait for "16" seconds
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "ATTENDANCE.TardyCount" for student "Arsenio Durham" is "28"
      And the class for id "ATTENDANCE.TardyCount" for student "Arsenio Durham" is "countLevel4"

@wip
 Scenario: Teacher sees Tardy Count in 9-12 list of students view - second color
    When I navigate to the Dashboard home page
    When I select "Sunset School District 4526" and click go
    When I login as "cgray" "cgray1234"
    When I go to "/studentList"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
      And I select view "IL_9-12"
        And I wait for "16" seconds
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "ATTENDANCE.TardyCount" for student "Patricia Harper" is "4"
      And the class for id "ATTENDANCE.TardyCount" for student "Patricia Harper" is "countLevel2"
      
@wip
 Scenario: Teacher sees Tardy Count in 9-12 list of students view - first color
    When I navigate to the Dashboard home page
    When I select "Sunset School District 4526" and click go
    When I login as "cgray" "cgray1234"
    When I go to "/studentList"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
      And I select view "IL_9-12"
        And I wait for "16" seconds
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "ATTENDANCE.TardyCount" for student "Charde Lowery" is "0"
      And the class for id "ATTENDANCE.TardyCount" for student "Charde Lowery" is "countLevel1"

Scenario: Monster test until LOS is faster
    When I navigate to the Dashboard home page
    When I select "Sunset School District 4526" and click go
    When I login as "cgray" "cgray1234"
    When I go to "/studentList"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
      And I select view "IL_9-12"
        And I wait for "16" seconds
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "ATTENDANCE.TardyCount" for student "Charde Lowery" is "0"
      And the class for id "ATTENDANCE.TardyCount" for student "Charde Lowery" is "countLevel1"
      And the count for id "ATTENDANCE.TardyCount" for student "Patricia Harper" is "4"
      And the class for id "ATTENDANCE.TardyCount" for student "Patricia Harper" is "countLevel2"
      And the count for id "ATTENDANCE.TardyCount" for student "Arsenio Durham" is "28"
      And the class for id "ATTENDANCE.TardyCount" for student "Arsenio Durham" is "countLevel4"
      And I should see a table heading "Tardy Rate %"
      And the count for id "ATTENDANCE.TardyRate" for student "Charde Lowery" is "0"
      And the class for id "ATTENDANCE.TardyRate" for student "Charde Lowery" is "perfLevel5"
      And the count for id "ATTENDANCE.TardyRate" for student "Jolene Ashley" is "4"
      And the class for id "ATTENDANCE.TardyRate" for student "Jolene Ashley" is "perfLevel4"
      And the count for id "ATTENDANCE.TardyRate" for student "Delilah Sims" is "6"
      And the class for id "ATTENDANCE.TardyRate" for student "Delilah Sims" is "perfLevel3"
      And the count for id "ATTENDANCE.TardyRate" for student "Arsenio Durham" is "13"
      And the class for id "ATTENDANCE.TardyRate" for student "Arsenio Durham" is "perfLevel1"
      And I should see a table heading "Attendance Rate %"
      And the count for id "ATTENDANCE.AttendanceRate" for student "Marvin Miller" is "99"
      And the class for id "ATTENDANCE.AttendanceRate" for student "Marvin Miller" is "perfLevel5"
      And the count for id "ATTENDANCE.AttendanceRate" for student "Alec Swanson" is "95"
      And the class for id "ATTENDANCE.AttendanceRate" for student "Alec Swanson" is "perfLevel4"
      And the count for id "ATTENDANCE.AttendanceRate" for student "Nomlanga Mccormick" is "94"
      And the class for id "ATTENDANCE.AttendanceRate" for student "Nomlanga Mccormick" is "perfLevel3"
      And the count for id "ATTENDANCE.AttendanceRate" for student "Johnny Patel" is "87"
      And the class for id "ATTENDANCE.AttendanceRate" for student "Johnny Patel" is "perfLevel1"
      And I should see a table heading "Absence Count"
      And the count for id "ATTENDANCE.AbsenceCount" for student "Charde Lowery" is "13"

