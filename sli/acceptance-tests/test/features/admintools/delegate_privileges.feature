@RALLY_US134
Feature: Delegating View Security Events from a district administrator to a state administrator

 Scenario: LEA Super Administrator delegating access to security event logs
  Given I have an open web browser
  When I hit the delegation url
    And I select "inBloom" from the dropdown and click go
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
    And I am redirected to the delegation page for my district
    And "View Security Events" is unchecked
    And I check the "View Security Events"
    And I should click the "Save" button
  Then I am redirected to the delegation page for my district
    And "View Security Events" is checked
