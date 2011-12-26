Feature: <US565> In order to manage sections
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a section.

Background: Logged in as a super-user and using the small data set
  	Given the SLI_SMALL dataset is loaded
    Given I am logged in using "demo" "demo1234"
    Given I have access to all students

#### Happy Path 
Scenario: Create a new section JSON
    Given format "application/json"
      	And the unique section code is "biologyF09"
      	And the sequence of course is 1
       	And the educational environment is "CLASSROOM"
       	And the medium of instruction is "FACE_TO_FACE_INSTRUCTION"
       	And the population served is "GIFTED_AND_TALENTED_STUDENTS"
	When I navigate to POST "/sections/" 
    Then I should receive a return code of 201
       	And I should receive a ID for the newly created section

Scenario: Read a section by id JSON
    Given format "application/json"
    When I navigate to GET section "algebraIIS10"
    Then I should receive a return code of 200
    	And I should see the sequence of course is 1
     	And I should see the educational environment is "OFF_SCHOOL_CENTER"
     	And I should see the medium of instruction is "INDEPENDENT_STUDY"
     	And I should see the population served is "REGULAR_STUDENTS"

Scenario: Update an existing section JSON
    Given format "application/json"
    And the sequence of course is 2
    When I navigate to PUT section "algebraIIS10"
    Then I should receive a return code of 204
    When I navigate to GET section "algebraIIS10"
     And I should see the sequence of course is 2
        
Scenario: Delete an existing section JSON
    Given format "application/json"
    When I navigate to DELETE section "algebraIIS10"
    Then I should receive a return code of 204
    When I navigate to GET section "algebraIIS10"
    Then I should receive a return code of 404
    
    
#### XML version
@wip
Scenario: Create a new section XML
     Given format "application/xml"
        And the unique section code is "chemistryF11"
        And the sequence of course is 1
        And the educational environment is "CLASSROOM"
        And the medium of instruction is "VIRTUAL_ON_LINE_DISTANCE_LEARNING"
        And the population served is "BILINGUAL_STUDENTS"
    When I navigate to POST "/sections/" 
    Then I should receive a return code of 201
        And I should receive a ID for the newly created section
@wip
Scenario: Read a section by id XML
    Given format "application/xml"
    When I navigate to GET section "chemistryF11"
    Then I should receive a return code of 200
        And I should see the sequence of course is 1
        And I should see the educational environment is "CLASSROOM"
        And I should see the medium of instruction is "VIRTUAL_ON_LINE_DISTANCE_LEARNING"
        And I should see the population served is "BILINGUAL_STUDENTS"
@wip
Scenario: Update an existing section XML
    Given format "application/xml"
    And the sequence of course is 2
    When I navigate to PUT section "chemistryF11"
    Then I should receive a return code of 204
    When I navigate to GET section "biologyF09"
        And I should see the sequence of course is 2
@wip    
Scenario: Delete an existing section XML
    Given format "application/xml"
    When I navigate to DELETE section "chemistryF11"
    Then I should receive a return code of 204
    When  I navigate to GET section "chemistryF11"
    Then I should receive a return code of 404
    
###Links
@wip
Scenario: Section Resource links to teacher section association
   Given format "application/json"
   When I navigate to Section "567"
   Then I should receive a return code of 200
      And I should receive a link named "getTeacherSectionAssociations" with URI /teacher-section-associations/<'567' ID>
	  And I should receive a link named "getStudentSectionAssociations" with URI /student-section-associations/<'567' ID>
   	  And I should receive a link named "getTeachers" with URI /teacher-section-associations/<'567' ID>/targets
	  And I should receive a link named "getStudents" with URI /student-section-associations/<'567' ID>/targets
	  And I should receive a link named "getSections" with URI /sections/<'567' ID>
    
### Error handling
Scenario: Attempt to read a non-existing section
    Given format "application/json"
    When I navigate to GET section "Invalid"
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing section
    Given format "application/json"
    When I navigate to DELETE section "Invalid"
    Then I should receive a return code of 404      

Scenario: Update a non-existing section
    Given format "application/json"
    When I attempt to update a non-existing section "Invalid"
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given format "text/plain"
    When I navigate to GET section "physicsS08"
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given format "application/json"
    When I navigate to GET section "WrongURI"
    Then I should receive a return code of 404
    
 Scenario: Attempt to read the base section resource with no GUID
    Given format "application/json"
    When I navigate to GET section "NoGUID"
    Then I should receive a return code of 405