@smoke
Feature: Get available apps API 
 
 As a SE/LEA end user, I would like to be able to get a list of all the applications that are available to me. 

Scenario:  Authenticated SEA/LEA user requests for info on own available apps
 
Given I am logged in using "cgray" "cgray1234" to realm "IL"
When I make an API call to get my available apps
Then I receive a JSON object listing all the apps that my SEA/LEA have approved
And the object includes an app URL, admin URL, image URL, description, title, vendor, version, display method, is admin app

Scenario: Authenticated SEA/LEA user requests for info on available admin apps

Given I am logged in using "cgray" "cgray1234" to realm "IL"
When I make an API call to get my available apps filtered by admin
Then I receive a JSON object listing all the admin apps that my SEA/LEA have approved
And the object includes an app URL, admin URL, image URL, description, title, vendor, version, display method, is admin app

Scenario: SLI administrator has admin app endpoints filtered based on role

Given I am logged in using "operator" "operator1234" to realm "SLI"
When I make an API call to get my available apps filtered by admin
Then I receive a JSON object listing all the admin apps
And the list contains the admin app
And the admin app endpoints only contains SLI operator endpoints

Scenario: Authenticated State-level users see all apps within child districts

Given I am logged in using "jjackson" "jjackson1234" to realm "IL"
When I make an API call to get my available apps 
Then I receive a JSON object listing all the apps approved for "IL-DAYBREAK"
Given I am logged in using "llogan" "llogan1234" to realm "IL"
When I make an API call to get my available apps 
Then I receive a JSON object listing all the apps approved for "IL-SUNSET"
Given I am logged in using "mjohnson" "mjohnson1234" to realm "IL"
When I make an API call to get my available apps 
Then I receive a JSON object listing all the apps approved for both "IL-DAYBREAK" and "IL-SUNSET"

Scenario: Verify auto-approved and auto-authorized applications are visible
Given I am logged in using "cgray" "cgray1234" to realm "IL"
When I make an API call to get my available apps 
Then I receive a JSON object listing all the apps approved for "IL-DAYBREAK"
And the list contains and app named "AuthorizeTestApp1"

Scenario: Verify non-auto-authorized applications are not visible
Given I am logged in using "cgray" "cgray1234" to realm "IL"
When I make an API call to get my available apps 
Then I receive a JSON object listing all the apps approved for "IL-DAYBREAK"
And the list does not contain and app named "AuthorizeTestApp2"

Scenario: Verify non-admin-visible applications are not visible to admins
Given I am logged in using "operator" "operator1234" to realm "SLI"
When I make an API call to get my available apps 
Then I receive a JSON object listing all the apps approved for "SLI"
And the list does not contain and app named "AuthorizeTestApp3"

Scenario: Verify admin-visible applications are visible to admins
Given I am logged in using "operator" "operator1234" to realm "SLI"
When I make an API call to get my available apps 
Then I receive a JSON object listing all the apps approved for "SLI"
And the list contains and app named "AuthorizeTestApp4"
