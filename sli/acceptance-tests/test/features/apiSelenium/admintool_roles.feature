Feature: Admin Tool SLI Default Roles and Permissions Page
 
As a SLI Operator/Administrator, I want to login to the SLI Default Roles Admin Page,
so I could get an information about the default roles in SLI and their permissions.

@wipn
Scenario: Go to SLI Default Roles Admin Page when not authenticated to SLI IDP
 
Given I have an open web browser
And I am not authenticated to SLI IDP
When I navigate to the SLI Default Roles Admin Page
Then I should be redirected to the Realm page
 
 @wipn
Scenario: Go to SLI Default Roles Admin Page when authenticated to SLI IDP
 
Given I have an open web browser
And I am authenticated to SLI IDP
When I navigate to the SLI Default Roles Admin Page
Then I should be redirected to the SLI Default Roles Admin Page
 
 @wipn
Scenario: Valid SLI IDP user login to SLI Default Roles Admin Page
 
Given I am not authenticated to SLI IDP
And I have tried to access the SLI Default Roles Admin Page
And I was redirected to the Realm page
And I choose my realm
And I was redirected to the SLI IDP Login page
And I am user "demo"
And "demo" is valid "SLI IDP" user
When I enter "demo" in the username text field
And I enter "demo1234" in the password text field
And I click the Go button
Then I am authenticated to SLI IDP
And I should be redirected to the SLI Default Roles Admin Page
 
 @wip
Scenario: Invalid SLI IDP user login to SLI Default Roles Admin Page
 
Given I am not authenticated to SLI IDP
And I have tried to access the SLI Default Roles Admin Page
And I was redirected to the SLI IDP Login page
And I am user "InvalidJohnDoe"
And "InvalidJohnDoe" is invalid "SLI IDP" user
When I enter "InvalidJohnDoe" in the username text field
And I enter "badpass" in the password text field
And I click the Go button
Then I am informed that "InvalidJohnDoe" does not exists
And I am redirected to the SLI-IDP Login Page
 
 @wip
Scenario:  SLI Default Roles Admin Page logout
 
Given I am authenticated to SLI IDP
And I have navigated to the SLI Default Roles Admin Page
When I click on the Logout link
Then I am not authenticated to SLI IDP
And I am redirected to the SLI-IDP Login Page
 
 @wip
Scenario:  SLI Default Roles Admin Page - Clicking on Default SLI Roles and Permissions URL
 
Given I am authenticated to SLI IDP
And I have navigated to the SLI Default Roles Admin Page
When I click on the Default SLI Roles and Permissions URL
Then the browser opens the confluence Default SLI Roles and Permissions page in a new browser window
And the browser focus is on the new browser window
