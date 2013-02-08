@RALLY_US134
Feature: Delegating control from a district administrator to a state administrator

Scenario: State Administrator is denied access to app authorizations
  Given I have an open web browser
  When I hit the Admin Application Authorization Tool
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
   And I am redirected to the Admin Application Authorization Tool
   Then I get the message "You are not authorized to make application authorizations"

  
Scenario: District Super Administrator delegates app approval permission to state
	Given I have an open web browser
	When I hit the delegation url
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
    And I am redirected to the delegation page for my district
    And "Application Authorization" is unchecked
    And I check the "Application Authorization"
    And I should click the "Save" button
    Then I am redirected to the delegation page for my district
    And "Application Authorization" is checked
 
 
Scenario: State Administrator gets access to app approval for one district
  Given I have an open web browser
  When I hit the Admin Application Authorization Tool
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
   And I am redirected to the Admin Application Authorization Tool
  Then I see the list of all available apps on SLI
  
  Scenario: Second District Super Administrator delegates app approval permission to state
  Given I have an open web browser
  When I hit the delegation url
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
    And I am redirected to the delegation page for my district
    And "Application Authorization" is unchecked
    And I check the "Application Authorization"
    And I should click the "Save" button
    Then I am redirected to the delegation page for my district
    And "Application Authorization" is checked

 Scenario: State Administrator gets access to app approval for two districts and approves one app
  Given I have an open web browser
  When I hit the Admin Application Authorization Tool
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
   And I am redirected to the Admin Application Authorization Tool
  Then I see the list of all available apps on SLI
  And I see a dropdown box listing both districts
  And I select "Sunset School District 4526" in the district dropdown
  And I see the table for "Sunset School District 4526"
  And I do not see the table for "Daybreak School District 4529"
  And I select "Daybreak School District 4529" in the district dropdown  
  And I see the table for "Daybreak School District 4529"
  And I do not see the table for "Sunset School District 4526"
  And I see an application "Testing App" in the table
  And in Status it says "Not Approved"
  And I click on the "Approve" button next to it
  And I am asked 'Do you really want this application to access the district's data'
  And I click on Ok
  And the application is authorized to use data of "Daybreak School District"
  And the app "Testing App" Status becomes "Approved"
  And it is colored "green"
  And the Approve button next to it is disabled
  And the Deny button next to it is enabled
  And I see an application "Admin Delegation Test" in the table

 Scenario: LEA Super Administrator delegating access to security event logs
  Given I have an open web browser
  When I hit the delegation url
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
    And I am redirected to the delegation page for my district
    And "View Security Events" is unchecked
    And I check the "View Security Events"
    And I should click the "Save" button
  Then I am redirected to the delegation page for my district
    And "View Security Events" is checked
