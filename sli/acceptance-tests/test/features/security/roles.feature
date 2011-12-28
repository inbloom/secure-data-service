
Feature: SLI Default Roles and Permissions
 
As a SLI Operator/Administrator, I want to login to the SLI Default Roles Admin Page,
so I could get an information about the default roles in SLI and their permissions.

 As a SLI Super Administrator, I would like to have the possibility to map the SLI roles with those of the corresponding IDPs,
 so that only the users with existing and matching default SLI roles can access the system
 
 As an SLI Admin, I want to be able to enforce rights on the entity attribute level,
 so that  SEA/LEA end user can do CRUD on the entities stored in the data store, based on the SLI Default Role
 
@wip
Scenario: Go to SLI Default Roles Admin Page when not authenticated to SLI IDP
 
Given I have an open web browser
And I am not authenticated to SLI IDP
When I type the SLI Default Roles Admin Page
And I click on the Enter button
Then I should be redirected to the SLI IDP Login page
 
 @wip
Scenario: Go to SLI Default Roles Admin Page when authenticated to SLI IDP
 
Given I have an open web browser
And I am authenticated to SLI IDP
When I type the SLI Default Roles Admin Page
And I click on the Enter button
Then I should be redirected to the SLI Default Roles Admin Page
 
 @wip
Scenario: Valid SLI IDP user login to SLI Default Roles Admin Page
 
Given I am not authenticated to SLI IDP
And I have tried to access the SLI Default Roles Admin Page
And I was redirected to the SLI IDP Login page
And I am user  <JohnDoe>
And <JohnDoe> is valid <SLI IDP> user
When I enter <JohnDoe> in the username text field
And I enter  <***> in the password text field
And I click the Go button
Then I am authenticated to SLI IDP
And I am redirected to the SLI Default Roles Admin Page
 
 @wip
Scenario: Invalid SLI IDP user login to SLI Default Roles Admin Page
 
Given I am not authenticated to SLI IDP
And I have tried to access the SLI Default Roles Admin Page
And I was redirected to the SLI IDP Login page
And I am user  <InvalidJohnDoe>
And <InvalidJohnDoe> is invalid <SLI IDP> user
When I enter <InvalidJohnDoe> in the username text field
And I enter  <***> in the password text field
And I click the Go button
Then I am informed that <InvalidJohnDoe> does not exists
And I am redirected to the SLI-IDP Login Page
 
 @wip
Scenario:  SLI Default Roles Admin Page logout
 
Given I am authenticated to SLI IDP
And I have navigated to the SLI Default Roles Admin Page
When I click on the Logout link
Then I am not authenticated to SLI IDP
And I am redirected to the SLI-IDP Login Page
 
 @wip
Scenario:  SLI Default Roles Admin Page - Clicking on Default SLI Roles and Permissions URL
 
Given I am authenticated to SLI IDP
And I have navigated to the SLI Default Roles Admin Page
When I click on the Default SLI Roles and Permissions URL
Then the browser opens the confluence Default SLI Roles and Permissions page in a new browser window
And the browser focus is on the new browser window
 
Scenario Outline:  SEA/LEA user with a valid Default SLI Role making API call
 
Given  I am valid SEA/LEA end user <Username> with password <Password>
And I have a Role attribute returned from the "SEA/LEA IDP"
And the role attribute equals <AnyDefaultSLIRole>
And I am authenticated on "SEA/LEA IDP"
When I make a REST API call 
Then I get the JSON response displayed
Examples:
| Username        | Password            | AnyDefaultSLIRole  |
| "educator"      | "educator1234"      | "Educator"         |
| "aggregator"    | "aggregator1234"    | "Aggregate Viewer" |
| "administrator" | "administrator1234" | "IT Administrator" |
| "leader"        | "leader1234"        | "Leader"           |

Scenario:  SEA/LEA user with an invalid Default SLI Role making API call
 
Given I am valid SEA/LEA end user "baduser" with password "baduser1234"
And I have a Role attribute returned from the "SEA/LEA IDP"
And the role attribute equals "teacher"
And I am authenticated on "SEA/LEA IDP"
When I make a REST API call
Then I get response that I am not authorized to do that operation because I do not have a valid SLI Default Role
 
Scenario:  SEA/LEA user without Role attribute making API call
 
Given  I am valid SEA/LEA end user "nouser" with password "nouser1234" 
And I do not have a Role attribute returned from the "SEA/LEA IDP"
And I am authenticated on "SEA/LEA IDP"
When I make a REST API call
Then I get response that I am not authorized to do that operation because I do not have a valid SLI Default Role

@wip
Scenario: Authorized SLI Default Role trying to edit Student attribute
 
Given  I am valid SEA/LEA end user "administrator" with password "administrator1234" 
And I am authenticated on "SEA/LEA IDP"
And the role attribute equals "IT Administrator"
And IT Administrator is allowed to change Student address
When I make an API call to change the Student address to "1234 Somewhere"
Then the Student address is changed
 
 @wip
Scenario: Unauthorized SLI Default Role trying to edit Student attribute
 
Given  I am valid SEA/LEA end user "educator" with password "educator1234"  
And I am authenticated on "SEA/LEA IDP"
And the role attribute equals "Educator"
And Educator is not allowed to change Student address
When I make an API call to change the Student address to "9876 Nowhere"
Then a message is displayed that the Educator role does not allow this action 
