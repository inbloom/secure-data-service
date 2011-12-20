Feature: <US312> In order to manage teachers
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a teacher.

Background: Logged in as a super-user and using the small data set
	Given I am logged in using "demo" "demo1234"
	Given I have access to all teachers

#### Happy Path 
Scenario: Create an new teacher JSON
    Given format "application/json"
        And the teacher_unique_state id is 555
        And the name is "Anny" "" "Smith"
        And his or her level of education is "Master's"  
        And that his or her sex is "Female"
    When I navigate to POST "/teachers/" 
    Then I should receive a return code of 201
        And I should receive a ID for the newly created teacher
        And GET using that ID should return a code of 200

@wip                
Scenario: Read a teacher by id
    Given format "application/json"
    When I navigate to GET teacher Illiana 
    Then I should receive a return code of 200
        And I should see the teacher "Illiana" "Solis"
        And I should see that his or her sex is "Female"
        And I should see that his or her hispanic latino ethnicity is "false"
        And I should see that his or her level of education is "Master's"   

@wip
Scenario: Update an existing teacher
    Given teacher Christian exists
        And format "application/json"
        And his or her level of education is "none"
    When I navigate to PUT teacher Christian 
    Then I should receive a return code of 204
        And I should see the teacher "Christian" "Petersen"
        And I should see that his or her sex is "Male"
        And I should see that his or her level of education is "none"  

@wip        
Scenario: Delete an existing teacher
    Given teacher Christian exists
        And format "application/json"
    When I navigate to DELETE teacher Christian
    Then I should receive a return code of 204
    When I navigate to DELETE teacher Christian
    Then I should receive a return code of 404
        
#### XML version
@wip
Scenario: Create an new teacher XML
    Given format "application/xml"
        And the teacher_unique_state id is "556"
        And the name is "Anny" "" "Smith"
        And her level of education is "Master's"
        And that his or her sex is "Female"
    When I navigate to POST "/teachers/" 
    Then I should receive a return code of 201
        And I should receive a ID for the newly created teacher
        And GET using that ID should return a code of 200
                

### Error handling
@wip
Scenario: Attempt to read a non-existing teacher 
    Given format "application/json"
    When I navigate to GET teacher Unknown 
    Then I should receive a return code of 404      

@wip
Scenario: Attempt to delete a non-existing teacher
    Given format "application/json"
    When I navigate to DELETE teacher Unknown 
    Then I should receive a return code of 404      

@wip
Scenario: Update a non-existing teacher
    Given format "application/json"
    When I navigate to PUT teacher Unknown
    Then I should receive a return code of 404      

@wip    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given teacher Micah
        And format "text/plain"
    When I navigate to GET teacher Micah
    Then I should receive a return code of 406

@wip    
Scenario: Fail if going to the wrong URI
    Given teacher Belle exists
        And format "application/json"
    When I navigate to GET "/teacher/Belle" 
    Then I should receive a return code of 404
