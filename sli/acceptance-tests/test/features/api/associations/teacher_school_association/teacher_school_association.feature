
Feature: As an SLI application, I want to be able to manage teacher-school associations
This means I want to be able to perform CRUD on teacher-school-associations. 
Also so verify the correct links from that resource to the appropriate teacher and schools.
  
    This is the data I am assuming for these tests:
    Teacher: Ms Jones, Mr. Smith
    School: Chemistry Elementary, Physics Middle, Biology High, Algebra Alternative

Background: Nothing yet
    Given I am logged in using "demo" "demo1234"
    And I have access to all teachers and schools

Scenario: Create a teacher-school-association
   Given format "application/json"
     And  "teacherId" is "<'Mr. Smith' ID>"
     And  "schoolId" is "<'Algebra Alternative' ID>"
     And  "programAssignment" is "Regular Education"
     And  "instructionalGradeLevels" is "Tenth grade"
    When I navigate to POST "/teacher-school-associations"
    Then I should receive a return code of 201
     And I should receive an ID for the newly created teacher-school-association
    When I navigate to GET "/teacher-school-associations/<'newly created teacher-school-association' ID>"
    Then "teacherId" should be "<'Mr. Smith' ID>"
     And "schoolId" should be "<'Algebra Alternative' ID>"
     And "programAssignment" should be "Regular Education"
     And "instructionalGradeLevels" should be "Tenth grade"
     
Scenario: Reading a teacher-school-association
   Given format "application/json"
    When I navigate to GET "/teacher-school-associations/<'Teacher Mr. Smith and School Biology High' ID>"
    Then I should receive a return code of 200
     And "teacherId" should be "<'Mr. Smith' ID>"
     And "schoolId" should be "<'Biology High' ID>"
     And I should receive a link named "self" with URI "/teacher-school-associations/<'Teacher Mr. Smith and School Biology High' ID>"
     And I should receive a link named "getTeacher" with URI "/teachers/<'Mr. Smith' ID>"
     And I should receive a link named "getSchool" with URI "/schools/<'Biology High' ID>"
     
Scenario: Read a teacher-school-association for a teacher
   Given format "application/json"
    When I navigate to GET "/teacher-school-associations/<'Ms. Jones' ID>"
    Then I should receive a return code of 200
     And I should receive a collection of 3 teacher-school-association links
     And after resolving each link, I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Jones' ID>"
     And after resolution, I should receive a link named "getSchool" with URI "/schools/<'Algebra Alternative' ID>"
     And after resolution, I should receive a link named "getSchool" with URI "/schools/<'Biology High' ID>"
     And after resolution, I should receive a link named "getSchool" with URI "/schools/<'Chemistry Elementary' ID>"

Scenario: Reading a teacher-school-association for a school
   Given format "application/json"
    When I navigate to GET "/teacher-school-associations/<'Biology High' ID>"
    Then I should receive a return code of 200
     And I should receive a collection of 3 teacher-school-association links
     And after resolving each link, I should receive a link named "getSchool" with URI "/schools/<'Biology High' ID>"
     And after resolution, I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Jones' ID>"
     And after resolution, I should receive a link named "getTeacher" with URI "/teachers/<'Mr. Smith' ID>"
     And after resolution, I should receive a link named "getTeacher" with URI "/teachers/<'Mrs. Solis' ID>"

Scenario: Update a teacher-school-association
   Given format "application/json"
    When I navigate to GET "/teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>"
    Then "programAssignment" should be "Regular Education"
    When I set the "programAssignment" to "Special Education"
     And I navigate to PUT "/teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>"
    Then I should receive a return code of 204
     And I navigate to GET "/teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>"
     And "programAssignment" should be "Special Education"


Scenario: Delete a teacher-school-association
   Given format "application/json"
    When I navigate to DELETE "/teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>"
    Then I should receive a return code of 204
     And I navigate to GET "/teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>"
     And I should receive a return code of 404
     
     
 ### Error handling
Scenario: Attempt to read a non-existent resource
	Given format "application/json"
	When I navigate to GET "/teacher-school-associations/<'Invalid ID'>"
	Then I should receive a return code of 404
	
Scenario: Attempt to delete a non-existent resource
	Given format "application/json"
	When I navigate to DELETE "/teacher-school-associations/<'Invalid ID'>"
	Then I should receive a return code of 404
		
Scenario: Update a non-existing student-school-association
    Given format "application/json"
    When I attempt to update a non-existing association "/teacher-school-associations/<'Invalid ID'>"
    Then I should receive a return code of 404
