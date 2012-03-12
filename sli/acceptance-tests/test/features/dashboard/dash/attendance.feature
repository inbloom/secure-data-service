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
			And I should see a table heading "Abcence Count"
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
		  And I wait for "5" seconds
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
	      And I wait for "5" seconds
		Then the table includes header "Attendance"
			And I should see a table heading "Absence Count"
			And the count for id "ATTENDANCE.AbsenceCount" for student "Charde Lowery" is "25"