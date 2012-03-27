Feature: title
Navigate to dashboard link successfully using New York Realm under 'Select an Application' page
  
  #Background:
     #Given EULA has been accepted  
 
  Scenario:-User Login through New York Realm with wrong username and password
    Given I have an open web browser
    Then I am on the Realm selection page
    Then I select "New York Realm"
    #Then I click "Go"
    When I login with "mario.sanc" and "mario.sanchez"
    Then I should be on the authentication failed page
    Then I should see "Authentication failed."
    
  
  Scenario:-User Login through New York Realm
    Given I have an open web browser
    Then I am on the Realm selection page
    Then I select "New York Realm"
    #Then I click "Go"
    When I login with "mario.sanchez" and "mario.sanchez1234"
    Then I should be on the home page
    Then I should see " Dashboards"
    Then I follow "Dashboards"
    Then I select "New York Realm"
    #Then I click "Go"
    Then I should see "             Select an application         "
    Then I should see "Learning Map"
    Then I should see "Dashboard"
    
