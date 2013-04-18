Feature: Retrieve portions of the bulk extract file through the API and validate

Scenario: Make a bulk extract request using corrent and incorrectly populated headers
    #Retrieve file information
    When I retrieve the path to and decrypt the extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
    When I know the file-length of the extract file

    #Make a head call to retrieve etag and last-modified information
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make bulk extract API head call
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request

    #Make a bulk extract request with correct If-Match etag
    When the If-Match header field is set to "FILENAME"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"

    #Make a bulk extract request with incorrect If-Match etag
    When the If-Match header field is set to "incorrectFileName.tar"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"

    #Make a bulk extract request with correct If-Unmodified-Since time
    When the If-Unmodified-Since header field is set to "AFTER"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"

    #Make a bulk extract request with correct If-Unmodified-Since time
    When the If-Unmodified-Since header field is set to "BEFORE"
    And I make a custom bulk extract API call
    Then I get back a response code of "412"