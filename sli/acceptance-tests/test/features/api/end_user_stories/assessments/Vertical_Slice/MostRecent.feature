@wip
Feature: Display simple assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "test" mode

Scenario: Displaying simple ISAT reading results for all students
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
	And the view configuration file set "field.value" is  "ISAT Reading.Scale score"
	
	Then I should see a table heading "ISAT Reading"
	And I should see a field "SS" in this talbe
	And I should see student "Delilah Sims" in "student" field
	And I should see his/her ISAT Reading Scale Score is "223"
	

Scenario: Displaying most recent ISAT writing results for all students
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
	And the view configuration file set "field.value" is  is "ISAT Writing.Scale score"
	And the view configuration file set "field.timeslot" is "MOST_RECENT_RESULT"
    
    Then I should see a table heading "ISAT Writing (most recent)"
	And I should see a field "SS" in this talbe
	And I should see student "Delilah Sims" in "student" field
	And I should see his/her  ISAT Writing Scale Score is "265"