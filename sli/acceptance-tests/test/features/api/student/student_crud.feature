Feature: <US267> In order to manage students
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a student.

Background: Logged in as a super-user and using the small data set
	Given I am logged in using "demo" "demo1234"
	Given I have access to all students

#### Happy Path 
Scenario: Create a new student JSON
     Given format "application/json"
        And the name is "Mister" "John" "Doe"
        And the birth date is "1994-04-04"
        And that he or she is "Male"
        And the student_school id is "555"
    When I navigate to POST "/students/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created student
                
Scenario: Read a student by id
    Given format "application/json"
    When I navigate to GET student Alfonso 
    Then I should receive a return code of 200
        And I should see the student "Alfonso" "Ora" "Steele"
        And I should see that he or she is "Male"
        And I should see that he or she was born on "1999-07-12"   

Scenario: Update an existing student
    Given format "application/json"
       And the birth date is "1994-04-05"
    When I navigate to PUT student Priscilla
    Then I should receive a return code of 204
    When I navigate to GET student Priscilla
        And I should see that he or she was born on "1994-04-05" 
        
Scenario: Delete an existing student
    Given format "application/json"
    When I navigate to DELETE student Rachel
    Then I should receive a return code of 204
    When  I navigate to GET student Rachel
    Then I should receive a return code of 404
        
#### XML version
@wip
Scenario: Create a new student XML
    Given format "application/xml"
      And the name is "Student" "Crud" "Person XML"
        And the birth date is "4/4/1994"
        And that he or she is "Male"
        And the student_school id is "555"
    When I navigate to POST "/students/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created student

@wip
Scenario: Delete an existing student XML
    Given format "application/xml"
    When I DELETE the newly created student
    Then I should receive a return code of 204
     When I GET the newly created student by id
     Then I should receive a return code of 404

### Error handling
Scenario: Attempt to read a non-existing student
    Given format "application/json"
    When I navigate to GET student Invalid
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing student
    Given format "application/json"
    When I navigate to DELETE student Invalid
    Then I should receive a return code of 404      

Scenario: Update a non-existing student
    Given format "application/json"
	    When I attempt to update a non-existing student Invalid
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given format "text/plain"
    When I navigate to GET student Donna
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given format "application/json"
    When I navigate to GET student WrongURI
    Then I should receive a return code of 404
    
 Scenario: Attempt to read the base student resource with no GUID
	Given format "application/json"
	When I navigate to GET student NoGUID
	Then I should receive a return code of 405
	
    
