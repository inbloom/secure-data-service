Feature: Retrieve portions of the bulk extract file through the API and validate

@fakeTar
Scenario: Get the bulk extract file in chunks

    Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
    And I know the file length of the extract file
    When I make bulk extract API head call
    Then I get back a response code of "200"
    Then I have all the information to make a byte range request
 
    #Consecutive chunks   
    When I prepare the custom headers for byte range from "0" to "100"
    And I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "101"
    And I store the file content
    When I prepare the custom headers for byte range from "101" to "end"
    And I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "443"
    And I store the file content
    Then the file is decrypted
    And I see that the combined file matches the tar file
    
    
    #First n bytes and last n bytes
    When I prepare the custom headers for the first "300" bytes
    And I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "300"
    And I store the file content
    When I prepare the custom headers for the last "244" bytes
    And I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "244"
    And I store the file content
    Then the file is decrypted
    And I see that the combined file matches the tar file


    #Overlapping chunks
    When I prepare the custom headers for byte range from "0" to "100"
    And I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "101"
    And I store the file content
    When I prepare the custom headers for byte range from "50" to "end"
    And I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "494"
    And I combine the overlapped parts
    Then the file is decrypted
    And I see that the combined file matches the tar file
    
    #Disjoint ranges   
    When I prepare the custom headers for byte range from "30" to "150"
    And I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "121"
    And I store the file content
    And I verify the bytes I have are correct
    And I prepare the custom headers for byte range from "200" to "300"
    When I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "101"
    And I verify the bytes I have are correct
    And I store the file content
    And I verify I do not have the complete file

    #Invalid/Incomplete range calls
    When I prepare the custom headers for byte range from "0" to "700"
    And I make a ranged bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is "544"
    When I prepare the custom headers for byte range from "550" to "700"
    And I make a ranged bulk extract API call
    Then I get back a response code of "416"
    When I prepare the custom headers for byte range from "50" to "6000"
    And I make a ranged bulk extract API call
    Then I get back a response code of "206"
    And the content length in response header is "494"
    When I prepare the custom headers for byte range from "500" to "150"
    And I make a ranged bulk extract API call
    Then I get back a response code of "416"
    When I prepare the custom headers for byte range from "" to "544"
    And I make a ranged bulk extract API call
    Then I get back a response code of "200"
    And the content length in response header is "544"
    
    #Multiple Range
    #When I prepare the custom headers for multiple byte ranges "0-100,101-543"
    #And I make a ranged bulk extract API call
    #Then I get back a response code of "206"
    #And the content length in response header is "544"
    #And I process the file content
    #And the file size is "544"
    #Then the file is decrypted
    #And I see that the combined file matches the tar file