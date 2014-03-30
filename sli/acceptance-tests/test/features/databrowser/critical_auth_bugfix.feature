@RALLY_US215
@RALLY_DE1375
Feature: Validate critical bugs do not resurface after being sqashed
I want to detect the ressurection of 'zombie bugs' for critical defects fixed previously

Background:
	Given that databrowser has been authorized for all ed orgs

	
Scenario: Zombie Bug 1: Gaining access as the previously authenticated user when given no credentials

	Given Another user has authenticated to SLI previously
	When I access the API resource "/system/session/debug" with no authorization headers present
	Then I should receive a return code of 401
	When I access the API resource "/v1/students" with no authorization headers present
	Then I should receive a return code of 401
	
Scenario: Zombie Bug 2: Infinite redirect loop when accessing Databrowser while having no entity in datastore
	
	Given I have an open web browser
	And I navigated to the Data Browser Home URL
	And I was redirected to the Realm page
	And I choose realm "Illinois Sunset School District 4526" in the drop-down list
	And I click on the realm page Go button
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "badadmin" "badadmin1234" for the "Simple" login page
	Then I should see a message that I am forbidden

Scenario: Zombie Bug 3: Infinite redirect loop when accessing Databrowser while having expired session
	
	Given I have an open web browser
	And I have a _tla cookie set to an expired session
	And I navigated to the Data Browser Home URL
	And I was redirected to the Realm page
    And I choose realm "Illinois Sunset School District 4526" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
    Then I should be redirected to the Data Browser home page

Scenario: Zombie Bug 4: Deny logging in when user supplies incorrect credentials due to case sensitivity
  
  Given I have an open web browser
	And I navigated to the Data Browser Home URL
	And I was redirected to the Realm page
	And I choose realm "Illinois Sunset School District 4526" in the drop-down list
	And I click on the realm page Go button
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "Jwashington" "jwashington1234" for the "Simple" login page
	Then I should see a message that I am an invalid user

@DE_2700
Scenario: Clicking Logout clears all sessions for an authenticated user

  Given I have an open web browser
  # login as rrogers 1st time
  And I navigated to the Data Browser Home URL
  And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And I should be redirected to the Data Browser home page
  # remember _tla cookie value, delete cookies
  And I remember the _tla cookie value
  And I clear all session cookies
  # login as rrogers 2nd time
  And I navigated to the Data Browser Home URL
  And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And I should be redirected to the Data Browser home page
  And I remember the _tla cookie value
  # verify rrogers can do a few things
  And I click on the "Staff Program Associations" link
  Then I am redirected to the associations list page
  And I see a table displaying the associations in a list
  # logout as rrogers (clear 2nd session)
  When I logout of the databrowser
  # verify 1st session has been removed from db
  Then I should see that both sessions have been removed from the userSession collection
  
