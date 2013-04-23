Feature: Retrieve portions of the bulk extract file through the API and validate

@sampleTar
Scenario: Get the bulk extract file in chunks

    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And in my list of rights I have BULK_EXTRACT
    When I make a call to the bulk extract end point "/bulk/extract/tenant"
    Then I get back a response code of "200"
    Then I have all the information to make a custom bulk extract request
 
    #Consecutive chunks   
    When I prepare the custom headers for byte range from "0" to "100"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "101"
    And I store the file content
    When I prepare the custom headers for byte range from "101" to "end"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "101" less than the total content length
    And I store the file content
    Then the file is decrypted
    And I see that the combined file matches the tar file
    
    
    #First n bytes and last n bytes
    When I prepare the custom headers for the first "100000" bytes
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "100000"
    And I store the file content
    When I prepare the custom headers for the last "54128" bytes
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "54128"
    And I store the file content
    Then the file is decrypted
    And I see that the combined file matches the tar file


    #Overlapping chunks
    When I prepare the custom headers for byte range from "0" to "100"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "101"
    And I store the file content
    When I prepare the custom headers for byte range from "50" to "end"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "50" less than the total content length
    And I combine the overlapped parts
    Then the file is decrypted
    And I see that the combined file matches the tar file
    
    #Disjoint ranges   
    When I prepare the custom headers for byte range from "30" to "15000"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "14971"
    And I store the file content
    And I verify the bytes I have are correct
    And I prepare the custom headers for byte range from "2000" to "120300"
    When I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "118301"
    And I verify the bytes I have are correct
    And I store the file content
    And I verify I do not have the complete file

    #Invalid/Incomplete range calls
    When I prepare the custom headers for byte range from "0" to "500000"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is "154128"
    When I prepare the custom headers for byte range from "500000" to "700000"
    And I make a custom bulk extract API call
    Then I get back a response code of "416"
    When I prepare the custom headers for byte range from "50" to "500000"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "154078"
    When I prepare the custom headers for byte range from "500000" to "150"
    And I make a custom bulk extract API call
    Then I get back a response code of "416"
    When I prepare the custom headers for byte range from "" to "154128"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is "154128"
    

    #Multiple Ranges
    When I prepare the custom headers for multiple byte ranges "0-15000,15001-"
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is "154128"
    And I store the file content
    Then the file is decrypted
    And I see that the combined file matches the tar file
    When I prepare the custom headers for multiple byte ranges "0-15000,20001-42000"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    Then I store the contents of the first call
    When I prepare the custom headers for multiple byte ranges "15001-20000,42001-"
    And I make a custom bulk extract API call
    Then I get back a response code of "206"
    Then I store the contents of the second call
    And I combine the file contents
    And the file is decrypted
    And I see that the combined file matches the tar file

    #Invalid API Call
    When I prepare the custom headers with incorrect etag
    And I make a custom bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is "154128"

    #Concurrent Call
    When I make a concurrent ranged bulk extract API call and store the results
    Then the file is decrypted
    And I see that the combined file matches the tar file