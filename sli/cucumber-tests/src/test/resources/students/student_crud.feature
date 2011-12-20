Feature: <US267> In order to manage students
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a student.

#Background probably eliminates for most of the Given clauses in the scenarios - revisit
Background: Logged in as a super-user and using the small data set
	Given the SLI_SMALL dataset is loaded
	Given I am logged in using "jimi" "jimi"
	Given I have access to all students

#### Happy Path 
Scenario: Create an new student
    When I navigate to POST "/students/1234" 
        And format "application/json"
        And the name is "Jason Mallister"
        And the birth date is "4/4/1004"
        And that he is male
    Then I should receive a return code of 201
        And GET "/students/1234" should return a code of 200
                
Scenario: Read a student by id
    When I navigate to GET "/students/231101422" 
        And format "application/json"
    Then I should receive a return code of 200
        And I should see the student Alfonso Ora Steele
        And that he is male
        And born on "7/12/99"   

Scenario: Delete an existing student
    When I navigate to DELETE "/students/231101422" 
        And format "application/json"
    Then I should receive a return code of 200
        And GET "/students/231101422" should return a code of 404


Scenario: Update an existing student
    When I navigate to POST "/students/231101422" 
        And format "application/json"
        And birth date is "7/13/99"
    Then I should receive a return code of 200
        And I should see the student Alfonso Ora Steele
        And that he is male
        And born on "7/13/99"   
        
#### XML version
Scenario: Create an new student XML
    When I navigate to POST "/students/1234" 
        And format "application/xml"
        And the name is "Jason Mallister"
        And the birth date is "4/4/1004"
        And that he is male
    Then I should receive a return code of 201
        And GET "/students/1234" should return a code of 200


### Error handling
Scenario: Attempt to read a non-existing student
    When I navigate to GET "/students/1" 
        And format "application/json"
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing student
    When I navigate to DELETE "/students/1" 
        And format "application/json"
    Then I should receive a return code of 404      

Scenario: Update a non-existing student
    When I navigate to POST "/students/1" 
        And format "application/json"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given student 231101422 exists
    When I navigate to GET "/students/231101422" 
        And format "text/plain"
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given student 231101422 exists
    When I navigate to GET "/student/231101422" 
        And format "application/json"
    Then I should receive a return code of 404
    
