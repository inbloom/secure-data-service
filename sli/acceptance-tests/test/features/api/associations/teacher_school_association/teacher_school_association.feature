
Feature: As an SLI application, I want to be able to manage teacher-school associations
This means I want to be able to perform CRUD on teacher-school-associations. 
Also so verify the correct links from that resource to the appropriate teacher and schools.
  
    This is the data I am assuming for these tests:
    Teacher: Ms Jones, Mr. Smith
    School: Chemistry Elementary, Physics Middle, Biology High, Algebra Alternative

Background: Nothing yet
    Given I am logged in using "demo" "demo1234"

Scenario: Create a teacher-school-association
   Given format "application/json"
     And the "teacherID" is <'Mr. Smith' ID>
     And the "schoolID" is <'Algebra Alternative' ID>
     And the "programAssignmentType" is "REGULAR_EDUCATION"
     And the "instructionalGradeLevel is "10th"
    When I navigate to POST "/teacher-school-associations"
    Then I should receive a return code of 201
     And I should receive an ID for the newly created teacher-school-association
    When I navigate to /teacher-school-associations/<'newly created teacher-school-association' ID>
    Then the "teacherID" should be <'Mr. Smith' ID>
     And the "schoolID" should be <'Algebra Alternative' ID>
     And the "programAssignmentType"is "REGULAR_EDUCATION"
     And the "instructionalGradeLevel" is "10th"

Scenario: Read a teacher-school-association for a teacher
   Given format "application/json"
    When I navigate to GET /teacher-school-associations/<'Ms. Jones' ID>
    Then I should receive a return code of 200
     And I should receive a collection of 3 teacher-school-associations that resolve to
     And I should receive a link named "getTeacher" with URI /teachers/<'Ms. Jones' ID>
     And I should receive a link named "getSchool" with URI /schools/<'Algebra Alternative' ID>
     And I should receive a link named "getSchool" with URI /schools/<'Biology High' ID>
     And I should receive a link named "getSchool" with URI /schools/<'Chemistry Elementary' ID>

Scenario: Reading a teacher-school-association
   Given format "application/json"
    When I navigate to GET /teacher-school-associations/<'Teacher Mr. Smith and School Biology High' ID>
    Then I should receive a return code of 200
     And I should receive a teacher-school-associations
     And I should receive a link named "self" with URI /teacher-school-associations/<'Teacher Mr. Smith and School Biology High' ID>
     And I should receive a link named "getTeacher" with URI /teachers/<'Mr. Smith' ID>
     And I should receive a link named "getSchool" with URI /schools/<'Biology High' ID>

Scenario: Reading a teacher-school-association for a school
   Given format "application/json"
    When I navigate to GET /teacher-school-associations/<'Biology High' ID>
    Then I should receive a return code of 200
     And I should receive a collection of 2 teacher-school-associations that resolve to
     And I should receive a link named "getSchool" with URI /schools/<'Biology High' ID>
     And I should receive a link named "getTeacher" with URI /teachers/<'Ms. Jones' ID>
     And I should receive a link named "getTeacher" with URI /teachers/<'Mr. Smith ID>

Scenario: Update a teacher-school-association
   Given format "application/json"
     And I navigate to GET /teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>
     And the "programAssignmentType" is "REGULAR_EDUCATION"
    When I set the "programAssignmentType to "SPECIAL_EDUCATION"
     And I navigate to PUT /teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>
    Then I should receive a return code of 204
     And I navigate to GET /teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>
     And the "programAssignmentType" should be "SPECIAL_EDUCATION"

Scenario: Delete a teacher-school-association
   Given format "application/json"
    When I navigate to DELETE /teacher-school-associations/<'Teacher Ms. Jones and School Algebra Alternative' ID>Teacher School Associations for Teacher <Ms. Jones' ID> and School <Algebra Alternative's ID>
    Then I should receive a return code of 204
     And I navigate to PUT /teacher-school-associations/<the previous association Id>
     And I should receive a return code of 404
