@US5865
Feature: Authorize Applications with Federated Users

Background:
Given I have an open web browser

Scenario: Prepare Custom Roles (set up)
#Reset mapping to default to start from known state
When I navigate to the Custom Role Mapping Page
And I select "inBloom" from the dropdown and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
When I click on the Custom Roles button next to Illinois Daybreak School District 4529
Then I have navigated to my Custom Role Mapping Page
When I click on the Reset Mapping button
And I got a warning message saying "Are you sure you want to reset the mappings to factory defaults? This will remove any custom defined roles!"
When I click 'OK' on the warning message
Then I am no longer in edit mode
Then the Leader, Educator, Aggregate Viewer and IT Administrator roles are now only mapped to themselves
And the Leader, Educator, Aggregate Viewer and IT Administrator role groups have the correct default role names
And the IT Administrator role is the only admin role
#Create custom role
When I click on the Add Group button
And I type the name "Application Authorizer" in the Group name textbox
When I add the right "APP_AUTHORIZE" to the group "Application Authorizer"
When I add the right "READ_GENERAL" to the group "Application Authorizer"
And I add the role "Application Authorizer" to the group "Application Authorizer"
And I hit the save button
Then I am no longer in edit mode
And the group "Application Authorizer" contains the roles "Application Authorizer"
And the group "Application Authorizer" contains the "right" rights "APP AUTH"

Scenario: Prepare Staff Education Organization Associations (set up)
#check educator seoa exists
Given "linda.kim" has an active staffEducationOrganizationAssociation of "Educator" for "East Daybreak Junior High" in tenant "Midgar"
When I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
And a staffEducationOrgAssignmentAssociation is created for user "linda.kim" with role "IT Administrator" for education organization "Daybreak School District 4529" in tenant "Midgar"
And a staffEducationOrgAssignmentAssociation is created for user "linda.kim" with role "Application Authorizer" for education organization "South Daybreak Elementary" in tenant "Midgar"
And a staffEducationOrgAssignmentAssociation is created for user "linda.kim" with role "Educator" for education organization "Sunset Central High School" in tenant "Midgar"
