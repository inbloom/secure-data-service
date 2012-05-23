@RALLY_US174
Feature:  Complex-Configurable Role Mapping Tool
 
As an SEA/LEA  Admin, I would like to have the Complex Role Mapping admin tool, so that I can map lists of SEA/LEA Directory roles to the SLI Default Roles.
 
#Admin Scenarios
 
Scenario: Reset the mapping to default mappings
 
Given I have an open web browser
When I navigate to the Complex-Configurable Role Mapping Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Complex-Configurable Role Mapping Page
 When I click on the Reset Mapping button
And I got warning message saying 'Are you sure you want to reset the role mappings?'
When I click 'OK'
Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
 
Scenario Outline: Creating correct mappings for roles 
 
Given I have an open web browser
When I navigate to the Complex-Configurable Role Mapping Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Complex-Configurable Role Mapping Page
When I click on the role <Role> radio button
And I enter <Custom Role> in the text field
And I click the add button
Then the custom role <Custom Role> is mapped to the default role <Role> 
And The user <Username> who is a <Custom Role> can now log in to SLI as a <Role> from my realm "IL"
Examples:
| Role               | Custom Role | Username  |
| "Educator"         | "Teacher"   | "teacher" |
| "Leader"           | "Principal" | "prince"  |
| "IT Administrator" | "Admin"     | "root"    |
| "Aggregate Viewer" | "Observer"  | "bigbro"  |
 
 Scenario Outline: Removing mappings from roles
Given I have an open web browser
When I navigate to the Complex-Configurable Role Mapping Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Complex-Configurable Role Mapping Page
When I click on the remove button between role <Role> and custom role <Custom Role>
Then the custom role <Custom Role> is no longer mapped to the default role <Role> 
And The user <Username> who is a <Custom Role> can not access SLI as a <Role> from my realm "IL"
Examples:
| Role               | Custom Role | Username  |
| "Educator"         | "Teacher"   | "teacher" |
| "Leader"           | "Principal" | "prince"  |
| "IT Administrator" | "Admin"     | "root"    |
| "Aggregate Viewer" | "Observer"  | "bigbro"  |
 
Scenario Outline: Creating duplicated mappings for different roles
 
Given I have an open web browser
When I navigate to the Complex-Configurable Role Mapping Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Complex-Configurable Role Mapping Page
When I click on the role <First Role> radio button
And I enter <Custom Role> in the text field
And I click the add button
And I wait for a second
And I click on the role <Other Role> radio button
And I enter <Custom Role> in the text field
And I click the add button
Then I get a message that I cannot map the same custom role to multiple SLI Default roles
Examples:
| First Role | Custom Role | Other Role |
| "Educator" | "Teachipal" | "Leader"   |
| "Leader"   | "Princator" | "Educator" |

Scenario Outline: Click Save in case of repeating values for same roles
 
Given I have an open web browser
When I navigate to the Complex-Configurable Role Mapping Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Complex-Configurable Role Mapping Page
When I click on the role <Role> radio button
And I enter <Custom Role> in the text field
And I click the add button
And I wait for a second
And I enter <Custom Role> in the text field
And I click the add button
Then I get a message that I already have this role mapped to a SLI Default role
Examples:
| Role               | Custom Role |
| "Educator"         | "Learner"   |
| "Leader"           | "Commander" |
| "IT Administrator" | "Techie"    |
 
 Scenario Outline: Try to save incorrect values for roles
 
Given I have an open web browser
When I navigate to the Complex-Configurable Role Mapping Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Complex-Configurable Role Mapping Page
When I click on the role <Role> radio button
And I enter <Bad Text> in the text field
And I click the add button
Then I see a message that tells me that I can put only alphanumeric values as a custom role 
And the mapping is not added between default role <Role> and custom role <Bad Text>
Examples:
| Role       | Bad Text   |
| "Educator" | "Learner!" |
| "Leader"   | "$*(&##@)" |

Scenario: Reset the mapping to default mappings when previous mappings exist
 
Given I have an open web browser
When I navigate to the Complex-Configurable Role Mapping Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Complex-Configurable Role Mapping Page
And I see pre-existing mappings
When I click on the Reset Mapping button
And I got warning message saying 'Are you sure you want to reset the role mappings?'
When I click 'OK'
Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
And I no longer see the pre-existing mappings
