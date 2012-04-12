Feature: Traversal of district level data

Scenario: Leader traversing through the available district data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is Leader
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through all of the data within my district
And that data includes free or reduced lunch

Scenario: Leader trying to edit data through API

Given I am an authenticated SEA/LEA end user
And my role is Leader
When I make an API call to edit any data within my District
Then I get message that I am not authorized

Scenario: IT Administrator traversing through the available district data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is IT Administrator
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through all of the data within the district
And that data includes free or reduced lunch

Scenario: IT Administrator trying to edit data through API in a school within own district

Given I am an authenticated SEA/LEA end user
And my role is IT Administrator
When I make a call to edit any data in my district's school
Then the data is edited

Scenario: IT Administrator trying to edit data through API in a school not in own district

Given I am an authenticated SEA/LEA end user
And my role is IT Administrator
When I make a call to edit any data in a school that is not in my district
Then I get a message that I am not authorized

Scenario: Aggregate Viewer traversing through the available district data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is Aggregate Viewer
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through the educational organization hierarchy
And I can see the edorg info only

Scenario: Aggregate Viewer trying to access non-edorg info data through API

Given I am an authenticated SEA/LEA end user
And my role is Aggregate Viewer
When I make an API call to get student within a school of my district
Then I get message that I am not authorized