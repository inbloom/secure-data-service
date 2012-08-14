@rc
Feature:  Portal Login 

Background:
Given I have an open web browser
When I navigate to the Portal home page

@wip
Scenario: SEA Login
When I select "Shared Learning Infrastructure" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Account Approval"
And I click on log out

#TODO log in as jstevenson, make sure i dont' see dash/databrowser

Scenario:  Daybreakadmin approves Dashboard
When I select "Shared Learning Infrastructure" and click go
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
And I switch to the iframe
Then the application is authorized to use data of "Daybreak School District 4529"
And is put on the top of the table
And the Status becomes "Approved"
And it is colored "green"
And the Approve button next to it is disabled
And the Deny button next to it is enabled

@wip
Scenario:  Daybreakadmin approves Databrowser
When I select "Shared Learning Infrastructure" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Application Authorization"
Then I am redirected to the Admin Application Authorization Tool
And I switch to the iframe
And I see an application "SLC Data Browser" in the table
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

Scenario: Admin Logs into databrowser from portal
When I select "Illinois Daybreak School District 4529" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under Application Configuration, I see the following: "SLC Dashboards"
And under System Tools, I see the following "SLC Data Browser"
And under System Tools, I click on "SLC Data Browser"

#TODO: daybreakadmin approves slc dashboard and slc data browser
#TODO add log out test
