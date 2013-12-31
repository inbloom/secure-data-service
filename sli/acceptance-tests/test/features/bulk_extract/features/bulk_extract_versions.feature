@RALLY_US5594

Feature: As an API user, I want to be able to make bulk extract requests to with different versions.

Background: An authorized bulk extract user logs in and gets the information for the extract from a HEAD call
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
 
Scenario: Validate backwards compatibility 
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers

Scenario: Vaidate request with only major version
    When I make a call retrieve the header for the bulk extract end point "/v1/bulk/extract"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1/bulk/extract/LEA_DAYBREAK_ID"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers

Scenario: Validate requests with minor versions
    When I make a call retrieve the header for the bulk extract end point "/v1.1/bulk/extract/LEA_DAYBREAK_ID"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1.1/bulk/extract/LEA_DAYBREAK_ID"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1.1/bulk/extract"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1.5/bulk/extract"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers
    When I make a call retrieve the header for the bulk extract end point "/v1.5/bulk/extract"
    When the return code is 200 I get expected tar downloaded
    Then I check the version of http response headers

Scenario: Validate requests with invalid versions
    When I make a call retrieve the header for the bulk extract end point "/v1.6/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "404"
    When I make a call retrieve the header for the bulk extract end point "/v2.0/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "404"
    When I make a call retrieve the header for the bulk extract end point "/1.6/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "404"
    When I make a call retrieve the header for the bulk extract end point "/abc/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "404"
    When I make a call retrieve the header for the bulk extract end point "/v1.6/bulk/extract"
    Then I get back a response code of "404"
    When I make a call retrieve the header for the bulk extract end point "/v2.0/bulk/extract"
    Then I get back a response code of "404"
    When I make a call retrieve the header for the bulk extract end point "/1.6/bulk/extract"
    Then I get back a response code of "404"
    When I make a call retrieve the header for the bulk extract end point "/abc/bulk/extract"
    Then I get back a response code of "404"
