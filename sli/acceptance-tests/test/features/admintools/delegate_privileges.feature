@RALLY_US134
Feature: Delegating control from a district administrator to a state administrator

Scenario: State Administrator is allowed access to app authorizations
  Given I have an open web browser
  When I hit the Admin Application Authorization Tool
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
   And I am redirected to the Admin Application Authorization Tool
  Then I see the list of all available apps on SLI

  
# confirm obsoletion by F186 once requirements firm up
# US5866 will remove delegation for app authorization and the functionality
# tested here, so that this scenario can be removed.
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
 
# confirm obsoletion by F186 once requirements firm up
# US5866 will remove delegation for app authorization and the functionality
# tested here, so that this scenario can be removed.
Scenario: State Administrator gets access to app approval for one district
  Given I have an open web browser
  When I hit the Admin Application Authorization Tool
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
   And I am redirected to the Admin Application Authorization Tool
  Then I see the list of all available apps on SLI
  
# confirm obsoletion by F186 once requirements firm up
# US5866 will remove delegation for app authorization and the functionality
# tested here, so that this scenario can be removed.
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

# confirm obsoletion by F186 once requirements firm up
# US5866 will remove delegation for app authorization and the functionality
# tested here, so that this scenario can be removed.
 Scenario: State Administrator gets access to app approval for two districts and approves one app
  Given I have an open web browser
  When I hit the Admin Application Authorization Tool
   And I was redirected to the "Simple" IDP Login page
   And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
   And I am redirected to the Admin Application Authorization Tool
  Then I see the list of all available apps on SLI
  And I see an application "Testing App" in the table
#  And in Status it says "Not Approved"
#  And I click on the "Approve" button next to it
#  And I switch focus to the popup matching the regex "^Do you really.*access.*education organization.*data"
#  And I click on Ok
#  And the application is authorized to use data of "Daybreak School District"
#  And the app "Testing App" Status becomes "Approved"
#  And it is colored "green"
#  And the Approve button next to it is disabled
#  And the Deny button next to it is enabled
   Then I click on the "Edit Authorizations" button next to it
   And I deselect hierarchical mode
   And I authorize the educationalOrganization "Daybreak School District 4529"
   And I click Update
   And I see an application "Testing App" in the table
   And in Status it says "1 EdOrg(s)"

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
