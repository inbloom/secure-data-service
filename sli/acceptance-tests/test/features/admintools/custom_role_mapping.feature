@RALLY_US176
@RALLY_US174
@RALLY_US3331
@RALLY_US2669
Feature: Custom Role Mapping Tool
As an SEA/LEA  Admin, I would like to have the Complex Role Mapping admin tool, so that I can map lists of SEA/LEA Directory roles to their associated SLI Access Rights.

Background:
Given I have an open web browser
When I navigate to the Custom Role Mapping Page
And I was redirected to the "Simple" IDP Login page

@production
Scenario: Reset to default role to right mapping
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I click on the Reset Mapping button
And I got a warning message saying "Are you sure you want to reset the mappings to factory defaults? This will remove any custom defined roles!"
When I click 'OK' on the warning message
Then I am no longer in edit mode
Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
And the IT Administrator role is the only admin role

@production
Scenario: Create new group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I click on the Add Group button
And I type the name "New Custom" in the Group name textbox
When I add the right "READ_GENERAL" to the group "New Custom"   
And I add the role "Dummy" to the group "New Custom"
And I hit the save button
Then I am no longer in edit mode
And the group "New Custom" contains the roles "Dummy"
And the group "New Custom" contains the rights "Read General"

@production
Scenario: Add role to existing group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I create a new role <Role> to the group <Group> that allows <User> to access the API
| Group              | Role        | User      |
| "Educator"         | "Teacher"   | "teacher" |
| "Leader"           | "Principal" | "prince"  |
| "IT Administrator" | "Admin"     | "root"    |
| "Aggregate Viewer" | "Observer"  | "bigbro"  |
| "New Custom"       | "Custom"    | "custom"  |
Then I see the mapping in the table
And That user can now access the API

@production
Scenario: Add rights to group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
And the user "custom" in tenant "IL" can access the API with rights "Read General"
And I edit the group "New Custom"
When I add the right "WRITE_GENERAL" to the group "New Custom"
And I check the admin role box
And I hit the save button
Then I am no longer in edit mode
And the group "New Custom" contains the rights "Read and Write General"
And the user "custom" in tenant "IL" can access the API with rights "Read and Write General"
And the group "New Custom" has the admin role box checked

@production
Scenario: Remove rights from group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I edit the group "New Custom"
When I remove the right "WRITE_GENERAL" from the group "New Custom"
Then the group "New Custom" contains the rights "Read General"
And I hit the save button
Then I am no longer in edit mode
And the group "New Custom" contains the rights "Read General"
And the user "custom" in tenant "IL" can access the API with rights "Read General"
When I edit the group "New Custom"
When I remove the right "READ_GENERAL" from the group "New Custom"
Then I am informed that I must have at least one role and right in the group
And the user "custom" in tenant "IL" can access the API with rights "Read General"

@production
Scenario: Remove role from group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I remove the role <Role> from the group <Group> that denies <User> access to the API
| Group              | Role        | User      |
| "Educator"         | "Teacher"   | "teacher" |
| "Leader"           | "Principal" | "prince"  |
| "IT Administrator" | "Admin"     | "root"    |
| "Aggregate Viewer" | "Observer"  | "bigbro"  |
| "New Custom"       | "Custom"    | "custom"  |
Then I no longer see that mapping in the table
And That user can no longer access the API

@production
Scenario: Cannot remove last role in group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I edit the group "New Custom"
When I remove the role "Dummy" from the group "New Custom"
Then I am informed that I must have at least one role and right in the group

@production
Scenario: Remove group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I remove the group "New Custom"
Then the group "New Custom" no longer appears on the page

@production
Scenario: Cannot add duplicate rights within a group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I edit the rights for the group <Group> to include the duplicate right <Right>
| Right             | Group              |
| "Read General"    | "Educator"         |
| "Write General"   | "IT Administrator" |
| "Read Restricted" | "Leader"           |
Then I cannot find the right in the dropdown

@production
Scenario: Cannot add duplicate roles
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I edit the roles for the group <Group> to include the duplicate role <Role>
| Role               | Group              |
| "Educator"         | "Educator"         |
| "Educator"         | "IT Administrator" |
| "Aggregate Viewer" | "Leader"           |
Then I am informed that "Role names must be unique across all groups"

@production
Scenario: Cancel button discards any unsaved changes for a group
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I edit the group "Educator"
When I add the right "WRITE_GENERAL" to the group "Educator"
And I add the role "Teacher" to the group "Educator"
And I click the cancel button
Then the group "Educator" contains the rights "Read General Public and Aggregate"
And the group "Educator" contains the roles "Educator"

@production
Scenario: An Educator is given WRITE_GENERAL in self context, they can write to themselves but no one else
When the user "cgray" in tenant "IL" tries to update the "firstName" for staff "cgray" to "Chuck"
Then I should receive a return code of 403
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
And I edit the group "Educator"
And I add the self right "WRITE_GENERAL" to the group "Educator"
And I add the self right "WRITE_RESTRICTED" to the group "Educator"
And I hit the save button
Then the user "cgray" in tenant "IL" can access the API with self rights "Read Restricted, Write Restricted and Write General"
And  the user "cgray" in tenant "IL" tries to update the "firstName" for staff "cgray" to "Chuck"
Then I should receive a return code of 204
And I should see that the "firstName" for staff "cgray" is "Chuck"
When the user "cgray" in tenant "IL" tries to update the "firstName" for staff "stweed" to "Chuck"
Then I should receive a return code of 403

@production
@derp
Scenario: An Aggregate Viewer is given all self rights, they can read and write to themselves but can't access anything else
When the user "jvasquez" in tenant "IL" tries to retrieve the staff "jvasquez"
Then I should receive a return code of 200
And  the user "jvasquez" in tenant "IL" tries to update the "firstName" for staff "jvasquez" to "Jerry"
Then I should receive a return code of 403
When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
And I edit the group "Aggregate Viewer"
And I remove the right "READ_GENERAL" from the group "Aggregate Viewer"
And I remove the right "READ_RESTRICTED" from the group "Aggregate Viewer"
And I hit the save button
Then the user "jvasquez" in tenant "IL" can access the API with self rights "none"
When the user "jvasquez" in tenant "IL" tries to retrieve the staff "jvasquez"
Then I should receive a return code of 403

@sandbox
Scenario: Developer modifies roles in their tenant without affecting other tenant
When I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I remove the group "Educator"
Then the group "Educator" no longer appears on the page
And the user "linda.kim" in tenant "developer-email" can access the API with rights "none"
And the user "linda.kim" in tenant "sandboxadministrator" can access the API with rights "IT Administrator"
When I click on the Add Group button
And I type the name "New Custom" in the Group name textbox
When I add the right "READ_GENERAL" to the group "New Custom"   
And I add the role "Educator" to the group "New Custom"
And I hit the save button
Then I am no longer in edit mode
Then the group "New Custom" contains the roles "Educator"
And the group "New Custom" contains the rights "Read General"
And the user "linda.kim" in tenant "developer-email" can access the API with rights "Read General"
And the user "linda.kim" in tenant "sandboxadministrator" can access the API with rights "IT Administrator"

@sandbox
Scenario: Sandbox reset to defaults
When I submit the credentials "sandboxdeveloper" "sandboxdeveloper1234" for the "Simple" login page
Then I have navigated to my Custom Role Mapping Page
When I edit the group "IT Administrator"
When I add the right "AGGREGATE_WRITE" to the group "IT Administrator"   
And I hit the save button
Then I am no longer in edit mode
And the user "linda.kim" in tenant "sandboxadministrator" can access the API with rights "all defaults"
And the user "linda.kim" in tenant "developer-email" can access the API with rights "Read General"
When I click on the Reset Mapping button
And I got a warning message saying "Are you sure you want to reset the mappings to factory defaults? This will remove any custom defined roles!"
When I click 'OK' on the warning message
Then I am no longer in edit mode
Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
And the IT Administrator role is the only admin role
And the user "linda.kim" in tenant "sandboxadministrator" can access the API with rights "IT Administrator"
And the user "linda.kim" in tenant "developer-email" can access the API with rights "Read General"