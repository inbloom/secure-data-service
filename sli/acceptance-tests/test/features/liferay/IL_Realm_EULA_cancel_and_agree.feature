Feature: title
Navigate to dashboard link successfully using Illinois Realm under 'Select an Application' page
  
  Background:
     Given EULA has been canceled
  
  Scenario:-User Login through Illinois Realm with wrong username and wrong password
    Then I am on the Realm selection page
    Then I select "Illinois Realm"
    Then I click "Go"
    When I login with "jd" and "jd"
    Then I should be on the authentication failed page
    Then I should see "Authentication failed."
  
  Scenario:-User Logs in through Illinois Realm, cancels the EULA and then agrees
    Then I am on the Realm selection page
    Then I select "Illinois Realm"
    Then I click "Go"
    When I login with "jdoe" and "jdoe1234"
    Then I should see "License Agreement"
    Then I click "Cancel"
    Then I should see " Sorry You need to accept License Agreement to progress further!!! "
    Then I follow "TabsBack"
    Then I should see "License Agreement"
    Then I click "Agree"
    Then I should be on the home page
    Then I Should see "WGEN Dash Board"
