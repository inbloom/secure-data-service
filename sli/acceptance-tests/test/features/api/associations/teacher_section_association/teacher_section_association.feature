Feature: As an SLI application, I want to be able to manage teacher-section associations
  This means I want to be able to perform CRUD on teacher-section-associations
  Also verify the correct links from that resource to the appropriate teacher and section.

This is the data I am assuming for these tests:
Teacher: Ms. Jones, Ms. Smith
Section: Chem I, Physics II, Biology III, Algebra II


Background: Logged in as a super-user 
  Given I am logged in using "demo" "demo1234"
  And I have access to all teachers and sections

Scenario: Create a teacher-section-association
Given format "application/json"
  And "teacherId" is "<'Ms. Smith' ID>"
  And "sectionId" is "<'Algebra II' ID>"
  And "beginDate" is "2011-08-15"
  And "endDate" is "2011-12-15"
  And "classroomPosition" is "Teacher of Record"
When I navigate to POST "/teacher-section-associations"
Then I should receive a return code of 201
  And I should receive an ID for the newly created teacher-section-association
When I navigate to GET "/teacher-section-associations/<'newly created teacher-section-association' ID>"
Then I should receive a return code of 200
Then "beginDate" should be "2011-08-15"
    And "endDate" should be "2011-12-15"
    And "classroomPosition" should be "Teacher of Record"

Scenario: Read a teacher-section-association
Given format "application/json"
When I navigate to GET "/teacher-section-associations/<'Teacher Ms. Jones and Section Algebra II' ID>"
Then I should receive a return code of 200
  And I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Jones' ID>"
  And I should receive a link named "getSection" with URI "/sections/<'Algebra II' ID>"
  And I should receive a link named "self" with URI "/teacher-section-associations/<'Teacher Ms. Jones and Section Algebra II' ID>"
  And "beginDate" should be "2011-09-01"
  And "endDate" should be "2011-12-16"
  And "highlyQualifiedTeacher" should be "true"
  And "classroomPosition" should be "Teacher of Record"

Scenario: Reading a teacher-section-association for a teacher
Given format "application/json"
When I navigate to GET "/teacher-section-associations/<'Ms. Jones' ID>"
Then I should receive a return code of 200
  And I should receive a collection of 4 teacher-section-association links
  And after resolving each link, I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Jones' ID>"
  And after resolution, I should receive a link named "getSection" with URI "/sections/<'Chem I' ID>"
  And after resolution, I should receive a link named "getSection" with URI "/sections/<'Physics II' ID>"
  And after resolution, I should receive a link named "getSection" with URI "/sections/<'Biology III' ID>"

Scenario: Reading a teacher-section-association for a section
Given format "application/json"
When I navigate to GET "/teacher-section-associations/<'Chem I' ID>"
Then I should receive a return code of 200
  And I should receive a collection of 2 teacher-section-association links
  And after resolution, I should receive a link named "getSection" with URI "/sections/<'Chem I' ID>"
  And after resolution, I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Jones' ID>"
  And after resolution, I should receive a link named "getTeacher" with URI "/teachers/<'Ms. Smith' ID>"

Scenario: Update a teacher-section-association
Given format "application/json"
  And I navigate to GET "/teacher-section-associations/<'Teacher Ms. Smith and Section Chem I' ID>"
  And "classroomPosition" is "Teacher of Record"
When I set "classroomPosition" to "Assistant Teacher"
  And I navigate to PUT "/teacher-section-associations/<'Teacher Ms. Smith and Section Chem I' ID>"
Then I should receive a return code of 204
  And I navigate to GET "/teacher-section-associations/<'Teacher Ms. Smith and Section Chem I' ID>"
  And "classroomPosition" should be "Assistant Teacher"


Scenario: Delete a teacher-section-association
Given format "application/json"
When I navigate to DELETE "/teacher-section-associations/<'Teacher Ms. Smith and Section Physics II' ID>"
Then I should receive a return code of 204
  And I navigate to GET "/teacher-section-associations/<'Teacher Ms. Smith and Section Physics II' ID>"
  And I should receive a return code of 404
