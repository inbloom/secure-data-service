Feature: title
Navigate to dashboard link successfully using Illinois Realm under 'Select an Application' page
  
 # Background:
   #  Given EULA has been accepted  
   
  Scenario:-User Login through Illinois Realm with wrong username and wrong password
   Given I have an open web browser
    Then I am on the Realm selection page
    Then I select "Illinois Realm"
    #Then I click "Go"
    When I login with "jd" and "jd"
    Then I should be on the authentication failed page
    Then I should see "Authentication failed."
    
  Scenario:-User Login through Illinois Realm
    Given I have an open web browser
    Then I am on the Realm selection page
    Then I select "Illinois Realm"
    #Then I click "Go"
    When I login with "jdoe" and "jdoe1234"
    Then I should be on the home page
    Then I Should see "WGEN Dash Board"
    Then I follow "WGEN Dash Board"
    Then I select "Illinois Realm"
    #Then I click "Go"
    Then I should see "             Select an application         "
     
    
