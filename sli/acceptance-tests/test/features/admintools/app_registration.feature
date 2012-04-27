Feature: Application Registration
As a super-admin I want to be able to create new application keys to allow the onboarding of new applications to SLI

Background:
Given I have an open web browser

Scenario: SLI Developer Logging in

Given I am a valid SLI Developer "developer" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "developer" and password "developer1234"
Then I am redirected to the Application Registration Tool page
And I see all of the applications that are registered to SLI
And those apps are sorted by the Last Update column

Scenario: Non-SLI hosted user tries to access the App Registration Tool

Given I am a valid IT Administrator "administrator" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "administrator" and password "administrator1234"
Then I receive a message that I am not authorized

Scenario: Register a new application

Given I am a valid SLI Developer "developer" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "developer" and password "developer1234"
Then I am redirected to the Application Registration Tool page
And I have clicked to the button New
And I am redirected to a new application page
When I entered the name "NewApp" into the field titled "Name"
And I have entered data into the other required fields except for the shared secret and the app id which are read-only
And I click on the button Submit
Then I am redirected to the Application Registration Tool page
And the application "NewApp" is listed in the table on the top
And the client ID and shared secret fields are Pending
And the Registration Status field is Pending

Scenario: View application details
Given I am a valid SLI Developer "developer" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "developer" and password "developer1234"
Then I am redirected to the Application Registration Tool page
And application "NewApp" does not have an edit link
When I click on the row of application named "NewApp" in the table
Then the row expands
And I see the details of "NewApp"
And all the fields are read only 

Scenario: SLC Operator denies application registration request
Given I am a valid SLC Operator "operator" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "operator" and password "operator1234"
Then I am redirected to the Application Approval Tool page
And the pending apps are on top
And application "NewApp" is pending approval
When I click on 'X' next to application "NewApp"
And I get a dialog asking if I want to continue
When I click 'Yes'
Then application "NewApp" is not registered 
And application "NewApp" is removed from the list

Scenario: Vendor edits denied application

Given I am a valid SLI Developer "developer" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "developer" and password "developer1234"
Then I am redirected to the Application Registration Tool page
And I clicked on the button Edit for the application "NewApp"
And every field except the shared secret and the app ID became editable
And I have edited the field named "Image URL" to say "http://placekitten.com/100/100"
And I have edited the field named "Description" to say "Kittens"
When I clicked Save
And I the field named "Image URL" still says "http://placekitten.com/100/100"
And I the field named "Description" still says "Kittens"

Scenario: SLC Operator accepts application registration request

Given I am a valid SLC Operator "operator" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "operator" and password "operator1234"
Then I am redirected to the Application Approval Tool page
And I see all the applications registered on SLI
And I see all the applications pending registration
And the pending apps are on top
When I click on 'Y' next to application "NewApp"
Then application "NewApp" is registered
And the 'Y' button is disabled for application "NewApp"

Scenario: SLC Operator un-registers already-registered application
Given I am a valid SLC Operator "operator" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "operator" and password "operator1234"
Then I am redirected to the Application Approval Tool page
And application "NewApp" is registered
When I click on 'X' next to application "NewApp"
And I get a dialog asking if I want to continue
When I click 'Yes'
Then application "NewApp" is not registered 
And application "NewApp" is removed from the list


Scenario: Deleting Application

Given I am a valid SLI Developer "developer" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "developer" and password "developer1234"
Then I am redirected to the Application Registration Tool page
And I have clicked on the button 'X' for the application named "NewApp"
And I got warning message saying 'You are trying to remove this application from SLI. By doing so, you will prevent any active user to access it. Do you want to continue?'
When I click 'Yes'
Then the application named "NewApp" is removed from the SLI


@sandbox @wip
Scenario: App Developer logs-in to App Registration Tool in Sandbox (Vendor in Prod should see own apps respectively)
	Given I am a valid App Developer
	When I hit the App Registration Tool Sandbox 
		And I am redirected to the hosted IDP
		And I submit my username and password
		And I click on Login
	Then I am logged-in to the App Registration Tool
		And I see the list of my registered applications only

@sandbox @wip
Scenario: App Developer registers an application in App Registration Tool in Sandbox
	Given I am a valid App Developer
		And I am authenticated to the App Registration Tool in Sandbox
		And I have clicked on New Application
		And I have entered the application information
	When I click Register
	Then the application is registered
		And I can see the client ID and shared secret
		And the Registration Status field is Registered

#Enable when email support is available. Additional "Pending" verification is in normal app registration step
@wip
Scenario: Vendor send registration request for an application in App Registration Tool in Production
	Given I am a valid Vendor
		And I am authenticated to the App Registration Tool in Production
		And I have clicked on New Application
		And I have entered the application information
	When I click Register
	Then the application registration request is sent to the SLC Operator
		And an email notification 'Vendor ABC wants to register the App xzy, version 123' is sent to the SLC Operator
		And the client ID and shared secret fields are Pending
		And the Registration Status field is Pending

