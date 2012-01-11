Feature: <US562> In order to manage teachers
   As a client application using SLI
   I want create, read, update, and delete functionality for a teacher
   So I can manage them.
 
Background: Logged in as a super-user and using the small data set
   Given I am logged in using "demo" "demo1234"
   Given I have access to all teachers
 
### Happy Path
 
Scenario: Create a new teacher in JSON format
   Given format "application/json"
      And the "name" is "Rafe" "Hairfire" "Esquith"
      And the "birthDate" is "1954-08-31"
      And the "sex" is "Male"
      And the "yearsOfPriorTeachingExperience" is "32"
      And the "staffUniqueStateId" is "<'Teacher Home State' ID>"
      And the "highlyQualifiedTeacher" status is "true"
      And the "highestLevelOfEducationCompleted" is "Masters"
   When I navigate to POST "/teachers/"
   Then I should receive a return code of 201
      And I should receive an ID for the newly created teacher
   When I navigate to GET "/teachers/<'newly created teacher' ID>"
   Then the "name" should be "Rafe" "Hairfire" "Esquith"
      And the "birthDate" should be "1954-08-31"
      And the "sex" should be "Male"
      And the "yearsOfPriorTeachingExperience" should be "32"
      And the "staffUniqueStateId" should be "<'Teacher Home State' ID>"
      And the "highlyQualifiedTeacher" status should be "true"
      And the "highestLevelOfEducationCompleted" should be "Masters"
              
Scenario: Read a teacher by ID in JSON format
   Given format "application/json"
   When I navigate to GET "/teachers/<'Macy' ID>"
   Then I should receive a return code of 200
      And the "name" should be "Macy" "" "Finch"
      And the "sex" should be "Female"
      And the "birthDate" should be "1956-08-14"
      And the "yearsOfPriorTeachingExperience" should be "22"
      And the "staffUniqueStateId" should be "<'Teacher Home State' ID>"
      And the "highlyQualifiedTeacher" status should be "false"
      And the "highestLevelOfEducationCompleted" should be "Bachelors"
      And I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<'Macy' ID>"
      And I should receive a link named "getSections" with URI "/teacher-section-associations/<'Macy' ID>/targets"
      And I should receive a link named "getTeacherSchoolAssociations" with URI "/teacher-school-associations/<'Macy' ID>"
      And I should receive a link named "getSchools" with URI "/teacher-school-associations/<'Macy' ID>/targets"
      And I should receive a link named "self" with URI "/teachers/<'Macy' ID>"


Scenario: Update an existing teacher in JSON format
   Given format "application/json"
   When I navigate to GET "/teachers/<'Belle' ID>"
   Then I should receive a return code of 200   
     And the "highlyQualifiedTeacher" status should be "false"
  When I set the "highlyQualifiedTeacher" status to "true"
   And I navigate to PUT "/teachers/<'Belle' ID>"
   Then I should receive a return code of 204
   When I navigate to GET "/teachers/<'Belle' ID>"
   Then I should receive a return code of 200   
     And the "highlyQualifiedTeacher" status should be "true"
      
Scenario: Delete an existing teacher in JSON format
   Given format "application/json"
   When I navigate to DELETE "/teachers/<'Christian' ID>"
   Then I should receive a return code of 204
   When I navigate to GET "/teachers/<'Christian' ID>"
   Then I should receive a return code of 404
 
 
### XML VERSION
@wip
Scenario: Create a new teacher in XML format
   Given format "application/xml"
      And the "name" is "Rafe" "Hairfire" "Esquith"
      And the "birthDate" is "1954-08-31"
      And the "sex" is "Male"
      And the "yearsOfPriorTeachingExperience" is "32"
      And the "staffUniqueStateId" is "567"
      And the "highlyQualifiedTeacher" status is "1"
   When I navigate to POST "/teachers/<'Rafe' ID>"
   Then I should receive a return code of 201
      And I should receive an ID for the newly created teacher
         
@wip     
Scenario: Read a teacher by ID in XML format
   Given format "application/xml"
   When I navigate to GET "/teachers/<'Esquith' ID>"
   Then I should receive a return code of 200
      And the "name" should be "Rafe" "Hairfire" "Esquith"
      And the "sex" should be "Male"
      And the "birthDate" should be "1954-08-31"
      And the "yearsOfPriorTeachingExperience" should be "32"
      And the "staffUniqueStateId" should be "567"
      And the "highlyQualifiedTeacher" status should be "1"
  
@wip 
Scenario: Update an existing teacher in XML format
   Given format "application/xml"
    When I navigate to GET "/teachers/<'Betty' ID>"
   Then I should receive a return code of 200
     And the "highlyQualifiedTeacher" status should be "false"
  When I set the "highlyQualifiedTeacher" status to "true"
   And I navigate to PUT "/teachers/<'Belle' ID>"
   Then I should receive a return code of 204
   When I navigate to GET" /teachers/<'Belle' ID>"
   Then I should receive a return code of 200
     And the "highlyQualifiedTeacher" status should be "true"
       
@wip 
Scenario: Delete an existing teacher in XML format
   Given format "application/xml"
   When I navigate to DELETE "/teachers/<'Esquith' ID>"
   Then I should receive a return code of 200
   When I navigate to GET "/teachers/<'Esquith' ID>"
   Then I should receive a return code of 200
 
 
###Links
@wip
Scenario: Teacher Resource links to teacher section association
   Given format "application/json"
   When I navigate to GET "/teachers/<'Illiana' ID>"
   Then I should receive a return code of 201
      And I should receive a link named "getTeacherSectionAssociations" with URI "/teacher-section-associations/<Ms. Jones' ID>"
      And I should receive a link named "getSections" with URI "/teacher-section-associations/<Ms. Jones' ID>/targets"
      And I should receive a link named "getTeacherSchoolAssociations" with URI "/teacher-school-associations/< Ms. Jones' ID>"
      And I should receive a link named "getSchools" with URI "/teacher-school-associations/<Ms. Jones' ID>/targets"
 
 
### Error Handling
Scenario: Attempt to read a non-existent teacher
   Given format "application/json"
   When I navigate to GET "/teachers/<Unknown>"
   Then I should receive a return code of 404

Scenario: Attempt to delete a non-existent teacher
   Given format "application/json"
   When I navigate to DELETE "/teachers/<Unknown>"
   Then I should receive a return code of 404

Scenario: Attempt to update a non-existent student
   Given format "application/json"
   When I navigate to DELETE "/teachers/<Unknown>"
   Then I should receive a return code of 404
   
Scenario: Attempt to read the base teacher resource with no GUID
  Given format "application/json"
  When I navigate to GET "/teachers/<NoGUID>"
  Then I should receive a return code of 405
  
Scenario: Fail when asking for an unsupported format "text/plain"
  Given format "text/plain"
  When I navigate to GET "/teachers/<'Simone' ID>"
  Then I should receive a return code of 406
  
Scenario: Fail if going to the wrong URI
    Given format "application/json"
    When I navigate to GET "/teachers/<'WrongURI' ID>"
    Then I should receive a return code of 404
 