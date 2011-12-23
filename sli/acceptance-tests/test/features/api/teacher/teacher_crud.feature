Feature: <US562>
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
      And his "Highly Qualified Teacher" status is "1"
      And his "Level of Education" is "Master's"
   When I navigate to POST teacher "Rafe"
   Then I should receive a return code of 201
      And I should receive an ID for the newly created teacher
              
Scenario: Read a teacher by ID in JSON format
   Given format "application/json"
   When I navigate to GET teacher "Macey"
   Then I should receive a return code of 200
      And I should see that the name of the teacher is "Macey" "" "Finch"
      And I should see that he is "Female"
      And I should see that he was born on "1960-02-01"
      And I should see that his "Years of Prior Teaching Experience" is "32"
      And I should see that his "Teacher Unique State ID" is "738543275"
      And I should see that his "Highly Qualified Teacher" status is "0"
      And I should see that his "Level of Education" is "Bachelor's"

Scenario: Update an existing teacher in JSON format
   Given format "application/json"
     And his "Highly Qualified Teacher" status is "0"
   When I navigate to GET teacher "Belle"
   Then I should receive a return code of 200   
     And I should see that his "Highly Qualified Teacher" status is "1"
   When I navigate to PUT teacher "Belle"
   Then I should receive a return code of 204
   When I navigate to GET teacher "Belle"
   Then I should see that his "Highly Qualified Teacher" status is "0"
      
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
   When I navigate to GET teacher "Macey"
   Then I should receive a return code of 200
      And I should see that the name of the teacher is "Macey" "" "Finch"
      And I should see that he is "Female"
      And I should see that he was born on "1960-02-01"
      And I should see that his "Years of Prior Teaching Experience" is "32"
      And I should see that his "Teacher Unique State ID" is "738543275"
      And I should see that his "Highly Qualified Teacher" status is "0"
      And I should see that his "Level of Education" is "Bachelor's"
  
@wip 
Scenario: Update an existing teacher in XML format
   Given format "application/json"
     And his "Highly Qualified Teacher" status is "0"
   When I navigate to GET teacher "Belle"
   Then I should receive a return code of 200   
     And I should see that his "Highly Qualified Teacher" status is "1"
   When I navigate to PUT teacher "Belle"
   Then I should receive a return code of 204
   When I navigate to GET teacher "Belle"
   Then I should see that his "Highly Qualified Teacher" status is "0"
       
@wip 
Scenario: Delete anexisting teacher in XML format
   Given format "application/json"
   When I navigate to DELETE teacher "Christian"
   Then I should receive a return code of 204
   When I navigate to GET teacher "Christian"
   Then I should receive a return code of 404
 
 
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
 