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

Scenario: Developer creates new application (set up data)
Given I am a valid SLI Developer "admintest-developer@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I select "inBloom" from the dropdown and click go
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And I have clicked to the button New
And I am redirected to a new application page
When I entered the name "Boyne" into the field titled "Name"
And I have entered data into the other required fields except for the shared secret and the app id which are read-only
And I click on the button Submit
Then I am redirected to the Application Registration Tool page
And the application "Boyne" is listed in the table on the top
When I expand the application row for "Boyne"
And the client ID and shared secret fields are Pending
And the Registration Status field is Pending

Scenario: SLC Operator accepts application registration request (set up data)
Given I am a valid SLC Operator "slcoperator-email@slidev.org" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I select "inBloom" from the dropdown and click go
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
Then I am redirected to the Application Approval Tool page
And I see all the applications registered on SLI
And I see all the applications pending registration
And the pending apps are on top
When I click on 'Approve' next to application "Boyne"
Then application "Boyne" is registered
And the 'Approve' button is disabled for application "Boyne"

Scenario: Developer registers application (set up data)
Given I am a valid SLI Developer "slcdeveloper" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
And I select "inBloom" from the dropdown and click go
And I was redirected to the "Simple" IDP Login page
And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
And I see the list of (only) my applications
And a "In Progress" button is displayed for application "Boyne"
And I clicked on the button Edit for the application "Boyne"
And I expand all nodes
And I enable the educationalOrganization "Illinois State Board of Education" in tenant "Midgar"
And I click on Save
Then "Boyne" is enabled for "200" education organizations

Scenario: Linda Kim encounters Access Denied Message when attempting to access Application Authorization Tool using default Educator role
#check educator seoa exists
Given "linda.kim" has an active staffEducationOrganizationAssociation of "Educator" for "East Daybreak Junior High" in tenant "Midgar"
And the sli securityEvent collection is empty
When I hit the Admin Application Authorization Tool
And I select "Illinois Daybreak School District 4529" from the dropdown and click go
And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
And the error message "Sorry, you don't have access to this page. if you feel like you are getting this message in error, please contact your administrator." is displayed

Scenario: Create Application Authorizer Staff Education Organization Association (set up)
When I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
And a staffEducationOrgAssignmentAssociation is created for user "linda.kim" with role "Application Authorizer" for education organization "South Daybreak Elementary" in tenant "Midgar"
And a staffEducationOrgAssignmentAssociation is created for user "linda.kim" with role "Educator" for education organization "Sunset Central High School" in tenant "Midgar"

Scenario: Linda Kim Approves application as Application Authorizer
When I hit the Admin Application Authorization Tool
And I select "Illinois Daybreak School District 4529" from the dropdown and click go
And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
And I see an application "Boyne" in the table
And in Status it says "Not Approved"
And the sli securityEvent collection is empty
And I click on the "Edit Authorizations" button next to it
And I expand all nodes
And I deselect hierarchical mode
And I authorize the educationalOrganization "South Daybreak Elementary"
And I click Update
Then there are "1" edOrgs for the "Boyne" application in the applicationAuthorization collection for the "Midgar" tenant
And I check to find if record is in sli db collection:
| collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
| securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
| securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                               |
And there are "1" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
And I see an application "Boyne" in the table
And in Status it says "1 EdOrg(s)"
Given the sli securityEvent collection is empty
When I click on the "Edit Authorizations" button next to it
And I expand all nodes
And I deselect hierarchical mode
And I de-authorize the educationalOrganization "South Daybreak Elementary"
And I click Update
Then there are "0" edOrgs for the "Boyne" application in the applicationAuthorization collection for the "Midgar" tenant
And I check to find if record is in sli db collection:
| collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
| securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
| securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                               |
And there are "1" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"
And I see an application "Boyne" in the table
And in Status it says "Not Approved"

Scenario: Give Educators APP_AUTHORIZE Right (set up)
When I navigate to the Custom Role Mapping Page
And I select "inBloom" from the dropdown and click go
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
When I click on the Custom Roles button next to Illinois Daybreak School District 4529
Then I have navigated to my Custom Role Mapping Page
And I edit the group "Educator"
When I add the right "APP_AUTHORIZE" to the group "Educator"
And I hit the save button
Then I am no longer in edit mode
And the group "Educator" contains the "right" rights "EDUCATOR APP AUTH"

Scenario: Linda Kim Approves application as Educator
When I hit the Admin Application Authorization Tool
And I select "Illinois Daybreak School District 4529" from the dropdown and click go
And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
And I see an application "Boyne" in the table
And in Status it says "Not Approved"
And the sli securityEvent collection is empty
And I click on the "Edit Authorizations" button next to it
And I expand all nodes
And I deselect hierarchical mode
And I authorize the educationalOrganization "East Daybreak Junior High"
And I authorize the educationalOrganization "Sunset Central High School"
And I click Update
Then there are "2" edOrgs for the "Boyne" application in the applicationAuthorization collection for the "Midgar" tenant
And I check to find if record is in sli db collection:
| collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
| securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
| securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                               |
And there are "2" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
And I see an application "Boyne" in the table
And in Status it says "2 EdOrg(s)"
Given the sli securityEvent collection is empty
When I click on the "Edit Authorizations" button next to it
And I expand all nodes
And I deselect hierarchical mode
And I de-authorize the educationalOrganization "East Daybreak Junior High"
And I de-authorize the educationalOrganization "Sunset Central High School"
And I click Update
Then there are "0" edOrgs for the "Boyne" application in the applicationAuthorization collection for the "Midgar" tenant
And I check to find if record is in sli db collection:
| collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
| securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
| securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                               |
And there are "2" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"
And I see an application "Boyne" in the table
And in Status it says "Not Approved"

Scenario: Create IT Administrator Staff Education Organization Association (set up)
When I log in to realm "Illinois Daybreak School District 4529" using simple-idp as "IT Administrator" "rrogers" with password "rrogers1234"
And a staffEducationOrgAssignmentAssociation is created for user "linda.kim" with role "IT Administrator" for education organization "Daybreak School District 4529" in tenant "Midgar"

@wip
Scenario: Linda Kim Approves application as an LEA level IT Administrator
When I hit the Admin Application Authorization Tool
And I select "Illinois Daybreak School District 4529" from the dropdown and click go
And I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
And I see an application "Boyne" in the table
And in Status it says "Not Approved"
And the sli securityEvent collection is empty
And I click on the "Edit Authorizations" button next to it
And I expand all nodes
And I authorize the educationalOrganization "Daybreak School District 4529"
And I click Update
Then there are "45" edOrgs for the "Boyne" application in the applicationAuthorization collection for the "Midgar" tenant
And I check to find if record is in sli db collection:
| collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
| securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
| securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                               |
And there are "45" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
And I see an application "Boyne" in the table
And in Status it says "45 EdOrg(s)"
Given the sli securityEvent collection is empty
When I click on the "Edit Authorizations" button next to it
And I expand all nodes
And I de-authorize the educationalOrganization "Daybreak School District 4529"
And I click Update
Then there are "0" edOrgs for the "Boyne" application in the applicationAuthorization collection for the "Midgar" tenant
And I check to find if record is in sli db collection:
| collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
| securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
| securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                               |
And there are "45" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"
And I see an application "Boyne" in the table
And in Status it says "Not Approved"

Scenario: Federated SEA Admin Approves application
When I hit the Admin Application Authorization Tool
And I select "Illinois Daybreak School District 4529" from the dropdown and click go
And I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I see an application "Boyne" in the table
And in Status it says "Not Approved"
And the sli securityEvent collection is empty
And I click on the "Edit Authorizations" button next to it
And I expand all nodes
And I authorize the educationalOrganization "Illinois State Board of Education"
And I click Update
Then there are "200" edOrgs for the "Boyne" application in the applicationAuthorization collection for the "Midgar" tenant
And I check to find if record is in sli db collection:
| collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
| securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
| securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                               |
And there are "200" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
And I see an application "Boyne" in the table
And in Status it says "200 EdOrg(s)"
Given the sli securityEvent collection is empty
When I click on the "Edit Authorizations" button next to it
And I expand all nodes
And I de-authorize the educationalOrganization "Illinois State Board of Education"
And I click Update
Then there are "0" edOrgs for the "Boyne" application in the applicationAuthorization collection for the "Midgar" tenant
And I check to find if record is in sli db collection:
| collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
| securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
| securityEvent       | 1                   | body.userEdOrg        | IL-DAYBREAK                               |
And there are "200" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"
And I see an application "Boyne" in the table
And in Status it says "Not Approved"

