@wip

Feature: Context-based Permissions for Educator 

I would like to implement context-based permissions, so that when a SEA/LEA end user (that represents an Educator) access the SLI, he/she is provided with the student data that the Educator has relationship with.

#School

Scenario Outline: Authenticated Educator makes API call to get own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get <School>
Then I receive a JSON response that includes  the School entity and its attributes
Examples:
| Realm  | Username  | Password      | School                      |
| "idp1" | "jdoe"    | "jdoe1234"    | "Fry High School"           |
| "idp2" | "johndoe" | "johndoe1234" | "Parker-Dust Middle School" |
| "idp1" | "ejane"   | "ejane1234"   | "Watson Elementary School"  |

Scenario Outline: Authenticated Educator makes API call to get not own School
Given I am a valid <Realm> end user <Username> with password <Password>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <School>
When I make an API call to get <OtherSchool>
Then I should get a message that I am not authorized
Examples:
| Realm  | Username   | Password       | School                      | OtherSchool |
| "idp1" | "tbear"    | "tbear1234"    | "Fry High School"           | "Parker-Dust Middle School" |
| "idp1" | "john_doe" | "john_doe1234" | "Watson Elementary School"  | "Fry High School"           |
| "idp2" | "johndoe"  | "johndoe1234"  | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "idp2" | "ejane"    | "ejane1234"    | "Parker-Dust Middle School" | "Watson Elementary School"  |
| "idp1" | "ejane"    | "ejane1234"    | "Watson Elementary School"  | "Parker-Dust Middle School" |

Scenario: Authenticated non-Educator makes API call to get School
Given I am a valid SEA/LEA end user
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that does not equals "Educator"
When I make an API call to get <school>
Then I should get a message that I am not authorized

#Teacher

Scenario: Authenticated Educator makes API call to get self (Teacher)
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <my school>
When I make an API call to get <teacher>
Then I receive a JSON response that includes the Teacher entity and its attributes (allowed to be seen as an Educator role)

Scenario: Authenticated Educator makes API call to get list of Teachers within own School
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <my school>
When I make an API call to get list of teachers  from <my school>
Then I receive a JSON response that includes the list of Teachers

Scenario: Authenticated Educator makes API call to get list of Teachers not in own School
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <my school>
When I make an API call to get list of teachers  from  <not my school>
Then I should get a message that I am not authorized

Scenario: Authenticated Educator makes API call to get Teacher in own School
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <my school>
When I make an API call to get <teacher> from <my school>
Then I receive a JSON response that includes the Teacher

Scenario: Authenticated Educator makes API call to get Teacher not in own School
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And my School is <my school>
When I make an API call to get <teacher> from <not my school>
Then I should get a message that I am not authorized

Scenario: Authenticated non-Educator makes API call to get Teacher
Given I am a valid SEA/LEA end user
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that does not equals "Educator"
When I make an API call to get <teacher>
Then I should get a message that I am not authorized

Scenario: Authenticated non-Educator makes API call to get list of Teachers
Given I am a valid SEA/LEA end user
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that does not equals "Educator"
When I make an API call to get <list of teachers>
Then I should get a message that I am not authorized

#Section

Scenario: Authenticated Educator makes API call to get list of Sections
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get list of sections
Then I receive a JSON response that includes the sections that I am teaching only

Scenario: Authenticated Educator makes API call to get own Section
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And I teach in <my section #>
When I make an API call to get <my section #>
Then I receive a JSON response with that section

Scenario: Authenticated Educator makes API call to get not own Section
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get <not my section #>
Then I should get a message that I am not authorized

Scenario: Authenticated non-Educator makes API call to get Section
Given I am a valid SEA/LEA end user
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that does not equals "Educator"
When I make an API call to get <section>
Then I should get a message that I am not authorized

Scenario: Authenticated non-Educator makes API call to get list of Sections
Given I am a valid SEA/LEA end user
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that does not equals "Educator"
When I make an API call to get <list of sections>
Then I should get a message that I am not authorized

#Student

Scenario: Authenticated Educator makes API call to get list of Students
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get list of students
Then I receive a JSON response that includes the Students that I am teaching only

Scenario: Authenticated Educator makes API call to get Student that he/she is teaching
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
And I teach  <my student #>
When I make an API call to get <my student  #>
Then I receive a JSON response with that student

Scenario: Authenticated Educator makes API call to get Student that he/she is not teaching
Given I am a valid SEA/LEA end user <teacher>
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that equals "Educator"
When I make an API call to get <not my student  #>
Then I should get a message that I am not authorized

Scenario: Authenticated non-Educator makes API call to get Student
Given I am a valid SEA/LEA end user
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that does not equals "Educator"
When I make an API call to get <teacher>
Then I should get a message that I am not authorized

Scenario: Authenticated non-Educator makes API call to get list of Students
Given I am a valid SEA/LEA end user
And I am authenticated to SEA/LEA IDP
And I have a Role attribute that does not equals "Educator"
When I make an API call to get <list of teachers>
Then I should get a message that I am not authorized
