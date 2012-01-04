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
      	And the unique section code is "SpanishB09"
      	And the sequence of course is 1
       	And the educational environment is "Off_school_center"
       	And the medium of instruction is "Independent_study"
       	And the population served is "Regular_students"
	When I navigate to POST "/sections/" 
    Then I should receive a return code of 201
       	And I should receive a ID for the newly created section
    When I navigate to GET section "SpanishB09"
    Then I should see the sequence of course is 1
      And I should see the educational environment is "Off_school_center"
      And I should see the medium of instruction is "Independent_study"
      And I should see the population served is "Regular_students"
    Examples:
    		| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"  |

Scenario Outline: Read a section by id
    Given format <format>
    When I navigate to GET section "biologyF09"
    Then I should receive a return code of 200
    	And I should see the sequence of course is 1
     	And I should see the educational environment is "Off_school_center"
     	And I should see the medium of instruction is "Independent_study"
     	And I should see the population served is "Regular_students"
    Examples:
    		| format                     |
    		| "application/json"         |
#   		| "application/vnd.slc+json" |
#    		| "application/xml"  |

Scenario Outline: Update an existing section
    Given format <format>
    And the sequence of course is 2
    When I navigate to PUT section "biologyF09"
    Then I should receive a return code of 204
    When I navigate to GET section "biologyF09"
     And I should see the sequence of course is 2
    Examples:
    		| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/vnd.slc+xml"  |
        
Scenario Outline: Delete an existing section
    Given format <format>
    When I navigate to DELETE section "biologyF09"
    Then I should receive a return code of 204
    When I navigate to GET section "biologyF09"
    Then I should receive a return code of 404
    Examples:
    		| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/vnd.slc+xml"  |
    
    
###Links
@wip
Scenario: Section Resource links to teacher section association
   Given format "application/vnd.slc+json"
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