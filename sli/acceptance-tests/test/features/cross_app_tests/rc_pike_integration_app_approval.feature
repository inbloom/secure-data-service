@RALLY_US4835
@rc
Feature:  RC Integration Tests

Background:
Given I have an open web browser

Scenario: Realm Admin Logins to create realm
When I navigate to the Portal home page
When I see the realm selector I authenticate to "inBloom"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "<SECONDARY_EMAIL>" "<SECONDARY_EMAIL_PASS>" for the "Simple" login page
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Manage Realm"
And I switch to the iframe
And I should see that I am on the new realm page
And all of the input fields should be blank
And I should enter "Daybreak Test Realm" into the Display Name field
And I enter "<CI_IDP_Redirect_URL>" in the IDP URL field
And I enter "<CI_IDP_Redirect_URL>" in the Redirect Endpoint field
And I should enter "RC-IL-Daybreak" into Realm Identifier
And I should click the "Save" button
And I switch to the iframe
And I should receive a notice that the realm was successfully "created"
Then I see the realms for "Daybreak School District 4529 (IL-DAYBREAK)"
And the realm "Daybreak Test Realm" will exist
And I exit out of the iframe
And I click on log out

Scenario: User cannot access Bootstrapped Apps before approval
When I navigate to the Portal home page
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be on Portal home page
Then I should not see "inBloom Dashboards"
And I click on Admin
And I should be on the admin page
And I should not see "inBloom Data Browser"
And I click on log out

Scenario: App developer creates new Installed app and Full window web app
When I navigate to the Portal home page
When I see the realm selector I authenticate to "inBloom App Developers"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Register Application"
And I switch to the iframe
#Create a new installed App
Then I am redirected to the Application Registration Tool page
And I have clicked to the button New
And I am redirected to a new application page
When I entered the name "NotTheAppYoureLookingFor" into the field titled "Name"
And I entered the name "Best.  Description.  Ever." into the field titled "Description"
And I entered the name "0.0" into the field titled "Version"
And I make my app an installed app
And I click on the button Submit
And I switch to the iframe
Then I am redirected to the Application Registration Tool page
And the application "NotTheAppYoureLookingFor" is listed in the table on the top
And the client ID and shared secret fields are present
And I exit out of the iframe
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Register Application"
And I switch to the iframe
Then I am redirected to the Application Registration Tool page
#Create a new web full window app
And I have clicked to the button New
And I am redirected to a new application page
When I entered the name "Schlemiel" into the field titled "Name"
And I entered the name "Yes, I totally made Schlemiel the painter's algorithm for SLI'" into the field titled "Description"
And I entered the name "1.0" into the field titled "Version"
And I entered the name "http://www.google.com" into the field titled "Application_URL"
And I entered the name "http://wwww.google.com" into the field titled "Redirect_URI"
And I select the app display method to "Full Window App" 
And I click on the button Submit
Then I am redirected to the Application Registration Tool page
And the application "Schlemiel" is listed in the table on the top
And the client ID and shared secret fields are present
And I exit out of the iframe
And I click on log out

Scenario: App developer enables the new Installed app and Full window web app
When I navigate to the Portal home page
When I see the realm selector I authenticate to "inBloom App Developers"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Register Application"
And I switch to the iframe
Then I am redirected to the Application Registration Tool page
And I see an application "NotTheAppYoureLookingFor" in the table
And the client ID and shared secret fields are present
And I clicked on the button Edit for the application "NotTheAppYoureLookingFor"
Then I can see the on-boarded states
When I select a state
Then I see all of the Districts
Then I check the Districts
When I click on Save
Then the "NotTheAppYoureLookingFor" is enabled for Districts
And I see an application "Schlemiel" in the table
And my new apps client ID is present
And my new apps shared secret is present
And I clicked on the button Edit for the application "Schlemiel"
Then I can see the on-boarded states
When I select a state
Then I see all of the Districts
Then I check the Districts
When I click on Save
Then the "Schlemiel" is enabled for Districts
And I exit out of the iframe
And I click on log out

Scenario:  LEA approves Dashboard, Databrowser and New Developer Applications
When I navigate to the Portal home page
When I see the realm selector I authenticate to "inBloom"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "<SECONDARY_EMAIL>" "<SECONDARY_EMAIL_PASS>" for the "Simple" login page    
Then I should be on Portal home page
Then I should see Admin link
And I click on Admin
Then I should be on the admin page
And under System Tools, I click on "Authorize Applications"
And I switch to the iframe
Then I am redirected to the Admin Application Authorization Tool
#Authorize the Dashboard
And I see an application "inBloom Dashboards" in the table
And in Status it says "Not Approved"
And I click on the "Approve" button next to it
And I am asked 'Do you really want this application to access the district's data'
When I click on Ok
Then the application is authorized to use data of "Daybreak School District 4529"
And the app "inBloom Dashboards" Status becomes "Approved"
And it is colored "green"
And the Approve button next to it is disabled
And the Deny button next to it is enabled
#Authorize the Databrowser
And I see an application "inBloom Data Browser" in the table
And in Status it says "Not Approved"
And I click on the "Approve" button next to it
And I am asked 'Do you really want this application to access the district's data'
When I click on Ok
# switch back to iframe because of the page reload
And I switch to the iframe
Then the application is authorized to use data of "Daybreak School District 4529"
And the app "inBloom Data Browser" Status becomes "Approved"
And it is colored "green"
And the Approve button next to it is disabled
And the Deny button next to it is enabled
#Authorize the New Installed App
And I see an application "NotTheAppYoureLookingFor" in the table
And in Status it says "Not Approved"
And I click on the "Approve" button next to it
And I am asked 'Do you really want this application to access the district's data'
When I click on Ok
# switch back to iframe because of the page reload
And I switch to the iframe
Then the application is authorized to use data of "Daybreak School District 4529"
And the app "NotTheAppYoureLookingFor" Status becomes "Approved"
And it is colored "green"
And the Approve button next to it is disabled
And the Deny button next to it is enabled
#Authorized the new Web-App
And I see an application "Schlemiel" in the table
And in Status it says "Not Approved"
And I click on the "Approve" button next to it
And I am asked 'Do you really want this application to access the district's data'
When I click on Ok
# switch back to iframe because of the page reload
And I switch to the iframe
Then the application is authorized to use data of "Daybreak School District 4529"
And the app "Schlemiel" Status becomes "Approved"
And it is colored "green"
And the Approve button next to it is disabled
And the Deny button next to it is enabled
And I exit out of the iframe
And I click on log out

#US4878: Logout Test Coverage
@wip 
Scenario: Sessions are shared between Databrowser and Dashboard apps
When I navigate to the databrowser page
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be redirected to the Data Browser home page
When I navigate to the dashboard page
Then I do not see any login pages
And I am redirected to the dashboard home page
And I click on log out
Then I will be redirected to the realm selector web page
When I navigate to the databrowser page
Then I should forced to reauthenticate to gain access

Scenario: Sessions are shared between Dashboard and Databrowser apps
When I navigate to the Portal home page
When I select "Daybreak Test Realm" and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page    
Then I should be on Portal home page
When I navigate to the dashboard page
And I am redirected to the dashboard home page
When I navigate to the databrowser page
Then I do not see any login pages
Then I am redirected to the databrowser home page
And I exit out of the iframe
And I click on log out
When I navigate to the databrowser page
And I should forced to reauthenticate to gain access
When I navigate to the dashboard home page
Then I should forced to reauthenticate to gain access


Scenario: User sees non-installed Developer App 
When I navigate to the Portal home page
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page    
Then I should be on Portal home page
And under My Applications, I see the following apps: "inBloom Dashboards;Schlemiel"
And under My Applications, I click on "Schlemiel"
Then my current url is "http://www.google.com/"	

Scenario: User logs into recently created installed app
#We cannot use the portal to access the installed app, since you cannot navigate to a URL to use it
Given the testing device app key has been created
When I navigate to the API authorization endpoint with my client ID
When I select "Daybreak Test Realm" and click go
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "cgray" "cgray1234" for the "Simple" login page
Then I should receive a json response containing my authorization code
When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
Then I should receive a json response containing my authorization token
And I should be able to use the token to make valid API calls

Scenario: Educator performs student search
Given the testing device app key has been created
When I navigate to the API authorization endpoint with my client ID
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "cgray" "cgray1234" for the "Simple" login page
Then I should receive a json response containing my authorization code
When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
Then I should receive a json response containing my authorization token
Given I search in API for "Daniella"
Then I should receive a return code of 200
And I see the following search results at index 0:
 |Field              |Value                                           |
 |name.middleName    |Daniella                                        |
 |name.lastSurname   |Ortiz                                           |
 |name.firstName     |Carmen                                          |
 Given I search in API for "matt"
 Then I should receive a return code of 200
 Then I should receive a collection with 0 elements
 Given I search in API for "tran"
 Then I should receive a return code of 200
 Then I should receive a collection with 0 elements

Scenario: Staff performs student search
Given the testing device app key has been created
When I navigate to the API authorization endpoint with my client ID
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
Then I should receive a json response containing my authorization code
When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
Then I should receive a json response containing my authorization token
Given I search in API for "rudolp"
Then I should receive a return code of 200
And I see the following search results at index 0:
 |Field              |Value                                           |
 |name.firstName     |Rudolph                                         |
 And I see the following search results at index 1:
 |Field              |Value                                           |
 |name.firstName     |Rudolph                                         |
  Given I search in API for "matt"
 Then I should receive a return code of 200
 Then I should receive a collection with 1 elements
 And I see the following search results at index 0:
 |Field              |Value                                           |
 |name.firstName     |Matt                                            |
 Given I search in API for "tran"
 Then I should receive a return code of 200
 Then I should receive a collection with 1 elements
 And I see the following search results at index 0:
 |Field              |Value                                           |
 |name.lastSurname   |Tran                                            |

 Scenario: School Level searching for student not in school
  Given the testing device app key has been created
When I navigate to the API authorization endpoint with my client ID
When I selected the realm "Daybreak Test Realm"
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "mgonzales" "mgonzales1234" for the "Simple" login page
Then I should receive a json response containing my authorization code
When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
Then I should receive a json response containing my authorization token
 Given I search in API for "Alton"
 Then I should receive a return code of 200
 Then I should receive a collection with 0 elements
  Given I search in API for "matt"
 Then I should receive a return code of 200
 Then I should receive a collection with 0 elements
