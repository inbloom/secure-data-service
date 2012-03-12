
Feature: Display Higest score for assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "test" mode
   
Scenario: Calculating Highest ReportingResultType for any a defined assessment 
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
  And the view configuration file set "field.value" is "ISAT Writing.Scale score"
	And the view configuration file set "field.timeslot" is "HIGHEST_EVER"
  
   Then I should see a table heading "ISAT Writing (highest)"
	And I should see a field "SS" in this table
	And I should see  "Delilah Sims" in student field
	And I should see his/her highest ISAT Writing Scale Score is "295"