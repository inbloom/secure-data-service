@RALLY_US4835
@rc
@sandbox
Feature: RC Sandbox Integration - Developer Account Management Tool (DAMT)

  Background:
    Given I have an open web browser
    When I navigate to the Portal home page
    Then I will be redirected to realm selector web page
    When I click on the "Admin" realm in "Sandbox"
    And I was redirected to the "Simple" IDP Login page

    Scenario: Creating a developer account using DAMT
      When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page
      Then I should be on Portal home page
      And I should see Admin link
      And I click on Admin
      Then I should be on the admin page
      And under System Tools, I click on "Developer Account Management"
      And I switch to the iframe
      Then I am redirected to the "Sandbox Account Management" page

      Then I delete the user "RCTestDev PartTwo" if exists
      And I switch to the iframe
      Then I click on the "Add User" button
      And I switch to the iframe
      And I am redirected to the "Add a User" page
      And I can directly update the "Full Name" field to "RCTestDev PartTwo"
      And I can directly update the "Email" field to "<DEVELOPER2_SB_EMAIL>"
      And I can select "Sandbox Administrator" from a choice of "Sandbox Administrator, Application Developer, Ingestion User" Role
      And I can also check "Ingestion User" Role
      And I can also check "Application Developer" Role
      When I click button "Save"
      Then I am redirected to the "Sandbox Account Management" page
      And the "Success" message is displayed
      Then I set my password to "<DEVELOPER2_SB_EMAIL_PASS>"

    Scenario: Newly created sandbox developer can login and check available admin links
      When I submit the credentials "<DEVELOPER2_SB_EMAIL>" "<DEVELOPER2_SB_EMAIL_PASS>" for the "Simple" login page
      Then I should be on Portal home page
      And I should see Admin link
      And I click on Admin
      Then I should be on the admin page
      And under System Tools, I see the following "Register Application;Create Custom Roles;Create Landing Zone;Change Password;Manage Developer Accounts"

    Scenario: Original developer change the role of new developer and confirm admin links
      When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page
      Then I should be on Portal home page
      And I should see Admin link
      And I click on Admin
      Then I should be on the admin page
      And under System Tools, I click on "Manage Developer Accounts"
      And I switch to the iframe
      Then I am redirected to the "Sandbox Account Management" page
      When I click the "edit" link for "RCTestDev PartTwo"
      Then I am redirected to "Update a User" page
      And the "Full Name" field is prefilled with "RCTestDev PartTwo"
      And the "Email" field is prefilled with "<DEVELOPER2_SB_EMAIL>"
      And the Role combobox is populated with "Sandbox Administrator"
      And the Role checkbox is checked with "Application Developer,Ingestion User"
      And I can select "Application Developer" from a choice of "Sandbox Administrator, Application Developer, Ingestion User" Role
      And I can also check "Ingestion User" Role
      And I can also check "Application Developer" Role
      When I click button "Update"
      Then I am redirected to the "Sandbox Account Management" page
      And the "Success" message is displayed
      When I click on log out
      Then I will be redirected to realm selector web page
      When I click on the "Sandbox" realm in "Sandbox"
      Then I should be redirected to the impersonation page
      When I click on the simple-idp logout link
      Then I was redirected to the "Simple" IDP Login page

      # Switch account and verify admin links
      When I navigate to the Portal home page
      Then I will be redirected to realm selector web page
      When I click on the "Admin" realm in "Sandbox"
      And I was redirected to the "Simple" IDP Login page
      When I submit the credentials "<DEVELOPER2_SB_EMAIL>" "<DEVELOPER2_SB_EMAIL_PASS>" for the "Simple" login page
      Then I should be on Portal home page
      And I should see Admin link
      And I click on Admin
      Then I should be on the admin page
      And under System Tools, I see the following "Register Application;Create Custom Roles;Change Password"
      And under System Tools, I shouldn't see the following "Create Landing Zone;Manage Developer Accounts"
