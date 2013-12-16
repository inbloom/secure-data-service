@RALLY_US5768

Feature: Use the APi to successfully post student data while having roles over many schools

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin setup application and realm data

  Scenario: Role with all the write rights in school can post a student with restricted data
    When I log in as "msmith"

    Given format "application/json"
    Given I create a student entity with restricted data
    When I navigate to POST "/v1/students"
    Then I should receive a return code of 403

    Given I change all SEOAs of "msmith" to the edorg "East Daybreak High"
    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right

    When I log in as "msmith"

    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201

    Then I remove the posted student

  Scenario: Role with different roles in a school can post a student
    When I log in as "jmacey"

    Given format "application/json"
    Given I create a student entity without restricted data
    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201
    Then I remove the posted student

    Given format "application/json"
    Given I create a student entity with restricted data
    When I navigate to POST "/v1/students"
    Then I should receive a return code of 403

    And I change the custom role of "Educator" to add the "WRITE_RESTRICTED" right

    When I log in as "jmacey"

    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201
    Then I remove the posted student

    Given I change all SEOAs of "jmacey" to the edorg "East Daybreak High"

    When I log in as "jmacey"

    Given format "application/json"
    Given I create a student entity with restricted data
    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201
    Then I remove the posted student