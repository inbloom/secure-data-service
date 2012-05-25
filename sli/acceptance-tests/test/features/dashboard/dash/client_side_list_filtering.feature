@wip
Feature: Client side list filtering (US 145)

As a SEA/LEA user, I want to be able to select different filters
in my dashboard application, that will change the subset of students
that are displayed.

    Background:
        Given I have an open web browser
        Given the server is in "test" mode

@RALLY_US145
    Scenario: Check user has multiple filters available
        Given I am authenticated to SLI as "cgray" "cgray"
        When I select <edOrg> "Daybreak School District 4529"
          And I select <school> "Daybreak Central High"
          And I select <course> "American Literature"
          And I select <section> "Sec 145"
        Then I should have a dropdown selector named "studentFilterSelector"
          And I should have multiple filters available

  @RALLY_US145
    Scenario: Students are filtered based on filter selected
        Given I am authenticated to SLI as "cgray" "cgray"
        When I select <edOrg> "Daybreak School District 4529"
          And I select <school> "Daybreak Central High"
          And I select <course> "American Literature"
          And I select <section> "Sec 145"
        When I select filter "English Language Learner"
        Then I should see a student named "Arsenio Durham"
          And I should see a student named "Alec Swanson"
        When I select filter "Section 504"
        Then I should see a student named "Madeline Hinton"

