Feature: User authenticates against a mock IDP
 
Background: Realm selector is set up to reflect two sandbox tenancies and database has redirect links
Given I have an open web browser


Scenario: Mock IDP Page components
Given I have selected the realm using the realm selector
Then I should be redirected to the Mock IDP page for the realm
And the Mock IDP Page has a drop down with all the users defined in tenancy
And the Mock IDP Page has a multi select which has all the roles defined in the tenancy
And the Mock IDP Page has a button the user can use to log in
And the heading of the Mock IDP Page contains the realm "SLI"


Scenario: Use Mock IDP to log in as IT Admin
Given I navigate to sample app web page
Then I will be redirected to realm selector web page
When I select the "SimpleIDP Test Realm - SLI" realm
Then I should be redirected to the Mock IDP page for the realm
When I select "John Doe" from the user drop down
And I select "IT Administrator" from role selector
And I click Login
And I wait for 5 second
Then I should be redirected to sample app web page
Then I have "IT Administrator" access to the sandbox tenancy
And I am able to write student data


Scenario: Use Mock IDP to log in as Educator and Leader

Given I navigate to sample app web page
Then I will be redirected to realm selector web page
When I select the "SimpleIDP Test Realm - SLI" realm
Then I should be redirected to the Mock IDP page for the realm
When I select "John Doe" from the user drop down
And I select "Educator"  and "Leader" from role selector
And I click Login
And I wait for 5 second
Then I should be redirected to sample app web page
Then I have "Educator" and "Leader" access to the sandbox tenancy
And I am able Read student data
