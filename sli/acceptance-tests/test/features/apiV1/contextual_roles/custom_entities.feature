@RALLY_US5775 @wip
Feature: Test CRUD fuctionality of Custom Entities with multiple roles

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin setup application and realm data
    And I have an open web browser

Scenario:  User can WRITE custom data to an EdOrg with Write Access, cannot write to custom data in EdOrg without Write Access
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "rbelding" "rbelding1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls
    Then the following student section associations in Midgar are set correctly
      | student         | teacher              | edorg                 | enrolledInAnySection? |
      | jack.jackson    | rbelding             | East Daybreak High    | no                    |
    #Jack Jackson
    Given format "application/json"
    And I add a key value pair "Drives" : "True" to the object
    When I navigate to POST "/v1/students/df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id/custom"
    Then I should receive a return code of 201
    When I navigate to GET "/v1/students/df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id/custom"
    Then I should receive a key value pair "Drives" : "True" in the result

    When I navigate to DELETE "/v1/students/df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id/custom"
    Then I should receive a return code of 204
    When I navigate to GET "/v1/students/df54047bf88ecd7e2f6fbf00951196f747c9ccfc_id/custom"
    Then I should receive a return code of 404

    #Bert Jakeman
    Given format "application/json"
    And I add a key value pair "Drives" : "true" to the object
    When I navigate to POST "/v1/students/3d7084654aa96c1fdc68a27664760f6bb1b97b5a_id/custom"
    Then I should receive a return code of 201

Scenario:  User with multiple roles gets hierarchical access
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    And I add a key value pair "isCharter" : "True" to the object
    When I navigate to POST "/v1/educationOrganizations/2a30827ed4cf5500fb848512d19ad73ed37c4464_id/custom"
    Then I should receive a return code of 201
    When I navigate to GET "/v1/educationOrganizations/2a30827ed4cf5500fb848512d19ad73ed37c4464_id/custom"
    Then I should receive a return code of 200
    Then I should receive a key value pair "isCharter" : "True" in the result

    Given I add a key value pair "isCharter" : "False" to the object
    When I navigate to PUT "/v1/educationOrganizations/2a30827ed4cf5500fb848512d19ad73ed37c4464_id/custom"
    Then I should receive a return code of 204
    When I navigate to DELETE "/v1/educationOrganizations/2a30827ed4cf5500fb848512d19ad73ed37c4464_id/custom"
    Then I should receive a return code of 204

Scenario Outline:  User writes to self custom data with multiple roles
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Leader" to add the "WRITE_GENERAL" right
    And I change the custom role of "Leader" to add the "WRITE_PUBLIC" right
    When I navigate to the API authorization endpoint with my client ID
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jmacey" "jmacey1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls

    Given format "application/json"
    And I add a key value pair "Commuter" : "True" to the object
    When I navigate to POST "/v1/staff/7810ac678851ae29a450cc18bd9f47efa37bfaef_id/custom"
    Then I should receive a return code of 201
    When I navigate to GET "/v1/staff/7810ac678851ae29a450cc18bd9f47efa37bfaef_id/custom"
    Then I should receive a return code of 200
    Then I should receive a key value pair "Commuter" : "True" in the result

    Given I add a key value pair "Commuter" : "False" to the object
    When I navigate to PUT "/v1/staff/7810ac678851ae29a450cc18bd9f47efa37bfaef_id/custom"
    Then I should receive a return code of 204
    When I navigate to GET "/v1/staff/7810ac678851ae29a450cc18bd9f47efa37bfaef_id/custom"
    Then I should receive a return code of 200
    Then I should receive a key value pair "Commuter" : "False" in the result

    Given format "application/json"
    And I add a key value pair "Commuter" : "True" to the object
    When I navigate to POST "/v1/staffEducationOrgAssignmentAssociations/57edc58caa226f4ab888e51ef8b5531b98800cca_id/custom"
    Then I should receive a return code of 201
    When I navigate to GET "/v1/staffEducationOrgAssignmentAssociations/57edc58caa226f4ab888e51ef8b5531b98800cca_id/custom"
    Then I should receive a return code of 200
    Then I should receive a key value pair "Commuter" : "True" in the result

    When I navigate to DELETE "/v1/staffEducationOrgAssignmentAssociations/57edc58caa226f4ab888e51ef8b5531b98800cca_id/custom"
    Then I should receive a return code of 204
    When I navigate to GET "/v1/staffEducationOrgAssignmentAssociations/57edc58caa226f4ab888e51ef8b5531b98800cca_id/custom"
    Then I should receive a return code of 404
