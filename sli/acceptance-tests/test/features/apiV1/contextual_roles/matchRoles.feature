@RALLY_US5764
Feature: Match IDP roles with the ingested inBloom Roles during Authentication
         As an EdOrg Admin, I would like my users to authenticate against the edOrg+roles that I ingested,
         so that they are assigned correct roles by the inBloom system.

Background:
  Given the testing device app key has been created
  And I import the odin setup application and realm data
  And I have an open web browser

Scenario: As a staff member, I can log in and see data for myself, if at least one role with valid end date matches
  When I navigate to the API authorization endpoint with my client ID
  And I was redirected to the "Simple" IDP Login page
  And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
  Then I should receive a json response containing my authorization code
  When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
  Then I should receive a json response containing my authorization token
  And I should be able to use the token to make valid API calls

  Given format "application/json"
  When I navigate to GET "/v1/home"
  Then I should get and store the link named "self"
  And I should extract the "staff" id from the "self" URI

Scenario: As a staff member, I cannot log in, if all of my roles are expired
    Given I expire all SEOA expiration dates for "linda.kim" in tenant "Midgar"
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
    Then I should receive a response page with http error code 403

Scenario: As a staff member, I can log in and see data for myself, if at least one role with no end date matches
  Given I remove all SEOA expiration dates for "linda.kim" in tenant "Midgar"
  When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
      Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
      Then I should receive a json response containing my authorization token
      And I should be able to use the token to make valid API calls

  Given format "application/json"
    When I navigate to GET "/v1/home"
      Then I should get and store the link named "self"
      And I should extract the "staff" id from the "self" URI

Scenario: As a staff member, I cannot log in, if none of my roles match
  Given I modify all SEOA staff classifications for "linda.kim" in tenant "Midgar" to "Leader"
  When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
      Then I should receive a response page with http error code 403

Scenario: As a staff member, I cannot log in, if my roles are empty
    Given I modify all SEOA staff classifications for "linda.kim" in tenant "Midgar" to ""
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
    Then I should receive a response page with http error code 403

Scenario: As a staff member, I cannot log in, if I have no roles in the database
  Given I remove all SEOAs for "linda.kim" in tenant "Midgar"
  When I navigate to the API authorization endpoint with my client ID
  And I was redirected to the "Simple" IDP Login page
  And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
  Then I should receive a response page with http error code 403

Scenario: As a staff member, I cannot log in, if my roles do not match one of the roles in the system
    Given I remove "Educator" from the custom roles
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
    Then I should receive a response page with http error code 403

@wip
Scenario: As a staff user, I can log in even when none of my roles have a context right
    Given I change the custom role of "Educator" to remove the "TEACHER_CONTEXT" right
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
