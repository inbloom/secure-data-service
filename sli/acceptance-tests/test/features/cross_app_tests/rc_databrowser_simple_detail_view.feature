@RALLY_US4835
@rc
Feature: Data Browser
As a Data Browser user, I want to be able to traverse all of the data I have access to so that I can investigate/troubleshoot issues as they come up

Background:
  Given I have an open web browser
  And I navigated to the Data Browser Home URL
  And I was redirected to the Realm page

Scenario: Login and logout
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be redirected to the Data Browser home page
  When I click on the Logout link
  #Then I am redirected to a page that informs me that I have signed out
  And I am forced to reauthenticate to access the databrowser

Scenario: Navigate to home page from any page
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be redirected to the Data Browser home page
  And I should see my available links labeled
  And I have navigated to the <Page> of the Data Browser
    | Page                                       |
    | GetStaffEducationOrgAssignmentAssociations |
    | GetStaffProgramAssociations                |
    | Me                                         |
  Then I should click on the Home link and be redirected back

Scenario: Associations List - Simple View
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And I click on the "GetStaffProgramAssociations" link
  Then I am redirected to the associations list page
  And I see a table displaying the associations in a list
  And those names include the IDs of both "ProgramId" and "StaffId" in the association
 
Scenario: Associations List - Expand/Collapse between Simple View and Detail View
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And I have navigated to the "GetStaffCohortAssociations" page of the Data Browser
  When I click on the row containing "b408635d-8fd5-11e1-86ec-0021701f543f"
  Then the row expands below listing the rest of the attributes for the item
  When I click on the row containing "b408635d-8fd5-11e1-86ec-0021701f543f"
  Then the row collapses hiding the additional attributes

Scenario Outline: Entity Detail View
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And I have navigated to the <Page> page of the Data Browser
  When I click on the row containing <Text>
  And I click on the <Link> of any of the associating entities
  Then I am redirected to a page that page lists all of the <Entity> entity's fields
   Examples:
  | Page                          | Text                                   | Link        | Entity                                                          |
  | "GetStaffProgramAssociations" | "9bf906cc-8fd5-11e1-86ec-0021701f5431" | "Me"        | "9bf906cc-8fd5-11e1-86ec-0021701f5431"                          |
  | "GetStaffCohortAssociations"  | "8fef446f-fc63-15f9-8606-0b85086c07d5" | "GetCohort" | "District-wide academic intervention cohort for Social Studies" |
  | "GetStaffCohortAssociations"  | "8fef446f-fc63-15f9-8606-0b85086c07d5" | "GetStaff"  | "rrogers"                                                       |

Scenario: Click on Available Links associations
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And I have navigated to the "Me" page of the Data Browser
  When I click on the "GetStaffCohortAssociations" link
  Then I am redirected to the particular associations Simple View

Scenario: Click on Available Links entities
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And I have navigated to the "Me" page of the Data Browser
  Then I am redirected to the particular entity Detail View

  @wip
Scenario: Get a Forbidden message when we access something that is forbidden
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  And I have navigated to the "Schools" listing of the Data Browser
  When I should navigate to "/entities/schools/a13489364c2eb015c219172d561c62350f0453f3_id"
  And I click on the "GetTeachers" link
  Then I see a "You do not have access to view this." alert box
  And I click the X
  Then the error is dismissed

Scenario: Traverse Edorg Hiearchy from SEA down to LEA
  When I choose realm "Daybreak Test Realm" in the drop-down list
  And I click on the realm page Go button
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be redirected to the Data Browser home page
  When I click on the "GetEducationOrganizations" link
  Then I should be on the detailed page for an SEA
  When I click on the "GetFeederEducationOrganizations" link
  Then I should be on the detailed page for an LEA

  @wip
Scenario: Educators are not authorized to use databrowser