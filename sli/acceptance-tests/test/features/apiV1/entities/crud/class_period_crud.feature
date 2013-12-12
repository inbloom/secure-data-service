@RALLY_US5250
Feature: Verify CRUD operations for class period

Scenario: Verify CRUD for class period
Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
When I POST a class period
Then I GET the class period
When I try the not supported POST for the class period id endpoint
When I try the not supported PUT for the class period
When I try the not supported PATCH for the class period
When I try the not supported PUT for the class period list endpoint
When I try the not supported PATCH for the class period list endpoint
When I try the not supported DELETE for the class period list endpoint
When I POST a custom class period
Then I GET the custom class period
When I PUT a custom class period
Then I GET the custom class period
When I try the not supported PATCH for custom class period
When I DELETE the custom class period
Then I GET the deleted custom class period
When I POST a custom class period
Then I GET the custom class period
When I DELETE the class period
Then I GET the deleted entity
Then I GET the deleted custom class period

Scenario: Verify CRUD for class period
  Given I am logged in using "rbraverman" "rbraverman1234" to realm "IL"
  Then I GET the class periods
  And the result contains the only class period in the context of the user
  Then I GET the class periods using education organization
  And the result contains the only class period in the context of the user