Feature: Traversal of school level data

Scenario: Leader traversing through the available school data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is Leader
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through all of the data 
And that data includes free or reduced lunch

Scenario: Leader trying to edit data through API

Given I am an authenticated SEA/LEA end user
And my role is Leader
When I make a call to edit any data within my school
Then I get message that I am not authorized

Scenario: IT Administrator traversing through the available school data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is IT Administrator
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through all of the data 
And that data includes free or reduced lunch

Scenario: IT Administrator trying to edit data through API

Given I am an authenticated SEA/LEA end user
And my role is IT Administrator
When I make a call to edit any data within my school
Then the data is edited

Scenario: Aggregate Viewer traversing through the available school data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is Aggregate Viewer
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through the school info only

Scenario: Aggregate Viewer trying to access non-school info data through API

Given I am an authenticated SEA/LEA end user
And my role is Aggregate Viewer
When I make an API call to get student within my school
Then I get message that I am not authorized