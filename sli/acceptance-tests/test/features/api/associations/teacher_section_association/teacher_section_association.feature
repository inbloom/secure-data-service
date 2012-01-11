Feature: As an SLI application, I want to be able to manage teacher-section associations
  This means I want to be able to perform CRUD on teacher-section-associations
  Also verify the correct links from that resource to the appropriate teacher and section.

This is the data I am assuming for these tests:
Teacher: Ms Jones, Mr. Smith
Section: Chem I, Physics II, Biology III, Algebra II


Background: Logged in as a super-user 
  Given I am logged in using "demo" "demo1234"

Scenario: Create a teacher-section-association
Given format "application/json"
  And "teacherId" is "<'Ms. Jones' ID>"
  And "sectionId" is "<'Algebra II' ID>"
  And "beginDate" is "2011-8-15"
  And "endDate" is "2011-12-15"
  And "classroomPosition" is "TEACHER_OF_RECORD"
When I navigate to POST "/teacher-section-associations"
Then I should receive a return code of 201
  And I should receive a ID for the newly created teacher-section-association
When I navigate to GET "/teacher-section-associations/<'newly created teacher-section-association' ID>"
Then I should receive a return code of 200
Then the "endDate" should be "2011-8-15"
    And the "beginDate" should be "2011-12-15"
    And the "classroomPosition" should be "TEACHER_OF_RECORD"

Scenario: Read a teacher-section-association
Given format "application/json"
When I navigate to GET "/teacher-section-associations/<'Teacher "Ms. Smith" and Section "Algebra II"' ID>"
Then I should receive a return code of 200
  And I should receive 1 teacher-section-association
  And I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Smith' ID>"
  And I should receive a link named "getSection" with URI "/sections/<'Algebra II' ID>"
  And I should receive a link named "self" with URI "/teacher-section-association/<'self' ID>"
  And the "beginDate" should be "2011-9-1"
  And the "endDate" should be "2011-12-16"
  And "highlyQualifiedTeacher" should be "true"
  And "classroomPosition" should be "TEACHER_OF_RECORD"

Scenario: Reading a teacher-section-association for a teacher
Given format "application/json"
When I navigate to GET "/teacher-section-associations/<'Ms. Nancy' ID>"
Then I should receive a return code of 200
  And I should receive a collection of 4 teacher-section-associations 
  And after resolution, I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Nancy’ ID>"
  And after resolution, I should receive a link named "getSection" with URI "/sections/<'Chem I' ID>"
  And after resolution, I should receive a link named "getSection" with URI "/sections/<'Physics II' ID>"
  And after resolution, I should receive a link named "getSection" with URI "/sections/<'Biology III' ID>"

Scenario: Reading a teacher-section-association for a section
Given format "application/json"
When I navigate to GET "/teacher-section-associations/<'Chem I' ID>"
Then I should receive a return code of 200
  And I should receive a collection of 3 teacher-section-associations
  And after resolution, I should receive a link named "getSection" with URI "/sections/<'Chem I' ID>"
  And after resolution, I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Nancy' ID>"
  And after resolution, I should receive a link named "getTeacher" with URI "/teachers/<'Mr. Thomas' ID>"

Scenario: Update a teacher-section-association
Given format "application/json"
  And I navigate to GET "/teacher-section-associations/<'Teacher "Ms. Jenny" and Section "Algebra II"' ID>"
  And "classroomPosition" is "TEACHER_OF_RECORD"
When I set the "classroomPosition" to "ASSISTANT_TEACHER"
  And I navigate to PUT "/teacher-section-associations/<'Teacher "Ms. Jenny" and Section "Algebra II"' ID>
Then I should receive a return code of 200
  And I navigate to GET /teacher-section-associations/<'Teacher "Ms. Jenny" and Section "Algebra II"' ID>
  And the "classroomPosition" should be "ASSISTANT_TEACHER"


Scenario: Delete a teacher-section-association
Given format "application/json"
When I navigate to DELETE "/teacher-section-associations/<'a currently valid and independent association'' ID>"
Then I should receive a return code of 200
  And I navigate to GET "/teacher-section-associations/<'a currently valid and independent association' ID>"
  And I should receive a return code of 404
