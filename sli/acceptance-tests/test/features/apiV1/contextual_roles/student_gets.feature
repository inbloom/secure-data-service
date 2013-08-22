@RALLY_US5765

Feature: Use the APi to successfully get student data while having roles over many schools

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin setup application and realm data

  Scenario Outline: Get a student's data using various staff-student combination
    Given the only SEOA for "rbraverman" is as a "Educator" in "District 9"
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | linda.kim            | Daybreak Central High | yes                   |
      | carmen.ortiz    | rbraverman           | Daybreak Central High | yes                   |
      | bert.jakeman    | linda.kim            | Daybreak Central High | yes                   |
      | lashawn.taite   | linda.kim            | Daybreak Central High | no                    |
      | carmen.ortiz    | linda.kim            | Daybreak Bayside High | yes                   |
      | nate.dedrick    | linda.kim            | Daybreak Bayside High | yes                   |
      | mu.mcneill      | linda.kim            | Daybreak Bayside High | yes                   |
      | matt.sollars    | rbraverman           | East Daybreak High    | yes                   |
    And "lashawn.taite" is not associated with any program that belongs to "linda.kim"
    And "lashawn.taite" is not associated with any cohort that belongs to "linda.kim"

    When I log in as "<staff>"

    Given format "application/json"
    When I navigate to GET "<student's URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response <should see restricted?> have restricted student data

    Examples:
    | staff       | student's URI           | should see restricted? |
    | sbantu      | <lashawn.taite URI>     | should                 |
    | sbantu      | <matt.sollars URI>      | should                 |
    | rbraverman  | <matt.sollars URI>      | should not             |
    | rbraverman  | <carmen.ortiz URI>      | should not             |
    | jstevenson  | <matt.sollars URI>      | should                 |
    | jstevenson  | <carmen.ortiz URI>      | should                 |
    | jstevenson  | <lashawn.taite URI>     | should                 |
    | jstevenson  | <bert.jakeman URI>      | should                 |
    | jstevenson  | <nate.dedrick URI>      | should                 |
    | jstevenson  | <mu.mcneill URI>        | should                 |
    | linda.kim   | <carmen.ortiz URI>      | should not             |
    | linda.kim   | <bert.jakeman URI>      | should not             |
    | linda.kim   | <nate.dedrick URI>      | should not             |
    | linda.kim   | <mu.mcneill URI>        | should not             |

  Scenario: Staff with multiple roles in edOrg hierarchy
    Given the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |
      | jack.jackson    | jmacey               | East Daybreak High    | no                    |
      | lashawn.taite   | jmacey               | East Daybreak High    | no                    |
    And "jack.jackson" is not associated with any program that belongs to "jmacey"
    And "jack.jackson" is not associated with any cohort that belongs to "jmacey"
    And "lashawn.taite" is not associated with any program that belongs to "jmacey"
    And "lashawn.taite" is not associated with any cohort that belongs to "jmacey"

    When I log in as "jmacey"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

    Given format "application/json"
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

    Given I remove the SEOA with role "Leader" for staff "jmacey" in "District 9"

    When I log in as "jmacey"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data


    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 403

    Given I expire all section associations that "matt.sollars" has with "jmacey"
    And "matt.sollars" is not associated with any program that belongs to "jmacey"
    And "matt.sollars" is not associated with any cohort that belongs to "jmacey"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403

  Scenario: Staff with multiple roles in edOrg hierarchy, rights are unionized
    Given the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |
      | jack.jackson    | jmacey               | East Daybreak High    | no                    |
      | lashawn.taite   | jmacey               | East Daybreak High    | no                    |
    And "jack.jackson" is not associated with any program that belongs to "jmacey"
    And "jack.jackson" is not associated with any cohort that belongs to "jmacey"
    And "lashawn.taite" is not associated with any program that belongs to "jmacey"
    And "lashawn.taite" is not associated with any cohort that belongs to "jmacey"
    And I change the custom role of "Leader" to remove the "READ_GENERAL" right

    When I log in as "jmacey"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should not have general student data
    And the response should have restricted student data
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should not have general student data
    And the response should have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should not have general student data
    And the response should have restricted student data
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And the response should not have general student data
    And the response should have restricted student data

  Scenario: Staff with multiple roles where one of the roles is missing context rights
    Given the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |
      | lashawn.taite   | jmacey               | East Daybreak High    | yes                    |
    And I change the custom role of "Leader" to remove the "STAFF_CONTEXT" right

    When I log in as "jmacey"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

    Given I change the custom role of "Leader" to add the "STAFF_CONTEXT" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right
    And I change the custom role of "Educator" to remove the "TEACHER_CONTEXT" right
    And I change the custom role of "Educator" to add the "READ_RESTRICTED" right

    When I log in as "jmacey"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

  Scenario: Student belongs to different schools
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

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

    Given I change the type of "rbelding" to "staff"
    When I log in as "rbelding"

    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

    Given I remove the teacherSectionAssociation for "rbelding"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 403

  Scenario: Aggregate Viewer can not access student data
    When I log in as "msmith"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    And the response should have general student data
    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 403

    Given I change all SEOAs of "msmith" to the edorg "East Daybreak High"
    And I change the custom role of "Aggregate Viewer" to add the "READ_RESTRICTED" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right

    When I log in as "msmith"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

    Given I remove the SEOA with role "Leader" for staff "msmith" in "East Daybreak High"
    And I change the custom role of "Aggregate Viewer" to remove the "READ_RESTRICTED" right

    When I log in as "msmith"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403

    Given format "application/json"
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403

 Scenario: Staff access student with admin role
   Given I change the type of "Leader" admin role to "true"
   Given I change the type of "Educator" admin role to "true"
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

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

    Given I change the type of "rbelding" to "staff"
    When I log in as "rbelding"

    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

 Scenario: Student belongs to schools in different LEAs
    When I log in as "mgonzales"

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 200

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

 Scenario: User gets data based on roles in SEOA, even if user has more roles defined in IDP
   Given I have an open web browser
   When I navigate to the API authorization endpoint with my client ID
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "xbell" "xbell1234" for the "Simple" login page
   Then I should receive a json response containing my authorization code
   When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
   Then I should receive a json response containing my authorization token
   And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

 Scenario: User gets additional data of new role if a seoa is added to match additional role defined in IDP
   Given I add a SEOA for "xbell" in "District 9" as a "Leader"
   And I have an open web browser
   When I navigate to the API authorization endpoint with my client ID
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "xbell" "xbell1234" for the "Simple" login page
   Then I should receive a json response containing my authorization code
   When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
   Then I should receive a json response containing my authorization token
   And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

 Scenario: Teacher can only access students associated with her/him.
    When I log in as "linda.kim"

    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | linda.kim            | Daybreak Central High | yes                   |
      | bert.jakeman    | linda.kim            | Daybreak Central High | yes                   |
      | lashawn.taite   | linda.kim            | Daybreak Central High | no                    |
      | carmen.ortiz    | linda.kim            | Daybreak Bayside High | no                    |
      | nate.dedrick    | linda.kim            | Daybreak Bayside High | no                    |
      | mu.mcneill      | linda.kim            | Daybreak Bayside High | yes                   |
    And "lashawn.taite" is not associated with any program that belongs to "linda.kim"
    And "lashawn.taite" is not associated with any cohort that belongs to "linda.kim"
    And "nate.dedrick" is not associated with any program that belongs to "linda.kim"
    And "nate.dedrick" is not associated with any cohort that belongs to "linda.kim"

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 403

    Given I remove the SEOA with role "Educator" for staff "linda.kim" in "Daybreak Bayside High"

    When I log in as "linda.kim"

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 403

    Given I remove the school association with student "bert.jakeman" in tenant "Midgar"
    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403

    Given I remove all SEOAs for "linda.kim" in tenant "Midgar"
    Given I add a SEOA for "linda.kim" in "IL-DAYBREAK" as a "Educator"

    When I log in as "linda.kim"

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

 Scenario: Educators can only access students associated with them
    Given the only SEOA for "rbraverman" is as a "Educator" in "District 9"
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | rbraverman           | Daybreak Central High | yes                   |
      | matt.sollars    | rbraverman           | East Daybreak High    | no                    |
    And "matt.sollars" is not associated with any program that belongs to "rbraverman"
    And "matt.sollars" is not associated with any cohort that belongs to "rbraverman"

    When I log in as "rbraverman"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given I expire all section associations that "carmen.ortiz" has with "rbraverman"
    And "carmen.ortiz" is not associated with any program that belongs to "rbraverman"
    And "carmen.ortiz" is not associated with any cohort that belongs to "rbraverman"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403

 Scenario: GET lists of students for a staff member with multiple roles in an edorg heirarchy
    Given parameter "limit" is "0"
    When I log in as "msmith"

    When I navigate to GET "/v1/students"
    Then I should receive a return code of 200
    And the response should have the following students
    | student         |
    | matt.sollars    |
    | jack.jackson    |
    | lashawn.taite   |
    And the response should not have the following students
    | student         |
    | bert.jakeman    |
    | carmen.ortiz    |
    | nate.dedrick    |
    | mu.mcneill      |

 Scenario: GET lists of students for a staff member with multiple roles in an edorg hierarchy
    Given I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I add a SEOA for "msmith" in "Daybreak Central High" as a "Aggregate Viewer"
    And parameter "limit" is "0"
    When I log in as "msmith"

    When I navigate to GET "/v1/students"
    Then I should receive a return code of 200
    And the response should have the following students
      | student         |
      | matt.sollars    |
      | jack.jackson    |
      | lashawn.taite   |
      | bert.jakeman    |
      | carmen.ortiz    |
    And the response should not have the following students
      | student         |
      | nate.dedrick    |
      | mu.mcneill      |

 Scenario: GET lists of students for a staff member with multiple roles
   Given the following student section associations in Midgar are set correctly
     | student         | teacher              | edorg                 | enrolledInAnySection? |
     | carmen.ortiz    | rbelding             | Daybreak Central High | yes                   |
     | lashawn.taite   | rbelding             | Daybreak Central High | no                    |
     | bert.jakeman    | rbelding             | Daybreak Central High | no                    |
   And "lashawn.taite" is not associated with any program that belongs to "rbelding"
   And "lashawn.taite" is not associated with any cohort that belongs to "rbelding"
   And "bert.jakeman" is not associated with any program that belongs to "rbelding"
   And "bert.jakeman" is not associated with any cohort that belongs to "rbelding"
   And parameter "limit" is "0"

   When I log in as "rbelding"

   When I navigate to GET "/v1/students"
   Then I should receive a return code of 200
   #TODO: After US5787, the returned list should now have matt.sollars, jack.jackson, and lashawn.taite
   And the response should have the following students
      | student         |
      | carmen.ortiz    |
      | matt.sollars    |
      | jack.jackson    |
      | lashawn.taite   |
   And the response should not have the following students
      | student         |
      | bert.jakeman    |
      | nate.dedrick    |
      | mu.mcneill      |

  Scenario: GET lists of students for a staff member with multiple roles in the same edorg
    Given I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I change the custom role of "Leader" to remove the "READ_GENERAL" right
    And I change all SEOAs of "xbell" to the edorg "Daybreak Bayside High"
    And I add a SEOA for "xbell" in "Daybreak Bayside High" as a "Leader"
    And parameter "limit" is "0"
    When I log in as "xbell"

    When I navigate to GET "/v1/students"
    Then I should receive a return code of 200
    And the response should have the following students
      | student         |
      | nate.dedrick    |
      | mu.mcneill      |
      | carmen.ortiz    |
    And "nate.dedrick" in the response should have restricted data
    And "mu.mcneill" in the response should have restricted data
    And "carmen.ortiz" in the response should have restricted data
    And the response should not have the following students
      | student         |
      | matt.sollars    |
      | jack.jackson    |
      | lashawn.taite   |
      | bert.jakeman    |

 Scenario: GET lists of students for an educator in multiple schools
    Given the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | linda.kim            | Daybreak Central High | yes                   |
      | bert.jakeman    | linda.kim            | Daybreak Central High | yes                   |
      | lashawn.taite   | linda.kim            | Daybreak Central High | no                    |
      | carmen.ortiz    | linda.kim            | Daybreak Bayside High | yes                   |
      | nate.dedrick    | linda.kim            | Daybreak Bayside High | yes                   |
      | mu.mcneill      | linda.kim            | Daybreak Bayside High | no                    |
    And "lashawn.taite" is not associated with any program that belongs to "linda.kim"
    And "lashawn.taite" is not associated with any cohort that belongs to "linda.kim"
    And "mu.mcneill" is not associated with any program that belongs to "linda.kim"
    And "mu.mcneill" is not associated with any cohort that belongs to "linda.kim"
    And parameter "limit" is "0"

    When I log in as "linda.kim"

    When I navigate to GET "/v1/students"
    Then I should receive a return code of 200
    And the response should have the following students
      | student         |
      | carmen.ortiz    |
      | bert.jakeman    |
      | nate.dedrick    |
    And the response should not have the following students
      | student         |
      | matt.sollars    |
      | lashawn.taite   |
      | jack.jackson    |
      | mu.mcneill      |

  Scenario: GET list of students for an educator associated with the LEA, instead of the schools
    Given the only SEOA for "rbraverman" is as a "Educator" in "District 9"
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | rbraverman           | Daybreak Central High | yes                   |
      | bert.jakeman    | rbraverman           | Daybreak Central High | no                    |
      | lashawn.taite   | rbraverman           | Daybreak Central High | no                    |
      | matt.sollars    | rbraverman           | East Daybreak High    | yes                   |
      | jack.jackson    | rbraverman           | East Daybreak High    | no                    |
      | lashawn.taite   | rbraverman           | East Daybreak High    | no                    |
    And "jack.jackson" is not associated with any program that belongs to "rbraverman"
    And "jack.jackson" is not associated with any cohort that belongs to "rbraverman"
    And "lashawn.taite" is not associated with any program that belongs to "rbraverman"
    And "lashawn.taite" is not associated with any cohort that belongs to "rbraverman"
    And "bert.jakeman" is not associated with any program that belongs to "rbraverman"
    And "bert.jakeman" is not associated with any cohort that belongs to "rbraverman"
    And parameter "limit" is "0"

    When I log in as "rbraverman"

    When I navigate to GET "/v1/students"
    Then I should receive a return code of 200
    And the response should have the following students
      | student         |
      | carmen.ortiz    |
      | matt.sollars    |
    And the response should not have the following students
      | student         |
      | lashawn.taite   |
      | jack.jackson    |
      | bert.jakeman    |
      | mu.mcneill      |
      | nate.dedrick    |
  
  Scenario: User has role with no context rights
    Given I change the custom role of "Educator" to remove the "TEACHER_CONTEXT" right
    When I log in as "linda.kim"
    And the following student section associations in Midgar are set correctly
       | student         | teacher              | edorg                 | enrolledInAnySection? |
       | carmen.ortiz    | linda.kim            | Daybreak Central High | yes                   |

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403

  Scenario: User has role with both context rights
    Given I change the custom role of "Educator" to add the "STAFF_CONTEXT" right
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | linda.kim            | Daybreak Central High | no                    |
      | bert.jakeman    | linda.kim            | Daybreak Central High | no                    |
      | mu.mcneill      | linda.kim            | Daybreak Bayside High | no                    |
    And "mu.mcneill" is not associated with any program that belongs to "linda.kim"
    And "mu.mcneill" is not associated with any cohort that belongs to "linda.kim"
    And "bert.jakeman" is not associated with any program that belongs to "linda.kim"
    And "bert.jakeman" is not associated with any cohort that belongs to "linda.kim"
    And "carmen.ortiz" is not associated with any program that belongs to "linda.kim"
    And "carmen.ortiz" is not associated with any cohort that belongs to "linda.kim"

    When I log in as "linda.kim"

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

  Scenario: User has roles where teacher contect role has higher rights than the staff context role
    Given I change the custom role of "Educator" to add the "READ_RESTRICTED" right
    And I change the custom role of "Leader" to remove the "READ_RESTRICTED" right
    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |
      | jack.jackson    | jmacey               | East Daybreak High    | no                    |
      | lashawn.taite   | jmacey               | East Daybreak High    | no                    |
    And "jack.jackson" is not associated with any program that belongs to "jmacey"
    And "jack.jackson" is not associated with any cohort that belongs to "jmacey"
    And "lashawn.taite" is not associated with any program that belongs to "jmacey"
    And "lashawn.taite" is not associated with any cohort that belongs to "jmacey"

    When I log in as "jmacey"

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

    Given format "application/json"
    When I navigate to GET "<jack.jackson URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
