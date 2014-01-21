@attendance
Feature:
  As an API user
  In order to maintain a student's attendance record
  I want to be able create, read, partially update and delete attendance and yearly attendance events

  Background:
    Given I am logged in as a tenant-level IT Administrator

  # Attendance Event Scenarios

  Scenario: An API user can create (POST) a new attendance event
    When I POST an attendance event
    Then the response status should be 201 Created
     And the response location header should link to the new attendance event
     And the attendance event should be saved

  Scenario: An API user cannot create (POST) a duplicate attendance event
    Given I create an attendance event
     When I POST an attendance event
     Then the response status should be 409 Conflict

  Scenario: An API user reads (GET) an attendance event
    Given I create an attendance event
     When I GET that attendance event
     Then the response status should be 200 OK
      And the response resource should have an id
      And the response resource entity type should be "attendance"
      And the response resource should have HATEOAS links for attendance
      And the response resource should contain expected attendance data

  Scenario: An API user reads (GET) a non-existent attendance event
    When I GET a non-existent attendance event
    Then the response status should be 404 Not Found

  Scenario: An API user fully updates (PUT) an attendance event
    Given I create an attendance event
     When I PUT that attendance event
     Then the response status should be 405 Method Not Allowed

  Scenario: An API user deletes (DELETE) an attendance event
    Given I create an attendance event
    When I DELETE that attendance event
    Then the response status should be 204 No Content
     And the attendance event should be deleted

  Scenario: An API user partially updates (PATCH) an attendance event
    Given I create an attendance event
     When I PATCH that attendance event
     Then the response status should be 204 No Content
      And the attendance event should be updated

  # Yearly Attendance Event Scenarios

  Scenario: An API user can create (POST) a new yearly attendance event
    When I POST a yearly attendance event
    Then the response status should be 201 Created
     And the response location header should link to the new attendance event
     And the attendance event should be saved

  Scenario: An API user cannot create (POST) a duplicate yearly attendance event
    Given I create a yearly attendance event
    When I POST a yearly attendance event
    Then the response status should be 409 Conflict

  Scenario: An API user reads (GET) a yearly attendance event
    Given I create a yearly attendance event
     When I GET that attendance event
     Then the response status should be 200 OK
      And the response resource should have an id
      And the response resource entity type should be "attendance"
      And the response resource should have HATEOAS links for attendance
      And the response resource should contain expected attendance data

  Scenario: An API user reads (GET) a non-existent yearly attendance event
    When I GET a non-existent yearly attendance event
    Then the response status should be 404 Not Found

  Scenario: An API user fully updates (PUT) a yearly attendance event
    Given I create a yearly attendance event
    When I PUT that attendance event
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user deletes (DELETE) a yearly attendance event
    Given I create a yearly attendance event
    When I DELETE that attendance event
    Then the response status should be 204 No Content
    And the attendance event should be deleted

  Scenario: An API user partially updates (PATCH) an attendance event
    Given I create a yearly attendance event
    When I PATCH that attendance event
    Then the response status should be 204 No Content
    And the attendance event should be updated