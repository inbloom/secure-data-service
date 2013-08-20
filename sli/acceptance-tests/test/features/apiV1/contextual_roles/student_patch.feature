@RALLY_US5774

Feature: Use the APi to successfully patch student data while having roles over many schools

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin setup application and realm data
    And format "application/json"

  Scenario: Role with all the write rights in school can patch a student with restricted data
    When I log in as "msmith"

    When I change the field "sex" to "Male"
    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 403

    Given I change all SEOAs of "msmith" to the edorg "East Daybreak High"
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right

    When I log in as "msmith"

    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 204

    When I change the field "economicDisadvantaged" to "true"
    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 403

    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right

    When I log in as "msmith"

    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 204

    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"
    And "economicDisadvantaged" should be "true"

    When I change the field "economicDisadvantaged" to "false"
    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 204

    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "false"

    When I change the field "sex" to "Male"
    When I navigate to PATCH "<bert.jakeman URI>"
    Then I should receive a return code of 403

  Scenario: Role with hierachical write rights in school can patch a student

    And I change the custom role of "Leader" to remove the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Aggregate Viewer" to add the "WRITE_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right

    When I log in as "msmith"

    When I change the field "sex" to "Male"
    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"


    When I change the field "sex" to "Female"
    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

    When I change the field "sex" to "Male"
    When I navigate to PATCH "<bert.jakeman URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"


    When I change the field "sex" to "Female"
    When I navigate to PATCH "<bert.jakeman URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

    When I change the field "economicDisadvantaged" to "true"
    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 204

    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "true"

    When I change the field "economicDisadvantaged" to "false"
    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 204

    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "false"

    When I navigate to PATCH "<bert.jakeman URI>"
    Then I should receive a return code of 403


  Scenario: Staff with restricted write right in one school can not patch general student data in either school
    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Educator" to add the "WRITE_GENERAL" right

    When I log in as "rbelding"
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | rbelding             | Daybreak Central High | yes                   |
      | lashawn.taite   | rbelding             | Daybreak Central High | no                    |
      | bert.jakeman    | rbelding             | Daybreak Central High | no                    |
    And "lashawn.taite" is not associated with any program that belongs to "rbelding"
    And "lashawn.taite" is not associated with any cohort that belongs to "rbelding"
    And "bert.jakeman" is not associated with any program that belongs to "rbelding"
    And "bert.jakeman" is not associated with any cohort that belongs to "rbelding"

    When I change the field "sex" to "Female"
    When I navigate to PATCH "<jack.jackson URI>"
    Then I should receive a return code of 403

    When I change the field "sex" to "Female"
    When I navigate to PATCH "<carmen.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And "sex" should be "Female"

    When I change the field "sex" to "Male"
    When I navigate to PATCH "<carmen.ortiz URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And "sex" should be "Male"

    When I navigate to PATCH "<bert.jakeman URI>"
    Then I should receive a return code of 204

  Scenario: Staff with restricted write right in one school can patch restricted student data in one school
    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Educator" to add the "WRITE_GENERAL" right

    When I log in as "rbelding"
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | rbelding             | Daybreak Central High | yes                   |
      | lashawn.taite   | rbelding             | Daybreak Central High | no                    |
      | bert.jakeman    | rbelding             | Daybreak Central High | no                    |
      | jack.jackson    | rbelding             | East Daybreak High    | no                    |
    And "lashawn.taite" is not associated with any program that belongs to "rbelding"
    And "lashawn.taite" is not associated with any cohort that belongs to "rbelding"
    And "bert.jakeman" is not associated with any program that belongs to "rbelding"
    And "bert.jakeman" is not associated with any cohort that belongs to "rbelding"

    When I change the field "economicDisadvantaged" to "true"
    When I navigate to PATCH "<jack.jackson URI>"
    Then I should receive a return code of 204

    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "true"

    When I change the field "economicDisadvantaged" to "false"
    When I navigate to PATCH "<jack.jackson URI>"
    Then I should receive a return code of 204

    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "false"

    When I navigate to PATCH "<carmen.ortiz URI>"
    Then I should receive a return code of 403

    When I navigate to PATCH "<bert.jakeman URI>"
    Then I should receive a return code of 403