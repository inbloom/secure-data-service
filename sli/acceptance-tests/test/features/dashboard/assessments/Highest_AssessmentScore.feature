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
	
#USE:  "assessmentFamilyHierarchy" is  "AP English"
@wip
Scenario: Calculating most highest ever for an assessment 
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  Then I should only see one view named "IL_9_12"
  And the view configuration file set "field.value" is "Literature.ScaleScore"
  And the view configuration file set "field.value" is "Language.ScaleScore"
  And the view configuration file set "field.timeslot" is "HIGHEST_EVER"
  
    Then I should see a table heading "AP Eng Exam Scores (highest)"
    And I should see "Lang"  Assessment
    And I should see "Lit"  Assessment
	And I should see a field "SS" in this table for "Lang" and "Lit" Objective Assessment
	And I should see  "Suzy Queue" in student field
	And I should see his/her highest English Literature and Composition ScaleScore is "2"
	And I should see his/her highest English Language and Composition ScaleScore is "3"
	
#USE :  "assessmentFamilyHierarchy" is  "SAT"
@wip
Scenario: Calculating most highest ever for an objective assessment 
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  Then I should only see one view named "IL_9_12"
  And the view configuration file set "field.value" is "Critical Reading.ScaleScore"
  And the view configuration file set "field.timeslot" is "HIGHEST_EVER"
  
    Then I should see a table heading "Reading Test Score(highest)"
    And I should see "Critical Reading" Objective Assessment
	And I should see a field "SAT" for ScaleScore
	And I should see a field "%ile" for PercentileScore 
	And I should see  "Suzy Queue" in student field
	And I should see his/her highest ScaleScore in SAT Critical Reading is "680"
	And I should see his/her correspoending %ile Score in SAT Critical Reading is "78"