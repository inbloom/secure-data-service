Feature: As an SLI application, I want to be able to manage courses of instruction
  This means I want to be able to perform CRUD on courses

Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234"
  Given I have access to all courses

### Happy Path
Scenario: Create a new course
  Given format "application/json"
    And I have data for the course "<new-course: Chinese 1>"
  When I navigate to POST "/v1/courses"
  Then I should receive a return code of 201
    And I should receive an ID for the newly created course
  When I navigate to GET "/v1/courses/<newly created ID>"
  Then I should receive a return code of 200
    And the response entity should match "<new-course: Chinese 1>"
    
Scenario: Read base resource
    Given format "application/json"
    When I navigate to GET "/v1/courses"
    Then I should receive a return code of 200

Scenario: Read a course by ID
  Given format "application/json"
  When I navigate to GET "/v1/courses/<id: 'Spanish 1 Course'>"
  Then I should receive a return code of 200
    And I should see the "courseDescription" is "Intro to Spanish"
    And I should see the "courseCode.id" is "SP1"

Scenario: Update an existing course
  Given format "application/json"
  When I navigate to GET "/v1/courses/<id: 'Russian 1 Course'>"
  Then I should receive a return code of 200
  When I set the "maximumAvailableCredit" to "3.0"
    And I navigate to PUT "/v1/courses/<id: 'Russian 1 Course'>"
  Then I should receive a return code of 204
  When I navigate to GET "/v1/courses/<id: 'Russian 1 Course'>"
  Then I should see the "maximumAvailableCredit" is 3.0

Scenario: Delete an existing course
  Given format "application/json"
  When I navigate to DELETE "/v1/courses/<id: 'Russian 1 Course'>"
  Then I should receive a return code of 204
  When I navigate to GET "/v1/courses/<id: 'Russian 1 Course'>"
  Then I should receive a return code of 404

### Unhappy Path
Scenario: Attempt to read a non-existing course
  Given format "application/json"
  When I navigate to GET "/v1/courses/<non-existent ID>"
  Then I should receive a return code of 404

Scenario: Attempt to update a non-existing course
  Given format "application/json"
  When I attempt to update "/v1/courses/<non-existent ID>"
  Then I should receive a return code of 404

Scenario: Attempt to delete a non-existing course
  Given format "application/json"
  When I navigate to DELETE "/v1/courses/<non-existent ID>"
  Then I should receive a return code of 404
  
Scenario: Fail when asking for an unsupported format "text/plain"
    Given format "text/plain"
    When I navigate to GET "/v1/courses/<id: 'Russian 1 Course'>"
    Then I should receive a return code of 406
    
Scenario: Fail if going to the wrong URI
	Given format "application/json"
	When I navigate to GET "/v1/course/<id: 'using a wrong URI'>"
     Then I should receive a return code of 404
