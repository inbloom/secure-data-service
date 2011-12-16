@wip
Feature: Student-School Association in the Databrowser


    Scenario: Add a student to a school, update the association, and then remove the student from the school
        Given I go to the databrowser
        And I go to the Student-School Association Menu page
        When I select a student
        And Add the Student to a School
        Then the student should appear associated with that School
        And I change the students grade to "KINDERGARTEN"
        And press the update link
        Then the student appears now in "Kindergarten" 
        And I delete the association
        Then the student does not appear associated with that School
        
     
