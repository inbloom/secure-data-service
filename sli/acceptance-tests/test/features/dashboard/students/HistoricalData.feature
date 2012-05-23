@wip
@RALLY_US149
Feature: Display student historical data
As a SLI user, I want to be able to select different views in my dashboard application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "live" mode

Scenario: Displaying historical data for all students
  Given I am authenticated to SLI as "jjones" "jjones1234"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "Algebra-11"
    And I select <section> "Algebra-11-A"
    And I wait for "30" seconds
	And I select <viewSelector> "Historical Data"
	And I wait for "60" seconds
	
  Then I should see a table heading "2010-2011 Fall Semester"
	And I should see a field "Course" in this table
	And I should see a field "Grade" in this table	
	And I should see "Shannon Delgado" in student field
	And I should see his/her "Algebra-10-A" course grade is "A-" in this table
	
	And I should see a table heading "2010-2011 Spring Semester"
	And I should see a field "Course" in this table
	And I should see a field "Grade" in this table
	And I should see "Ian Anderson" in student field
	And I should see his/her "Geometry-10-A" course grade is "F" in this table
	And I should see "Jorge Lopez" in student field
	And I should see his/her "(none)" course grade is "(none)" in this table