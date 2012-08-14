@rc
Feature:  Portal Login 

Background:
Given I have an open web browser
When I navigate to the Portal home page

Scenario: SEA Login
When I selected the realm "Shared Learning Infrastructure"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Account Approval"
And I click on log out

#TODO log in as jstevenson, make sure i dont' see dash/databrowser

Scenario:  Daybreakadmin approves Dashboard and Databrowser
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

Scenario: Admin Logs into databrowser from portal
When I selected the realm "Illinois Daybreak School District 4529"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under Application Configuration, I see the following: "SLC Dashboards"
And under System Tools, I see the following "SLC Data Browser"
And under System Tools, I click on "SLC Data Browser"
