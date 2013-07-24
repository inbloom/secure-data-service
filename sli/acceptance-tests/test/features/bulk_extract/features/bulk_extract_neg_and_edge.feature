Feature: Check the bulk extractor on negative and edge cases

Scenario: Run the bulk extractor on an unauthorized tenant
	Given I am using local data store
	Given I am using preconfigured Ingestion Landing Zone for "Hyrule-NYC"
    And the tenant "Hyrule" does not have any bulk extract apps for any of its education organizations
	And I post "StoriedDataSet_NY.zip" file as the payload of the ingestion job
	When zip file is scp to ingestion landing zone
	And a batch job for file "StoriedDataSet_NY.zip" is completed in database
	Then I should not see an error log file created
	And I should not see a warning log file created
	Given I trigger an extract for tenant "Hyrule"
	Then I should not see an extract for tenant "Hyrule"
	
Scenario: Try accessing the API's bulk extract endpoint with a user that doesn't have the proper rights
	Given I trigger a bulk extract
	And I am a valid 'service' user with an authorized long-lived token "9f58b6dc-0880-4e2a-a65f-3aa8b5201fbd"	
	When I make a call to the bulk extract end point "/bulk/extract/tenant"
	Then I get back a response code of "403"
	
Scenario: Valid User tries to get a bulk extract before it's been triggered
	Given the extraction zone is empty
	And the bulk extract files in the database are scrubbed
	And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
	And in my list of rights I have BULK_EXTRACT
	When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
	Then I get back a response code of "404"
	
Scenario: Valid User tries to POST to bulk extract endpoint
	Given I trigger a bulk extract
	And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
	And in my list of rights I have BULK_EXTRACT
	When I try to POST to the bulk extract endpoint
	Then I get back a response code of "405"
	
Scenario: Valid User tries to get a bulk extract after it's been triggered but is missing from the filesystem
	Given the extraction zone is empty
	And I am a valid 'service' user with an authorized long-lived token "92FAD560-D2AF-4EC1-A2CC-F15B460E1E43"
	And in my list of rights I have BULK_EXTRACT
	When I make a call to the bulk extract end point "/bulk/extract/LEA_DAYBREAK_ID"
	Then I get back a response code of "404"
		
Scenario: Run the bulk extractor on an empty database
	Given I am using preconfigured Ingestion Landing Zone for "Midgar-Daybreak"
	And all collections are empty
	And the extraction zone is empty
    And the bulk extract files in the database are scrubbed
	And I trigger a bulk extract
	Then I failed retrieve the path to and decrypt the extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"


Scenario: Run the bulk extractor on a non-existing tenant
	Given the extraction zone is empty
	When I use an invalid tenant to trigger a bulk extract
	Then the extraction zone should still be empty