Feature:
  As an API user
  In order to access different versions of resources
  I want to be able to make requests to different versions of the API

Background:
  Given I have a session as a tenant-level IT Administrator
    And I want to use format "application/json"

Scenario Outline: Validate all links returned by the API are versioned based on input
  When I navigate to GET "/<version>/home"
  Then the response status should be 200 OK
   And all returned links should be version "<version>"
  Examples:
    | version |
    | v1.0    |
    | v1.1    |

Scenario: Validate non v1.0 resources
  When I navigate to GET "/v1.0/search"
  Then the response status should be 404 Not Found
