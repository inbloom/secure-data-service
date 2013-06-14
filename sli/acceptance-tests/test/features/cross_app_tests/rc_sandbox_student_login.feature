@RALLY_F38
@rc
@sandbox
Feature:  RC Integration Tests - Test Student Login

###
###  This feature assumes rc_sandbox_app_approval.feature has run before it.
###

  Background:
    Given I have an open web browser

  Scenario: App developer enables Students to use databrowser so they can see login and see data.
  #Installed App
    When I navigate to the Portal home page
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page
    Then I should be on Portal home page
    Then I should see Admin link
    And I click on Admin
    Then I should be on the admin page
    And under System Tools, I click on "Create Custom Roles"
    And I switch to the iframe
    When I edit the group "Student"
    And I check the admin role box
    And I hit the save button
    Then I am no longer in edit mode
    And I switch to the iframe
    And the group "Student" has the admin role box checked
    And I exit out of the iframe
    And I click on log out
#    Then I should be redirected to the impersonation page
#    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in
#    And I want to select "800000025" from the "SmallDatasetUsers" in automatic mode
#    Then I should be on Portal home page
#    And I click on Admin
#    Then I should be on the admin page
#    And under System Tools, I click on "inBloom Data Browser"
#    Then I should be redirected to the Data Browser home page
#    And I should see the name "Matt Sollars" on the page
#    Then I should click on the Home link and be redirected back
#    And I have navigated to the "Me" page of the Data Browser
#    Then I am redirected to the particular entity Detail View
#    When I click and go back to Home
#    Then I log out of Databrowser
