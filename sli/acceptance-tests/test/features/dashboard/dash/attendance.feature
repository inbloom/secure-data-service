Feature: Attendance in the dashboard

As a SEA/LEA user, I want to be able to see various
attendance data for a list of students.

Background:
    Given I have an open web browser
    Given the server is in "live" mode
	
@wip
Scenario: Teacher sees Absence Count in K-3 list of students view
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
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
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
	  	  And I select view "IL_3-8 ELA"
		  And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Absence Count"
			And the count for id "ATTENDANCE.AbsenceCount" for student "Charde Lowery" is "25"
 
Scenario: Teacher sees Absence Count in 9-12 list of students view
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Absence Count"
			And the count for id "ATTENDANCE.AbsenceCount" for student "Charde Lowery" is "25"

Scenario: Teacher sees Attendance Rate in 9-12 list of students view - red
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Attendance Rate %"
			And the count for id "ATTENDANCE.AttendanceRate" for student "Ursa Oconnor" is "88"
      			And the class for id "ATTENDANCE.AttendanceRate" for student "Ursa Oconnor" is "perfLevel1"

Scenario: Teacher sees Attendance Rate in 9-12 list of students view - yellow 
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Attendance Rate %"
			And the count for id "ATTENDANCE.AttendanceRate" for student "Zachary Patton" is "90"
      			And the class for id "ATTENDANCE.AttendanceRate" for student "Zachary Patton" is "perfLevel3"

Scenario: Teacher sees Attendance Rate in 9-12 list of students view - light green
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Attendance Rate %"
			And the count for id "ATTENDANCE.AttendanceRate" for student "Madeline Hinton" is "94"
      			And the class for id "ATTENDANCE.AttendanceRate" for student "Madeline Hinton" is "perfLevel4"

Scenario: Teacher sees Attendance Rate in 9-12 list of students view - green
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Attendance Rate %"
			And the count for id "ATTENDANCE.AttendanceRate" for student "Uriah Jordan" is "100"
      			And the class for id "ATTENDANCE.AttendanceRate" for student "Uriah Jordan" is "perfLevel5"

Scenario: Teacher sees Tardy Rate in 9-12 list of students view - red
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Tardy Rate %"
			And the count for id "ATTENDANCE.TardyRate" for student "Rhonda Delgado" is "11"
      			And the class for id "ATTENDANCE.TardyRate" for student "Rhonda Delgado" is "perfLevel1"
			
Scenario: Teacher sees Tardy Rate in 9-12 list of students view - yellow
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Tardy Rate %"
			And the count for id "ATTENDANCE.TardyRate" for student "Patricia Harper" is "7"
      			And the class for id "ATTENDANCE.TardyRate" for student "Patricia Harper" is "perfLevel3"

Scenario: Teacher sees Tardy Rate in 9-12 list of students view - light green 
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Tardy Rate %"
			And the count for id "ATTENDANCE.TardyRate" for student "TaShya Alston" is "2"
      			And the class for id "ATTENDANCE.TardyRate" for student "TaShya Alston" is "perfLevel4"

Scenario: Teacher sees Tardy Rate in 9-12 list of students view - green 
		When I navigate to the Dashboard home page
		When I select "Illinois Realm" and click go
		When I login as "cgray" "cgray1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		  And I select view "IL_9-12"
	      And I wait for "7" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Tardy Rate %"
			And the count for id "ATTENDANCE.TardyRate" for student "Marvin Miller" is "0"
      			And the class for id "ATTENDANCE.TardyRate" for student "Marvin Miller" is "perfLevel5"
			
 Scenario: Teacher sees Tardy Count in 9-12 list of students view - third color
    When I navigate to the Dashboard home page
    When I select "Illinois Realm" and click go
    When I login as "cgray" "cgray1234"
    When I go to "/studentList"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
      And I select view "IL_9-12"
        And I wait for "7" seconds
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "ATTENDANCE.TardyCount" for student "Rhonda Delgado" is "25"
      And the class for id "ATTENDANCE.TardyCount" for student "Rhonda Delgado" is "countLevel4"


 Scenario: Teacher sees Tardy Count in 9-12 list of students view - second color
    When I navigate to the Dashboard home page
    When I select "Illinois Realm" and click go
    When I login as "cgray" "cgray1234"
    When I go to "/studentList"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
      And I select view "IL_9-12"
        And I wait for "7" seconds
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "ATTENDANCE.TardyCount" for student "Delilah Sims" is "2"
      And the class for id "ATTENDANCE.TardyCount" for student "Delilah Sims" is "countLevel2"
      
 Scenario: Teacher sees Tardy Count in 9-12 list of students view - first color
    When I navigate to the Dashboard home page
    When I select "Illinois Realm" and click go
    When I login as "cgray" "cgray1234"
    When I go to "/studentList"
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
      And I select view "IL_9-12"
        And I wait for "12" seconds
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "ATTENDANCE.TardyCount" for student "Astra Vincent" is "0"
      And the class for id "ATTENDANCE.TardyCount" for student "Astra Vincent" is "countLevel1"

