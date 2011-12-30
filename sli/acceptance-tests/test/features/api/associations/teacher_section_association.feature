@wip
Feature: As an SLI application, I want to be able to manage teacher-section associations
  This means I want to be able to perform CRUD on teacher-section-associations
  And also verify the correct links from that resource to the appropriate teacher and section.

This is the data I am assuming for these tests:
Teacher: Ms Jones, Mr. Smith
Section: Chem I, Physics II, Biology III, Algebra II


Background: Nothing yet

Scenario: Create a teacher-section-association
Given format "application/vnd.slc+json"
  And Teacher is <Ms. Jones' ID>
  And Section is <Algebra II>
  And the BeginDate is "8/15/2011"
  And the EndDate is "12/15/2011"
  And the ClassroomPosition is "teacher of record"
When I navigate to POST "/teacher-section-associations"
Then I should receive a return code of 201
  And I should receive a ID for the newly created teacher-section-association
  And the EndDate should be "8/15/2011"
  And the BeginDate should be "12/15/2011"
  And the ClassroomPosition should be "teacher of record"

Scenario: Read a teacher-section-association
Given format "application/vnd.slc+json"
When I navigate to Teacher Section Associations for Teacher "Ms. Jones" and Section "Algebra II"
Then I should receive a return code of 200
  And I should receive 1 teacher-section-associations
  And I should receive a link named “getTeachers” with URI /teachers/<Ms. Jones’ ID>
  And I should receive a link named “getSections” with URI /sections/<Algebra II ID>
  And I should receive a link named "self" with URI /teacher-section-association/<'self' ID>
  And the BeginDate should be "9/1/2011"
  And the EndDate should be "12/16/2011"
  And HighlyQualifiedTeacher should be true
  And ClassroomPosition should be teacher of record


Scenario: Reading a teacher-section-association for a teacher
Given format "application/vnd.slc+json"
When I navigate to Teacher Section Associations for the Teacher “Ms. Jones”
Then I should receive a return code of 200
  And classroom position is teacher of record
  #the line below seems incomplete, but is as intended.
  And I should receive a collection of 4 teacher-section-association links that resolve to
  And I should receive a link named “getTeachers” with URI /teachers/<Ms. Jones’ ID>
  And I should receive a link named “getSections” with URI /sections/<Chem I ID>
  And I should receive a link named “getSections” with URI /sections/<Physics II ID>
  And I should receive a link named “getSections” with URI /sections/<Biology III ID>

Scenario: Reading a teacher-section-association for a section
Given format "application/vnd.slc+json"
When I navigate to Teacher Section Association for the Section “Chem I”
Then I should receive a return code of 200
And classroom position is teacher of record
  And I should receive a collection of 3 teacher-section-association links that resolve to
  And I should receive a link named “getTeacher” with URI /teachers/<Ms. Jones’ ID>
  And I should receive a link named “getTeacher” with URI /teachers/<Mr. Smith’s ID>
  And I should receive a link named “getSection” with URI /sections/<Chem I ID>


Scenario: Update a teacher-section-association
Given format "application/vnd.slc+json"
  And  I navigate to Teacher Section Associations for Teacher "Ms. Jones" and Section "Algebra II"
  And classroom position is teacher of record
When  I set the classroom position to assistant teacher
  And I navigate to PUT /teacher-section-associations/<the previous association Id>
Then I should receive a return code of 200
  And I navigate to GET /teacher-section-associations/<the previous association Id>
  And the classroom position should be assistant teacher


Scenario: Delete a teacher-section-association
Given format "application/vnd.slc+json"
When  I navigate to DELETE /teacher-section-associations/<the previous association Id>
Then I should receive a return code of 200
  And I navigate to GET /teacher-section-associations/<the previous association Id>
  And I should receive a return code of 404