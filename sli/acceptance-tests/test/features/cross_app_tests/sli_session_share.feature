@integration
Feature: Applications sharing SLI Sessions
As a user of SLI, I want to have my session shared across SLI Apps
@wip
Scenario: Session sharring between Databrowser & Dashboard

	Given I have an open web browser
	And I have navigated to the databrowser page
	And I was redirected to the realmchooser page
	And I selected the realm "Illinois Sunset School District 4526"
	When I submit the credentials "cgray" "cgray1234" for the "OpenAM" login page
	Then I am redirected to the databrowser home page
	When I navigate to the dashboard page
	Then I do not see any login pages
	And I am redirected to the dashboard home page
	When I navigate to the databrowser page
	And I click on the logout link
#	Then I should see a message that I was logged out
	And I should forced to reauthenticate to gain access
	When I navigate to the dashboard home page
	Then I should forced to reauthenticate to gain access
	
Scenario: Session sharring between Databrowser & Sample App

	Given I have an open web browser
	And I have navigated to the databrowser page
	And I was redirected to the realmchooser page
	And I selected the realm "Illinois Sunset School District 4526"
	When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
	Then I am redirected to the databrowser home page
	When I navigate to the sample app page
	Then I do not see any login pages
	And I am redirected to the sample app home page
	When I navigate to the databrowser page
	And I click on the logout link
#	Then I should see a message that I was logged out
	And I should forced to reauthenticate to gain access
	When I navigate to the sample app page
	Then I should forced to reauthenticate to gain access