@RALLY_US176
@RALLY_US174
Feature:  Complex-Configurable Role Mapping Tool
 
As an SEA/LEA  Admin, I would like to have the Complex Role Mapping admin tool, so that I can map lists of SEA/LEA Directory roles to the SLI Default Roles.
 
#Admin Scenarios

Scenario: Monster Complex Role Mapping Tool

Given I have an open web browser
When I navigate to the Complex-Configurable Role Mapping Page
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Complex-Configurable Role Mapping Page
#Scenario: Reset mappings to default
When I click on the Reset Mapping button
And I got warning message saying 'Are you sure you want to reset the role mappings?'
When I click 'OK'
Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
#Scenario: Creating correct mappings for roles
When I create a mapping between <Role> and <Custom Role> that allows <User> to access the API
| Role               | Custom Role | User      |
| "Educator"         | "Teacher"   | "teacher" |
| "Leader"           | "Principal" | "prince"  |
| "IT Administrator" | "Admin"     | "root"    |
| "Aggregate Viewer" | "Observer"  | "bigbro"  |
Then I see the mapping in the table
And That user can now access the API
#Scenario: Removing amppings from roles
When I remove the mapping between <Role> and <Custom Role> that denies <User> access to the API
| Role               | Custom Role | User      |
| "Educator"         | "Teacher"   | "teacher" |
| "Leader"           | "Principal" | "prince"  |
| "IT Administrator" | "Admin"     | "root"    |
| "Aggregate Viewer" | "Observer"  | "bigbro"  |
Then I no longer see that mapping in the table
And That use can no longer access the API
#Scenario: Creating duplicated mappings for different roles
When I create a mapping for <Custom Role> to both <First Role> and <Second Role>
| First Role | Custom Role | Second Role |
| "Educator" | "Teachipal" | "Leader"   |
| "Leader"   | "Princator" | "Educator" |
Then I get an error message when I create the second mapping
#Scenario: Click Save in case of repeating values for same roles
When I create a mapping between <Custom Role> and <Role> twice
| Role               | Custom Role |
| "Educator"         | "Learner"   |
| "Leader"           | "Commander" |
| "IT Administrator" | "Techie"    |
Then I get an error message complaining that the mapping already exists
#Scenario: Try to save incorrect values for roles
When I create a mapping between <Role> and <Custom Role> which contains non-alphanumeric characters
| Role       | Custom Role |
| "Educator" | "Learner!"  |
| "Leader"   | "$*(&##@)"  |
Then I get an error stating we cannot have mappings containing special characters

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
