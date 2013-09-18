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
    Then I should receive a return code of 403

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


  Scenario: Staff user with dual context in different edOrgs with restricted write right can patch some student data
    Given I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Educator" to add the "WRITE_GENERAL" right
    And I change the custom role of "Educator" to add the "WRITE_RESTRICTED" right

    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |
      | lashawn.taite   | jmacey               | East Daybreak High    | no                    |
      | jack.jackson    | jmacey               | East Daybreak High    | no                    |
    And "lashawn.taite" is not associated with any program that belongs to "jmacey"
    And "lashawn.taite" is not associated with any cohort that belongs to "jmacey"
    And "jack.jackson" is not associated with any program that belongs to "jmacey"
    And "jack.jackson" is not associated with any cohort that belongs to "jmacey"

    When I log in as "jmacey"

    When I change the field "economicDisadvantaged" to "true"
    When I navigate to PATCH "<matt.sollars URI>"
    Then I should receive a return code of 204

    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And "economicDisadvantaged" should be "true"

    When I change the field "economicDisadvantaged" to "true"
    When I navigate to PATCH "<jack.jackson URI>"
    Then I should receive a return code of 403

    When I change the field "economicDisadvantaged" to "true"
    When I navigate to PATCH "<lashawn.taite URI>"
    Then I should receive a return code of 403

    When I clear out the patch request
    When I change the field "hispanicLatinoEthnicity" to "true"
    When I navigate to PATCH "<lashawn.taite URI>"
    Then I should receive a return code of 204

    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And "hispanicLatinoEthnicity" should be "true"

    When I change the field "economicDisadvantaged" to "true"
    When I navigate to PATCH "<bert.jakeman URI>"
    Then I should receive a return code of 403

Scenario: Students in schools with multiple parents can be written to by the appropriate users
  Given I change the custom role of "Leader" to add the "WRITE_GENERAL" right

  When I log in as "rbelding"
  And I change the field "displacementStatus" to "New Displacement Status"
  When I navigate to PATCH "<yvonne.seymour URI>"
  Then I should receive a return code of 403

  Given I remove the SEOA with role "Leader" for staff "tcuyper" in "District 9"
  When I log in as "tcuyper"
  And I change the field "displacementStatus" to "New Displacement Status"
  And I navigate to PATCH "<yvonne.seymour URI>"
  Then I should receive a return code of 204
  When I navigate to GET "<yvonne.seymour URI>"
  Then I should receive a return code of 200
  And "displacementStatus" should be "New Displacement Status"

  Given I add a SEOA for "rbelding" in "District 9" as a "Leader"
  When I log in as "rbelding"
  And I change the field "displacementStatus" to "Another Displacement Status"
  And I navigate to PATCH "<yvonne.seymour URI>"
  Then I should receive a return code of 204
  When I navigate to GET "<yvonne.seymour URI>"
  Then I should receive a return code of 200
  And "displacementStatus" should be "Another Displacement Status"

  Given I add a SEOA for "xbell" in "District 31" as a "Leader"
  When I log in as "xbell"
  And I change the field "displacementStatus" to "Wow! A Displacement Status"
  And I navigate to PATCH "<yvonne.seymour URI>"
  Then I should receive a return code of 204
  When I navigate to GET "<yvonne.seymour URI>"
  Then I should receive a return code of 200
  And "displacementStatus" should be "Wow! A Displacement Status"
