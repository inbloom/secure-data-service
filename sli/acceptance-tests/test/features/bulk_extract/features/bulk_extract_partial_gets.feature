Feature: Retrieve portions of the bulk extract file through the API and validate

Scenario: Get the bulk extract file in chunks

	Given I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
    And I set up a fake tar file on the file system and in Mongo
    And in my list of rights I have BULK_EXTRACT
	And I know the file length of the extract file
	When I make HEAD bulk extract API call
	Then I get back a response code of 200
	And the file length given in the HEAD matches the actual file length
	
    When I prepare the custom headers for byte range from 0 to 100
	And I make a ranged bulk extract API call
    Then I get back a response code of 206
    And the content length in response header is 100
    And I prepare the custom headers for byte range from 101 to end
	And I make another ranged bulk extract API call
    Then I get back a response code of 206
    #assuming total file size is 543
    And the content length in response header is 443 
	And I combine the parts
	Then the response is decrypted 
    And I see that the response matches the tar file
    
    When I prepare the custom headers for byte range from 0 to 100
    And I make a ranged bulk extract API call
    Then I get back a response code of 206
    And the content length in response header is 100
    And I prepare the custom headers for byte range from 50 to end
    And I make another ranged bulk extract API call
    Then I get back a response code of 206
    And the content length in response header is 493 
    And I combine the parts
	Then the response is decrypted 
    And I see that the response matches the tar file
    
    When I prepare the custom headers for the first 300 bytes
    And I make a ranged bulk extract API call
    Then I get back a response code of 206
    And the content length in response header is 300
    And I prepare the custom headers for the last 200 bytes
    And I make another ranged bulk extract API call
    Then I get back a response code of 206
    And the content length in response header is 200
    And I combine the parts
	Then the response is decrypted 
    And I see that the response matches the tar file
    
    When I prepare the custom headers for byte range from 30 to 150
    And I make a ranged bulk extract API call
    Then I get back a response code of 206
    And the content length in response header is 120
    And I prepare the custom headers for byte range from 200 to 300
    And I make another ranged bulk extract API call
    Then I get back a response code of 206
    And the content length in response header is 100
    Then I verify I don't have the complete file
    And I verify the bytes I do have are correct