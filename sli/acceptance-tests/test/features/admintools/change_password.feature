@RALLY_DE720

Feature: Change Password
  As a super-admin I want to be able to create new application keys to allow the onboarding of new applications to SLI

  Background:
    Given I have an open web browser

  Scenario: SLI Developer Logging in

    Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
    When I hit the Change Password URL
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
    Then I am redirected to the Change Password page
    And I see the input boxes to change my password
    And I fill out the input field "Change_password_Old_Pass" as ""
    And I fill out the input field "Change_password_New_Pass" as ""
    And I fill out the field "Change_password_Confirmation" as ""
    Then I click on "submitChangePasswordButton"
    Then I check for message  "4 errors prevented your password change attempt."
    And I fill out the input field "Change_password_Old_Pass" as "testpswd123"
    And I fill out the input field "Change_password_New_Pass" as "testpswd123"
    And I fill out the field "Change_password_Confirmation" as ""
    Then I click on "submitChangePasswordButton"
    Then I check for message  "3 errors prevented your password change attempt."
    And I fill out the input field "Change_password_Old_Pass" as "test1234"
    And I fill out the input field "Change_password_New_Pass" as "dummypswd123"
    And I fill out the field "Change_password_Confirmation" as "dummypswd123"
    Then I click on "submitChangePasswordButton"
    Then I check for message  "Your password has been successfully modified."
    And I fill out the input field "Change_password_Old_Pass" as "dummypswd123"
    And I fill out the input field "Change_password_New_Pass" as "test1234"
    And I fill out the field "Change_password_Confirmation" as "test1234"
    Then I click on "submitChangePasswordButton"
    Then I check for message  "Your password has been successfully modified."


