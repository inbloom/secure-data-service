
Feature: As an SLI application, I want to be able to manage session-course associations
This means I want to be able to perform CRUD on session-course-associations. 
and verify the correct links from that resource to the appropriate session and course.
  
  
Background: Nothing yet
    Given I am logged in using "demo" "demo1234"
    And I have access to all sessions and courses

Scenario: Create a session-course-association
   Given format "application/json"
     And  "sessionId" is "<'Spring 2010 Session' ID>"
     And  "courseId" is "<'French 1 Course' ID>"
     And  "localCourseCode" is "LCCFR1"
     And  "localCourseTitle" is "French 1 - Intro to French"
    When I navigate to POST "/session-course-associations"
    Then I should receive a return code of 201
     And I should receive an ID for the newly created session-course-association
    When I navigate to GET "/session-course-associations/<'newly created session-course-association' ID>"
    Then I should receive a return code of 200
     And  "sessionId" should be "<'Spring 2010 Session' ID>"
     And  "courseId" should be "<'French 1 Course' ID>"
     And  "localCourseCode" should be "LCCFR1"
     And  "localCourseTitle" should be "French 1 - Intro to French"
     
Scenario: Reading a session-course-association
   Given format "application/json"
    When I navigate to GET "/session-course-associations/<'German 1 during Fall 2011' ID>"
    Then I should receive a return code of 200
     And "sessionId" should be "<'Fall 2011 Session' ID>"
     And "courseId" should be "<'German 1 Course' ID>"
     And "localCourseCode" should be "LCCGR1"
     And "localCourseTitle" should be "German 1 - Intro to German"
     And I should receive a link named "self" with URI "/session-course-associations/<'German 1 during Fall 2011' ID>"
     And I should receive a link named "getSession" with URI "/sessions/<'Fall 2011 Session' ID>"
     And I should receive a link named "getCourse" with URI "/courses/<'German 1 Course' ID>"
     
Scenario: Read a session-course-association for a session
   Given format "application/json"
    When I navigate to GET "/session-course-associations/<'Fall 2011 Session' ID>"
    Then I should receive a return code of 200
     And I should receive a collection of 3 session-course-association links
     And after resolving each link, I should receive a link named "getSession" with URI "/sessions/<'Fall 2011 Session' ID>"
     And after resolution, I should receive a link named "getCourse" with URI "/courses/<'French 1 Course' ID>"
     And after resolution, I should receive a link named "getCourse" with URI "/courses/<'German 1 Course' ID>"
     And after resolution, I should receive a link named "getCourse" with URI "/courses/<'Spanish 1 Course' ID>"

Scenario: Reading a session-course-association for a course
   Given format "application/json"
    When I navigate to GET "/session-course-associations/<'French 1 Course' ID>"
    Then I should receive a return code of 200
     And I should receive a collection of 3 session-course-association links
     And after resolving each link, I should receive a link named "getCourse" with URI "/courses/<'French 1 Course' ID>"
     And after resolution, I should receive a link named "getSession" with URI "/sessions/<'Spring 2010 Session' ID>"
     And after resolution, I should receive a link named "getSession" with URI "/sessions/<'Fall 2011 Session' ID>"

Scenario: Update a session-course-association
   Given format "application/json"
    When I navigate to GET "/session-course-associations/<'German 1 during Fall 2011' ID>"
    Then "localCourseCode" should be "LCCGR1"
    When I set the "localCourseCode" to "ATGR1"
     And I navigate to PUT "/session-course-associations/<'German 1 during Fall 2011' ID>"
    Then I should receive a return code of 204
     And I navigate to GET "/session-course-associations/<'German 1 during Fall 2011' ID>"
     And "localCourseCode" should be "ATGR1"


Scenario: Delete a session-course-association
   Given format "application/json"
    When I navigate to DELETE "/session-course-associations/<'German 1 during Fall 2011' ID>"
    Then I should receive a return code of 204
     And I navigate to GET "/session-course-associations/<'German 1 during Fall 2011' ID>"
     And I should receive a return code of 404
     
     
 ### Error handling
Scenario: Attempt to read a non-existent resource
    Given format "application/json"
    When I navigate to GET "/session-course-associations/<'Invalid ID'>"
    Then I should receive a return code of 404

    Scenario: Attempt to delete a non-existent resource
    Given format "application/json"
    When I navigate to DELETE "/session-course-associations/<'Invalid ID'>"
    Then I should receive a return code of 404
        
    Scenario: Update a non-existing student-course-association
    Given format "application/json"
    When I attempt to update a non-existing association "/session-course-associations/<'Invalid ID'>"
    Then I should receive a return code of 404
