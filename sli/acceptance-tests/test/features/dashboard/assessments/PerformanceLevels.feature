@wip
Feature: Display performance levels for assessment contents 

As a SEA/LEA user, I want to be able to see student Performance Level of their assessment result in my dashboard
application

Background:
  Given I have an open web browser
  Given the server is in "test" mode
  
Scenario: Calculating Most Recent Performance Level for an Assessment Family
 Given I am authenticated to SLI as  "rbraverman" "rbraverman1234"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <section> "Sec 100"
	And I select <viewSelector> "IL_K-3"
	And the view configuration file has a AssessmentFamilyHierarchy like "DIBELS.DIBELS Next*"
	And the view configuration file set "field.value" is "DIBELS.Mastery level"
	And the view configuration file set "field.timeslot" is "MOST_RECENT_WINDOW_RESULT"
  
 Then I should see a table heading "DIBELS Next"
	And I should see a field "Perf. Lvl." in this table
	And I should see  "Jenny Dean" in student field
	And I should see his/her most recent ISAT Writing Perf. level is "2"
	
Scenario: Calculating Most Recent Performance Level for an Assessment
 Given I am authenticated to SLI as  "linda.kim" "linda.kim1234"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 300"
	And I select <viewSelector> "IL_3-8_ELA"
	And the view configuration file has a AssessmentFamilyHierarchy like "ISAT Writing for Grades 3-8.ISAT Writing for Grades 8*"
	And the view configuration file set "field.value" is "ISAT Writing.Mastery level"
	And the view configuration file set "field.timeslot" is "MOST_RECENT_RESULT"
  
 Then I should see a table heading "ISAT Writing (most recent)"
	And I should see a field "Perf. Lvl." in this table
	And I should see  "Delilah Sims" in student field
	And I should see his/her most recent ISAT Writing Perf. level is "3"	