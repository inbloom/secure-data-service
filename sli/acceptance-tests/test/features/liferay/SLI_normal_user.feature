Feature: title
  In order to keep control of website information the expectation of the results 
  As an normal User If you login, you should not see SLI administrator 

  Background:
    Given a normal user exists with "educator" and "educator1234"
   
   
 Scenario: Normal User Login with wrong username and password
    When I go to the login page
    When I login with "dem" and "change"
    Then I should be on the authentication failed page
    Then I should see "Authentication failed."
    
 Scenario: Normal User Login with blank username and blank password
    When I go to the login page
    When I login with "" and ""
    Then I should be on the authentication failed page
    Then I should see "Authentication failed."   
    
 Scenario: Normal User Login
    When I go to the login page
    When I login with "educator" and "educator1234"
    Then I should be on the home page
    Then I should see "Admin"
    Then I should logged out
