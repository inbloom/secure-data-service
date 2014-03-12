Feature: Display Higest score for assessment contents

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
Given that dashboard has been authorized for all ed orgs

Scenario: Calculating Highest ReportingResultType for any a defined assessment
  Given I am authenticated to SLI as "cgray" "cgray"
  When I go to "/studentlist"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
	And I select <viewSelector> "IL_3-8_ELA"
  And the view configuration file set "field.value" is "StateTest Writing.Scale score"
	And the view configuration file set "field.timeslot" is "HIGHEST_EVER"

   Then I should see a table heading "StateTest Writing (highest)"
	And I should see a field "SS" in this table
	And I should see  "Delilah Sims" in student field
	And I should see his/her highest StateTest Writing Scale Score is "295"

Scenario: Calculating most highest ever for an objective assessment
    When I navigate to the Dashboard home page
    When I select "Illinois Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
      When I select <edOrg> "Daybreak School District 4529"
        And I select <school> "Daybreak Central High"
        And I select <course> "American Literature"
        And I select <section> "Sec 145"
      And I select view "College Ready ELA View"
    Then the table includes header "Reading"
      And I should see a table heading "Tardy Count"

    Then I should see a table heading "Reading Test Scores (Highest)"
	And I should see a field "SAT" for ScaleScore in this table
	And I should see a field "%ile" for PercentileScore in this table
	And I should see a student  "Delilah Sims" in student field
	And I should see his/her highest ScaleScore in SAT Critical Reading is "700"
	And I should see his/her corresponding %ile Score in SAT Critical Reading is "88"