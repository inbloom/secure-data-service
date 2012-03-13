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

	Scenario: Teacher sees Tardy Rate in 9-12 list of students view
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
      And the class for id "ATTENDANCE.TardyCount" for student "Rhonda Delgado" is "countLevel3"


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
        And I wait for "7" seconds
    Then the table includes header "Attendance"
      And I should see a table heading "Tardy Count"
      And the count for id "ATTENDANCE.TardyCount" for student "Astra Vincent " is "0"
      And the class for id "ATTENDANCE.TardyCount" for student "Astra Vincent" is "countLevel1"

