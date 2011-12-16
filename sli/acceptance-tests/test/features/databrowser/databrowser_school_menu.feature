@wip
Feature: Schools in the DataBrowser

    Scenario: Total # of Schools on the School Menu Page
        Given I go to the databrowser
        And I go to School Menu page
        Then there should be Total Schools "0"
    
    Scenario: Adding, updating and deleting a school
        Given I go to the databrowser
        And I go to School Menu page
        When I enter the name "ChangeMeSchool"
        And click the Add School link
        Then "ChangeMe" should appear on the list school page
        When I update the school "ChangeMeSchool" with "DeleteMeSchool"
        Then "ChangeMeSchoolDeleteMeSchool" should appear on the list school page
        When I click the Delete School link for "ChangeMeSchoolDeleteMeSchool"
        Then "ChangeMeSchoolDeleteMeSchool" should not appear on the list school page
        