@wip
Feature: Attendance in the dashboard

As a SEA/LEA user, I want to be able to see various
attendance data for a list of students.

Background:
    Given I have an open web browser
    Given the server is in "live" mode

	Scenario: Teacher sees Absence Count in K-3 list of students view
		Given I am authenticated to SLI as "linda.kim" "linda.kim1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		Then I should see an Attendance column on the right end of the table
			And the Attendance column title should have 2 rows
			And in the first (top) row of the title should be written 'Attendance'
			And in the second row or the title should be written 'Absence Count'
			And for every row (Student) there should be an absence count in the respective Absence Count table cell   
 
	Scenario: Teacher sees Absence Count in 3-8 list of students view
		Given I am authenticated to SLI as "linda.kim" "linda.kim1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		Then I should see an Attendance column on the right end of the table
			And the Attendance column title should have 2 rows
			And in the first (top) row of the title should be written 'Attendance'
			And in the second row or the title should be written 'Absence Count'
			And for every row (Student) there should be an absence count in the respective Absence Count table cell
 
	Scenario: Teacher sees Absence Count in 9-12 list of students view
		Given I am authenticated to SLI as "linda.kim" "linda.kim1234"
		When I go to "/studentList"
	    When I select <edOrg> "Daybreak School District 4529"
	      And I select <school> "Daybreak Central High"
	      And I select <course> "American Literature"
	      And I select <section> "Sec 145"
		Then I should see an Attendance column on the right end of the table
			And the Attendance column title should have 2 rows
			And in the first (top) row of the title should be written 'Attendance'
			And in the second row or the title should be written 'Absence Count'
			And for every row (Student) there should be an absence count in the respective Absence Count table cell