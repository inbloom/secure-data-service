Feature: In order to manage sections and students
As a SLI application I want to use this association to get the course section(s) a student is assigned to, or all the student(s) a section has.  
I must be able to perform CRUD functions on student-section association.

This is the data I am assuming for these tests
Section:  Biology II - C
Student: Jane Doe

Background: 
	Given I am logged in using "demo" "demo1234"

Scenario Outline: Create a student-section-association
	Given format <format>
		And "SectionID" is "<'Biology II - C' ID>"
		And "beginDate" is "2011-01-12"
    	And "endDate" is "2011-04-12"
		And "StudentID" is "<'Jane Doe' ID>"
	When I navigate to POST "/student-section-associations"
	Then I should receive a return code of 201
		And I should receive a ID for the newly created student-section-association
	When I navigate to GET "/student-section-associations/<'newly created student-section-association' ID>"
	Then I should receive a return code of 200
		And the "beginDate" should be "2011-01-12"
		And the "endDate" should be "2011-04-12"
	Examples:
	    	| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"          |

Scenario Outline: Read a student-section-association
	Given format <format>
	When I navigate to GET "/student-section-associations/<'Student "Albert Wright" and Section "Foreign Language - A"' ID>"
	Then I should receive a return code of 200
		And I should receive 1 student-section-associations
		And I should receive a link named "getStudent" with URI "/students/<'Albert Wright' ID>"
		And I should receive a link named "getSection" with URI "/sections/<'Foreign Language - A' ID>"
        And I should receive a link named "self" with URI "/student-section-associations/<'self' ID>"
		And the "beginDate" should be "2011-09-15"
		And the "endDate" should be "2011-12-15"
		And the "repeatIdentifier" should be "Repeated_counted_in_grade_point_average"
	Examples:
	    	| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"          |

Scenario Outline: Update a student-section-association 
	Given format <format>
	When I navigate to GET "/student-section-associations/<'Section "Foreign Language - A" and Student "Albert Wright"' ID>"
      Then  the "repeatIdentifier" should be "Repeated"
	When I set the "repeatIdentifier" to "Not_repeated"
		And I navigate to PUT "/student-section-associations/<'Section "Foreign Language - A" and Student "Albert Wright"' ID>"
	Then I should receive a return code of 204
	When I navigate to GET "/student-section-associations/<'Section "Foreign Language - A" and Student "Albert Wright"' ID>"
	Then the "repeatIdentifier" should be "Not_repeated"
	Examples:
	    	| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"          |

Scenario Outline: Delete a student-section-association
	Given format <format>
	And I navigate to DELETE "/student-section-associations/<'Section "Foreign Language - A" and Student "Albert Wright"' ID>"
	Then I should receive a return code of 204
		And I navigate to GET "/student-section-associations/<'Section "Foreign Language - A" and Student "Albert Wright"' ID>"
		And I should receive a return code of 404
		Examples:
	    	| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"          |

Scenario: Reading a student-section-association for a student
	Given format "application/json"
	When I navigate to GET "/student-section-associations/<'Jane Doe' ID>"
	Then I should receive a return code of 200
		And I should receive a collection of 4 student-section-association links that resolve to
		And I should receive a link named "getStudent" with URI "/students/<'Jane Doe' ID>"
		And I should receive a link named "getSection" with URI "/sections/<'Foreign Language - A' ID>"
		And I should receive a link named "getSection" with URI "/sections/<'Biology II - C' ID>"
		And I should receive a link named "getSection" with URI "/sections/<'Physics I - B' ID>"
		And I should receive a link named "getSection" with URI "/sections/<'Chemistry I - A' ID>"

Scenario: Reading a student-section-association for a section
	Given format "application/json"
	When I navigate to GET "/student-section-associations/<'Chemistry I - A' ID>"
	Then I should receive a return code of 200
	And I should receive a collection of 3 student-section-association links that resolve to
    And I should receive a link named "getSection" with URI "/sections/<'Chemistry I - A' ID>"
	And I should receive a link named "getStudent" with URI "/students/<'Jane Doe' ID>"
	And I should receive a link named "getStudent" with URI "/students/<'Albert Wright' ID>"
	And I should receive a link named "getStudent" with URI "/students/<'Kevin Smith' ID>"
	
### Error Handling
Scenario: Attempt to read a non-existing student section association
	Given format "application/json"
	When I navigate to GET "/student-section-associations/<'WrongURI' ID>"
	Then I should receive a return code of 404
	
Scenario: Attempt to delete a non-existent resource
	Given format "application/json"
	When I navigate to DELETE "/student-section-associations/<'WrongURI' ID>"
	Then I should receive a return code of 404

Scenario: Attempt to read the base resource with no GUID
	Given format "application/json"
	When I navigate to GET "/student-section-associations/<'No GUID' ID>"
	Then I should receive a return code of 405
