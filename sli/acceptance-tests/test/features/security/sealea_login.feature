@wip
Feature: SEA/LEA User Login through ADFS/OpenAM
As a SEA/LEA user, I want to be authenticated to SLI with my existing (SEA/LEA) credentials, so I could query the SLIs API.
 
Scenario: Calling the API when logged in
 
Given I have an open web browser
And I am authenticated to SLI
When I type a RESTful web API URI
And I click on the Enter button
Then I should get the response JSON object displayed in the browser
 
Scenario:  Calling the API when not logged in
 
Given I have an open web browser
And I am not authenticated to SLI
When I type a RESTful web API URI
And I click on the Enter button
Then I should be redirected to the Realm page
 
Scenario: SLI Authentication when trying to login to a SEA/LEA directory with valid username and valid password
 
Given I have navigated to the <State/District> Login Page
And I am user  <JohnDoe>
And <JohnDoe> is valid <State/District> user
When I enter <JohnDoe> in the username text field
And I enter  <***> in the password text field
And I click the Go button
Then I am authenticated to SLI
 
Scenario: SLI Authentication when trying to login to a SEA/LEA directory with valid username and invalid password
 
Given I have navigated to the <State/District> Login Page
And I am user  <JohnDoe>
And <JohnDoe> is valid  <State/District> user
When I enter  <JohnDoe> in the username text field
And I enter <invalid***> in the password text field
And I click the Go button
Then I am informed that I have entered invalid password
 
Scenario: SLI Authentication when trying to login to a SEA/LEA directory with invalid user
 
Given I have navigated to <State/District> login page
And I am user  <InvalidJohnDoe>
And <InvalidJohnDoe> is invalid  <State/District> user
When I enter  <InvalidJohnDoe> in the username text field
And I enter <invalid***> in the password text field
And I click the Go button
Then I am informed that <InvalidJohnDoe> is an invalid user