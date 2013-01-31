@RALLY_US4835
@rc
Feature: Data Browser
As a Data Browser user, I want to be able to traverse all of the data I have access to so that I can investigate/troubleshoot issues as they come up

Background:
  Given I have an open web browser
  When I navigate to the Portal home page
  And I see the realm selector I authenticate to "Daybreak Test Realm"
  And I was redirected to the "Simple" IDP Login page

Scenario: Login and logout
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "inBloom Data Browser"
  Then I should be redirected to the Data Browser home page
  When I click on the Logout link
  And I am forced to reauthenticate to access the databrowser

Scenario: Navigate to home page from any page
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "inBloom Data Browser"
  Then I should be redirected to the Data Browser home page
  And I should see my available links labeled
  And I have navigated to the <Page> of the Data Browser
    | Page                                       |
    | GetStaffEducationOrgAssignmentAssociations |
    | GetStaffProgramAssociations                |
    | Me                                         |
  Then I should click on the Home link and be redirected back

Scenario: Associations List - Expand/Collapse between Simple View and Detail View
  When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "inBloom Data Browser"
  Then I should be redirected to the Data Browser home page
  And I click on the "GetStaffProgramAssociations" link
  Then I am redirected to the associations list page
  And I see a table displaying the associations in a list
  And those names include the IDs of both "ProgramId" and "StaffId" in the association
  When I click on the row containing "ce92126960911fc94f308947cc3d14bb50e81927_id"
  Then the row expands below listing the rest of the attributes for the item
  When I click on the row containing "ce92126960911fc94f308947cc3d14bb50e81927_id"
  Then the row collapses hiding the additional attributes

Scenario Outline: Entity Detail View
  When I submit the credentials "jstevenson" "jstevenson1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "inBloom Data Browser"
  Then I should be redirected to the Data Browser home page
  And I have navigated to the <Page> page of the Data Browser
  When I click on the row containing <Text>
  And I click on the <Link> of any of the associating entities
  Then I am redirected to a page that page lists all of the <Entity> entity's fields
  Examples:
  | Page                          | Text                                          | Link        | Entity                                                          |
  | "GetStaffProgramAssociations" | "ce92126960911fc94f308947cc3d14bb50e81927_id" | "Me"        | "ce92126960911fc94f308947cc3d14bb50e81927_id"                   |
#  | "GetStaffCohortAssociations"  | "8fef446f-fc63-15f9-8606-0b85086c07d5"        | "GetCohort" | "District-wide academic intervention cohort for Social Studies" |
#  | "GetStaffCohortAssociations"  | "8fef446f-fc63-15f9-8606-0b85086c07d5"        | "GetStaff"  | "rrogers"                                                       |

Scenario: Click on Available Links associations and entities
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "inBloom Data Browser"
  Then I should be redirected to the Data Browser home page
  And I have navigated to the "Me" page of the Data Browser
  Then I am redirected to the particular entity Detail View
  When I click on the "GetStaffCohortAssociations" link
  Then I am redirected to the particular associations Simple View

Scenario: Get a Forbidden message when we access something that is forbidden
  When I submit the credentials "akopel" "akopel1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "inBloom Data Browser"
  Then I should be redirected to the Data Browser home page
  And I click on the "GetEducationOrganizations" link
  And I click on the "GetParentEducationOrganization" link
  And I click on the "GetFeederSchools" link
  When I click on the row containing "Daybreak Central High"
  And I click on the "Me" of any of the associating entities
  And I click on the "GetTeachers" link
  Then I see a "You do not have access to view this." alert box
  And I click the X
  Then the error is dismissed

Scenario: Traverse Edorg Hiearchy from SEA down to LEA
  When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "inBloom Data Browser"
  Then I should be redirected to the Data Browser home page
  When I click on the "GetEducationOrganizations" link
  Then I should be on the detailed page for an SEA
  When I click on the "GetFeederEducationOrganizations" link
  Then I should be on the detailed page for an LEA

Scenario: Educators are not authorized to use databrowser
  When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should not see Admin link
  When I navigated to the Data Browser Home URL
  Then I am notified that "You are not authorized to use this app."

Scenario: Search by id
  When I submit the credentials "akopel" "akopel1234" for the "Simple" login page
  Then I should be on Portal home page
  And I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "inBloom Data Browser"
  Then I should be redirected to the Data Browser home page
  When I search for the identifier "<BRANDON SUZUKI UNIQUE ID>" in "students"
  Then I should see the text "Brandon"
  And I should see the text "Suzuki"
  When I search for the identifier "South Daybreak Elementary" in "educationOrganizations"
  Then I should see the text "South Daybreak Elementary"
  And I should see the text "Elementary School"
  When I search for the identifier "<REBECCA BRAVERMAN UNIQUE ID>" in "staff"
  Then I should see the text "Rebecca"
  And I should see the text "Braverman"
  When I search for the identifier "<AMY KOPEL UNIQUE ID>" in "staff"
  Then I should see the text "Amy"
  And I should see the text "Kopel"
  # Search for something I don't have access to
  When I search for the identifier "<ZOE LOCUST UNIQUE ID>" in "students"
  Then I see a "There were no entries matching your search" alert box
