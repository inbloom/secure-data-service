@wip

Feature: Dashboard User Login Authentication

As a SEA/LEA user, I want to use the SLI IDP Login to authenticate 
on SLI, so I could use the Dashboard application.

Scenario: Go to Dashboard page when not authenticated to SLI

Given I have an open web browser
And I am not authenticated to SLI
When I type the Dashboard home page
And I click on the Enter button
Then I should be redirected to the Realm page

Scenario: Go to Dashboard page when authenticated to SLI

Given I have an open web browser
And I am authenticated to SLI
When I type the Dashboard home page
And I click on the Enter button
Then I should be redirected to the Dashboard home page

Scenario: Valid user login

Given I am not authenticated to SLI
And I have tried to access the Dashboard home page
And was redirected to the Realm page
And I chose "SLI IDP" 
And I clicked the button Go 
And I was redirected to the SLI IDP Login page
And I am user "demo"
And "demo" is valid "SLI IDP" user
When I enter "demo" in the username text field
And I enter "demo1234" in the password text field
And I click the Go button
Then I am authenticated to SLI
And I am redirected to the Dashboard home page

Scenario: Invalid user login

Given I am not authenticated to SLI
And I have tried to access the Dashboard home page
And was redirected to the Realm page
And I chose "SLI IDP" 
And I clicked the button Go 
And I was redirected to the SLI IDP Login page
And I am user "InvalidJohnDoe"
And "InvalidJohnDoe" is invalid "SLI IDP" user
When I enter "InvalidJohnDoe" in the username text field
And I enter "demo1234" in the password text field
And I click the Go button
Then I am informed that "InvalidJohnDoe" does not exists
And I am redirected to the SLI-IDP Login Page
