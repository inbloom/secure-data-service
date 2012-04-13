@wip
Feature: Contextual Permissions for District level Staff (non-educators)

Scenario Outline: Staff accessing data from own district

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "district" is <District>
When I try to access the data for <Data> in my "district" from the API
Then I get the data returned in json format
Examples:
	|Username|Password|Realm|Role|District|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Leader"|"Sunset"|"/schools"|

Scenario Outline: Staff access data from another "district"

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "district" is <District>
When I try to access the data for <Data> in another "district" from the API
Then I should receive a return code of 403
Examples:
	|Username|Password|Realm|Role|District|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Leader"|"Sunset"|"/schools"|

Scenario Outline: IT Administrator trying to edit data for own district

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "district" is <District>
When I try to access the data for <Data> in my "district" from the API
Then I should receive a return code of 205
And the data should be updated
Examples:
	|Username|Password|Realm|District|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools"|

Scenario Outline: IT Administrator trying to edit data for other district

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "district" is <District>
When I try to access the data for <Data> in another "district" from the API
Then I should receive a return code of 403
And the data should not have changed
Examples:
	|Username|Password|Realm|District|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools"|

Scenario Outline: Aggregate Viewer getting their available district data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "district" is <District>
When I try to access the data for <Data> in my "district" from the API
Then I get the data returned in json format
Examples:
	|Username|Password|Realm|District|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools"|

Scenario Outline: Aggregate Viewer trying to access non-school info data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "district" is <District>
When I try to access the data for <Data> in my "district" from the API
Then I should receive a return code of 403
Examples:
	|Username|Password|Realm|District|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools"|
