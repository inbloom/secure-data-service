@wip
Feature: SEA/LEA User Login through ADFS/OpenAM
As a SEA/LEA user, I want to be authenticated to SLI with my existing (SEA/LEA) credentials, so I could query the SLIs API.
 

Scenario: SLI Authentication when trying to login to a SEA/LEA directory with valid username and valid password
 
Given I have navigated to the "State/District" login page
And I am user "JohnDoe"
And "JohnDoe" is valid "State/District" user
When I enter "JohnDoe" in the username text field
And I enter "***" in the password text field
And I click the Go button
Then I am now authenticated to SLI
 
Scenario: SLI Authentication when trying to login to a SEA/LEA directory with valid username and invalid password
 
Given I have navigated to the "State/District" login page
And I am user "JohnDoe"
And "JohnDoe" is valid "State/District" user
When I enter "JohnDoe" in the username text field
And I enter "invalid***" in the password text field
And I click the Go button
Then I am informed that I have entered invalid password
 
Scenario: SLI Authentication when trying to login to a SEA/LEA directory with invalid user
 
Given I have navigated to the "State/District" login page
And I am user "InvalidJohnDoe"
And "InvalidJohnDoe" is invalid "State/District" user
When I enter "InvalidJohnDoe" in the username text field
And I enter "invalid***" in the password text field
And I click the Go button
Then I am informed that "InvalidJohnDoe" is an invalid user