@RALLY_US187
@RALLY_US185
@RALLY_US103
Feature: Application Authorization Tool
As an SEA or LEA  Administrator / Operator, I want to be able to allow specific applications access to my district(s) data

	Scenario: District Super Administrator logs in to the authorization tool
	
	Given I have an open web browser
	Given I am a valid District Super Administrator for "Sunset School District"
	When I hit the Admin Application Authorization Tool
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
	Then I am redirected to the Admin Application Authorization Tool
	And I see a label in the middle "IL-SUNSET"
	And I see the list of all available apps on SLI
	And the authorized apps for my district are colored green
	And the unauthorized are colored red
	And are sorted by 'Status'
	And I see the Name, Version, Vendor and Status of the apps

Scenario: Non SLI-hosted valid user tries to access the Application Authorization Tool
	Given I have an open web browser
	When I hit the Admin Application Authorization Tool
     And I submit the credentials "administrator" "administrator1234" for the "Simple" login page
     Then the api should generate a 403 error

Scenario: Deny application
  
  Given I am an authenticated District Super Administrator for "Sunset School District"
  And I am logged into the Application Authorization Tool
  And I see an application "SDK Sample" in the table
  And in Status it says "Approved"
  And I click on the "Deny" button next to it
  And I am asked 'Do you really want deny access to this application of the district's data'
  When I click on Ok
  Then the application is denied to use data of "Sunset School District"
  And the app "SDK Sample" Status becomes "Not Approved"
  And it is colored "red"
  And the Approve button next to it is enabled
  And the Deny button next to it is disabled
  
	Scenario: Approve application
	
	Given I am an authenticated District Super Administrator for "Sunset School District"
	And I am logged into the Application Authorization Tool
	And I see an application "SDK Sample" in the table
	And in Status it says "Not Approved"
	And I click on the "Approve" button next to it
	And I am asked 'Do you really want this application to access the district's data'
	When I click on Ok
	Then the application is authorized to use data of "Sunset School District"
	And the app "SDK Sample" Status becomes "Approved"
	And it is colored "green"
	And the Approve button next to it is disabled
	And the Deny button next to it is enabled
