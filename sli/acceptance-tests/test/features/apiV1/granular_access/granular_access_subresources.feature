@RALLY_US4214
@wip
Feature: As an SLI API, I want to be able to provide granular access to data for sub-resources.
  This means the user is able to request all the data within a school year range.

  Background: Use JSON format
    Given format "application/json"

  Scenario: Applying date range on sub-resources - staff
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And parameter "limit" is "0"
    When I GET the sub-resources that are time-sensitive
    Then I should receive a return code of 200 for all requests
    Then I should receive correct counts without date range
    Given parameter "schoolYears" is "2010-2011"
    When I GET the sub-resources that are time-sensitive
    Then I should receive a return code of 200 for all requests
    Then I should receive correct counts with date range

  Scenario: Applying date range on time-insensitive sub-resources
    Given I am logged in using "rrogers" "rrogers1234" to realm "IL"
    And parameter "schoolYears" is "2010-2011"
    And parameter "limit" is "0"
    When I GET the sub-resources that are time-insensitive
    Then I should receive a return code of 400 for all requests
    # TODO: update error message
    And the error message should say "NOOOOOOOOOOOOOOO!" for all requests
