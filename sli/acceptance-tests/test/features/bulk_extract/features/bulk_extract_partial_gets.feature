Feature: Retrieve portions of the bulk extract file through the API and validate

Scenario: Get the bulk extract file in chunks

	Given I have an extract file ready
	And I know the file length of the extract file
	When I make an API call to request the HEAD of the bulk extract
	Then I get back a response code of 200
	And the file length given in the HEAD matches the actual file length
	
	When I make an API call to get the byte range from 0 to 100 of the extract file
	And I make another API call to get the byte range from 101 to the end of the extract file
	And I combine the parts
	Then the response is decrypted 
    And I see that the response matches the tar file
    
    When I make an API call to get the byte range from 0 to 100 of the extract file
    And I make another API call to get the byte range from 50 to the end of the extract file
    And I combine the parts
	Then the response is decrypted 
    And I see that the response matches the tar file
    
    When I make an API call to get the first 300 bytes of the extract file
    And I make another API call to get the last 200 bytes of the extract file
    And I combine the parts
	Then the response is decrypted 
    And I see that the response matches the tar file
    
    When I make an API call to get the byte range from 30 to 150 of the extract file
    And I make another API call to get the byte range from 200 to 300 of the extract file
    Then I verify I don't have the complete file
    And I verify the bytes I do have are correct