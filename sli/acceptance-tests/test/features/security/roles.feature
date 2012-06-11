@RALLY_US175
@RALLY_US217
Feature: SLI Default Roles and Permissions
 
 As a SLI Super Administrator, I would like to have the possibility to map the SLI roles with those of the corresponding IDPs,
 so that only the users with existing and matching default SLI roles can access the system
 
 As an SLI Admin, I want to be able to enforce rights on the entity attribute level,
 so that  SEA/LEA end user can do CRUD on the entities stored in the data store, based on the SLI Default Role
  
#DE719
#@wip
Scenario Outline:  SEA/LEA user with a valid Default SLI Role making API call
 
Given  I am valid SEA/LEA end user <Username> with password <Password>
And I have a Role attribute returned from the "SLI"
And the role attribute equals <AnyDefaultSLIRole>
And I am authenticated on "SLI"
When I make a REST API call 
Then I get the JSON response displayed
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "educator"      | "educator1234"      | "Educator"         |
#| "aggregator"    | "aggregator1234"    | "Aggregate Viewer" |  We need data and associated getters and setters for aggregate data before we can run this specific test
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |

Scenario:  SEA/LEA user with an invalid Default SLI Role making API call
 
Given I am valid SEA/LEA end user "baduser" with password "baduser1234"
And I have a Role attribute returned from the "SLI"
And the role attribute equals "teacher"
And I am authenticated on "SLI"
When I make a REST API call
Then I get response that I am not authorized to do that operation because I do not have a valid SLI Default Role
 
Scenario:  SEA/LEA user without Role attribute making API call
 
Given  I am valid SEA/LEA end user "nouser" with password "nouser1234" 
And I do not have a Role attribute returned from the "SLI"
And I am authenticated on "SLI"
When I make a REST API call
Then I get response that I am not authorized to do that operation because I do not have a valid SLI Default Role

#DE719
#@wip
Scenario: Authorized SLI Default Role trying to edit Student attribute
 
Given  I am valid SEA/LEA end user "administrator" with password "administrator1234" 
And I am authenticated on "SLI"
And the role attribute equals "IT Administrator"
And "IT Administrator" is allowed to change Student address
When I make an API call to change the Student address to "1234 Somewhere"
Then the Student address is changed


#DE719
#@wip
Scenario Outline: Unauthorized SLI Default Role trying to edit Student attribute
 
Given  I am valid SEA/LEA end user <Username> with password <Password>  
And I am authenticated on "SLI"
And the role attribute equals <Role>
And <Role> is not allowed to change Student address
When I make an API call to change the Student address to "9876 Nowhere"
Then a message is displayed that the <Role> role does not allow this action 
Examples:
| Username   | Password       | Role       |
| "educator" | "educator1234" | "Educator" |
| "leader"   | "leader1234"   | "Leader"   |

Scenario: Unauthorized SLI Default Role trying to view Student object

Given  I am valid SEA/LEA end user "aggregator" with password "aggregator1234" 
And I am authenticated on "SLI"
And the role attribute equals "Aggregate Viewer"
And "Aggregate Viewer" is not allowed to view Student data
When I make an API call to view a Student's data
Then a message is displayed that the "Aggregate Viewer" role cannot view this data

#DE719
#@wip
Scenario: Authorized SLI Default Role trying to view Student restricted field

Given  I am valid SEA/LEA end user "leader" with password "leader1234"
And I am authenticated on "SLI"
And the role attribute equals "Leader"
And "Leader" is allowed to view restricted Student fields
When I make an API call to view a Student's data
Then the Student restricted fields are visible in the response
@wip 
Scenario: Unauthorized SLI Default Role trying to view Student restricted field

Given  I am valid SEA/LEA end user "educator" with password "educator1234"
And I am authenticated on "SLI"
And the role attribute equals "Educator"
And "Educator" is not allowed to view restricted Student fields
When I make an API call to view a Student's data
Then the Student restricted fields are not visible in the response
