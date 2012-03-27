Feature: title
Navigate to dashboard link successfully using New York Realm under 'Select an Application' page
  
  Background:
     Given EULA has been canceled
  
   Scenario:-User Login through New York Realm with wrong username and password
  
    Then I am on the Realm selection page
    Then I select "New York Realm"
    Then I click "Go"
    When I login with "mario.sanc" and "mario.sanchez"
    Then I should be on the authentication failed page
    Then I should see "Authentication failed." 
  
  Scenario:-User Logs in through New York Realm, cancels the EULA and then agrees
    Then I am on the Realm selection page
    Then I select "New York Realm"
    Then I click "Go"
    When I login with "mario.sanchez" and "mario.sanchez1234"
    Then I should see "License Agreement"
    Then I click "Cancel"
    Then I should see " Sorry You need to accept License Agreement to progress further!!! "
    Then I follow "TabsBack"
    Then I should see "License Agreement"
    Then I click "Agree"
    Then I should be on the home page
    Then I Should see "WGEN Dash Board"
