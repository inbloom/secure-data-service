@RALLY_US215
Feature: Data Browser
As a Data Browser user, I want to be able to traverse all of the data I have access to so that I can investigate/troubleshoot issues as they come up
Background:
  Given that databrowser has been authorized for all ed orgs

Scenario: Go to Data Browser when authenticated SLI
 
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
And I should see my available links labeled
 
Scenario: Logout 

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
When I click on the Logout link
#Then I am redirected to a page that informs me that I have signed out
And I am forced to reauthenticate to access the databrowser

@smoke
Scenario: Navigate to home page from any page

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I have navigated to the <Page> of the Data Browser
	|Page|
	|GetStaffEducationOrgAssignmentAssociations|
	|GetStaffProgramAssociations|
	|Me|
Then I should click on the Home link and be redirected back 
	
Scenario: Associations List - Simple View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I click on the "GetStaffProgramAssociations" link
Then I am redirected to the associations list page
And I see a table displaying the associations in a list
And those names include the IDs of both "ProgramId" and "StaffId" in the association
 
Scenario: Associations List - Expand/Collapse between Simple View and Detail View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I have navigated to the "GetStaffCohortAssociations" page of the Data Browser
When I click on the row containing "b408635d-8fd5-11e1-86ec-0021701f543f"
Then the row expands below listing the rest of the attributes for the item
When I click on the row containing "b408635d-8fd5-11e1-86ec-0021701f543f"
Then the row collapses hiding the additional attributes

Scenario Outline: Entity Detail View

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
And I have navigated to the <Page> page of the Data Browser
When I click on the row containing <Text>
And I click on the <Link> of any of the associating entities
Then I am redirected to a page that page lists all of the <Entity> entity's fields
 Examples:
| Page                                      | Text                                    | Link         | Entity                                 |
| "GetStaffProgramAssociations"             | "9bf906cc-8fd5-11e1-86ec-0021701f5431"  | "Me"         | "9bf906cc-8fd5-11e1-86ec-0021701f5431" |
| "GetStaffCohortAssociations" | "8fef446f-fc63-15f9-8606-0b85086c07d5"| "GetCohort" | "District-wide academic intervention cohort for Social Studies" |
| "GetStaffCohortAssociations" | "8fef446f-fc63-15f9-8606-0b85086c07d5"| "GetStaff" | "rrogers"        |


Scenario: Click on Available Links associations

Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
And I have navigated to the "Me" page of the Data Browser
When I click on the "GetStaffCohortAssociations" link
Then I am redirected to the particular associations Simple View


Scenario: Click on Available Links entities
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
And I have navigated to the "Me" page of the Data Browser
Then I am redirected to the particular entity Detail View


Scenario: Get a Forbidden message when we access something that is forbidden
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "jwashington" "jwashington1234" for the "Simple" login page
And I have navigated to the "Schools" listing of the Data Browser
When I should navigate to "/entities/schools/a189b6f2-cc17-4d66-8b0d-0478dcf0cdfb"
And I click on the "GetTeachers" link
Then I see a "You do not have access to view this." alert box
And I click the X
Then the error is dismissed

@DE1948
Scenario: Traverse Edorg Hiearchy from SEA down to LEA
Given I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
When I click on the "GetEducationOrganizations" link
Then I should be on the detailed page for an SEA
When I click on the "GetFeederEducationOrganizations" link
Then I should be on the detailed page for an LEA

 @wip
Scenario: Click on an entity ID in Simple View (same for Detail View)

Given I have an open web browser
And I am authenticated to SLI IDP as user "jwashington" with pass "jwashington1234"
And I have navigated to the "Teacher to Section List" page of the Data Browser
When I click on any of the entity IDs
Then I am redirected to the particular entity Detail View
