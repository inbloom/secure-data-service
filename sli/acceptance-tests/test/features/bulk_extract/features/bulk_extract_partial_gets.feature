@US5618
@TempFileCleanup
Feature: Retrieve portions of the bulk extract file through the API and validate

Background: An authorized bulk extract user logs in and gets the information for the extract from a HEAD call
    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    And I delete the previous tar file if it exists
    When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request
 
 Scenario: Get the extract file with consecutive range calls
    When I prepare the custom headers for byte range from "the beginning" to "halfway"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    And I store the file content
    When I prepare the custom headers for byte range from "where I left off" to "end"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    And I store the file content
    Then the file is decrypted
    And I see that the combined file matches the tar file

 Scenario: Get the extract file with first-n and last-n calls
    When I prepare the custom headers for the first "X" bytes
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    And I store the file content
    When I prepare the custom headers for the last "remaining" bytes
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    And I store the file content
    Then the file is decrypted
    And I see that the combined file matches the tar file

 Scenario: Get the extract file by making overlapping range calls
    When I prepare the custom headers for byte range from "the beginning" to "halfway"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    And I store the file content
    When I prepare the custom headers for byte range from "within the previous range" to "end"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    And I combine the overlapped parts
    Then the file is decrypted
    And I see that the combined file matches the tar file
 
 Scenario: Make disjointed range calls and verify we don't have the complete extract
    When I prepare the custom headers for byte range from "near the beginning" to "halfway"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    And I store the file content
    And I verify the bytes I have are correct
    And I prepare the custom headers for byte range from "within the previous range" to "three quarters of the way"
    When I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    And I verify the bytes I have are correct
    And I store the file content
    And I verify I do not have the complete file

 Scenario: Make invalid and incomplete range calls
    When I prepare the custom headers for byte range from "the beginning" to "past the end of the file"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is correct
    When I prepare the custom headers for byte range from "" to "past the end of the file"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is correct
    When I prepare the custom headers for byte range from "past the end of the file" to "way past the end of the file"
    And I make a custom bulk extract API call
    Then I get back a response code of "416"
    When I prepare the custom headers for byte range from "near the beginning" to "past the end of the file"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is correct
    When I prepare the custom headers for byte range from "past the end of the file" to "near the beginning"
    And I make a custom bulk extract API call
    Then I get back a response code of "416"

Scenario: Get the extract file by asking for multiple ranges in a single call
    When I prepare the custom headers for multiple byte ranges "for the entire file in a single call"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is correct
    And I store the file content
    Then the file is decrypted
    And I see that the combined file matches the tar file
    
Scenario: Get the extract file by asking for multiple ranges in multiple calls    
    When I prepare the custom headers for multiple byte ranges "in the first multipart call"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    Then I store the contents of the first call
    When I prepare the custom headers for multiple byte ranges "in the second multipart call"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    Then I store the contents of the second call
    And I combine the file contents
    And the file is decrypted
    And I see that the combined file matches the tar file

Scenario: Get the full extract if making a call with an Invalid e-tag
    When I prepare the custom headers with incorrect etag
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is correct

 Scenario: Get the extract file by making concurrent range request API calls
    When I make a concurrent ranged bulk extract API call and store the results
    Then the file is decrypted
    And I see that the combined file matches the tar file
