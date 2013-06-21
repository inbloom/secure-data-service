@US5618
Feature: Retrieve portions of the bulk extract file through the API and validate

Background: Make a bulk extract request using corrent and incorrectly populated headers
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request

Scenario: Make a bulk extract request with correct If-Match etag
    When the If-Match header field is set to "FILENAME"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"

	Scenario: Make a bulk extract request with incorrect If-Match etag
    When the If-Match header field is set to "INCORRECT_FILENAME"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"

Scenario: Make a bulk extract request with correct If-Unmodified-Since time
    When the If-Unmodified-Since header field is set to "AFTER"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"

Scenario: Make a bulk extract request with incorrect If-Unmodified-Since time
    When the If-Unmodified-Since header field is set to "BEFORE"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"

Scenario: Make a bulk extract request with correct If-Range time, receiving part of the file
    When the If-Range header field is set to "VALID_DATE" for range up to "20000"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "20000"

Scenario: Make a bulk extract request with incorrect If-Range time, receiving the full file
    When the If-Range header field is set to "INVALID_DATE" for range up to "20000"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is greater than the requested range of "20000"