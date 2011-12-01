@wip
Feature: <US312> In order to manage teachers
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a teacher.

Background: Logged in as a super-user and using the small data set
	Given the SLI_SMALL dataset is loaded
	Given I am logged in using "jimi" "jimi"
	Given I have access to all teachers

#### Happy Path 
Scenario: Create an new teacher JSON
    Given format "application/json"
        And the name is "Anny Smith"
        And his or her degree is "Master's"  
        And that his or her sex is "female"
    When I navigate to POST "/teachers/" 
    Then I should receive a return code of 201
        And I should receive a ID for the newly created teacher
        And GET using that ID should return a code of 200
                
Scenario: Read a teacher by id
    Given format "application/json"
    When I navigate to GET "/teachers/227791546" 
    Then I should receive a return code of 200
        And I should see the teacher "Illiana Solis"
        And I should see that his or her sex is "female"
        And I should see that his or her race is not "hispanic"
        And I should see that his or her degree is "Master's"   


Scenario: Update an existing teacher
    Given Teacher 578301014 exists
        And format "application/json"
        And his or her degree is "none"
    When I navigate to PUT "/teachers/578301014" 
    Then I should receive a return code of 204
        And I should see the teacher "Christian Petersen"
        And I should see that his or her sex is "male"
        And I should see that his or her degree is "none"  
        
Scenario: Delete an existing teacher
    Given Teacher 578301014 exists
        And format "application/json"
    When I navigate to DELETE "/teachers/578301014" 
    Then I should receive a return code of 200
        And GET "/students/578301014" should return a code of 404
        
#### XML version
Scenario: Create an new teacher XML
    Given format "application/xml"
        And the name is "Anny Smith"
        And her degree is "Master's"
        And that his or her sex is "female"
    When I navigate to POST "/teachers/" 
    Then I should receive a return code of 201
        And I should receive a ID for the newly created teacher
        And GET using that ID should return a code of 200
                

### Error handling
Scenario: Attempt to read a non-existing teacher 
    Given format "application/json"
    When I navigate to GET "/teachers/1" 
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing teacher
    Given format "application/json"
    When I navigate to DELETE "/teachers/1" 
    Then I should receive a return code of 404      

Scenario: Update a non-existing teacher
    Given format "application/json"
    When I navigate to PUT "/teachers/1" 
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given Teacher 578301014 exists
        And format "text/plain"
    When I navigate to GET "/teachers/578301014" 
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given Teacher 578301014 exists
        And format "application/json"
    When I navigate to GET "/teacher/578301014" 
    Then I should receive a return code of 404

    
