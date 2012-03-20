@wip
Feature: Application Registration
As a super-admin I want to be able to create new application keys to allow the onboarding of new applications to SLI

Background:
Given I have an open web browser
	
Scenario: SLI Administrator Logging in

Given I am a valid SLI Administrator "demo" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "demo" and password "demo1234"
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

Given I am a valid SLI Administrator "demo" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "demo" and password "demo1234"
Then I am redirected to the Application Registration Tool page
And I have clicked to the button New
And a new row was created in the table
When I entered the name "NewApp" into the field titled "Name"
And I have ented data into the other requried fields except for the shared secret and the app id which are read-only
And I click on the button Save
Then the new application is created
And the application is listed in the table on the top
And a client ID is created for the new application that can be used to access SLI

Scenario: View application details

Given I am a valid SLI Administrator "demo" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "demo" and password "demo1234"
Then I am redirected to the Application Registration Tool page
When I click on the row of application named "NewApp" in the table
Then the row expands
And I see the details of "NewApp"
And all the fields are read only 

Scenario: Edit application

Given I am a valid SLI Administrator "demo" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "demo" and password "demo1234"
Then I am redirected to the Application Registration Tool page
And I clicked on the button ‘Edit’ 
And the row of the app "NewApp" expanded
And every field except the shared secret and the app ID became editable
And I have edited the field named "Image URL" to say "Kittens"
And I have edited the field named "Description" to say "Kittens"
When I clicked Save
Then the info for "NewApp" was updated
And I the field named "Image URL" still says "Kittens"
And I the field named "Description" still says "Kittens"

Scenario: Removing (Un-registering) Application

Given I am a valid SLI Administrator "demo" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I get redirected to the IDP login page
And I authenticate with username "demo" and password "demo1234"
Then I am redirected to the Application Registration Tool page
And I have clicked on the button 'X' for the application named "NewApp"
And I got warning message saying 'You are trying to remove this application from SLI. By doing so, you will prevent any active user to access it. Do you want to continue?'
When I click 'Yes'
Then the application named "NewApp" is removed from the SLI
And the previously generated client ID can no longer be used to access SLI
