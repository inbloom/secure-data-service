@wip
Feature: In order to manage sections and students
As a SLI application I want to use this association to get the course section(s) a student is assigned to, or all the student(s) a section has.  
I must be able to perform CRUD functions on student-section association.

This is the data I am assuming for these tests
Section:  Biology II - C
Student: Jane Doe

Background: Nothing yet

Scenario: Create a student-section-association
Given format "application/json"
	And Section is <'Biology II - C' ID>
	And beginDate is "2001/01/12"
    And endDate is "2011/04/12"
	And Student is <'Jane Doe' ID>
When I navigate to POST "/student-section-associations"
Then I should receive a return code of 201
	And I should receive a ID for the newly created student-section-association
When I navigate to GET /student-section-associations/<'newly created student-section-association' ID>
Then the endDate should be "2011/04/12"
  And the beginDate should be "2011/01/12"

Scenario: Read a student-section-association
Given format "application/json"
When I navigate to GET /student-section-association/<'Student "Jane Doe" and Section "Biology II - C"' ID>
Then I should receive a  return code of 200
	And I should receive 1 student-section-associations
	And I should receive a link named "getStudent" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getSection" with URI /sections/<'Biology II - C' ID>
    And I should receive a link named "self" with URI /student-section-association/<'self' ID>
	And the beginDate should be "2011/09/15"
	And the endDate should be "2011/12/15"
	And the repeatIdentifier should be 1

Scenario: Reading a student-section-association for a student
Given format "application/json"
When I navigate to GET /student-section-associations/<'Jane Doe' ID>
Then I should receive a return code of 200
    And I should receive collection of 4 student-section-association links that resolve to
	And I should receive a link named "getStudent" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getSection" with URI /sections/<'Foreign Language - A' ID>
	And I should receive a link named "getSection" with URI /sections/<'Biology II - C' ID>
	And I should receive a link named "getSection" with URI /sections/<'Physics I - B' ID>
	And I should receive a link named "getSection" with URI /sections/<'Chemistry I - A' ID>

Scenario: Reading a student-section-association for a section
Given  format "application/json"
When I navigate to GET /student-section-associations/<'Chemistry I - A' ID>
Then I should receive a return code of 200
	And I should receive a collection of 3 student-section-association links that resolve to
    And I should receive a link named "getSection" with URI /sections/<'Chemistry I - A' ID>
	And I should receive a link named "getStudent" with URI /students/<'Jane Doe' ID>
	And I should receive a link named "getStudent" with URI /students/<'Albert Wright' ID>
	And I should receive a link named "getStudent" with URI /students/<'Kevin Smith' ID>


Scenario: Update a student-section-association 
Given  format "application/json"
And I navigate to GET /student-section-associations/<'Section "Biology II - C" and Student "Jane Doe"' ID>
	And beginDate is "2011/06/1"
When I set the beginDate to "2011/08/15"
	And I navigate to PUT /student-section-associations/<'the previous association' ID>
Then I should get a return code of 200
	And I navigate to GET /student-section-associations/<'the previous association' ID>
	And the beginDate should be "2011/08/15"

Scenario: Delete a student-section-association
Given format "application/json"
And I navigate to DELETE /student-section-associations/<'the previous association' Id>
Then I should get a return code of 200
	And I navigate to GET /student-section-associations/<'the previous association' Id>
	And I should receive a return code of 404

