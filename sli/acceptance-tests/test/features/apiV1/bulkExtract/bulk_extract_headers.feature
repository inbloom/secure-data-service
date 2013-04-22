Feature: Retrieve portions of the bulk extract file through the API and validate

Scenario: Make a bulk extract request using corrent and incorrectly populated headers

    #Make a head call to retrieve etag and last-modified information
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a sample tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    And I know the file length of the extract file
    When I make a call retrieve the header for the bulk extract end point "/bulk/extract/tenant"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request

    #Make a bulk extract request with correct If-Match etag
    When the If-Match header field is set to "FILENAME"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"

    #Make a bulk extract request with incorrect If-Match etag
    When the If-Match header field is set to "INCORRECT_FILENAME"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"

    #Make a bulk extract request with correct If-Unmodified-Since time
    When the If-Unmodified-Since header field is set to "AFTER"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"

    #Make a bulk extract request with incorrect If-Unmodified-Since time
    When the If-Unmodified-Since header field is set to "BEFORE"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"