@wip 
Feature: User authenticates against a mock IDP
 
Background: Realm selector is set up to reflect two sandbox tenancies and database has redirect links
 
Scenario: Mock IDP Page components
 
 # there is a 1:1 mapping between realm and tenancy
Given I have selected the realm using the realm selector
Then I am redirected to the Mock IDP page for the realm
And the Mock IDP Page has a drop down with all the users defined in tenancy
And the Mock IDP Page has a multi select which has all the roles defined in the tenancy
And the Mock IDP Page has a button the user can use to log in
And the Mock IDP Page has a log out link
And the heading of the Mock IDP Page is realm followed by "IDP"
 
Scenario: Use Mock IDP to log in as IT Admin
Given I have selected the realm using the realm selector
When I am redirected to the Mock IDP page for the realm
And I select "Linda Kim" from the user drop down
And I select "IT Admin" role
And I click "Go"
Then I have "IT Admin" access to the sandbox tenancy
And I am able to write student data

 
Scenario: Use Mock IDP to log in as Educator and Leader
Given I have selected the realm using the realm selector
When I am redirected to the Mock IDP page for the realm
And I select "Linda Kim" from the user drop down
And I select "Educator" role
And I select "Leader"
And I click "Go"
Then I have "Educator" and "Leader" access to the sandbox tenancy
And I am able "Read" student data

Scenario: Logout
 
Given I am logged in to Mock-IDP
When I click on the Logout link
Then I am logged out
And I am redirected to Realm Selector