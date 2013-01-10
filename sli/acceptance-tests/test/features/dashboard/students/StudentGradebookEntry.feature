@wip @RALLY_US149
Feature: Display student gradebook entry data
As a SLI user, I want to be able to select different views in my dashboard application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given that dashboard has been authorized for all ed orgs

Scenario: Displaying gradebook entry data for all students - Linda Kim
  	When I navigate to the Dashboard home page
  	When I select "Illinois Daybreak School District 4529" and click go
 	And I was redirected to the "Simple" IDP Login page
 	When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page 
	When I select ed org "Daybreak School District 4529"
	When I select school "East Daybreak Junior High"
	And I select course "8th Grade English"
	And I select section "8th Grade English - Sec 6"
	Then I see a list of 28 students
    And I click on student "Matt Sollars"

    # Historical data
  Then I should see a table heading "2010-2011 Fall Semester"
    And I should see a field "Course" in this table
    And I should see a field "Grade" in this table
    And I should see the name "Matt Sollars" in student field with link
    And I should see his/her "7th Grade English" course grade is "A" in this table
    # Gradebook entry
    And I look at his/her test grades
    And I should see his/her "<Matt Sollars FIRST UNIT TEST>" grade is 68
    And I should see his/her "<Matt Sollars SECOND UNIT TEST>" grade is 78
    And I should see his/her "<Matt Sollars THIRD UNIT TEST>" grade is 69

Scenario: Displaying gradebook entry data for all students - Charles Gray
    When I navigate to the Dashboard home page
    And I select "Sunset School District 4526" and click go
    And I login as "cgray" "cgray1234"
    And I go to the old dashboard page
    When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
    And I wait for "10" seconds
    And I select <viewSelect> "IL_9-12"
    Then I see a list of students

    # Historical data
    Then I should see a table heading "2010-2011 Fall Semester"
    And I should see a field "Course" in this table
    And I should see a field "Grade" in this table
    And I should see the name "Carmen Ortiz" in student field with link
    And I should see his/her "(none)" course grade is "(none)" in this table

    Then I should see a table heading "2010-2011 Spring Semester"
    And I should see the name "Jolene Ashley" in student field with link
    And I should see his/her "7th Grade Composition" course grade is "B" in this table
    # Gradebook entry
#   And I look at his/her test grades
#   And I should see his/her "<Carmen Ortiz Current Grade>" grade is 100