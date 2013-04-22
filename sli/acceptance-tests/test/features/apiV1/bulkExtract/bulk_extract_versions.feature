@RALLY_US5594
@wip
Feature: As an API user, I want to be able to make bulk extract requests to with different versions.

Scenario: Validate backwards compatibility 
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract"
    When the return code is 200
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/tenant"
    When the return code is 200
    Then I check the version of http response headers


Scenario: Valicate request with only major version
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/v1/bulk/extract"
    When the return code is 200
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1/bulk/extract/tenant"
    When the return code is 200
    Then I check the version of http response headers

Scenario: Validate requests with minor versions
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/v1.1/bulk/extract/tenant"
    When the return code is 200
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1.1/bulk/extract/tenant"
    When the return code is 200
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1.1/bulk/extract"
    When the return code is 200
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1.2/bulk/extract"
    When the return code is 200
    Then I check the version of http response headers

Scenario: Validate requests with invalid versions
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/v1.3/bulk/extract/tenant"
    Then the return code is 404
    When I make a call retrieve the header for the bulk extract end point "/v2.0/bulk/extract/tenant"
    Then the return code is 404
    When I make a call retrieve the header for the bulk extract end point "/1.3/bulk/extract/tenant"
    Then the return code is 404
    When I make a call retrieve the header for the bulk extract end point "/abc/bulk/extract/tenant"
    Then the return code is 404
    When I make a call retrieve the header for the bulk extract end point "/v1.3/bulk/extract"
    Then the return code is 404
    #Why would this fail? /v1.0/ is a valid version....
    When I make a call retrieve the header for the bulk extract end point "/v1.0/bulk/extract"
    Then the return code is 404
    When I make a call retrieve the header for the bulk extract end point "/1.3/bulk/extract"
    Then the return code is 404
    When I make a call retrieve the header for the bulk extract end point "/abc/bulk/extract"
    Then the return code is 404