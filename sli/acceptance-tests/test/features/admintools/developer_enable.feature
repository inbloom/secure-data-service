Feature:
	As a developer I want to be able to enable my application for specific states and districts.
	As an operator I want to be able to approve the applications that developers enabled.
	
Background:
	Given I have an open web browser

@wip
Scenario: App Developer or Vendor enabling application for a District
Given I am a valid SLI Developer "developer" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
	And I get redirected to the IDP login page
	And I authenticate with username "developer" and password "developer1234"
Then I am redirected to the Application Registration Tool page
	And I see the list of (only) my applications
	And I click on the row of application named "Testing App" in the table
Then I can see the on-boarded states/districts
Then I check the <District>
When I click on Save
Then the "Testing App" is enabled for <District>
Then I log out
Then I log in as a valid SLI Operator "operator" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
	And I get redirected to the IDP login page
	And I authenticate with username "operator" and password "operator1234"
	And I am redirected to the Application Registration Tool page
	And I am logged in to my Application Registration tool
Then I see the newly enabled application