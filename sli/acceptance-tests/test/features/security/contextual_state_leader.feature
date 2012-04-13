@wip
Feature: Contextual Permissions for State level Staff (non-educators)

Scenario Outline: Staff accessing data from own state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "state" is <State>
When I try to access the data for <Data> in my "state" from the API
Then I get the data returned in json format
Examples:
	|Username|Password|Realm|Role|State|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Leader"|"Sunset"|"/schools"|

Scenario Outline: Staff access data from another "state"

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "state" is <State>
When I try to access the data for <Data> in another "state" from the API
Then I should receive a return code of 403
Examples:
	|Username|Password|Realm|Role|State|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Leader"|"Sunset"|"/schools"|

Scenario Outline: IT Administrator trying to edit data for own state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "state" is <State>
When I try to access the data for <Data> in my "state" from the API
Then I should receive a return code of 205
And the data should be updated
Examples:
	|Username|Password|Realm|State|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools"|

Scenario Outline: IT Administrator trying to edit data for other state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "state" is <State>
When I try to access the data for <Data> in another "state" from the API
Then I should receive a return code of 403
And the data should not have changed
Examples:
	|Username|Password|Realm|State|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools"|

Scenario Outline: Aggregate Viewer getting their available state data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "state" is <State>
When I try to access the data for <Data> in my "state" from the API
Then I get the data returned in json format
Examples:
	|Username|Password|Realm|State|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools"|

Scenario Outline: Aggregate Viewer trying to access non-school info data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "state" is <State>
When I try to access the data for <Data> in my "state" from the API
Then I should receive a return code of 403
Examples:
	|Username|Password|Realm|State|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools"|
