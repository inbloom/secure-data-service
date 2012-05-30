@RALLY_US215
Feature: Validate critical bugs do not resurface after being sqashed
I want to detect the ressurection of 'zombie bugs' for critical defects fixed previously

Scenario: Zombie Bug 1: Gaining access as the previously authenticated user when given no credentials

	Given Another user has authenticated to SLI previously
	When I access the API resource "/system/session/debug" with no authorization headers present
	Then I should receive a return code of 401
	When I access the API resource "/v1/students" with no authorization headers present
	Then I should receive a return code of 401
	
Scenario: Zombie Bug 2: Infinate redirect loop when accessing Databrowser while having no entity in datastore
	
	Given I have an open web browser
	And I navigated to the Data Browser Home URL
	And I was redirected to the Realm page
	And I choose realm "Illinois Sunset School District 4526" in the drop-down list
	And I click on the realm page Go button
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "badadmin" "badadmin1234" for the "Simple" login page
	Then I should see a message that I am forbidden