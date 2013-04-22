@RALLY_US5626
Feature: Changes to the data result in deltas recorded in the delta collection

  Scenario: Deltas after an ingestion job
    Given all collections are empty
    And I have an empty delta collection
    When I run a small ingestion job
    Then I see deltas for each educationOrganization update operation
    #When I run a delete ingestion job
    #Then I see deltas for each educationOrganization delete operation
