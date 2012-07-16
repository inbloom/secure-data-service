@RALLY_DE720
@RALLY_US2121

Feature: Reset and Change Password
   As a super-admin/developer I want to be able to change my password and reset my password

  Background:
    Given I have an open web browser

 Scenario: SLI Developer Reset Password

    Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
    When I hit the Admin URL
    And I was redirected to the "Simple" IDP Login page
    Then I click the "forgotPassword" link
    Then I am redirected to the Forgot Password page
    And I see the input box to enter user id
    And I fill out the input field "user_id" as "idontexist@test.org"
    Then I click on "submit"
    Then I check for message  "Unable to verify your user ID."
    And I see the input box to enter user id
    And I fill out the input field "user_id" as "developer-email@slidev.org"
    Then I click on "submit"
    Then I am redirected to the Reset Password page
    Then I check for message  "Your password reset instructions are sent to your email. Please follow the instructions in the email" 
    When I visit the link sent to "developer-email@slidev.org"
    Then I am redirected to the Reset Password page
    And I fill out the input field "Forgot_password_New_Pass" as "testpswd123"
    And I fill out the field "Forgot_password_Confirmation" as ""
    Then I click on "submitForgotPasswordButton"
    Then I check for message  "2 errors prohibited you from resetting your password"
    And I fill out the input field "Forgot_password_New_Pass" as "test1234"
    And I fill out the field "Forgot_password_Confirmation" as "test1234"
    Then I click on "submitForgotPasswordButton"
    Then I check for message  "Your password has been successfully modified."
    
@LDAP_Reset_developer-email    
  Scenario: SLI Developer Change Password

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


