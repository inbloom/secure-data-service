@bell_schedule
Feature:
  As an API user
  In order to maintain a school's bell schedule
  I want to be able create, read, and delete bell schedules

  Background:
    Given I am logged in as a tenant-level IT Administrator
    And I want to use format "application/vnd.slc+json"

  Scenario: An API user can create (POST) a new bell schedule
    When I POST a new bell schedule
    Then the response status should be 201 Created
    And the response location header should link to the new bell schedule
    And the bell schedule should be saved

  Scenario: An API user reads (GET) a bell schedule
    Given I create a bell schedule
    When I GET that bell schedule
    Then the response status should be 200 OK
    And the response resource should have an id
    And the response resource entity type should be "bellSchedule"
    And the response resource should have HATEOAS links for bell schedule
    And the response resource should contain expected bell schedule data

  Scenario: An API user fully updates (PUT) a bell schedule
    Given I create a bell schedule
    When I PUT that bell schedule
    Then the response status should be 204 No Content
    And the bell schedule should be updated

  Scenario: An API user partially updates (PATCH) a bell schedule
    Given I create a bell schedule
    When I PATCH that bell schedule
    Then the response status should be 204 No Content
    And the bell schedule should be updated

  Scenario: An API user attempts to POST to a bell schedule resource
    Given I create a bell schedule
    When I POST to that bell schedule
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user attempts to fully update (PUT) the bell schedule list resource
    When I PUT the bell schedule list
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user attempts to partially update (PATCH) the bell schedule list resource
    When I PATCH the bell schedule list
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user attempts to delete (DELETE) the bell schedule list resource
    When I DELETE the bell schedule list
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user deletes (DELETE) a bell schedule
    Given I create a bell schedule
    When I DELETE that bell schedule
    Then the response status should be 204 No Content
    And the bell schedule should be deleted

# Custom Data for Bell Schedule

  Scenario: An API user adds (POST) custom data to a bell schedule
    Given I create a bell schedule
    When I POST custom data to that bell schedule
    Then the response status should be 201 Created
    And the custom bell schedule data should be saved

  Scenario: An API user gets (GET) custom data for a bell schedule
    Given I create a bell schedule
    And I POST custom data to that bell schedule
    When I GET the custom data for that bell schedule
    Then the response status should be 200 OK
    And the response resource should contain expected custom data

  Scenario: An API user fully updates (PUT) the bell schedule custom data
    Given I create a bell schedule
    When I PUT the custom data for that bell schedule
    Then the response status should be 204 No Content
    And the custom bell schedule data should be saved

  Scenario: An API user deletes (DELETE) the bell schedule custom data
    Given I create a bell schedule
    And I POST custom data to that bell schedule
    When I DELETE the custom data for that bell schedule
    Then the response status should be 204 No Content
    And the custom bell schedule data should be deleted

  Scenario: An API user attempts to partially update (PATCH) the bell schedule custom data
    Given I create a bell schedule
    When I PATCH the bell schedule custom data
    Then the response status should be 405 Method Not Allowed

  # Verify that bell schedules listed are only for the appropriate ed org

  Scenario: An API user reads (GET) a list of bell schedules
    When I GET the list of bell schedules
    Then the response status should be 200 OK
     And the response resource should be a list of bell schedules
     And I should only see bell schedules for my education organization

  Scenario: An API users gets the bell schedules for a school
    Given I create a school-level bell schedule
     When I get the bell schedules for that school
     Then the result should include that bell schedule