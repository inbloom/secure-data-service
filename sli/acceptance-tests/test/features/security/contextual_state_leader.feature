@RALLY_US177
Feature: Contextual Permissions for State level Staff (non-educators)

Scenario Outline: Staff accessing data from own state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "state" is <State>
When I try to access the data for <Data> in my "state" from the API
Then I get the data containing <Data> returned in json format
Examples:
	|Username  |Password      |Realm|Role      |State|Data|
	|"jbarrera"|"jbarrera1234"|"NY" |"Leader"  |"NY" |"Students in Parker Elementary"|
	|"ckoch"   |"ckoch1234"   |"IL" |"Leader"  |"IL" |"Teachers in South Daybreak Elementary"|
	|"jpratt"  |"jpratt1234"  |"NY" |"IT Admin"|"NY" |"Malcolm Haehn NY"|
	|"rrogers" |"rrogers1234" |"IL" |"IT Admin"|"IL" |"Linda Kim"|

Scenario Outline: Staff access data from another state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "state" is <State>
When I try to access the data for <Data> in another "state" from the API
Then I should receive a return code of 403
Examples:
	|Username  |Password      |Realm|Role      |State|Data|
	|"jbarrera"|"jbarrera1234"|"NY" |"Leader"  |"NY" |"Students in South Daybreak Elementary"|
	|"ckoch"   |"ckoch1234"   |"IL" |"Leader"  |"IL" |"Dale Reiss"|
	|"jpratt"  |"jpratt1234"  |"NY" |"IT Admin"|"NY" |"Teachers in South Daybreak Elementary"|
	|"rrogers" |"rrogers1234" |"IL" |"IT Admin"|"IL" |"Malcolm Haehn NY"|

Scenario Outline: Staff listing teachers they have context to

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "state" is <State>
When I try to access the data for "My Teachers" in my "state" from the API
Then I get the data containing <Data> returned in json format
Examples:
  |Username  |Password      |Realm|Role      |State|Data|
  |"jbarrera"|"jbarrera1234"|"NY" |"Leader"  |"NY" |"Teachers in New York State"|
  |"ckoch"   |"ckoch1234"   |"IL" |"Leader"  |"IL" |"Teachers in Illinois State"|

Scenario Outline: IT Administrator trying to edit data for own state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "state" is <State>
When I try to update the data for <Data> in my "state" from the API
Then I should receive a return code of 204
And the data should be updated
Examples:
	|Username  |Password      |Realm|State|Data|
	|"jpratt"  |"jpratt1234"  |"NY" |"NY" |"Malcolm Haehn NY"|
	|"rrogers" |"rrogers1234" |"IL" |"IL" |"Matt Sollars"|

Scenario Outline: IT Administrator trying to edit data for other state

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "state" is <State>
When I try to update the data for <Data> in another "state" from the API
Then I should receive a return code of 403
Examples:
	|Username  |Password      |Realm|State|Data|
	|"rrogers" |"rrogers1234" |"IL" |"IL" |"Malcolm Haehn NY"|
	|"jpratt"  |"jpratt1234"  |"NY" |"NY" |"Matt Sollars"|

Scenario Outline: Aggregate Viewer getting their available state data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "state" is <State>
When I try to access the data for <Data> in my "state" from the API
Then I should get a response which includes the data containing <Data> returned in json format
Examples:
	|Username  |Password      |Realm|State|Data|
	|"mjohnson"|"mjohnson1234"|"IL" |"IL" |"Schools in Daybreak District"|
	|"sholcomb"|"sholcomb1234"|"NY" |"NY" |"Schools in Dusk District"|
	|"mjohnson"|"mjohnson1234"|"IL" |"IL" |"South Daybreak Elementary"|

Scenario Outline: Aggregate Viewer trying to access non-school info data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "state" is <State>
When I try to access the data for <Data> in my "state" from the API
Then I should receive a return code of 403
Examples:
	|Username  |Password      |Realm|State|Data|
	|"mjohnson"|"mjohnson1234"|"IL" |"IL" |"Students in South Daybreak Elementary"|
	|"sholcomb"|"sholcomb1234"|"NY" |"NY" |"Teachers in Dawn Elementary"|
