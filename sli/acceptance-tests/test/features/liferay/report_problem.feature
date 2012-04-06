Feature: title
  In order to keep control of website information the expectation of the results 
  for report a problem it should have to work 

  #Background:
    #Given a normal user exists with "educator" and "educator1234"
    
  Scenario:Report a problem submition for SLI normal User
    Given I have an open web browser
    When I go to the login page
    When I login with "educator" and "educator1234"
    Then I should be on the home page
    When I follow "Report a Problem"
    Then It open a popup
    Then I select "problem1" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field1"
    Then I fill "Some test Problems" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field2"
    Then I click button "Report A Problem"
    Then I should see "The form information was sent successfully."
   
   
   Scenario:Report a problem non happy submission for SLI normal User
    Given I have an open web browser
    When I go to the login page
    When I login with "educator" and "educator1234"
    Then I should be on the home page
    When I follow "Report a Problem"
    Then It open a popup
  
    Then I click button "Report A Problem"
    Then I should not see "The form information was sent successfully."
    
  Scenario:Report a problem submition for SLI admin User
    Given I have an open web browser
    When I go to the login page
    When I login with "demo" and "changeit"
    Then I should be on the home page
    When I follow "Report a Problem"
    Then It open a popup
    Then I select "problem1" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field1"
    Then I fill "Some test Problems" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field2"
    Then I click button "Report A Problem"
    Then I should see "The form information was sent successfully."
   
   
   Scenario:Report a problem non happy submission for SLI admin User
    Given I have an open web browser
    When I go to the login page
    When I login with "demo" and "changeit"
    Then I should be on the home page
    When I follow "Report a Problem"
    Then It open a popup
    Then I click button "Report A Problem"
    Then I should not see "The form information was sent successfully."  
    
    Scenario: Report a problem happy submission for New York Realm User
     Given I have an open web browser
     Then I am on the Realm selection page
     Then I select "New York Realm"
     When I login with "demo" and "changeit"
     Then I should be on the home page
     When I follow "Report a Problem"
     Then It open a popup
     Then I select "problem1" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field1"
     Then I fill "Some test Problems" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field2"
     Then I click button "Report A Problem"
     Then I should see "The form information was sent successfully."
     
    Scenario: Report a problem non happy submission for New York Realm User
     Given I have an open web browser
     Then I am on the Realm selection page
     Then I select "New York Realm"
     When I login with "mario.sanchez" and "mario.sanchez1234"
     Then I should be on the home page
     When I follow "Report a Problem"
     Then It open a popup
     Then I click button "Report A Problem"
     Then I should not see "The form information was sent successfully." 
     
