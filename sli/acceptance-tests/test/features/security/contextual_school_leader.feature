@smoke
Feature: Contextual Permissions for School level Staff (non-educators)

Scenario Outline: Staff accessing data from own school

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "school" is <School>
When I try to access the data for <Data> in my "school" from the API
Then I get the data containing <Data> returned in json format
Examples:
    |Username    |Password        |Realm|Role      |School                     |Data|
    |"kmelendez" |"kmelendez1234" |"NY" |"Leader"  |"Dawn Elementary"          |"Students in Dawn Elementary"|
    |"mgonzales" |"mgonzales1234" |"IL" |"Leader"  |"South Daybreak Elementary"|"Teachers in South Daybreak Elementary"|
    |"agibbs"    |"agibbs1234"    |"NY" |"IT Admin"|"Dawn Elementary"          |"Malcolm Haehn NY"|
    |"akopel"    |"akopel1234"    |"IL" |"IT Admin"|"South Daybreak Elementary"|"Rebecca Braverman"|

Scenario Outline: Staff access data from another school

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "school" is <School>
When I try to access the data for <Data> in another "school" from the API
Then I should receive a return code of <Code>
Examples:
    |Username    |Password        |Realm|Role      |School                     |Data                                   | Code |
    |"kmelendez" |"kmelendez1234" |"NY" |"Leader"  |"Dawn Elementary"          |"Students in Parker Elementary"        | 403  |
    |"kmelendez" |"kmelendez1234" |"NY" |"Leader"  |"Dawn Elementary"          |"Students in South Daybreak Elementary"| 200  |
    |"mgonzales" |"mgonzales1234" |"IL" |"Leader"  |"South Daybreak Elementary"|"Mark Anthony"                         | 403  |
    |"racosta"   |"racosta1234"   |"IL" |"Leader"  |"Sunset Central High"      |"Matt Sollars"                         | 403  |
    |"agibbs"    |"agibbs1234"    |"NY" |"IT Admin"|"Dawn Elementary"          |"Teachers in Parker Elementary"        | 403  |
    |"akopel"    |"akopel1234"    |"IL" |"IT Admin"|"South Daybreak Elementary"|"Students in AP Calculus Sec 201"      | 403  |

Scenario Outline: Staff listing teachers they have context to

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals <Role>
And my "school" is <School>
When I try to access the data for "My Teachers" in my "school" from the API
Then I get the data containing <Data> returned in json format
Examples:
  |Username    |Password        |Realm|Role      |School                     |Data|
  |"kmelendez" |"kmelendez1234" |"NY" |"Leader"  |"Dawn Elementary"          |"Teachers in Dawn Elementary"|
  |"akopel"    |"akopel1234"    |"IL" |"IT Admin"|"South Daybreak Elementary"|"Teachers in South Daybreak Elementary"|

Scenario Outline: IT Administrator trying to edit data for own school

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "school" is <School>
When I try to update the data for <Data> in my "school" from the API
Then I should receive a return code of 204
And the data should be updated
Examples:
    |Username|Password    |Realm|School                     |Data|
    |"agibbs"|"agibbs1234"|"NY" |"Dawn Elementary"          |"Malcolm Haehn NY"|
    |"akopel"|"akopel1234"|"IL" |"South Daybreak Elementary"|"Brandon Suzuki"|

Scenario Outline: IT Administrator trying to edit data for other school

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "IT Administrator"
And my "school" is <School>
When I try to update the data for <Data> in another "school" from the API
Then I should receive a return code of 403
Examples:
    |Username|Password    |Realm|School                     |Data|
    |"agibbs"|"agibbs1234"|"NY" |"Dawn Elementary"          |"Dale Reiss"|
    |"akopel"|"akopel1234"|"IL" |"South Daybreak Elementary"|"Lavern Chaney"|

Scenario Outline: Aggregate Viewer getting their available school data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "school" is <School>
When I try to access the data for <Data> in my "school" from the API
Then I should get a response which includes the data containing <Data> returned in json format
Examples:
    |Username   |Password       |Realm|School                     |Data|
    |"charrison"|"charrison1234"|"NY" |"Dawn Elementary"          |"Dawn Elementary"          |
    |"msmith"   |"msmith1234"   |"IL" |"South Daybreak Elementary"|"South Daybreak Elementary"|

Scenario Outline: Aggregate Viewer trying to access non-school info data

Given I am logged in using <Username> <Password> to realm <Realm>
And I have a Role attribute that equals "Aggregate Viewer"
And my "school" is <School>
When I try to access the data for <Data> in my "school" from the API
Then I should receive a return code of 403
Examples:
    |Username   |Password       |Realm|School                     |Data|
    |"msmith"   |"msmith1234"   |"IL" |"South Daybreak Elementary"|"Students in South Daybreak Elementary"|
    |"charrison"|"charrison1234"|"NY" |"Dawn Elementary"          |"Teachers in Dawn Elementary"|

Scenario Outline: Authenticated Leader makes API call to see Staff in their parent EdOrgs
  Given I am logged in using <Username> <Password> to realm <Realm>
  And I have a Role attribute that equals "Leader"
  When I make an API call to get the staff <Staff>
  Then I should get a message that I am not authorized
  Examples:
  |Username    |Password        |Realm|Role        |Staff|
  |"mgonzales" |"mgonzales1234" |"IL" |"Leader"    |"Rick Rogers"|
