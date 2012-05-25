Feature: User based view selection

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "live" mode
  When I navigate to the Dashboard home page
  When I select "Illinois Daybreak School District 4529" and click go

@integration @wip
Scenario: Check user has multiple views available
#notes: SDS doesn't have students that are in more than one school/grade level
  When I login as "cgray" "cgray1234"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  Then I should have a dropdown selector named "viewSelect"
    And I should have a selectable view named "College Ready ELA View"

@integration @wip
Scenario: Views are filtered based on student grades
#notes: SDS doesn't have students that are in more than one school/grade level
  When I login as "cgray" "cgray1234"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "Writing about Government"
    And I select <section> "Sec 923"
  Then I should only see one view named "Middle School ELA View"

@integration @wip
Scenario: Check changing view changes table headings
#notes: SDS doesn't have students that are in more than one school/grade level
  When I login as "cgray" "cgray1234"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "Daybreak Central High"
    And I select <course> "American Literature"
    And I select <section> "Sec 145"
  When I select view "Middle School ELA View"
  Then I should see a table heading "StateTest Reading"
    And I should see a table heading "StateTest Writing (most recent)"
    
 @integration @RALLY_US200
Scenario: Different users have different views defined
  When I login as "rbraverman" "rbraverman1234"
  When I select <edOrg> "Daybreak School District 4529"
    And I select <school> "South Daybreak Elementary"
    And I select <course> "1st Grade Homeroom"
    And I select <section> "Mrs. Braverman's Homeroom #38"
  Then I should only see one view named "Early Literacy View"