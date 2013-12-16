@RALLY_US5250
Feature: Verify CRUD operations for bell schedule

@wip
Scenario: Verify CRUD for attendance event on attendance endpoint for attendance events with section id
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
When I POST a bell schedule
Then I GET the bell schedule
When I try the not supported POST for the bell schedule id endpoint
When I PATCH the bell schedule
When I PUT the bell schedule
Then I GET the bell schedule
When I try the not supported PUT for the bell schedule list endpoint
When I try the not supported PATCH for the bell schedule list endpoint
When I try the not supported DELETE for the bell schedule list endpoint
When I POST a custom bell schedule
Then I GET the custom bell schedule
When I PUT a custom bell schedule
Then I GET the custom bell schedule
When I try the not supported PATCH for custom bell schedule
Then I GET the custom bell schedule
When I DELETE the custom bell schedule
Then I GET the deleted custom bell schedule
When I POST a custom bell schedule
Then I GET the custom bell schedule
When I DELETE the bell schedule
Then I GET the deleted entity
Then I GET the deleted custom bell schedule
