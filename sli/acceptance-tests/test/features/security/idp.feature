@wip 
Feature:<US417> Authentication: SLI Identity Provider
 
As a SLI/SEA/LEA Administrator
I would like to login to the SLI-IDP
So that I could perform administrative functions
 
Scenario: Go to SLI-IDP Login Page when not logged
 
Given I have an open web browser
And I am not Logged-in to SLI-IDP
When I type the SLI-IDP Login Page URL
And I click on the Enter button
Then I should be redirected to the SLI-IDP Login Page
 
Scenario:Redirect to SLI-IDP Login Page when not logged
 
Given I have an open web browser
And I am not Logged-in to SLI-IDP
When I type the SLI-IDP Home Page URL
And I click on the Enter button
Then I should be redirected to the SLI-IDP Login Page
 
Scenario: SLI-IDP Home Page Redirect when logged
 
Given I have an open web browser
And I am Logged-in to SLI-IDP
When I type the SLI-IDP Home Page URL
And I click on the Enter button
Then I should be redirected to the SLI-IDP Home Page
 
Scenario: Login with valid username and valid password
 
Given I have navigated to the SLI-IDP Login Page
Given I am user  <JohnDoe>
And <JohnDoe> is valid SLI-IDP user
When I enter <JohnDoe> in the username text field
And I enter  <***> in the password text field
And I click the Go button
Then I am logged-in
And I am redirected to the SLI-IDP Home Page
 
Scenario: Login with valid username and invalid password
 
Given I am user  <JohnDoe>
And <JohnDoe> is valid SLI-IDP user
When I enter  <JohnDoe> in the username text field
And I enter <invalid***> in the password text field
And I click the Go button
Then I am informed that I have entered invalid password
And I am redirected to the SLI-IDP Login Page
 
Scenario: Multiple unsuccessful logins with a valid user
 
Given I am user  <JohnDoe>
And  <JohnDoe> is valid SLI-IDP user
And I have unsuccessfully tried to login 4 times
When I enter <JohnDoe> in the username text field
And I enter <invalid***> in the password text field
And I click the Go button
Then I am informed that the user <JohnDoe> is locked for 30 mins
And I am redirected to the SLI-IDP Login Page
 
Scenario: Login with invalid user
 
Given I am user <non-existing/invalid>
And <non-existing/invalid> is not valid SLI-IDP user
When I enter <non-existing/invalid> in the username text field
And I enter <***> in the password text field
And I click the Go button
Then I am informed that <non-existing/invalid> does not exists
And I am redirected to the SLI-IDP Login Page
 
Scenario: Logout
 
Given I am logged in to SLI-IDP
When I click on the Logout link
Then I am logged out
And I am redirected to the SLI-IDP Login Page