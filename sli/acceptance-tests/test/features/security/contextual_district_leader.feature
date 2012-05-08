@wip
Feature: Contextual Permissions for District level Staff (non-educators)

Scenario Outline: Staff accessing data from own district

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my District is <District>
When I try to access the data for "" in my district from the API
Then I get the data returned in json format
Examples:
	|Username|Password|Realm|Role|District|

Scenario Outline: Staff access data from another district

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my District is <District>
When I try to access the data for "" from another district from the API
Then I should recieve a return code of 403
Examples:
	|Username|Password|Realm|Role|District|

Scenario: IT Administrator trying to edit data for own district

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my District is <District>
When I try to edit the data for "" in my district from the API
Then I should recieve a return code of 205
And the data should be updated

Scenario: IT Administrator trying to edit data for other district

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my District is <District>
When I try to edit the data for "" in another district from the API
Then I should recieve a return code of 403
And the data should not have changed

Scenario: Aggregate Viewer getting their available district data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my District is <District>
When I try to access the data for "" in my district from the API
Then I get the data returned in json format

Scenario Outline: Aggregate Viewer trying to access non-school info data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my District is <District>
When I try to access the data for "" in my district from the API
Then I should recieve a return code of 403
Examples:
	|Username|Password|Realm|District|

