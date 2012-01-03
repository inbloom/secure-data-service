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
      And the name is "Rafe" "Hairfire" "Esquith"
      And the birth date is "1954-08-31"
      And he is "Male"
      And his "Years of Prior Teaching Experience" is "32"
      And his "Teacher Unique State ID" is "567"
      And his "Highly Qualified Teacher" status is "true"
      And his "Level of Education" is "Masters"
   When I navigate to POST teacher "Rafe"
   Then I should receive a return code of 201
      And I should receive an ID for the newly created teacher
              
Scenario: Read a teacher by ID in JSON format
   Given format "application/json"
   When I navigate to GET teacher "Macey"
   Then I should receive a return code of 200
      And I should see that the name of the teacher is "Macey" "" "Finch"
      And I should see that he is "Female"
      And I should see that he was born on "1956-08-14"
      And I should see that his "Years of Prior Teaching Experience" is "22"
      And I should see that his "Teacher Unique State ID" is "738543275"
      And I should see that his "Highly Qualified Teacher" status is "false"
      And I should see that his "Level of Education" is "Bachelors"
      #And I should receive a link named "getTeacherSectionAssociations" with URI /teacher-section-associations/<'Teacher Macy' ID>
      #And I should receive a link named "getSections" with URI /teacher-section-associations/<'Teacher Macy' ID>/targets
      #And I should receive a link named "getTeacherSchoolAssociations" with URI /teacher-school-associations/<'Teacher Macy' ID>
      #And I should receive a link named "getSchools" with URI /teacher-school-associations/<'Teacher Macy' ID>/targets
      #And I should receive a link named "self" with URI /teachers/<'Teacher Macy' ID>


Scenario: Update an existing teacher in JSON format
   Given format "application/json"
     And her "Highly Qualified Teacher" status is "true"
   When I navigate to GET teacher "Belle"
   Then I should receive a return code of 200   
     And I should see that her "Highly Qualified Teacher" status is "false"
   When I navigate to PUT teacher "Belle"
   Then I should receive a return code of 204
   When I navigate to GET teacher "Belle"
   Then I should receive a return code of 200   
     And I should see that her "Highly Qualified Teacher" status is "true"
      
Scenario: Delete an existing teacher in JSON format
   Given format "application/json"
   When I navigate to DELETE teacher "Christian"
   Then I should receive a return code of 204
   When I navigate to GET teacher "Christian"
   Then I should receive a return code of 404
 
 
### XML VERSION
@wip
Scenario: Create a new teacher in XML format
   Given format "application/xml"
      And the name is "Rafe" "Hairfire" "Esquith"
      And the birth date is "1954-08-31"
      And he is "Male"
      And his "Years of Prior Teaching Experience" is "32"
      And his "Teacher Unique State ID" is "567"
      And his "Highly Qualified Teacher" status is "1"
   When I navigate to POST teacher "Rafe"
   Then I should receive a return code of 201
      And I should receive an ID for the newly created teacher
         
@wip     
Scenario: Read a teacher by ID in XML format
   Given format "application/xml"
   When I navigate to GET teacher Esquith
   Then I should receive a return code of 2XX
      And I should see that the name of the teacher is "Rafe" "Hairfire" "Esquith"
      And I should see that he is "Male"
      And I should see that he was born on "1954-08-31"
      And I should see that his "Years of Prior Teaching Experience" is "32"
      And I should see that his "Teacher Unique State ID" is "567"
      And I should see that his "Highly Qualified Teacher" status is "1"
  
@wip 
Scenario: Update an existing teacher in XML format
   Given format "application/xml"
      And I see that his "Highly Qualified Teacher" status is "0"
   When I navigate to PUT teacher Esquith
   Then I should receive a return code of 2XX
   When I navigate to GET teacher Esquith
   Then I should see that his "Highly Qualified Teacher" status is "0"
       
@wip 
Scenario: Delete anexisting teacher in XML format
   Given format "application/xml"
   When I navigate to DELETE teacher Esquith
   Then I should receive a return code of 2XX
   When I navigate to GET teacher Esquith
   Then I should receive a return code of 4XX
 
 
###Links
@wip
Scenario: Teacher Resource links to teacher section association
   Given format "application/json"
   When I navigate to GET teacher "Illiana"
   Then I should receive a return code of 201
      And I should receive a link named "getTeacherSectionAssociations" with URI /teacher-section-associations/<Ms. Jones' ID>
      And I should receive a link named "getSections" with URI /teacher-section-associations/<Ms. Jones' ID>/targets
      And I should receive a link named "getTeacherSchoolAssociations" with URI /teacher-school-associations/< Ms. Jones' ID>
      And I should receive a link named "getSchools" with URI /teacher-school-associations/<Ms. Jones' ID>/targets
 
 
### Error Handling
Scenario: Attempt to read a non-existent teacher
   Given format "application/json"
   When I navigate to GET teacher "Unknown"
   Then I should receive a return code of 404

Scenario: Attempt to delete a non-existent teacher
   Given format "application/json"
   When I navigate to DELETE teacher "Unknown"
   Then I should receive a return code of 404

Scenario: Attempt to update a non-existent student
   Given format "application/json"
   When I navigate to DELETE teacher "Unknown"
   Then I should receive a return code of 404
   
Scenario: Attempt to read the base teacher resource with no GUID
  Given format "application/json"
  When I navigate to GET teacher "NoGUID"
  Then I should receive a return code of 405
  
Scenario: Fail when asking for an unsupported format "text/plain"
  Given format "text/plain"
  When I navigate to GET teacher "Simone"
  Then I should receive a return code of 406
  
Scenario: Fail if going to the wrong URI
    Given format "application/json"
    When I navigate to GET teacher "WrongURI"
    Then I should receive a return code of 404
 