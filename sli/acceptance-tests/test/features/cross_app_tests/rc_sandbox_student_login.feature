@RALLY_F38
@rc
@sandbox
Feature:  RC Integration Tests - Test Student Login

###
###  This feature assumes rc_sandbox_app_approval.feature has run before it.
###

  Background:
    Given I have an open web browser

  Scenario: App developer impersonates a student and views data in the databrowser
  # Make the Student role Admin so students can use databro
    When I navigate to the Portal home page
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
    And under System Tools, I click on "Custom Roles"
    When I edit the group "Student"
    And I check the admin role box
    And I hit the save button
#    Then I am no longer in edit mode
#    And the group "Student" has the admin role box checked
    And I click on log out

# Login as a student a take a look at some stuff.
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in
    And I want to select "800000025" from the "SmallDatasetUsers" in automatic mode
    Then I should be on Portal home page
    And I click on Admin
    Then the portal should be on the admin page

    #students are not permitted to use the databrowser.
    #And under System Tools, I click on "inBloom Data Browser"
    #Then I should be redirected to the Data Browser home page
    #And I should see the name "Matt Sollars Jr" on the page
    #And I have navigated to the "Me" page of the Data Browser
    #Then I can see my "Gender" is "Male"
    #Then I can see my "State ID" is "800000025"
    #Then I log out of Databrowser
