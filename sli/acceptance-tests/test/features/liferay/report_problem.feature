Feature: title
  In order to keep control of website information the expectation of the results 
  for report a problem it should have to work 

  Background:
    Given a normal user exists with "educator" and "educator1234"
    
  Scenario:Report a problem submition
    When I go to the login page
    When I login with "educator" and "educator1234"
    Then I should be on the home page
    When I follow "Report a problem"
    Then It open a popup
    Then I select "problem1" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field1"
    Then I fill "Some test Problems" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field2"
    Then I click "Report A Problem"
    Then I should see "The form information was sent successfully."
   
   
   Scenario:Report a problem cacelation
    When I go to the login page
    When I login with "educator" and "educator1234"
    Then I should be on the home page
    When I follow "Report a problem"
    Then It open a popup
    Then I select "problem1" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field1"
    Then I fill "Some test Problems" from "_1_WAR_webformportlet_INSTANCE_FzH4hF7yJhCX_field2"
    Then I click "Cancel"
    Then I should not see "The form information was sent successfully."
