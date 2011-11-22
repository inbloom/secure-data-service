Feature: <US312> In order to manage teachers
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a teacher.

Background: 
	Given the SLI_SMALL dataset is loaded
	Given I am logged in using "jimi" "jimi"
	Given I have access to all teachers

#### Happy Path 
Scenario: Create an new teacher
    When I navigate to POST "/teachers/1234" 
        And format "application/json"
        And the name is "Anny Smith"
        And her degree is "Master's"
        And that she is female
    Then I should receive a return code of 201
        And GET "/teachers/1234" should return a code of 200
                
Scenario: Read a teacher by id
    When I navigate to GET "/teachers/227791546" 
        And format "application/json"
    Then I should receive a return code of 200
        And I should see the teacher Illiana		Solis
        And that she is female
		And that she is not hispanic
        And her degree is "Master's"   

Scenario: Delete an existing teacher
    When I navigate to DELETE "/students/578301014" 
        And format "application/json"
    Then I should receive a return code of 200
        And GET "/students/578301014" should return a code of 404


Scenario: Update an existing teacher
    When I navigate to POST "/teachers/578301014" 
        And format "application/json"
        And his degree is "none"
    Then I should receive a return code of 200
        And I should see the teacher Christian		Petersen
        And that he is male
        And his degree is "none"  
        
#### XML version
Scenario: Create an new teacher
    When I navigate to POST "/teachers/1234" 
        And format "application/xml"
        And the name is "Anny Smith"
        And her degree is "Master's"
        And that she is female
    Then I should receive a return code of 201
        And GET "/teachers/1234" should return a code of 200
                

### Error handling
Scenario: Attempt to read a non-existing teacher 
    When I navigate to GET "/teachers/1" 
        And format "application/json"
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing teacher
    When I navigate to DELETE "/teachers/1" 
        And format "application/json"
    Then I should receive a return code of 404      

Scenario: Update a non-existing teacher
    When I navigate to POST "/teachers/1" 
        And format "application/json"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given Teacher 578301014 exists
    When I navigate to GET "/teachers/578301014" 
        And format "text/plain"
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given Teacher 578301014 exists
    When I navigate to GET "/teacher/578301014" 
        And format "application/json"
    Then I should receive a return code of 404

    
