@RALLY_US5626
Feature: Changes to the data result in deltas recorded in the delta collection

  Scenario: Deltas after an ingestion job
    Given all collections are empty
    And I have an empty delta collection
    When I run a small ingestion job
    Then I see deltas for each educationOrganization update operation
    When I run a delete ingestion job
    Then I see deltas for each educationOrganization delete operation


  Scenario: POST/PUT multiple updates to the same entity, verify one delta per entity
    Given I clean the bulk extract file system and database
    And I log into "SDK Sample" with a token of "jstevenson", a "IT Administrator" for "IL-DAYBREAK" for "IL-Daybreak" in tenant "Midgar", that lasts for "300" seconds
    And format "application/json"
    # CREATE parent entity via POST
    And deltas collection should have "0" records
    When I POST a "newParentFather" of type "parent"
    Then I should receive a return code of 201
    When I PUT the "loginId" for a "newParentFather" entity to "my_shining_new_login@goodstuff.com" at "parents/41f42690a7c8eb5b99637fade00fc72f599dab07_id"
    Then I should receive a return code of 204
    And deltas collection should have "1" records
    Then I DELETE an "parent" of id "41f42690a7c8eb5b99637fade00fc72f599dab07_id"
    And deltas collection should have "1" records
