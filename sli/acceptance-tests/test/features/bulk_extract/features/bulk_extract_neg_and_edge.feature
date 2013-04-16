Feature: Check the bulk extractor on negative and edge cases
Scenario: Check the bulk extractor on negative and edge cases

	#Run Bulk Extract on an empty database
	Given all collections are empty
	And the extraction zone is empty
	And I trigger a bulk extract
	When I retrieve the path to and decrypt the extract file for the tenant "Midgar" and application with id "19cca28d-7357-4044-8df9-caad4b1c8ee4"
	And I verify that an extract tar file was created for the tenant "Midgar"
	And there is a metadata file in the extract	
	And the extract contains no entity files
	
	#Run Bulk Extract on an invalid tenant
	Given the extraction zone is empty
	When I use an invalid tenant to trigger a bulk extract
	Then the extraction zone should still be empty