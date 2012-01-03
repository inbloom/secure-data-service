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
		And "sectionId" is Section "Biology II - C"
		And "studentId" is Student "Jane Doe"
		And "beginDate" is "2011-12-15"
	When I navigate to POST "/student-section-associations"
	Then I should receive a return code of 201
		And I should receive a ID for the newly created student-section-association
	When I navigate to GET Student Section Association for Student "Jane Doe" and Section "Biology II - C"
	Then I should receive a return code of 200
		And "sectionId" should equal Section "Biology II - C"
		And "studentId" should equal Student "Jane Doe"
		And "beginDate" should equal "2011-12-15"
	Examples:
	    	| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"          |

Scenario Outline: Read a student-section-association
	Given format <format>
	When I navigate to GET Student Section Association for Student "Albert Wright" and Section "Foreign Language - A"
	Then I should receive a return code of 200
		And I should receive 1 student-section-association
		And I should receive a link named "getStudent" with URI for Student "Albert Wright"
		And I should receive a link named "getSection" with URI for Section "Foreign Language - A"
		And "beginDate" should equal "2011-09-15"
		And "endDate" should equal "2011-12-15"
		And "repeatIdentifier" should equal "Repeated_counted_in_grade_point_average"
	Examples:
	    	| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"          |

Scenario: Reading a student-section-association for a student
	Given format "application/vnd.slc+json"
	When I navigate to GET Student Section Associations for the Student "Jane Doe"
	Then I should receive a return code of 200
		And I should receive a collection of 4 student-section-associations that resolve to
		And I should receive a link named "getSection" with URI for Section "Foreign Language - A"
		And I should receive a link named "getSection" with URI for Section "Biology II - C"
		And I should receive a link named "getSection" with URI for Section "Physics I - B"
		And I should receive a link named "getSection" with URI for Section "Chemistry I - A"

Scenario: Reading a student-section-association for a section
	Given  format "application/vnd.slc+json"
	When I navigate to GET Student Section Associations for the Section "Chemistry I - A"
	Then I should receive a return code of 200
		And I should receive a collection of 3 student-section-associations that resolve to
		And I should receive a link named "getStudent" with URI for Student "Albert Wright"
		And I should receive a link named "getStudent" with URI for Student "Kevin Smith"
		And I should receive a link named "getStudent" with URI for Student "Jane Doe"

Scenario Outline: Update a student-section-association 
	Given format <format>
		And I navigate to GET Student Section Association for Student "Albert Wright" and Section "Foreign Language - A" 
	When "repeatIdentifier" is updated to "Not_repeated"
		And I navigate to PUT Student Section Association for Student "Albert Wright" and Section "Foreign Language - A"
	Then I should receive a return code of 204
	When I navigate to GET Student Section Association for Student "Albert Wright" and Section "Foreign Language - A"
	Then "repeatIdentifier" should equal "Not_repeated"
	Examples:
	    	| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"          |

Scenario Outline: Delete a student-section-association
	Given format <format>
	And I navigate to DELETE Student Section Association for Student "Albert Wright" and Section "Foreign Language - A"
	Then I should receive a return code of 204
		And I navigate to GET Student Section Association for Student "Albert Wright" and Section "Foreign Language - A"
		And I should receive a return code of 404
		Examples:
	    	| format                     |
    		| "application/json"         |
#    		| "application/vnd.slc+json" |
#    		| "application/xml"          |
	
### Error Handling
Scenario: Attempt to read a non-existing student section association
	Given format "application/vnd.slc+json"
	When I navigate to GET /student-section-associations/<WrongURI>
	Then I should receive a return code of 404
	
Scenario: Attempt to delete a non-existent resource
	Given format "application/vnd.slc+json"
	When I navigate to DELETE /student-section-associations/<WrongURI>
	Then I should receive a return code of 404

Scenario: Attempt to read the base resource with no GUID
	Given format "application/vnd.slc+json"
	When I navigate to GET /student-section-associations/<No GUID>
	Then I should receive a return code of 405
