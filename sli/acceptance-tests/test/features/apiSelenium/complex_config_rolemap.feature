@wip
Feature:  Complex-Configurable Role Mapping Tool
 
As an SEA/LEA  Admin, I would like to have the Complex Role Mapping admin tool, so that I can map lists of SEA/LEA Directory roles to the SLI Default Roles.
 
#Admin Scenarios
 
Scenario: Go to Complex-Configurable Role Mapping Page when not authenticated to SLI IDP
 
Given I have an open web browser
And I am not authenticated to SLI IDP
When I navigate to the Complex-Configurable Role Mapping Page
Then I should be directed to the Realm page
 
 @wip
Scenario: Go to Complex-Configurable Role Mapping Page when authenticated to IDP other than SLI
 
Given I have an open web browser
And I am authenticated to "Realm" IDP
When I navigate to the Complex-Configurable Role Mapping Page
Then I should get a message that I am not authorized
 
Scenario: Go to Complex-Configurable Role Mapping Page when authenticated to SLI IDP, when having a role other than Super Administrator
 
Given I have an open web browser
And I am authenticated to SLI IDP as user "leader" with pass "leader1234"
And I am not a Super Administrator
When I navigate to the Complex-Configurable Role Mapping Page
Then I should get a message that I am not authorized to access the page
 
Scenario: Go to Complex-Configurable Role Mapping Page when authenticated to SLI IDP, when having a role  Super Administrator
 
Given I have an open web browser
And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
And I am a Super Administrator for "SLI"
When I navigate to the Complex-Configurable Role Mapping Page
Then I should be redirected to the  Complex-Configurable Role Mapping Page for "SLI"
 
Scenario: Valid SLI IDP user (State Super Administrator) login to Complex-Configurable Role Mapping Page
 
Given I have an open web browser
And I am not authenticated to SLI IDP
And I have tried to access the Complex-Configurable Role Mapping Page
And I was redirected to the realm page
And I choose realm "Shared Learning Initiative" in the drop-down list
And I click on the page Go button
And I was redirected to the SLI IDP Login page
And I am user "demo"
And "demo" is valid "SLI IDP" user
And I have a Role attribute equal to "SLI IT Administrator"
When I enter "demo" in the username text field
And I enter "demo1234" in the password text field
And I click the Go button
Then I am authenticated to SLI IDP
And I am redirected to the Complex-Configurable Role Mapping Page
 
 @wip
Scenario:  Complex-Configurable Role Mapping Page logout
 
Given I have an open web browser
And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
And I have navigated to my Complex-Configurable Role Mapping Page
When I click on the Logout link
Then I am not authenticated to SLI IDP
 
Scenario: Reset the mapping to default mappings
 
Given I have an open web browser
And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
And I have navigated to my Complex-Configurable Role Mapping Page
When I click on the Reset Mapping button
Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now mapped to themselves
And no other mappings exist for this realm
 
Scenario Outline: Creating correct mappings for roles 
 
Given I have an open web browser
And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
And I have navigated to my Complex-Configurable Role Mapping Page
When I click on the role <Role> radio button
And I enter <Custom Role> in the text field
And I click the add button
Then the custom role <Custom Role> is mapped to the default role <Role> 
And The user <Username> who is a <Custom Role> can now log in to SLI as a <Role> from my realm "SLI"
Examples:
| Role               | Custom Role | Username  |
| "Educator"         | "Teacher"   | "teacher" |
| "Leader"           | "Principal" | "prince"  |
| "IT Administrator" | "Admin"     | "root"    |
| "Aggregate Viewer" | "Observer"  | "bigbro"  |
 
 Scenario Outline: Removing mappings from roles
Given I have an open web browser
And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
And I have navigated to my Complex-Configurable Role Mapping Page
When I click on the remove button between role <Role> and custom role <Custom Role>
Then the custom role <Custom Role> is no longer mapped to the default role <Role> 
And The user <Username> who is a <Custom Role> can not access SLI as a <Role> from my realm "SLI"
Examples:
| Role               | Custom Role | Username  |
| "Educator"         | "Teacher"   | "teacher" |
| "Leader"           | "Principal" | "prince"  |
| "IT Administrator" | "Admin"     | "root"    |
| "Aggregate Viewer" | "Observer"  | "bigbro"  |
 
Scenario Outline: Creating duplicated mappings for different roles
 
Given I have an open web browser
And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
And I have navigated to my Complex-Configurable Role Mapping Page
When I click on the role <First Role> radio button
And I enter <Custom Role> in the text field
And I click the add button
And I click on the role <Other Role> radio button
And I enter <Custom Role> in the text field
And I click the add button
Then I get a message that I cannot map the same custom role to multiple SLI Default roles
Examples:
| Fisrst Role | Custom Role | Other Role |
| "Educator"  | "Teachipal" | "Leader"   |
| "Leader"    | "Teachipal" | "Educator" |

 
Scenario Outline: Click Save in case of repeating values for same roles
 
 Given I have an open web browser
And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
And I have navigated to my Complex-Configurable Role Mapping Page
When I click on the role <Role> radio button
And I enter <Custom Role> in the text field
And I click the add button
And I enter <Custom Role> in the text field
And I click the add button
Then I get a message that I already have this role mapped to a SLI Default role
Examples:
| Role               | Custom Role |
| "Educator"         | "Learner"   |
| "Leader"           | "Commander" |
| "IT Administrator" | "Techie"    |
 
 @wip
Scenario Outline: Try to save incorrect values for roles
 
Given I have an open web browser
And I am authenticated to SLI IDP
And I have navigated to my Complex-Configurable Role Mapping Page
When I click on the role <Role> radio button
And I enter <Bad Text> in the text field
And I click the add button
Then I see a message that tells me that I can put only alphanumeric values as a custom role 
And the mapping is not added between <Role> and <Bad Text>
Examples:
| Role       | Bad Text   |
| "Educator" | "Learner!" |
| "Leader"   | "$*(&##@)" |

Scenario: Reset the mapping to default mappings when previous mappings exist
 
Given I have an open web browser
And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
And I have navigated to my Complex-Configurable Role Mapping Page
And I see pre-existing mappings
When I click on the Reset Mapping button
Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now mapped to themselves
And I no longer see the pre-existing mappings
