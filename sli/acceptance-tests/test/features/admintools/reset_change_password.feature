@RALLY_DE720
@RALLY_US2121

Feature: Reset and Change Password
   As a super-admin/developer I want to be able to change my password and reset my password

  Background:
    Given I have an open web browser

@LDAP_Reset_developer-email
 Scenario: SLI Developer Reset Password

    Given I am a SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
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
    Then I check for message  "Password reset instructions have been emailed to you. Please follow the instructions in the email." 
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

    Given I am a SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
    When I hit the Change Password URL
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
    Then I see change password is disabled for production developers

    @wip
@LDAP_Reset_sunsetadmin
  Scenario: Force Change Password
    
    Given I am a SLC Admin "sunsetadmin" from the "SLI" hosted directory logging in for the first time
    When I hit the Admin URL
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
    When I am forced to change password
    And I fill out the input field "Forgot_password_New_Pass" as "sunsetadmin1234"
    And I fill out the field "Forgot_password_Confirmation" as "sunsetadmin1234"
    Then I click on "submitForgotPasswordButton"
    Then I check for message  "Your password has been successfully modified."

 Scenario: I can navigate to reset password page after failed login attempts
    Given I am a SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
    When I hit the Admin URL
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "Iforgotmyusername" "Iforgotmypassword" for the "Simple" login page
    Then I check for message  "Invalid User Name or password"
    Then I click the "forgotPassword" link
    Then I am redirected to the Forgot Password page

