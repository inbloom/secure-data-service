Feature: As an SLI application, I want to be able to manage sessions of instruction
  This means I want to be able to perform CRUD on sessions

Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234"
  Given I have access to all sessions

Scenario: Create a new session
  Given format "application/json"
    And "sessionName" is "Spring 2012"
    And "schoolYear" is "2011-2012"
    And "term" is "Spring Semester"
    And "beginDate" is "2012-01-01"
    And "endDate" is "2012-06-31"
    And "totalInstructionalDays" is 88
  When I navigate to POST "/v1/sessions"
  Then I should receive a return code of 201
    And I should receive an ID for the newly created session
  When I navigate to GET "/v1/sessions/<newly created ID>"
  Then I should receive a return code of 200
    And "sessionName" should be "Spring 2012"
    And "schoolYear" should be "2011-2012"
    And "term" should be "Spring Semester"
    And "beginDate" should be "2012-01-01"
    And "endDate" should be "2012-06-31"
    And "totalInstructionalDays" should be 88
    
Scenario: Read base resource
    Given format "application/json"
    When I navigate to GET "/v1/sessions"
    Then I should receive a return code of 200    

Scenario: Read a session by ID
  Given format "application/vnd.slc+json"
  When I navigate to GET "/v1/sessions/<'FALL 2011 SESSION' ID>"
  Then I should receive a return code of 200
    And I should receive a link named "self" with URI "/v1/sessions/<'FALL 2011 SESSION' ID>"
    And "sessionName" should be "Fall 2011"
    And "schoolYear" should be "2011-2012"
    And "term" should be "Fall Semester"
    And "beginDate" should be "2011-09-01"
    And "endDate" should be "2011-12-31"
    And "totalInstructionalDays" should be 90

Scenario: Update an existing session
  Given format "application/json"
  When I navigate to GET "/v1/sessions/<'FALL 2011 SESSION' ID>"
  Then I should receive a return code of 200
  When I set "totalInstructionalDays" to 17
    And I navigate to PUT "/v1/sessions/<'FALL 2011 SESSION' ID>"
  Then I should receive a return code of 204
  
Scenario: Check the updated session  
  Given format "application/vnd.slc+json"
  When I navigate to GET "/v1/sessions/<'FALL 2011 SESSION' ID>"
  Then I should receive a link named "self" with URI "/v1/sessions/<'FALL 2011 SESSION' ID>"
    And "totalInstructionalDays" should be 17

Scenario: Delete an existing session
  Given format "application/json"
  When I navigate to DELETE "/v1/sessions/<'SPRING 2011 SESSION' ID>"
  Then I should receive a return code of 204
  When I navigate to GET "/v1/sessions/<'SPRING 2011 SESSION' ID>"
  Then I should receive a return code of 404
  
Scenario: Attempt to read a non-existing session
  Given format "application/json"
  When I navigate to GET "/v1/sessions/<non-existent ID>"
  Then I should receive a return code of 404

Scenario: Attempt to update a non-existing session
  Given format "application/json"
  When I navigate to GET "/v1/sessions/<non-existent ID>"
  Then I should receive a return code of 404

Scenario: Attempt to delete a non-existing session
  Given format "application/json"
  When I navigate to GET "/v1/sessions/<non-existent ID>"
  Then I should receive a return code of 404