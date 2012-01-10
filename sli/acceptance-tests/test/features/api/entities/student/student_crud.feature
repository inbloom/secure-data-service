Feature: <US267> In order to manage students
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a student.

Background: Logged in as a super-user and using the small data set
	Given I am logged in using "demo" "demo1234"
	Given I have access to all students

#### Happy Path 
Scenario: Create a new student JSON
     Given format "application/json"
        And the "name" is "Mister" "John" "Doe"
        And the "birthDate" is "1994-04-04"
        And the "sex" is "Male"
        And the "SchoolId" is <'Some Elementary School' ID>
    When I navigate to POST "/students/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created student
       And the name should be "Mister" "John" "Doe"
       And the "birthDate" should be "1994-04-04"
       And that the "sex" should be "Male"
       And the "SchoolId" should be <'Some Elementary School' ID>
                
Scenario: Read a student by id
    Given format "application/json"
    When I navigate to GET /students/<'Alfonso' ID>
    Then I should receive a return code of 200
        And the "name" is "Alfonso" "Ora" "Steele"
        And the "sex" is "Male"
        And the "birthDate" is "1999-07-12"
        And I should receive a link named “getStudentSectionAssociations” with URI /student-section-associations/<'Alfonso' ID>
        And I should receive a link named “getSections” with URI /student-section-associations/<'Alfonso' ID>/targets
        And I should receive a link named “getStudentSchoolAssociations” with URI /student-school-associations/<'Alfonso' ID>
        And I should receive a link named “getSchools” with URI /student-school-associations/<'Alfonso' ID>/targets
        And I should receive a link named "self" with URI /students/<'Alfonso' ID>

Scenario: Update an existing student
    Given format "application/json"
    When I navigate to GET "/students/<'Priscilla' ID>
       And the "birthDate" is "1994-04-05"
    When I set the "birthDate" to "1995-04-05"
      And I navigate to PUT /students/<'Priscilla' ID>
      Then I should receive a return code of 204
    When I navigate to GET /students/<'Priscilla' ID>
        And the "birthDate" is "1995-04-05"
        
Scenario: Delete an existing student
    Given format "application/json"
    When I navigate to DELETE /students/<'Rachel' ID>
    Then I should receive a return code of 204
    When  I navigate to GET /students/<'Rachel' ID>
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
    When I navigate to GET /student "Invalid"
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing student
    Given format "application/json"
    When I navigate to DELETE student "Invalid"
    Then I should receive a return code of 404      

Scenario: Update a non-existing student
    Given format "application/json"
	    When I attempt to update a non-existing student "Invalid"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given format "text/plain"
    When I navigate to GET student "Donna"
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given format "application/json"
    When I navigate to GET student "WrongURI"
    Then I should receive a return code of 404
    
 Scenario: Attempt to read the base student resource with no GUID
	Given format "application/json"
	When I navigate to GET student "NoGUID"
	Then I should receive a return code of 405
	
    
