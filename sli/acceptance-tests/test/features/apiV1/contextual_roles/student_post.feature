@RALLY_US5768

Feature: Use the APi to successfully post student data while having roles over many schools

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin setup application and realm data
    And I have an open web browser

  Scenario: Role with all the write rights in school can post a student with restricted data
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
    Then I should receive a return code of 403

    Given I change all SEOAs of "msmith" to the edorg "East Daybreak High"
    And I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "msmith" "msmith1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201

    Then I remove the posted student

  Scenario: Role with different roles in a school can post a student
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

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

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201
    Then I remove the posted student

    Given I change all SEOAs of "jmacey" to the edorg "East Daybreak High"

    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    Given I create a student entity with restricted data
    When I navigate to POST "/v1/students"
    Then I should receive a return code of 201
    Then I remove the posted student