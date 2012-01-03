#@wip

Feature: Dashboard User Login Authentication

As a SEA/LEA user, I want to use the SLI IDP Login to authenticate 
on SLI, so I could use the Dashboard application.

Scenario: Go to Dashboard page when not authenticated to SLI

Given I have an open web browser
And I am not authenticated to SLI
When I navigate to the Dashboard home page
Then I should be redirected to the Realm page

Scenario: Go to Dashboard page when authenticated to SLI

Given I have an open web browser
And I am authenticated to SLI as "cgray" password "cgray"
When I navigate to the Dashboard home page
Then I should be redirected to the Dashboard home page

Scenario: Valid user login

Given I have an open web browser
And I am not authenticated to SLI
And I navigate to the Dashboard home page
And was redirected to the Realm page
And I chose "SLI IDP" 
And I clicked the Go button
And was redirected to the SLI-IDP login page
And I enter "demo" in the username text field and "demo1234" in the password text field
And I clicked the Go button
Then I am redirected to the Dashboard home page

Scenario: Invalid user login

Given I have an open web browser
And I am not authenticated to SLI
And I navigate to the Dashboard home page
And was redirected to the Realm page
And I chose "SLI IDP" 
And I clicked the Go button 
And was redirected to the SLI-IDP login page
And I enter "InvalidJohnDoe" in the username text field and "demo1234" in the password text field
And I click the Go button
Then I am informed that "InvalidJohnDoe" does not exists
And I am redirected to the SLI-IDP Login Page

Scenario: hitting diff types of URLs (protected, deny, static)

# Consider performing logout to make sure no auth
Given I have an open web browser
And I am not authenticated to SLI 
When I access "dashboard/simon"
Then I get an error code "???"

# TODO figure out what a good test URL and what should be the output
When I access "dashboard/static/*" 
Then I can see "fill desired content here"

When I access "dashboard/studentlist"
Then I am redirected to the SLI IDP Login page
