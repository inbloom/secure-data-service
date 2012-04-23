@wip
Feature: Contextual Permissions for School level Staff (non-educators)

Scenario Outline: Staff accessing data from own school

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "school" is <School>
When I try to access the data for <Data> in my "school" from the API
Then I get the data returned in json format
Examples:
	|Username|Password|Realm|Role|School|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Leader"|"Sunset"|"/schools"|

Scenario Outline: Staff access data from another school

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "school" is <School>
When I try to access the data for <Data> in another "school" from the API
Then I should receive a return code of 403
Examples:
	|Username|Password|Realm|Role|School|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Leader"|"Sunset"|"/schools/Sunset"|

Scenario Outline: IT Administrator trying to edit data for own school

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "school" is <School>
When I try to update the data for <Data> in my "school" from the API
Then I should receive a return code of 205
And the data should be updated
Examples:
	|Username|Password|Realm|School|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools/Sunset"|

Scenario Outline: IT Administrator trying to edit data for other school

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "school" is <School>
When I try to update the data for <Data> in another "school" from the API
Then I should receive a return code of 403
And the data should not have changed
Examples:
	|Username|Password|Realm|School|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools/Daybreak"|

Scenario Outline: Aggregate Viewer getting their available school data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "school" is <School>
When I try to access the data for <Data> in my "school" from the API
Then I get the data returned in json format
Examples:
	|Username|Password|Realm|School|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools/Sunset"|

Scenario Outline: Aggregate Viewer trying to access non-school info data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "school" is <School>
When I try to access the data for <Data> in my "school" from the API
Then I should receive a return code of 403
Examples:
	|Username|Password|Realm|School|Data|
	|"jdoe"|"jdoe1234"|"IL"|"Sunset"|"/schools/Sunset"|
