@RALLY_US5250
Feature: Verify CRUD operations for section - (class period story aspects)

Scenario: Verify CRUD for section with section classPeriodId
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
When I POST a class period for a section
And I POST a section
Then I GET the section
When I POST a custom section
Then I GET the custom section
When I PUT a custom section
Then I GET the custom section
When I try the not supported PATCH for custom section
Then I GET the custom section
When I DELETE the custom section
Then I GET the deleted custom section
When I POST a custom section
Then I GET the custom section
Then I GET the section
When I DELETE the section
Then I GET the deleted entity
Then I GET the deleted custom section
