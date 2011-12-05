Feature: <US267> In order to manage students
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a student.

Background: Logged in as a super-user and using the small data set
	Given the SLI_SMALL dataset is loaded
	Given I am logged in using "demo" "demo1234"
	Given I have access to all students

#### Happy Path 
Scenario: Create a new student JSON
     Given format "application/json"
        And the name is "Student" "Crud" "Person"
        And the birth date is "4/4/1994"
        And that he or she is "Male"
        And the student_school id is "555"
    When I navigate to POST "/students/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created student
                
Scenario: Read a student by id
    Given format "application/json"
    When I GET the newly created student by id
    Then I should receive a return code of 200
        And I should see the student "Student" "Crud" "Person"
        And I should see that he or she is "Male"
        And I should see that he or she was born on "4/4/1994"   

Scenario: Update an existing student
    Given format "application/json"
       And the birth date is "4/5/1994"
    When I PUT/update the newly created students's birthdate
    Then I should receive a return code of 204
    When I GET the newly created student by id
        And I should see that he or she was born on "4/5/1994" 
        
Scenario: Delete an existing student
    Given format "application/json"
    When I DELETE the newly created student
    Then I should receive a return code of 204
    When I GET the newly created student by id
    Then I should receive a return code of 404
        
#### XML version
Scenario: Create a new student XML
    Given format "application/xml"
      And the name is "Student" "Crud" "Person XML"
        And the birth date is "4/4/1994"
        And that he or she is "Male"
        And the student_school id is "555"
    When I navigate to POST "/students/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created student

Scenario: Delete an existing student XML
    Given format "application/xml"
    When I DELETE the newly created student
    Then I should receive a return code of 204
     When I GET the newly created student by id
     Then I should receive a return code of 404

### Error handling
Scenario: Attempt to read a non-existing student
    Given format "application/json"
    When I navigate to GET "/students/1" 
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing student
    Given format "application/json"
    When I navigate to DELETE "/students/1" 
    Then I should receive a return code of 404      

Scenario: Update a non-existing student
    Given format "application/json"
    When I attempt to update a non-existing student "/students/1"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given a known student exists
       And format "text/plain"
    When I navigate to GET to said student
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given a known student exists
       And format "application/json"
    When I navigate to GET to said student with "/student/"
    Then I should receive a return code of 404
    
