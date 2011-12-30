Feature: <US565> In order to manage sections
    As a client application using SLI
    I want to have create, read, update, and delete functionality for a section.

Background: Logged in as a super-user and using the small data set
  	Given the SLI_SMALL dataset is loaded
    Given I am logged in using "demo" "demo1234"
    Given I have access to all students

#### Happy Path 
Scenario Outline: Create a new section
    Given format <format>
      	And the uniqueSectionCode is "SpanishB09"
      	And the sequenceOfCourse is 1
       	And the educationalEnvironment is "OFF_SCHOOL_CENTER"
       	And the mediumOfInstruction is "INDEPENDENT_STUDY"
       	And the populationServed is "REGULAR_STUDENTS"
	When I navigate to POST "/sections/" 
    Then I should receive a return code of 201
       	And I should receive a ID for the newly created section
    When I navigate to GET /sections/<'newly created section' ID>
    Then I should see the sequenceOfCourse is 1
      And I should see the educationalEnvironment is "OFF_SCHOOL_CENTER"
      And I should see the mediumOfInstruction is "INDEPENDENT_STUDY"
      And I should see the populationServed is "REGULAR_STUDENTS"
      And I should see the uniqueSectionCode is "SpanishB09"
    Examples:
    		| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"  |

Scenario Outline: Read a section by id
    Given format <format>
    When I navigate to GET /sections/<'biologyF09' ID>
    Then I should receive a return code of 200
    	And I should see the sequenceOfCourse is 1
     	And I should see the educationalEnvironment is "OFF_SCHOOL_CENTER"
     	And I should see the mediumOfInstruction is "INDEPENDENT_STUDY"
     	And I should see the populationServed is "REGULAR_STUDENTS"
    Examples:
    		| format                     |
    		| "application/json"         |
#   		| "application/vnd.slc+json" |
#    		| "application/xml"  |

Scenario Outline: Update an existing section
    Given format <format>
    And the SequenceOfCourse is 2
    When I navigate to PUT /sections/<'biologyF09' ID>
    Then I should receive a return code of 204
    When I navigate to GET /sections/<'biologyF09' ID>
     And I should see the sequenceOfCourse is 2
    Examples:
    		| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/vnd.slc+xml"  |
        
Scenario Outline: Delete an existing section
    Given format <format>
    When I navigate to DELETE /sections/<'biologyF09' ID>
    Then I should receive a return code of 204
    When I navigate to GET /sections/<'biologyF09' ID>
    Then I should receive a return code of 404
    Examples:
    		| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/vnd.slc+xml"  |
    
    
###Links
@wip
Scenario: Section Resource links to teacher section association
   Given format "application/json"
   When I navigate to GET /sections/<'biology567' ID>
   Then I should receive a return code of 200
      And I should receive a link named "getTeacherSectionAssociations" with URI /teacher-section-associations/<'biology567' ID>
	  And I should receive a link named "getStudentSectionAssociations" with URI /student-section-associations/<'biology567' ID>
   	  And I should receive a link named "getTeachers" with URI /teacher-section-associations/<'biology567' ID>/targets
	  And I should receive a link named "getStudents" with URI /student-section-associations/<'biology567' ID>/targets
	  And I should receive a link named "self" with URI /sections/<'biology567' ID>
    
### Error handling
Scenario: Attempt to read a non-existing section
    Given format "application/json"
    When I navigate to GET /sections/<'Invalid' ID>
    Then I should receive a return code of 404      

Scenario: Attempt to delete a non-existing section
    Given format "application/json"
    When I navigate to DELETE /sections/<'Invalid' ID>
    Then I should receive a return code of 404      

Scenario: Update a non-existing section
    Given format "application/json"
    When I attempt to PUT /sections/<'Invalid' ID>
    Then I should receive a return code of 404      
    
Scenario: Fail when asking for an unsupported format "text/plain"
    Given format "text/plain"
    When I navigate to GET /sections/<'physicsS08' ID>
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
    Given format "application/json"
    When I navigate to GET /sections/<'WrongURI' ID>
    Then I should receive a return code of 404
    
 Scenario: Attempt to read the base section resource with no GUID
    Given format "application/json"
    When I navigate to GET /sections/<'NoGUID' ID>
    Then I should receive a return code of 405