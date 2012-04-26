@wip
Feature: User authenticates against a simple IDP
 
 Background: Realm selector is set up to reflect one sandbox tenancies and database has redirect links
Given I have an open web browser

Scenario: Mock IDP Page components
Given I have selected the realm using the realm selector
Then I should be redirected to the Mock IDP page for the realm
And the Mock IDP Page has a drop down with all the users defined in tenancy
And the Mock IDP Page has a list of roles with checkboxes next to them
And the Mock IDP Page has a password textbox
And the Mock IDP Page has a submit button
And the heading of the Simple IDP Page is the realm "Development IDP"


Scenario: As an Admin Use SimpleIDP set up roles/password for a teacher
Given sandbox sample data is ingested
When I select "Sandbox IL Realm" realm
Then I should be redirected to the SimpleIDP page for the realm

When I select "Linda Kim" from the user drop down
Then I can only select either "Educator" from the list of roles
When I select "Educator" for the list of roles
And I enter a password "lindakim1234" 
And I click submit
Then the role and encrypted password for "linda kim" is stored in the database


Scenario: As an Admin Use SimpleIDP set up roles/password for a staff
Given sandbox sample data is ingested
When I select "Sandbox IL Realm" realm
Then I should be redirected to the SimpleIDP page for the realm

When I select "Admin" from the user drop down
Then I select both "IT Admin" and "Leader" from the list of roles
When I select "IT Admin" and "Leader"  for the list of roles
When I enter a password "admin1234" 
And I click submit
Then the role of "IT Admin" and "Leader" and encrypted password for "Admin" is stored in the database

Scenario: Use Mock IDP to log in as Educator
Given I navigate to sample app web page
Then I will be redirected to realm selector web page
When I select the "Sandbox IL Realm" realm
Then I should be redirected to the Simple IDP Login page for the realm
When I enter "User Name" as "Linda Kim"
And I enter the password for Linda Kim
And I click Login
And I wait for 5 second
Then I should be redirected to sample app web page
Then I only have "Educator"access to the sandbox tenancy
And I am able Read student data


Scenario: Use Mock IDP to log in as IT Admin
Given I navigate to sample app web page
Then I will be redirected to realm selector web page
When I select the "Sandbox IL Realm" realm
Then I should be redirected to the Simple IDP Login  page for the realm
When I enter "User Name" as "Admin"
And I enter the password for Linda Kim
And I click Login
And I wait for 5 second
Then I should be redirected to sample app web page
Then I only have "IT Admin" and "Leader" access to the sandbox tenancy
And I am able Read student data