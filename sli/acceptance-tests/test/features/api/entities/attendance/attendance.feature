@wip
Feature: As an SLI application, I want to be able to perform CRUD on attendance.

Background: Logged in as a super-user and using the small data set
  Given I am logged in using "demo" "demo1234"
  Given I have access to all entities
  Given format "application/json"

Scenario: Create a new 'attendance' entity
  When I POST a new 'attendance' entity
  Then I should receive a new entity ID
  When I GET the new 'attendance' entity
  Then the response should match the POSTed 'attendance' entity

