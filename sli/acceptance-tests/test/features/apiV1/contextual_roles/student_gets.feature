@RALLY_5765
Feature: Use the APi to successfully get student data while having roles over many schools

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin-local-setup application and realm data
    And I have an open web browser

  @wip
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

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "<staff>" "<password>" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<student's URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response <should see restricted?> have restricted student data

    Examples:
    | staff       | password                  | student's URI           | should see restricted? |
    | sbantu      | sbantu1234                | <lashawn.taite URI>     | should                 |
    | sbantu      | sbantu1234                | <matt.sollars URI>      | should                 |
    | rbraverman  | rbraverman1234            | <matt.sollars URI>      | should not             |
    | rbraverman  | rbraverman1234            | <carmen.ortiz URI>      | should not             |
    | jstevenson  | jstevenson1234            | <matt.sollars URI>      | should                 |
    | jstevenson  | jstevenson1234            | <carmen.ortiz URI>      | should                 |
    | jstevenson  | jstevenson1234            | <lashawn.taite URI>     | should                 |
    #| jstevenson  | jstevenson1234            | <yishai.sokoll URI>     | should                 |
    | jstevenson  | jstevenson1234            | <bert.jakeman URI>      | should                 |
    | jstevenson  | jstevenson1234            | <nate.dedrick URI>      | should                 |
    | jstevenson  | jstevenson1234            | <mu.mcneill URI>        | should                 |
    | linda.kim   | linda.kim1234             | <carmen.ortiz URI>      | should not             |
    | linda.kim   | linda.kim1234             | <bert.jakeman URI>      | should not             |
    | linda.kim   | linda.kim1234             | <nate.dedrick URI>      | should not             |
    | linda.kim   | linda.kim1234             | <mu.mcneill URI>        | should not             |

  @wip
  Scenario: Staff with multiple roles in edOrg hierarchy
    Given the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | matt.sollars    | jmacey               | East Daybreak High    | yes                   |
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should have restricted student data

  #Commenting out since we do not support both staff and teacher context for a user(teacher)
    #When I navigate to GET "<carmen.ortiz URI>"
    #Then I should receive a return code of 200
    #And the response should have general student data
    #And the response should have restricted student data
    #When I navigate to GET "<lashawn.taite URI>"
    #Then I should receive a return code of 200
    #And the response should have general student data
    #And the response should have restricted student data
    #When I navigate to GET "<bert.jakeman URI>"
    #Then I should receive a return code of 200
    #And the response should have general student data
    #And the response should have restricted student data

    Given I remove the SEOA with role "Leader" for staff "jmacey" in "District 9"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

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

  @wip
  Scenario: Student belongs to different schools
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "rbelding" "rbelding1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    And the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | carmen.ortiz    | rbelding             | Daybreak Central High | yes                   |
      | lashawn.taite   | rbelding             | Daybreak Central High | no                   |

    Given format "application/json"
    #When I navigate to GET "<matt.sollars URI>"
    #Then I should receive a return code of 200
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    And the response should have general student data
    And the response should not have restricted student data
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403

    Given I remove the teacherSectionAssociation for "rbelding"

    Given format "application/json"
    #When I navigate to GET "<matt.sollars URI>"
    #Then I should receive a return code of 200
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403
    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403
    When I navigate to GET "<mu.mcneill URI>"
    Then I should receive a return code of 403

  @wip
  Scenario: Leader can access restricted data
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "sbantu" "sbantu1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data

  @wip
  Scenario: Aggregate Viewer can not access student data
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 200
    And the response should have restricted student data
    And the response should have general student data

    Given I remove the SEOA with role "Leader" for staff "msmith" in "East Daybreak High"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<matt.sollars URI>"
    Then I should receive a return code of 403

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 403

    Given format "application/json"
    When I navigate to GET "<lashawn.taite URI>"
    Then I should receive a return code of 403

  @wip
  Scenario: Student belongs to schools in different LEAs
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "mgonzales" "mgonzales1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    When I navigate to GET "<carmen.ortiz URI>"
    Then I should receive a return code of 200
    When I navigate to GET "<nate.dedrick URI>"
    Then I should receive a return code of 200

    Given format "application/json"
    When I navigate to GET "<bert.jakeman URI>"
    Then I should receive a return code of 403