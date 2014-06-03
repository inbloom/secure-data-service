##these scenarios are wip'd because they fail during the summer
##please fix odin test data generation

@wip
@RALLY_US5775
Feature: Test CRUD fuctionality of Custom Entities with multiple roles

  Background: Setup for the tests
    Given the testing device app key has been created
    And I import the odin setup application and realm data
    And I have an open web browser

Scenario:  User can WRITE custom data to an EdOrg with Write Access, cannot Delete custom data in EdOrg without Write Access
    Given I change the custom role of "Leader" to add the "WRITE_RESTRICTED" right
    And I change the custom role of "Educator" to remove the "WRITE_GENERAL" right
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
      | bert.jakeman    | rbelding             | Daybreak Central High | yes                   |

    #Jack Jackson
    Given format "application/json"
    And I add a key value pair "Drives" : "True" to the object
    When I navigate to POST "<JACK.JACKSON CUSTOM URI>"
    Then I should receive a return code of 201
    When I navigate to GET "<JACK.JACKSON CUSTOM URI>"
    Then I should receive a key value pair "Drives" : "True" in the result

    When I navigate to DELETE "<JACK.JACKSON CUSTOM URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<JACK.JACKSON CUSTOM URI>"
    Then I should receive a return code of 404

    #Bert Jakeman
    Given format "application/json"
    And I add a key value pair "Drives" : "True" to the object
    When I navigate to POST "<BERT.JAKEMAN CUSTOM URI>"
    Then I should receive a return code of 201
    When I navigate to GET "<BERT.JAKEMAN CUSTOM URI>"
    Then I should receive a return code of 200
    Then I should receive a key value pair "Drives" : "True" in the result

    Given I add a key value pair "Drives" : "False" to the object
    When I navigate to PUT "<BERT.JAKEMAN CUSTOM URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<BERT.JAKEMAN CUSTOM URI>"
    Then I should receive a return code of 200
    Then I should receive a key value pair "Drives" : "False" in the result
    When I navigate to DELETE "<BERT.JAKEMAN CUSTOM URI>"
    Then I should receive a return code of 403

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
    When I navigate to POST "<EAST.DAYBREAK CUSTOM URI>"
    Then I should receive a return code of 201
    When I navigate to GET "<EAST.DAYBREAK CUSTOM URI>"
    Then I should receive a return code of 200
    Then I should receive a key value pair "isCharter" : "True" in the result

    Given I add a key value pair "isCharter" : "False" to the object
    When I navigate to PUT "<EAST.DAYBREAK CUSTOM URI>"
    Then I should receive a return code of 204
    When I navigate to DELETE "<EAST.DAYBREAK CUSTOM URI>"
    Then I should receive a return code of 204

Scenario:  User writes to self custom data with multiple roles
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
    When I navigate to POST "<JMACEY CUSTOM URI>"
    Then I should receive a return code of 201
    When I navigate to GET "<JMACEY CUSTOM URI>"
    Then I should receive a return code of 200
    Then I should receive a key value pair "Commuter" : "True" in the result

    Given I add a key value pair "Commuter" : "False" to the object
    When I navigate to PUT "<JMACEY CUSTOM URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<JMACEY CUSTOM URI>"
    Then I should receive a return code of 200
    Then I should receive a key value pair "Commuter" : "False" in the result

    Given format "application/json"
    And I add a key value pair "Commuter" : "True" to the object
    When I navigate to POST "<JMACEY SEOAA CUSTOM URI>"
    Then I should receive a return code of 201
    When I navigate to GET "<JMACEY SEOAA CUSTOM URI>"
    Then I should receive a return code of 200
    Then I should receive a key value pair "Commuter" : "True" in the result

    When I navigate to DELETE "<JMACEY SEOAA CUSTOM URI>"
    Then I should receive a return code of 204
    When I navigate to GET "<JMACEY SEOAA CUSTOM URI>"
    Then I should receive a return code of 404
