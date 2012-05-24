@wip
@RALLY_US215
Feature: Traversal of school level data

Scenario: Leader traversing through the available school data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is Leader
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through all of the data 
And that data includes free or reduced lunch

Scenario: IT Administrator traversing through the available school data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is IT Administrator
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through all of the data 
And that data includes free or reduced lunch

Scenario: Aggregate Viewer traversing through the available school data in the Data Browser

Given I am an authenticated SEA/LEA end user
And my role is Aggregate Viewer
When I open the Data Browser
Then I am given my home URLs
And I am able to traverse through the school info only

