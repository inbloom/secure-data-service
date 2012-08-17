@rc
Feature:  RC Integration Tests

Background:
Given I have an open web browser

#This is just a sample of how portal is integrated
Scenario: SEA Login
When I navigate to the Portal home page
When I selected the realm "Shared Learning Infrastructure"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Account Approval"
And I click on log out

Scenario: Realm Admin Logins to create realm
When I navigate to the Portal home page
When I selected the realm "Shared Learning Infrastructure"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page  
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Realm Management"
And I switch to the iframe
And I should see that I am on the new realm page
And all of the input fields should be blank
And I should enter "Daybreak Test Realm" into the Display Name field
And I should enter "http://local.slidev.org:8082/simple-idp?realm=IL-Daybreak" into IDP URL
And I should enter "http://local.slidev.org:8082/simple-idp?realm=IL-Daybreak" into Redirect Endpoint
And I should enter "IL-Daybreak" into Realm Identifier
And I should click the "Save" button
Then I should be redirected back to the edit page
And I switch to the iframe
And I should receive a notice that the realm was successfully "created"
And I should see that I am on the "Daybreak Test Realm" edit page

Scenario: User cannot access Bootstrapped Apps before approval
When I navigate to the Portal home page
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should not see "SLC Dashboards"
And I click on Admin
And I should be on the admin page
And I should not see "SLC Data Browser"

Scenario:  Daybreakadmin approves Dashboard and Databrowser
When I navigate to the Portal home page
When I selected the realm "Shared Learning Infrastructure"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Application Authorization"
Then I am redirected to the Admin Application Authorization Tool
And I switch to the iframe
And I see an application "SLC Dashboards" in the table
And in Status it says "Not Approved"
And I click on the "Approve" button next to it
And I am asked 'Do you really want this application to access the district's data'
When I click on Ok
Then the application is authorized to use data of "Daybreak School District 4529"
And is put on the top of the table
And the Status becomes "Approved"
And it is colored "green"
And the Approve button next to it is disabled
And the Deny button next to it is enabled
And I see an application "SLC Data Browser" in the table
And in Status it says "Not Approved"
And I click on the "Approve" button next to it
And I am asked 'Do you really want this application to access the district's data'
When I click on Ok
# switch back to iframe because of the page reload
And I switch to the iframe
Then the application is authorized to use data of "Daybreak School District 4529"
And is put on the top of the table
And the Status becomes "Approved"
And it is colored "green"
And the Approve button next to it is disabled
And the Deny button next to it is enabled
#  deny
 # And I see an application "SLC Dashboards" in the table
 # And in Status it says "Approved"
 # And I click on the "Deny" button next to it
 # And I am asked 'Do you really want deny access to this application of the district's data'
 # When I click on Ok
 # Then the application is denied to use data of "Daybreak School District 4529"
  # And I see an application "SLC Data Browser" in the table
  #And in Status it says "Approved"
  #And I click on the "Deny" button next to it
  #And I am asked 'Do you really want deny access to this application of the district's data'
  #When I click on Ok
  # Not exactly sure why i need to switch to iframe again
#And I switch to the iframe
  #Then the application is denied to use data of "Daybreak School District 4529"
  #TODO switch context back
#And I click on log out

Scenario: User Logs into databrowser from portal
When I navigate to the Portal home page
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under Application Configuration, I see the following: "SLC Dashboards"
And under System Tools, I see the following "SLC Data Browser"
And under System Tools, I click on "SLC Data Browser"

Scenario: Sessions are shared between apps
When I navigate to the dashboard page
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
And I am redirected to the dashboard home page
When I navigate to the databrowser page
Then I do not see any login pages
And I click on the logout link
Then I should see a message that I was logged out
And I should forced to reauthenticate to gain access
When I navigate to the dashboard home page
Then I should forced to reauthenticate to gain access

Scenario: User accesses Developer App 
When I navigate to the Portal home page
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page    
Then I should be on Portal home page
# Enable after we know what the dev app is
#And under My Applications, I see the following apps: "SLC Dashboards;"
#And under My Applications, I click on "Dev App"
#Then I should see "app"
	
