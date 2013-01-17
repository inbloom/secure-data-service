@RALLY_US4835
@rc
@sandbox
Feature: Data Browser
  As a Data Browser user, I want to be able to traverse all of the data I have access to so that I can investigate/troubleshoot issues as they come up

  Background: None

  Scenario: All-in-one scenario
    Given I have an open web browser
    When I navigate to the Portal home page
    And I was redirected to the "Simple" IDP Login page
    When I submit the developer credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the impersonation login page
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

    # Illinois State IT Administrator - Rick Rogers
    And I want to select "rrogers" from the "SmallDatasetUsers" in automatic mode
    Then I should be on Portal home page
    And I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "inBloom Data Browser"
    Then I should be redirected to the Data Browser home page
    And I should see the name "Rick Rogers" on the page
    And I should see my available links labeled
    And I have navigated to the <Page> of the Data Browser
      | Page                                       |
      | GetStaffEducationOrgAssignmentAssociations |
      | GetStaffProgramAssociations                |
      | Me                                         |
    Then I should click on the Home link and be redirected back
    And I have navigated to the "Me" page of the Data Browser
    Then I am redirected to the particular entity Detail View
    When I click on the "GetStaffCohortAssociations" link
    Then I am redirected to the particular associations Simple View
    When I click and go back to Home
    And I click on the "GetEducationOrganizations" link
    Then I should be on the detailed page for an SEA
    When I click on the "GetFeederEducationOrganizations" link
    Then I should be on the detailed page for an LEA
    Then I log out of Databrowser 
    And I navigate to the Portal home page
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

    # Illinois Daybreak District IT Administrator - James Stevenson
    And I want to select "jstevenson" from the "SmallDatasetUsers" in automatic mode
    Then I should be on Portal home page
    And I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "inBloom Data Browser"
    Then I should be redirected to the Data Browser home page
    And I should see the name "James Stevenson" on the page
    And I click on the "GetStaffProgramAssociations" link
    Then I am redirected to the associations list page
    And I see a table displaying the associations in a list
    And those names include the IDs of both "ProgramId" and "StaffId" in the association
    When I click on the row containing "2012-02-15"
    Then the row expands below listing the rest of the attributes for the item
    When I click on the row containing "2012-02-15"
    Then the row collapses hiding the additional attributes
    When I click and go back to Home
    And I have navigated to the "GetStaffProgramAssociations" page of the Data Browser
    When I click on the row containing "2012-02-15"
    And I click on the "Me" of any of the associating entities
    Then I am redirected to a page that page lists all of the "2012-02-15" entity's fields
    Then I log out of Databrowser 
    And I navigate to the Portal home page
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

    # Illinois South Daybreak Elementary IT Administrator - Amy Kopel
    And I want to select "akopel" from the "SmallDatasetUsers" in automatic mode
    Then I should be on Portal home page
    And I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "inBloom Data Browser"
    Then I should be redirected to the Data Browser home page
    And I should see the name "Amy Kopel" on the page
    And I click on the "GetEducationOrganizations" link
    And I click on the "GetParentEducationOrganization" link
    And I click on the "GetFeederSchools" link
    When I click on the row containing "Daybreak Central High"
    And I click on the "Me" of any of the associating entities
    And I click on the "GetTeachers" link
    Then I see a "You do not have access to view this." alert box
    And I click the X
    Then the error is dismissed
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
    Then I log out of Databrowser
    And I navigate to the Portal home page
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

    # Illinois Daybreak Central High Educator - Charles Gray
    And I want to select "cgray" from the "SmallDatasetUsers" in automatic mode
    Then I should be on Portal home page
    And I should not see Admin link
    When I navigated to the Data Browser Home URL
    And I should see the name "Charles Gray" on the page
    Then I am notified that "You are not authorized to use this app."
    When I navigate to the Portal home page
    And I click on log out
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in

    # Illinois Daybreak Central High Educator - Charles Gray as IT Administrator
    And I want to manually imitate the user "cgray" who is a "IT Administrator"
    Then I should be on Portal home page
    And I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "inBloom Data Browser"
    Then I should be redirected to the Data Browser home page
    And I should see the name "Charles Gray" on the page
    And I should see my available links labeled
    And I have navigated to the <Page> of the Data Browser
      | Page                    |
      | Teacher to Section List |
      | My Sections             |
      | Teacher to School List  |
      | My Schools              |
      | Me                      |
    Then I should click on the Home link and be redirected back
    When I search for the identifier "<CARMEN ORTIZ UNIQUE ID>" in "students"
    Then I should see the text "Carmen"
    And I should see the text "Ortiz"
    When I search for the identifier "<MATT SOLLARS UNIQUE ID>" in "students"
    Then I see a "There were no entries matching your search" alert box
