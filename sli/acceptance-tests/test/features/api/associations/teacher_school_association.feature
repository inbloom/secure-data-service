@wip
Feature: As an SLI application, I want to be able to manage teacher-school associations
This means I want to be able to perform CRUD on teacher-school-associations. 
Also so verify the correct links from that resource to the appropriate teacher and schools.
  
    This is the data I am assuming for these tests:
    Teacher: Ms Jones, Mr. Smith
    School: Chemistry Elementary, Physics Middle, Biology High, Algebra Alternative

  Background: Nothing yet

  Scenario: Create a teacher-school-association
    Given format "application/vnd.slc+json"
      And the Teacher is <Ms. Jones' ID>
      And the School is <Algebra Alternative>
    When I navigate to POST "/teacher-school-associations"
    Then I should receive a return code of 201
      And I should receive an ID for the newly created teacher-school-association

 Scenario: Read a teacher-school-association
   Given format "application/vnd.slc+json"
   When I navigate to Teacher Section Associations for Teacher "Ms. Jones"
   Then I should receive a return code of 2xx
    And the grade levels should be "9"
    And the subjects taught should be "Algebra"
    And the school should be "Algebra Alterative School"
    And the program assignment type should be "Alternative Education"
    And I should receive 2 teacher-section-associations
    And I should receive a link named "getTeacher" with URI /teachers/<Ms. Jones' ID>
    And I should receive a link named "getSchool" with URI /schools/<Algebra Alternative School's ID>

  Scenario: Reading a teacher-school-association for a teacher
   Given format "application/vnd.slc+json"
   When I navigate to Teacher School Associations for Teacher "Mr. Smith"
   Then I should receive a return code of 2XX
      And the subjects taught should be "Biology"
      And the school type should be "High School"
      And the school should be "Biology High School"
      And I should receive 2 teacher-school-associations
      And I should receive a link named "getTeacher" with URI /teachers/<Mr. Smith's ID>
      And I should receive a link named "getSchool" with URI /schools/<Biology High School>

Scenario: Reading a teacher-school-association for a school
   Given format "application/vnd.slc+json"
   When I navigate to Teacher School Association for the School "Biology High School"
   Then I should receive a return code of 2XX
      And the school type should be "High School"
      And I should receive 3 teacher-school-associations
      And I should receive a link named "getTeacher" with URI /schools/<Ms. Jones's ID>
      And I should receive a link named "getTeacher" with URI/schools/<Mr. Smith's ID>
      And I should receive a link named "getSchool" with URI/schools/<Biology High School's ID>
      
  Scenario: Update a teacher-section-association
   Given format "application/vnd.slc+json"
     And I navigate to Teacher Section Associations for Teacher "Ms. Jones" and School "Algebra Alternative"
     And the program assignment type is "Alternative Education"
   When I set the program assignment type to "Special Education"
     And I navigate to PUT /teacher-section-associations/<the previous association Id>
   Then I should get a return code of 2xx
     And I navigate to PUT /teacher-section-associations/<the previous association Id>
     And the program assignment type should be "Special Education"

  Scenario: Delete a teacher-section-association
   Given format "application/vnd.slc+json"
   When I navigate to DELETE /teacher-section-associations/<the previous association Id>
   Then I should get a return code of 2xx
    And I navigate to PUT /teacher-section-associations/<the previous association Id>
    And I should receive a return code of 404

