@RALLY_US5250
Feature: As a staff user, I was able to perform Read/Write operation on classPeriod and bellSchedule entities with READ_PUBLIC and  WRITE_PUBLIC right.

  Background: Setup for the tests
    Given I import the odin setup application and realm data
    And the testing device app key has been created

  Scenario:
    Given the following collections are empty in datastore:
      | collectionName                            |
      | classPeriod                               |
      | bellSchedule                              |

  Scenario Outline: Ensure POST cannot be performed on all public entities without WRITE_PUBLIC right

    Given I change the custom role of "Leader" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Leader" to remove the "WRITE_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "WRITE_PUBLIC" right
    And I log in as "rbraverman"
    And parameter "limit" is "0"
    Given entity type "<ENTITY TYPE>"
    When I POST a "<ENTITY TYPE>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY TYPE                | ENTITY URI                  |
    | classPeriod                | classPeriods                |
    | bellSchedule               | bellSchedules               |

  Scenario Outline: Ensure POST can be performed on all public entities with WRITE_PUBLIC right

    Given I change the custom role of "Leader" to add the "READ_PUBLIC" right
    Given I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    Given I change the custom role of "Educator" to add the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to add the "WRITE_PUBLIC" right
    And I log in as "rbraverman"
    And parameter "limit" is "0"
    Given entity type "<ENTITY TYPE>"
    When I POST a "<ENTITY TYPE>"
    Then I should receive a return code of 201

  Examples:
    | ENTITY TYPE                | ENTITY URI                  |
    | classPeriod                | classPeriods                |
    | bellSchedule               | bellSchedules               |


  Scenario Outline: Ensure GET can be performed on all public entities with READ_PUBLIC right
    Given I change the custom role of "Leader" to add the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to add the "READ_PUBLIC" right
    And I log in as "rbraverman"
    And parameter "limit" is "0"
    Given entity type "<ENTITY TYPE>"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 200
    And I should see all global entities

  Examples:
    | ENTITY TYPE                | ENTITY URI                  |
    | classPeriod                | classPeriods                |
    | bellSchedule               | bellSchedules               |

Scenario Outline: Ensure GET can NOT be performed on any public entities without READ_PUBLIC right
    Given I change the custom role of "Leader" to remove the "READ_PUBLIC" right
    Given I change the custom role of "Educator" to remove the "READ_PUBLIC" right
    And I log in as "rbraverman"
    And parameter "limit" is "0"
    Given entity type "<ENTITY TYPE>"
    When I navigate to GET "/v1/<ENTITY URI>"
    Then I should receive a return code of 403

  Examples:
    | ENTITY TYPE                | ENTITY URI                  |
    | classPeriod                | classPeriods                |
    | bellSchedule               | bellSchedules               |
