@wip
Feature: Students in the DataBrowser

    Scenario: Total # of Students on the Student Menu Page
        Given I go to the databrowser
        And I go to Student Menu page
        Then there should be Total Students "0"
        And I click List All Students button
    
    Scenario: Adding, updating and deleting a student
        Given I go to the databrowser
        And I go to Student Menu page
        When I enter the first name "ChangeMe"
        And I enter the last name "Jones"
        And click the Add Student link
        Then "ChangeMe" should appear on the list student page
        When I update student "ChangeMe" with "DeleteMe"
        Then "ChangeMeDeleteMe" should appear on the list student page
        When I click the Delete Student link for "ChangeMeDeleteMe"
        Then "ChangeMeDeleteMe" should not appear on the list student page
