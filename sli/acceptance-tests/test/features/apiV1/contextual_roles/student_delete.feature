@RALLY_US5808

Feature: Use the API to successfully delete students  while having roles over many schools

  Background: Setup
    Given the testing device app key has been created
    And I import the odin setup application and realm data
    And I have an open web browser

  Scenario: User with one write right in one edorg and another in another, able to delete with either
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Aggregate Viewer" to add the "WRITE_GENERAL" right
    And I change the custom role of "Aggregate Viewer" to add the "READ_GENERAL" right
    And I add a SEOA for "xbell" in "District 9" as a "Leader"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "xbell" "xbell1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

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
    Given I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    Then the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | bert.jakeman    | rbelding             | Daybreak Central High | yes                   |

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "rbelding" "rbelding1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

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

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

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

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

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

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    Given I create a student entity with restricted data
    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201
    When I navigate to GET the new entity
    Then I should receive a return code of 200

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "rbelding" "rbelding1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    When I attempt to delete the new entity
    Then I should receive a return code of 403

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    When I attempt to delete the new entity
    Then I should receive a return code of 204
    When I navigate to GET the new student entity
    Then I should receive a return code of 404