@RALLY_DE3005
Feature: Verify CRUD operations for attendance event, part of the attendance endpoint

Scenario: Verify CRUD for attendance event on attendance endpoint for attendance events with section id
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
When I POST an attendance with an attendance event
Then I GET an attendance with an attendance event
When I PUT an attendance with an attendance event
Then I GET an attendance with an attendance event
When I PATCH an attendance's attendance events
Then I GET an attendance with an attendance event
When I DELETE an attendance with an attendance event
Then I GET the deleted entity

Scenario: Verify CRUD for attendance event on yearly attendance endpoint for attendance events with section id
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
When I POST a yearly attendance with an attendance event
Then I GET a yearly attendance with an attendance event
Then I PUT a yearly attendance with an attendance event
Then I GET a yearly attendance with an attendance event
When I PATCH a yearly attendance's attendance events
Then I GET a yearly attendance with an attendance event
Then I DELETE a yearly attendance with an attendance event
Then I GET the deleted entity
