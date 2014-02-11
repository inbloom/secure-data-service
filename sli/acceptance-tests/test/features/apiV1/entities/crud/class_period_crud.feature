@class_period
Feature:
  As an API user
  In order to maintain a school's class period schedule
  I want to be able create, read, and delete class periods

  Background:
    Given I am logged in as a tenant-level IT Administrator
    And I want to use format "application/vnd.slc+json"

  Scenario: An API user can create (POST) a new class period
    When I POST a new class period
    Then the response status should be 201 Created
    And the response location header should link to the new class period
    And the class period should be saved

  Scenario: An API user attempts to POST to a class period resource
    Given I create a class period
    When I POST to that class period
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user reads (GET) a class period
    Given I create a class period
    When I GET that class period
    Then the response status should be 200 OK
    And the response resource should have an id
    And the response resource entity type should be "classPeriod"
    And the response resource should have HATEOAS links for class period
    And the response resource should contain expected class period data

  Scenario: An API user reads (GET) a list of class periods
    When I GET the list of class periods
    Then the response status should be 200 OK
    And the response resource should contain multiple class periods

  Scenario: An API user attempts to fully update (PUT) a class period resource
    Given I create a class period
    When I PUT that class period
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user attempts to partially update (PATCH) a class period resource
    Given I create a class period
    When I PATCH that class period
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user attempts to fully update (PUT) the class period list resource
    When I PUT the class period list
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user attempts to partially update (PATCH) the class period list resource
    When I PATCH the class period list
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user attempts to delete (DELETE) the class period list resource
    When I DELETE the class period list
    Then the response status should be 405 Method Not Allowed

  Scenario: An API user deletes (DELETE) a class period
    Given I create a class period
    When I DELETE that class period
    Then the response status should be 204 No Content
    And the class period should be deleted

# Custom Data for a class period

  Scenario: An API user adds (POST) custom data to a class period
    Given I create a class period
    When I POST custom data to that class period
    Then the response status should be 201 Created
    And the custom class period data should be saved

  Scenario: An API user gets (GET) custom data for a class period
    Given I create a class period
    And I POST custom data to that class period
    When I GET the custom data for that class period
    Then the response status should be 200 OK
    And the response resource should contain expected custom data

  Scenario: An API user fully updates (PUT) the class period custom data
    Given I create a class period
    When I PUT the custom data for that class period
    Then the response status should be 204 No Content
    And the custom class period data should be saved

  Scenario: An API user deletes (DELETE) the class period custom data
    Given I create a class period
    And I POST custom data to that class period
    When I DELETE the custom data for that class period
    Then the response status should be 204 No Content
    And the custom class period data should be deleted

  Scenario: An API user attempts to partially update (PATCH) the class period custom data
    Given I create a class period
    When I PATCH the class period custom data
    Then the response status should be 405 Method Not Allowed

  # Verify restricted lists

  Scenario: An Educator gets the list of class periods for her education organizations
    Given I am logged in as a school-level Educator
    When I GET the list of class periods
    Then the results should contain only the class periods for my education organization