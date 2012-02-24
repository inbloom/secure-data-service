@wip
Feature: As an SLI application, I want to be able to perform CRUD on attendance.

Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234"
  Given I have access to all entities
  Given format "application/json"
  Given I am testing the 'attendance' entity

Scenario: Create a new entity
  When I POST a new entity
  Then I should receive a new entity ID
  When I GET the new entity
  Then the response should match the POSTed entity

Scenario: Read an entity by id
  When I GET an existing entity
  Then the response should contain the expected entity

Scenario: Update an existing entity
  When I GET an existing entity
  Then the response should contain the expected entity
  When I update fields in the existing entity
  And I PUT the updated entity
  When I GET the existing entity
  Then the response should contain the updated fields

Scenario: Delete an existing entity
  When I DELETE an existing entity
  Then I should receive a return code of 204
  When I GET the deleted entity
  Then I should receive a return code of 404

