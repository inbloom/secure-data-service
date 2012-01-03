
Feature: As an SLI application, I want to be able to manage teacher-school associations
This means I want to be able to perform CRUD on teacher-school-associations. 
Also so verify the correct links from that resource to the appropriate teacher and schools.
  
    This is the data I am assuming for these tests:
    Teacher: Ms Jones, Mr. Smith
    School: Chemistry Elementary, Physics Middle, Biology High, Algebra Alternative

Background: Nothing yet
    Given I am logged in using "demo" "demo1234"

Scenario: Create a teacher-school-association
   Given format "application/vnd.slc+json"
     And the Teacher is <Mr. Smith's ID>
     And the School is <Algebra Alternative's ID>
     And the Program Assignment Type is <Regular_Education>
     And an Instructional Grade Level is <9th>
     And an Instructional Grade Level is <10th>
    When I navigate to POST "/teacher-school-associations"
    Then I should receive a return code of 201
     And I should receive an ID for the newly created teacher-school-association
    When I navigate to /teacher-school-associations/<newly created teacher-school-association ID>
    Then the Teacher should be <Mr. Smith's ID>
     And the School should be <Algebra Alternative's ID>

Scenario: Read a teacher-school-association for a teacher
   Given format "application/vnd.slc+json"
    When I navigate to Teacher School Associations for Teacher <Ms. Jones' ID>
    Then I should receive a return code of 200
     And I should receive a collection of 3 teacher-school-associations that resolve to
     And I should receive a link named "getTeacher" with URI /teachers/<Ms. Jones' ID>
     And I should receive a link named "getSchool" with URI /schools/<Algebra Alternative's ID>
     And I should receive a link named "getSchool" with URI /schools/<Biology High's ID>
     And I should receive a link named "getSchool" with URI /schools/<Chemistry Elementary's ID>

Scenario: Reading a teacher-school-association
   Given format "application/vnd.slc+json"
    When I navigate to Teacher School Associations for Teacher <Mr. Smith's ID> and School <Biology High's ID>
    Then I should receive a return code of 200
     And I should receive a teacher-school-associations
     And a subject taught should be "Life_and_Physical_Sciences"
     And the program assignment type should be "Regular_Education"
     And a grade level should be "9th"
     And I should receive a link named "getTeacher" with URI /teachers/<Mr. Smith's ID>
     And I should receive a link named "getSchool" with URI /schools/<Biology High's ID>

Scenario: Reading a teacher-school-association for a school
   Given format "application/vnd.slc+json"
    When I navigate to Teacher School Associations for School <Biology High's ID>
    Then I should receive a return code of 200
     And I should receive a collection of 2 teacher-school-associations that resolve to
     And I should receive a link named "getSchool" with URI /schools/<Biology High's ID>
     And I should receive a link named "getTeacher" with URI /teachers/<Ms. Jones' ID>
     And I should receive a link named "getTeacher" with URI /teachers/<Mr. Smith's ID>

Scenario: Update a teacher-school-association
   Given format "application/vnd.slc+json"
     And I navigate to Teacher School Associations for Teacher <Ms. Jones' ID> and School <Algebra Alternative's ID>
     And the program assignment type is "Regular_Education"
    When I set the program assignment type to "Special_Education"
     And I navigate to PUT /teacher-school-associations/<the previous association Id>
    Then I should receive a return code of 204
     And I navigate to GET /teacher-school-associations/<the previous association Id>
     And the program assignment type should be "Special_Education"

Scenario: Delete a teacher-school-association
   Given format "application/vnd.slc+json"
    When I navigate to DELETE Teacher School Associations for Teacher <Ms. Jones' ID> and School <Algebra Alternative's ID>
    Then I should receive a return code of 204
     And I navigate to PUT /teacher-school-associations/<the previous association Id>
     And I should receive a return code of 404
