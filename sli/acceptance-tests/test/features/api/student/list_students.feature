Feature: <US267> In order to view a list of students
    As a client application using SLI
    I want to be able to view all students and all students by school.

Background: Logged in as a super-user and using the small data set
	Given the SLI_SMALL dataset is loaded
	Given I am logged in using "jimi" "jimi"
	Given I have access to all students

#### Happy Path 
# Make a table for multiple names
Scenario: List all students
    Given format "application/json"
    When I navigate to GET "/students/" 
    Then I should receive a return code of 200
        And I should see 100 students
	   And I should find "Alma Sopoline Hogan"
	   And I should find "Felix J Clayton"

Scenario: List all students for a school
    When I navigate to GET "/schools/152901001/students" 
        And format "application/json"
    Then I should receive a return code of 200
        And I should see 50 students
        And I should find "Alma Sopoline Hogan"
	   And I should not find "BellAllegra Guerrero"
		

