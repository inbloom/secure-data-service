@smoke @RALLY_US209 @RALLY_US210
Feature: As an SLI application I want to restrict user's access to restricted data
  That means the user should not be able to access/deduce any information about those data

Background:
  Given format "application/json"

Scenario: Linda Kim querying on the restricted fields
  Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
  When I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response
  Given parameter "economicDisadvantaged" is "false"
  And I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 400
  Given parameter "schoolFoodServicesEligibility" is "Reduced price"
  When I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 400

Scenario: Rick Rogers querying on the restricted fields
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
  And parameter "economicDisadvantaged" is "true"
  And parameter "schoolFoodServicesEligibility" is "Free"
  When I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 200
  And I should see that "entityType" is "student" in the JSON response
  And I should see that "economicDisadvantaged" is "true" in the JSON response
  And I should see that "schoolFoodServicesEligibility" is "Free" in the JSON response

Scenario: Rick Rogers querying on the restricted fields - incorrect values
  Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
  And parameter "economicDisadvantaged" is "false"
  When I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 404
  Given parameter "schoolFoodServicesEligibility" is "Reduced price"
  When I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 404
  
Scenario:  As a Aggregate Viewer I should see my own information
  Given I am logged in using "msmith" "msmith1234" to realm "IL"
  When I navigate to GET "/v1/staff/07294225-e6bc-44c9-97d7-2fff22e0de22"
  Then I should receive a return code of 200
  And I should see my personal information
  
  When I navigate to GET "/v1/staffEducationOrgAssignmentAssociations/c7be3645-8044-0ad0-8087-5d8356e09143"
  Then I should receive a return code of 200
  And I should see my personal information
  When I make an API call to get "<'MARVIN MILLER'>"
  Then I should receive a return code of 403
