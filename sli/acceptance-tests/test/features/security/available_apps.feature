
Feature: Get available apps API 
 
 As a SE/LEA end user, I would like to be able to get a list of all the applications that are available to me. 

Scenario:  Authenticated SEA/LEA user requests for info on own available apps
 
Given I am logged in using "ejane" "ejane1234" to realm "IL"
When I make an API call to get my available apps
Then I receive a JSON object listing all the apps that my SEA/LEA have approved
And the object includes an app URL, admin URL, image URL, description, title, vendor, version, display method, is admin app

Scenario: Authenticated SEA/LEA user requests for info on available admin apps

Given I am logged in using "ejane" "ejane1234" to realm "IL"
When I make an API call to get my available apps filtered by admin
Then I receive a JSON object listing all the apps that my SEA/LEA have approved
And the object includes an app URL, admin URL, image URL, description, title, vendor, version, display method, is admin app
