@wip
Feature: Display student gradebook entry data
As a SLI user, I want to be able to select different views in my dashboard application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "live" mode

Scenario: Displaying gradebook entry data for all students
  Given I am authenticated to SLI as "linda.kim" "linda.kim1234"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "East Daybreak Junior High"
    And I select <course> "8th Grade English"
    And I select <section> "8th Grade English - Sec 6"
    And I wait for "180" seconds
	And I select <viewSelector> "Middle School ELA View"
#	And I wait for "60" seconds
	
	#Historical data
  Then I should see a table heading "2010-2011 Fall Semester"
	And I should see a field "Course" in this table
	And I should see a field "Grade" in this table	
	And I should see the name "Gannon Roman" in student field with link
	And I should see his/her "7th Grade English - Sec 6" course grade is "F" in this table
	And I should see his/her current average grade is "66%"
	
	And I should see a table heading "Current"
	And I should see a field "Average" in this table
	And I should see the name "Nicholas Burks" in student field with link
	And I should see his/her current average grade is "44%"