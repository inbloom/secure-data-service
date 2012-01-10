Feature: <US63> In order to manage schools
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a school.

Background: Logged in as a super-user and using the small data set
	Given I am logged in using "demo" "demo1234"
	Given I have access to all schools

#### Happy Path 
Scenario: Create a new school JSON
    Given format "application/json"
       And the "shortName" is "SCTS"
	  And the "fullName" is "School Crud Test School"
	  And the "website" is "www.scts.edu"
    When I navigate to POST "/schools/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created school
    When I navigate to GET a school <'newly created school' ID>
    Then the "shortName" should be "SCTS"
      And the "fullName" should be "School Crud Test School"
      And the "website" should be "www.scts.edu"

	  
Scenario: Read a school by id
    Given format "application/json"
    When I navigate to GET /schools/<'Apple Alternative Elementary School' ID>
    Then I should receive a return code of 200
       And I should see the "fullName" is "Apple Alternative Elementary School"
       And I should see the "phoneNumber" is "(785) 667-6006"
       And I should receive a link named “getStudentSchoolAssociations” with URI /student-school-associations/<'Apple Alternative Elementary School' ID>
       And I should receive a link named “getStudents” with URI /student-school-associations/<'Apple Alternative Elementary School' ID>/targets
       And I should receive a link named “getTeacherSchoolAssociations” with URI /teacher-school-associations/<'Apple Alternative Elementary School' ID>
       And I should receive a link named “getTeachers” with URI /teacher-school-associations/<'Apple Alternative Elementary School' ID> /targets
       And I should receive a link named "self" with URI /schools/<'Apple Alternative Elementary School' ID>

Scenario: Update an existing school
    Given format "application/json"
    When I navigate to GET /schools/<'Yellow Middle School' ID>
      And the "fullName" is "Yellow Middle School"
    When I set the "fullName" to "Yellow Middle and High School"
    When I navigate to PUT /schools/<'Yellow Middle School' ID>
    Then I should receive a return code of 204
    When I navigate to GET /schools/<'Yellow Middle School' ID>
 	 And the "fullName" is "Yellow Middle and High School"

 	 
Scenario: Delete an existing school
    Given format "application/json"
    When I navigate to DELETE /schools/<'Delete Me Middle School' ID>
    Then I should receive a return code of 204
     When I navigate to GET /schools/<'Delete Me Middle School' ID>
     Then I should receive a return code of 404
        
#### XML version
@wip
Scenario: Create a new school XML
    Given format "application/xml"
       And the short name is "SCTSX"
	  And the full name is "School Crud Test School XML"
	  And the website is "www.sctsx.edu"
    When I navigate to POST "/schools/" 
    Then I should receive a return code of 201
       And I should receive a ID for the newly created school
       
@wip
Scenario: Delete an existing school XML
    Given format "application/xml"
    When I DELETE the newly created school
    Then I should receive a return code of 204
     When I GET the newly created school by id
     Then I should receive a return code of 404

### Error handling
Scenario: Attempt to read a non-existing school
    Given format "application/json"
    When I navigate to GET /schools/<'that doesn't exist' ID>
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing school
    Given format "application/json"
    When I navigate to DELETE /schools/<'that doesn't exist' ID>
    Then I should receive a return code of 404      

Scenario: Update a non-existing school
    Given format "application/json"
    When I attempt to update /schools/<'that doesn't exist' ID>
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given format "text/plain"
    When I navigate to GET /school/<'Apple Alternative Elementary School' ID>
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
	Given format "application/json"
	When I navigate to GET /schools/'using a wrong URI' ID>
     Then I should receive a return code of 404

Scenario: Attempt to read the base resource with no GUID
	Given format "application/json"
	When I navigate to GET /schools/<'with no GUID' ID>
	Then I should receive a return code of 405
