
Feature: Test schema based validation on entities/associations


Background: Logged in as a super-user and using the small data set
	Given I am logged in using "demo" "demo1234"
	And I have access to all entities
	
Scenario: Post a valid base Student/School with bare minimum required data
	Given format "application/json"
	And I create a valid base level student object
	When I navigate to POST "/students"
	Then I should receive a return code of 201
	Given I create a valid base level school object
	When I navigate to POST "/schools"
	Then I should receive a return code of 201


Scenario: Fail when posting a School object during a Student POST operation
	Given format "application/json"
	Given I create a valid base level school object
	When I navigate to POST "/students"
	Then I should receive a return code of 400

#tests all non-nullable fields
Scenario: Fail when passing blank object during POST for student
	Given I create a blank json object
	When I navigate to POST "/students"
	Then I should receive a return code of 400
	When I navigate to POST "/schools"
	Then I should receive a return code of 400
	When I navigate to POST "/student-school-associations"
	Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid

Scenario: Fail when passing bad enum during POST for student
	Given format "application/json"
	Given I create a student object with "sex" set to Guy
	When I navigate to POST "/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when passing an incorrectly capitalized enum during POST for student
	Given format "application/json"
    Given I create a student object with sex equal to "MALE" instead of "Male"
	When I navigate to POST "/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when passing map instead of array during POST for school
	Given I create a create a school object with "address" set to a single map
	When I navigate to POST "/schools"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid
    Given I create the same school object with "address" as an array with the same map
	When I navigate to POST "/schools"
    Then I should receive a return code of 201
 
    
Scenario: Fail when passing array instead of map during POST for student
	Given format "application/json"
    Given I create a student object with "name" set to an array of names
	When I navigate to POST "/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid
    Given I create the same student object with "name" as a map with the same data
	When I navigate to POST "/students"
    Then I should receive a return code of 201

	
Scenario: Fail when posting a StudentSchoolAssociation with invalid school ID
    Given an SSA object is valid except for "schoolID"
	When I navigate to POST "/student-school-associations"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when posting a string in a field expecting an integer
	Given format "application/json"
    Given I create a student object with "studentUniqueStateId" equal to a string
	When I navigate to POST "/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when posting an integer in a field expecting a string
	Given format "application/json"
    Given I create a student object with "studentUniqueStateId" equal to a integer
	When I navigate to POST "/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid
	

Scenario: Fail when posting a string to a field that has more characters than the schema allows
    Given I create a school object with "nameOfInstitution" equal to a 61 character string
	When I navigate to POST "/schools"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when posting a string to a field that has fewer characters than the schema allows
    Given I create a school object with "webSite" equal to a 4 character string
	When I navigate to POST "/schools"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid

	
Scenario: Fail when posting a string "true" to a field expecting a boolean
	Given format "application/json"
    Given I create a student object with "hispanicLatinoEthnicity" set to a true string
	When I navigate to POST "/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Fail when posting a date in the wrong format
	Given format "application/json"
    Given I create a student object with "birthDate" set to MM-DD-YYYY
	When I navigate to POST "/students"
    Then I should receive a return code of 400
#	   And the response body should tell me why the request was invalid


Scenario: Passing blank object to a valid entity with PUT should fail with validation error (not patch the existing object)
	Given format "application/json"
    When I navigate to GET "/teachers/<'Belle' ID>"
    Then I should receive a return code of 200   
    When I create a blank request body object
      And I navigate to PUT "/teachers/<'Belle' ID>"
    Then I should receive a return code of 400

Scenario: Given a known school object, perform a PUT with a base school object to confirm option attributes are gone (test non-patching)
	Given format "application/json"
    When I navigate to GET "/schools/<'Apple Alternative Elementary School' ID>"
    Then I should receive a return code of 200   
    When I create a valid base level school object
      And I navigate to PUT "/schools/<'Apple Alternative Elementary School' ID>"
    Then I should receive a return code of 204
    When I navigate to GET "/schools/<'Apple Alternative Elementary School' ID>"
    Then I should receive a return code of 200   
      And "nameOfInstitution" should be "school name"
      And "stateOrganizationId" should be "12345678"
      And "gradesOffered" should contain "First_grade" and "Second_grade"
      And there should be no other contents in the response body other than links
      
Scenario: Given a school entity with no associations, when a GET is performed with an association id, an empty collection should be returned (not 404)
	Given format "application/json"
	And I create a valid base level school object
	When I navigate to POST "/schools"
	Then I should receive a return code of 201
	And I should receive an ID for the newly created school
	When I navigate to GET "/teacher-school-associations/<'Previous School' ID>"
	Then I should receive a return code of 200
	And a collection of size 0
	When I navigate to GET "/teacher-school-associations/<'Previous School' ID>/targets"
	Then I should receive a return code of 200
	And a collection of size 0
	
	
	
	
	
