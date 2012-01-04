@wip
Feature: As an SLI application, I want to be able to manage teacher-section associations
  This means I want to be able to perform CRUD on teacher-section-associations
  And also verify the correct links from that resource to the appropriate teacher and section.

This is the data I am assuming for these tests:
Teacher: Ms Jones, Ms. Smith
Section: Chem I, Physics II, Biology III, Algebra II

Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234"
  Given that I have admin access

Scenario: Create a teacher-section-association
Given format "application/json"
  And Teacher is <Ms. Smith's ID>
  And Section is <Algebra II>
  And the BeginDate is "2011-08-15"
  And the EndDate is "2011-12-15"
  And the ClassroomPosition is "teacher of record"
When I navigate to POST "/teacher-section-associations"
Then I should receive a return code of 201
  And I should receive a ID for the newly created teacher-section-association
  And the EndDate should be "2011-08-15"
  And the BeginDate should be "2011-12-15"
  And the ClassroomPosition should be "teacher of record"

Scenario: Read a teacher-section-association
Given format "application/json"
When I navigate to Teacher Section Associations for Teacher "Ms. Jones" and Section "Algebra II"
Then I should receive a return code of 200
  And I should receive a link named "getTeacher" with URI /teachers/<Ms. Jones' ID>
  And I should receive a link named "getSection" with URI /sections/<Algebra II ID>
  And I should receive a link named "self" with URI /teacher-section-associations/<Association between Ms. Jones and Algebra II ID>
  And the BeginDate should be "2011-09-01"
  And the EndDate should be "2011-12-16"
  And HighlyQualifiedTeacher should be true
  And ClassroomPosition should be "teacher of record"


Scenario: Reading a teacher-section-association for a teacher
Given format "application/json"
When I navigate to Teacher Section Associations for the Teacher "Ms. Jones"
Then I should receive a return code of 200
  #the line below seems incomplete, but is as intended.
  And I should receive a collection of 4 teacher-section-association links that resolve to
  And everything in the collection should contain a link named "getTeacher" with URI /teachers/<Ms. Jones' ID>
  And the collection should contain a link named "getSection" with URI /sections/<Chem I ID>
  And the collection should contain a link named "getSection" with URI /sections/<Physics II ID>
  And the collection should contain a link named "getSection" with URI /sections/<Biology III ID>

Scenario: Reading a teacher-section-association for a section
Given format "application/json"
When I navigate to Teacher Section Association for the Section "Chem I"
Then I should receive a return code of 200
  And I should receive a collection of 2 teacher-section-association links that resolve to
  And the collection should contain a link named "getTeacher" with URI /teachers/<Ms. Jones' ID>
  And the collection should contain a link named "getTeacher" with URI /teachers/<Ms. Smith's ID>
  And everything in the collection should contain a link named "getSection" with URI /sections/<Chem I ID>


Scenario: Update a teacher-section-association
Given format "application/json"
  And I navigate to Teacher Section Associations for Teacher "Ms. Jones" and Section "Algebra II"
  And ClassroomPosition should be "teacher of record"
When I set the ClassroomPosition to assistant teacher
  And I navigate to PUT /teacher-section-associations/<Association between Ms. Jones and Algebra II ID>
Then I should receive a return code of 200
  And I navigate to Teacher Section Associations for Teacher "Ms. Jones" and Section "Algebra II"
  And the ClassroomPosition should be "assistant teacher"


Scenario: Delete a teacher-section-association
Given format "application/json"
When I navigate to DELETE Teacher Section Associations for Teacher "Ms. Smith" and Section "Physics II"
Then I should receive a return code of 204
  And I navigate to Teacher Section Associations for Teacher "Ms. Smith" and Section "Physics II"
  And I should receive a return code of 404
