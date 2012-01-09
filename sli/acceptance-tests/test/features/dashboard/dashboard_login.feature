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
And I wait for "2" seconds
Then I should be redirected to the Dashboard landing page

Scenario: Valid user login

Given I have an open web browser
And I am not authenticated to SLI
And I navigate to the Dashboard home page
And was redirected to the Realm page
And I chose "SLI IDP" 
And I clicked the Go button
And was redirected to the SLI-IDP login page
And I enter "cgray" in the username text field and "cgray" in the password text field
And I clicked the Submit button
And I wait for "2" seconds
Then I should be redirected to the Dashboard landing page

Scenario: Invalid user login

Given I have an open web browser
And I am not authenticated to SLI
And I navigate to the Dashboard home page
And was redirected to the Realm page
And I chose "SLI IDP" 
And I clicked the Go button 
And was redirected to the SLI-IDP login page
And I enter "InvalidJohnDoe" in the username text field and "demo1234" in the password text field
And I clicked the Submit button
And I wait for "2" seconds
Then I am informed that "InvalidJohnDoe" does not exists
And I am redirected to the SLI-IDP Login page

Scenario: hitting denied URL

Given I have an open web browser
And I am authenticated to SLI as "cgray" password "cgray"
When I access "dashboard/simon"
Then I get an error code "403"

Scenario: hitting static URL

Given I have an open web browser
And I am not authenticated to SLI
When I access "/static/html/test.html" 
Then I can see "Static HTML page"

Scenario: hitting protected URL

Given I have an open web browser
And I am not authenticated to SLI
When I access "/studentlist"
And I wait for "1" seconds
Then I should be redirected to the Realm page
