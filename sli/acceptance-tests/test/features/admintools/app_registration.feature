@RALLY_US187
@RALLY_US103
Feature: Application Registration
As a super-admin I want to be able to create new application keys to allow the onboarding of new applications to SLI

Background:
Given I have an open web browser
And LDAP server has been setup and running

Scenario: SLI Developer Logging in

Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And I see all of the applications that are registered to SLI
And those apps are sorted by the Last Update column

Scenario: Non-SLI hosted user tries to access the App Registration Tool

Given I am a valid IT Administrator "administrator" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "administrator" "administrator1234" for the "Simple" login page
Then the api should generate a 403 error

Scenario: Register a new application

Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
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
And a notification email is sent to "slcoperator-email@slidev.org"

Scenario: View application details
Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And application "NewApp" does not have an edit link
When I click on the row of application named "NewApp" in the table
Then the row expands
And I see the details of "NewApp"
And all the fields are read only 

Scenario: SLC Operator denies application registration request
Given I am a valid SLC Operator "slcoperator-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
Then I am redirected to the Application Approval Tool page
And the pending apps are on top
And application "NewApp" is pending approval
When I click on 'Deny' next to application "NewApp"
And I get a dialog asking if I want to continue
When I click 'Yes'
Then application "NewApp" is not registered 
And application "NewApp" is removed from the list


Scenario: Vendor edits denied application incorrectly

Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And I clicked on the button Edit for the application "NewApp"
And I have edited the field named "Image URL" to say "http://placekitten.com/100/100"
And I have edited the field named "Description" to say ""
When I clicked Save
Then I should get 1 error

Scenario: Vendor edits denied application incorrectly for optional url fields

Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And I clicked on the button Edit for the application "NewApp"
And I have edited the field named "Image URL" to say "burp.com"
And I have edited the field named "Administration URL" to say "burp.com"
When I clicked Save
Then I should get 2 errors

Scenario: Vendor edits denied application

Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And I clicked on the button Edit for the application "NewApp"
And I have edited the field named "Image URL" to say "https://imageurl"
And I have edited the field named "Description" to say "Kittens"
When I clicked Save
And I the field named "Application Icon Url" still says "https://imageurl"
And I the field named "Description" still says "Kittens"

Scenario: SLC Operator accepts application registration request

Given I am a valid SLC Operator "slcoperator-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
Then I am redirected to the Application Approval Tool page
And I see all the applications registered on SLI
And I see all the applications pending registration
And the pending apps are on top
When I click on 'Approve' next to application "NewApp"
Then application "NewApp" is registered
And the 'Approve' button is disabled for application "NewApp"
And a notification email is sent to "developer-email@slidev.org"

Scenario: Vendor inspects app after approval 
Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And the client ID and shared secret fields are present

Scenario: SLC Operator un-registers already-registered application
Given I am a valid SLC Operator "slcoperator-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
Then I am redirected to the Application Approval Tool page
And application "NewApp" is registered
When I click on 'Deny' next to application "NewApp"
And I get a dialog asking if I want to continue
When I click 'Yes'
Then application "NewApp" is not registered 
And application "NewApp" is removed from the list


Scenario: Deleting Application

Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And I have clicked on the button 'Deny' for the application named "NewApp"
And I got warning message saying 'You are trying to remove this application from SLI. By doing so, you will prevent any active user to access it. Do you want to continue?'
When I click 'Yes'
Then the application named "NewApp" is removed from the SLI


@sandbox @ycao
Scenario: App Developer logs-in to App Registration Tool in Sandbox (Vendor in Prod should see own apps respectively)
	Given I am a valid App Developer
	When I hit the Application Registration Tool URL
	And I was redirected to the "Simple" IDP Login page
	And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
	Then I am redirected to the Application Registration Tool page
    Then I see the list of my registered applications only

@sandbox @ycao
Scenario: Different App developer in same tenant should also see my apps
    Given there is a "Application Developer" with tenancy "developer-email@slidev.org" and in "STANDARD-SEA"
    Then I can navigate to app registration page with that user
	Then I am redirected to the Application Registration Tool page
	Then I see the list of registered applications as well

@sandbox
Scenario: App Developer registers an application in App Registration Tool in Sandbox
	Given I am a valid App Developer
	When I hit the Application Registration Tool URL
		And I was redirected to the "Simple" IDP Login page
		And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
	Then I am redirected to the Application Registration Tool page
		And I have clicked to the button New
		And I am redirected to a new application page
	When I entered the name "NewApp" into the field titled "Name"
		And I have entered data into the other required fields except for the shared secret and the app id which are read-only
	When I click on the button Submit
	Then the application is registered
		And I can see the client ID and shared secret
		And the Registration Status field is Registered
	When I click on the In Progress button
	  Then I can see the on-boarded states


