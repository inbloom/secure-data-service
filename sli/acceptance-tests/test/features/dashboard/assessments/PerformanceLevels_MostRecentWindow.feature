@RALLY_US510
@wip
Feature: Display either most recent performance levels for assessment contents or performance levels in a most recent window.

As a SEA/LEA user, I want to be able to see student Performance Level of their assessment result in my dashboard
application

Background:
  Given I have an open web browser
  Given the server is in "test" mode
  
# USE:  AssessmentFamilyHierarchy like "ISAT Writing for Grades 8" 
Scenario: Calculating Most Recent Performance Level for an Assessment
Given I am authenticated to SLI as "lkim" "lkim"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
	And the view configuration file set "field.value" is "ISAT Writing for Grades 8.Mastery level"
	And the view configuration file set "field.timeslot" is "MOST_RECENT_RESULT"
  
 Then I should see a table heading "ISAT Writing (most recent)"
	And I should see a field "Perf. Lvl." in this table
	And I should see  "Delilah Sims" in student field
	And I should see his/her most recent ISAT Writing Perf. level is "3"

# USE: AssessmentFamilyHierarchy like "DIBELS.DIBELS Next*"	
Scenario: Calculating Most Recent Performance Level for an Assessment Family
 Given I am authenticated to SLI as "rbraverman" "rbraverman"
 When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "South Daybreak Elementary"
    And I select <course> "1st Grade Homeroom"
	And I select <section> "Mrs. Braverman's Homeroom #38"
	And I select <viewSelector> "IL_K-3"
	And the view configuration file set "field.value" is "DIBELS.Mastery level"
	And the view configuration file set "field.timeslot" is "MOST_RECENT_WINDOW_RESULT"
  
 Then I should see a table heading "DIBELS Next"
	And I should see a field "Perf. Lvl." in this table
	And I should see  "Rhonda Delgado" in student field
	And I should see his/her Perf.level for DIBELS Next for most recent window is "2"
