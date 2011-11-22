Feature: GET /students
    This the SLI API to get all student information.

    Scenario: The client requests GET api/rest/students and the response code is 200
        Given I am logged in using "jimi" "jimi"
        And I go to "/students"
        Then the status code for "/students" format "application/json" is 200
    
    Scenario: The client requests GET api/rest/student and the response code is 404
        Given I am logged in using "jimi" "jimi"
        And I go to "/student"
        Then the status code for "/student" format "application/json" is 404
        
     Scenario: The client requests GET api/rest/students with an unsupported format
        Given I am logged in using "jimi" "jimi"
        And I go to "/students"
        Then the status code for "/students" format "text/plain" is 406   
        
    Scenario: The client requests GET api/rest/students
        Given I am logged in using "jimi" "jimi"
        And I go to "/students"
        Then "/students" format "application/json" contains "Uriah"
        
    Scenario: The client requests to add a student
        Given I am logged in using "jimi" "jimi"
        When I go to "/students" and request to add a student
        And I go to "/students"
     
    Scenario: The client requests to delete a student
        Given I am logged in using "jimi" "jimi"
        When I go to "/students" and request to delete a student
        And I go to "/students"
        
     Scenario: The client requests to update a student
        Given I am logged in using "jimi" "jimi"
        When I go to "/students" and request to change a student name
        And I go to "/students"
        
     Scenario: The client requests a single student
        Given  I am logged in using "jimi" "jimi"
        When I go to "/students" and request a student by id
        Then the student info is returned
        
    
        
        
     
        
    
    
    
    
        

    
    