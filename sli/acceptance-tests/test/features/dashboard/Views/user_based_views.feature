@wip
Feature: User based view selection

As a SEA/LEA user, I want to be able to select different views in my dashboard
application, that will change the subset of information that is displayed.

Background:
  Given I have an open web browser
  Given the server is in "test" mode

Scenario: Check user has multiple views available
  Given I am authenticated to SLI as "cgray" password "cgray"
  When I access "/studentlist"
  When I select edOrg "Daybreak School District 4529"
    And I select school "Daybreak Central High"
    And I select course "American Literature"
    And I select section "Sec 145"
  Then I should have a dropdown selector named "viewSelector"
    And I should have a selectable view named "IL_3-8_ELA"
    And I should have a selectable view named "IL_9-12"

Scenario: Check changing view changes table headings
  Given I am authenticated to SLI as "cgray" password "cgray"
  When I access "/studentlist"
  When I select edOrg "Daybreak School District 4529"
    And I select school "Daybreak Central High"
    And I select course "American Literature"
    And I select section "Sec 145"
  When I select view "IL_3-8_ELA"
  Then I should see a table heading "ISAT Reading"
    And I should see a table heading "ISAT Writing (most recent)"
  When I select view "IL_9-12"
  Then I should see a table heading "Reading Test Scores (Highest)"
    And I should see a table heading "Writing Test Scores (Highest)"
    And I should see a table heading "AP Eng. Exam Scores"
  
Scenario: Different users have different views defined
  Given I am authenticated to SLI as "rbraverman" password "rbraverman"
  When I access "/studentlist"
  When I select edOrg "Illinois State Board of Education"
    And I select school "South Daybreak Elementary"
    And I select course "1st Grade Homeroom"
    And I select section "Mrs. Braverman's Homeroom #38"
  Then I should have a dropdown selector named "viewSelector"
    And I should have a selectable view named "IL_K-3"