@RALLY_US5808

Feature: Use the API to successfully delete students  while having roles over many schools

  Background: Setup
    Given the testing device app key has been created
    And I import the odin setup application and realm data

  Scenario: User with one write right in one edorg and another in another, able to delete with either
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Aggregate Viewer" to add the "WRITE_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I add a SEOA for "xbell" in "District 9" as a "Leader"

    When I log in as "xbell"

    Given format "application/json"
    When I navigate to DELETE "<kate.dedrick URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<kate.dedrick URI>"
    Then I should receive a return code of 404

    When I navigate to DELETE "<shawn.taite URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<shawn.taite URI>"
    Then I should receive a return code of 404

  Scenario: User with write rights in first school and no write rights in second
    Given I remove the SEOA with role "IT Administrator" for staff "tcuyper" in "Daybreak Central High"
    And I add a SEOA for "tcuyper" in "East Daybreak High" as a "IT Administrator"

    When I log in as "tcuyper"

    Given format "application/json"
    When I navigate to DELETE "<john.johnson URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<john.johnson URI>"
    Then I should receive a return code of 404

    When I navigate to DELETE "<bert.jakeman URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200

    When I navigate to DELETE "<nate.dedrick URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

  Scenario: User with writes in school and no writes in parent LEA
    Given I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right

    When I log in as "msmith"

    Given format "application/json"
    When I navigate to DELETE "<pat.sollars URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<pat.sollars URI>"
    Then I should receive a return code of 404

    When I navigate to DELETE "<bert.jakeman URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200

    When I navigate to DELETE "<carmen.ortiz URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200

    When I navigate to DELETE "<nate.dedrick URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

  Scenario: User with writes in LEA and no writes in school (writes should trickle down to school level)
    Given I change the custom role of "Aggregate Viewer" to add the "WRITE_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right

    When I log in as "msmith"

    Given format "application/json"
    When I navigate to DELETE "<jake.bertman URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<jake.bertman URI>"
    Then I should receive a return code of 404

    When I navigate to DELETE "<herman.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<herman.ortiz URI>"
    Then I should receive a return code of 404

    When I navigate to DELETE "<nate.dedrick URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

  Scenario: User (and only that user) can delete an orphaned student they created
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right

    When I log in as "msmith"

    Given format "application/json"
    Given I create a student entity with restricted data
    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201
    When I navigate to GET the new entity
    Then I should receive a return code of 200

    When I log in as "tcuyper"

    When I attempt to delete the new entity
    Then I should receive a return code of 403

    When I log in as "msmith"

    When I attempt to delete the new entity
    Then I should receive a return code of 204
    When I navigate to GET the new student entity
    Then I should receive a return code of 404