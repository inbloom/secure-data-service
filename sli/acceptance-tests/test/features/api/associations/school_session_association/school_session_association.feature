
Feature: As an SLI application, I want to be able to manage school-session associations
This means I want to be able to perform CRUD on school-session-associations. 
and verify the correct links from that resource to the appropriate session and school.
  
  
Background: Nothing yet
    Given I am logged in using "demo" "demo1234"
    And I have access to all sessions and schools

Scenario: Create a school-session-association
   Given format "application/json"
     And  "sessionId" is "<'Fall 2011 Session' ID>"
     And  "schoolId" is "<'Algebra Alternative' ID>"
     And  grading period "gradingPeriod" is "Second Semester"
     And  grading period "beginDate" is "2011-09-01"
     And  grading period "endDate" is "2011-10-31"
     And  grading period "totalInstructionalDays" is 45
    When I navigate to POST "/school-session-associations"
    Then I should receive a return code of 201
     And I should receive an ID for the newly created school-session-association
    When I navigate to GET "/school-session-associations/<'newly created school-session-association' ID>"
    Then I should receive a return code of 200
     And  "sessionId" should be "<'Fall 2011 Session' ID>"
     And  "schoolId" should be "<'Algebra Alternative' ID>"
     And  grading period "gradingPeriod" should be "Second Semester"
     And  grading period "beginDate" should be "2011-09-01"
     And  grading period "endDate" should be "2011-10-31"
     And  grading period "totalInstructionalDays" should be 45
     
Scenario: Reading a school-session-association
   Given format "application/json"
    When I navigate to GET "/school-session-associations/<'Spring 2011 Session at Algebra Alternative' ID>"
    Then I should receive a return code of 200
     And "sessionId" should be "<'Spring 2011 Session' ID>"
     And "schoolId" should be "<'Algebra Alternative' ID>"
     And I should receive a link named "self" with URI "/school-session-associations/<'Spring 2011 Session at Algebra Alternative' ID>"
     And I should receive a link named "getSession" with URI "/sessions/<'Spring 2011 Session' ID>"
     And I should receive a link named "getSchool" with URI "/schools/<'Algebra Alternative' ID>"
     
Scenario: Read a school-session-association for a session
   Given format "application/json"
    When I navigate to GET "/school-session-associations/<'Spring 2011 Session' ID>"
    Then I should receive a return code of 200
     And I should receive a collection of 3 school-session-association links
     And after resolving each link, I should receive a link named "getSession" with URI "/sessions/<'Spring 2011 Session' ID>"
     And after resolution, I should receive a link named "getSchool" with URI "/schools/<'Algebra Alternative' ID>"
     And after resolution, I should receive a link named "getSchool" with URI "/schools/<'Biology High' ID>"
     And after resolution, I should receive a link named "getSchool" with URI "/schools/<'Chemistry Elementary' ID>"

Scenario: Reading a school-session-association for a school
   Given format "application/json"
    When I navigate to GET "/school-session-associations/<'Biology High' ID>"
    Then I should receive a return code of 200
     And I should receive a collection of 3 school-session-association links
     And after resolving each link, I should receive a link named "getSchool" with URI "/schools/<'Biology High' ID>"
     And after resolution, I should receive a link named "getSession" with URI "/sessions/<'Spring 2011 Session' ID>"
     And after resolution, I should receive a link named "getSession" with URI "/sessions/<'Spring 2010 Session' ID>"
     And after resolution, I should receive a link named "getSession" with URI "/sessions/<'Spring 2009 Session' ID>"

Scenario: Update a school-session-association
   Given format "application/json"
    When I navigate to GET "/school-session-associations/<'Spring 2009 Session at Biology High' ID>"
    Then "schoolId" should be "<'Biology High' ID>"
    When I set the "schoolId" to "<'Algebra Alternative' ID>"
     And I navigate to PUT "/school-session-associations/<'Spring 2009 Session at Biology High' ID>"
    Then I should receive a return code of 204
     And I navigate to GET "/school-session-associations/<'Spring 2009 Session at Biology High' ID>"
     And "schoolId" should be "<'Algebra Alternative' ID>"


Scenario: Delete a school-session-association
   Given format "application/json"
    When I navigate to DELETE "/school-session-associations/<'Spring 2009 Session at Biology High' ID>"
    Then I should receive a return code of 204
     And I navigate to GET "/school-session-associations/<'Spring 2009 Session at Biology High' ID>"
     And I should receive a return code of 404
     
     
 ### Error handling
Scenario: Attempt to read a non-existent resource
    Given format "application/json"
    When I navigate to GET "/school-session-associations/<'Invalid ID'>"
    Then I should receive a return code of 404

    Scenario: Attempt to delete a non-existent resource
    Given format "application/json"
    When I navigate to DELETE "/school-session-associations/<'Invalid ID'>"
    Then I should receive a return code of 404
        
    Scenario: Update a non-existing student-school-association
    Given format "application/json"
    When I attempt to update a non-existing association "/school-session-associations/<'Invalid ID'>"
    Then I should receive a return code of 404
