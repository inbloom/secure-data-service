@section
Feature:
  As an API user
  In order to maintain a school's section
  I want to be able create, read, and update and delete sections

  Background:
    Given I am logged in as a tenant-level IT Administrator
    And I want to use format "application/vnd.slc+json"

  Scenario: An API user can create (POST) a new section
    When I POST a new section
    Then the response status should be 201 Created
    And the response location header should link to the new section
    And the section should be saved

  Scenario: An API user reads (GET) a section
    Given I create a section
    When I GET that section
    Then the response status should be 200 OK
    And the response resource should have an id
    And the response resource entity type should be "section"
    And the response resource should have HATEOAS links for section
    And the response resource should contain expected section data

  Scenario: An API user deletes (DELETE) a section
    Given I create a section
    When I DELETE that section
    Then the response status should be 204 No Content
    And the section should be deleted

## TODO: Verify if PUT of section is a valid use case (this currently returns 400 Bad Request)
#  Scenario: An API user fully updates (PUT) a section
#    Given I create a section
#    When I PUT that section
#    Then the response status should be 204 No Content
#    And the section should be updated

## TODO: Verify if PATCH of section is a valid use case (this currently returns 400 Bad Request)
#  Scenario: An API user partially updates (PATCH) a section
#    Given I create a section
#    When I PATCH that section
#    Then the response status should be 204 No Content
#    And the section should be updated


# Custom Data for a section

  Scenario: An API user adds (POST) custom data to a section
    Given I create a section
    When I POST custom data to that section
    Then the response status should be 201 Created
    And the custom section data should be saved

  Scenario: An API user gets (GET) custom data for a section
    Given I create a section
    And I POST custom data to that section
    When I GET the custom data for that section
    Then the response status should be 200 OK
    And the response resource should contain expected custom data

  Scenario: An API user fully updates (PUT) the section custom data
    Given I create a section
    When I PUT the custom data for that section
    Then the response status should be 204 No Content
    And the custom section data should be saved

  Scenario: An API user deletes (DELETE) the section custom data
    Given I create a section
    And I POST custom data to that section
    When I DELETE the custom data for that section
    Then the response status should be 204 No Content
    And the custom section data should be deleted

  Scenario: An API user attempts to partially update (PATCH) the section custom data
    Given I create a section
    When I PATCH the section custom data
    Then the response status should be 405 Method Not Allowed