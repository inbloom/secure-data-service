Feature: As an SLI application, I want to be able to manage teachers
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
      And the "staffUniqueStateId" is "12345678"
      And the "highlyQualifiedTeacher" status is "true"
      And the "highestLevelOfEducationCompleted" is "Master's"
   When I navigate to POST "/v1/teachers/"
   Then I should receive a return code of 201
      And I should receive an ID for the newly created teacher
   When I navigate to GET "/v1/teachers/<'newly created teacher' ID>"
   Then the "name" should be "Rafe" "Hairfire" "Esquith"
      And the "birthDate" should be "1954-08-31"
      And the "sex" should be "Male"
      And the "yearsOfPriorTeachingExperience" should be "32"
      And the "staffUniqueStateId" should be "12345678"
      And the "highlyQualifiedTeacher" status should be "true"
      And the "highestLevelOfEducationCompleted" should be "Master's"
      
Scenario: Read base resource
    Given format "application/json"
    When I navigate to GET "/v1/teachers"
    Then I should receive a return code of 200
              
Scenario: Read a teacher by ID in JSON format
   Given format "application/vnd.slc+json"
   When I navigate to GET "/v1/teachers/<'Macey' ID>"
   Then I should receive a return code of 200
      And the "name" should be "Macey" "Mae" "Finch"
      And the "sex" should be "Female"
      And the "birthDate" should be "1956-08-14"
      And the "yearsOfPriorTeachingExperience" should be "22"
      And the "staffUniqueStateId" should be "<'Macey Home State' ID>"
      And the "highlyQualifiedTeacher" status should be "false"
      And the "highestLevelOfEducationCompleted" should be "Bachelor's"
      And I should receive a link named "getTeacherSectionAssociations" with URI "/v1/teachers/<'Macey' ID>/teacherSectionAssociations"
      And I should receive a link named "getSections" with URI "/v1/teachers/<'Macey' ID>/teacherSectionAssociations/sections"
      And I should receive a link named "getTeacherSchoolAssociations" with URI "/v1/teachers/<'Macey' ID>/teacherSchoolAssociations"
      And I should receive a link named "getSchools" with URI "/v1/teachers/<'Macey' ID>/teacherSchoolAssociations/schools"
      And I should receive a link named "self" with URI "/v1/teachers/<'Macey' ID>"


Scenario: Update an existing teacher in JSON format
   Given format "application/json"
   When I navigate to GET "/v1/teachers/<'Belle' ID>"
   Then I should receive a return code of 200   
     And the "highlyQualifiedTeacher" status should be "false"
  When I set the "highlyQualifiedTeacher" status to "true"
   And I navigate to PUT "/v1/teachers/<'Belle' ID>"
   Then I should receive a return code of 204
   When I navigate to GET "/v1/teachers/<'Belle' ID>"
   Then I should receive a return code of 200   
     And the "highlyQualifiedTeacher" status should be "true"
      
Scenario: Delete an existing teacher in JSON format
   Given format "application/json"
   When I navigate to DELETE "/v1/teachers/<'Christian' ID>"
   Then I should receive a return code of 204
   When I navigate to GET "/v1/teachers/<'Christian' ID>"
   Then I should receive a return code of 404
 
 
 
### Error Handling
Scenario: Attempt to read a non-existent teacher
   Given format "application/json"
   When I navigate to GET "/v1/teachers/<Unknown>"
   Then I should receive a return code of 404

Scenario: Attempt to delete a non-existent teacher
   Given format "application/json"
   When I navigate to DELETE "/v1/teachers/<Unknown>"
   Then I should receive a return code of 404

Scenario: Attempt to update a non-existent teacher
   Given format "application/json"
   When I navigate to PUT "/v1/teachers/<Unknown>"
   Then I should receive a return code of 404

# does not appy for v1 handled by 'Scenario: Read base resource'   
#Scenario: Attempt to read the base teacher resource with no GUID
#  Given format "application/json"
#  When I navigate to GET "/v1/teachers"
#  Then I should receive a return code of 405
  
Scenario: Fail when asking for an unsupported format "text/plain"
  Given format "text/plain"
  When I navigate to GET "/v1/teachers/<'Macey' ID>"
  Then I should receive a return code of 406

Scenario: Fail if going to the wrong URI
    Given format "application/json"
    When I navigate to GET "/teacher/<'Macey' ID>"
    Then I should receive a return code of 404
    
       

       
       
	
 