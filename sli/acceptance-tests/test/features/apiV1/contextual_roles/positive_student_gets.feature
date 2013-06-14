@RALLY_5765
Feature: Use the APi to successfully get student data while having roles over many schools

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin-local-setup application and realm data
    And I have an open web browser
    And the following student section associations in Midgar are set correctly
    | student         | teacher              | edorg                 | enrolledInAnySection? |
    | carmen.ortiz    | linda.kim            | Daybreak Central High | yes                   |
    | carmen.ortiz    | rbraverman           | Daybreak Central High | yes                   |
    | lashawn.taite   | linda.kim            | Daybreak Central High | no                    |
    | lashawn.taite   | rbraverman           | Daybreak Central High | yes                   |
    | carmen.ortiz    | linda.kim            | Daybreak Bayside High | yes                   |
    | lashawn.taite   | linda.kim            | East Daybreak High    | yes                   |
    | matt.sollars    | linda.kim            | East Daybreak High    | yes                   |

  @wip
  Scenario Outline: Get a student's data using various staff-student combination
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

    Examples:
    | staff       | password                  | student's URI           |
    | linda.kim   | linda.kim1234             | <carmen.ortiz URI>      |
    | linda.kim   | linda.kim1234             | <lashawn.taite URI>     |
#    | rbraverman  | rbraverman1234            | <carmen.ortiz URI>      |
#    | rbraverman  | rbraverman1234            | <matt.sollars URI>      |
#    | rbraverman  | rbraverman1234            | <lashawn.taite URI>     |
    | sbantu      | sbantu1234                | <lashawn.taite URI>     |
    | sbantu      | sbantu1234                | <carmen.ortiz URI>      |