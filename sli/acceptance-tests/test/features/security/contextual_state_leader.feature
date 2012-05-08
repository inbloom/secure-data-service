@wip
Feature: Contextual Permissions for State level Staff (non-educators)

Scenario Outline: Staff accessing data from own state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my State is <State>
When I try to access the data for "" in my state from the API
Then I get the data returned in json format
Examples:
	|Username|Password|Realm|Role|State|

Scenario Outline: Staff access data from another state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my State is <State>
When I try to access the data for "" from another state from the API
Then I should recieve a return code of 403
Examples:
	|Username|Password|Realm|Role|State|

Scenario: IT Administrator trying to edit data for own state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my State is <State>
When I try to edit the data for "" in my state from the API
Then I should recieve a return code of 205
And the data should be updated

Scenario: IT Administrator trying to edit data for other state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my State is <State>
When I try to edit the data for "" in another state from the API
Then I should recieve a return code of 403
And the data should not have changed

Scenario: Aggregate Viewer getting their available state data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my State is <State>
When I try to access the data for "" in my state from the API
Then I get the data returned in json format

Scenario Outline: Aggregate Viewer trying to access non-school info data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my State is <State>
When I try to access the data for "" in my state from the API
Then I should recieve a return code of 403
Examples:
	|Username|Password|Realm|State|

