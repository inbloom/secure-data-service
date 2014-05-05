Feature: Reset and Change Password
   As a registered developer
   In order to manage my security credentials
   I want to be able to change my password and reset my password

  Background:
    Given I have an open web browser

@LDAP_Reset_developer-email
 Scenario: SLI Developer Reset Password

    When I hit the Admin URL
    And I select "inBloom" from the dropdown and click go
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
    Then I am redirected to the Forgot Password notify page
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

    When I hit the Change Password URL
    And I select "inBloom" from the dropdown and click go
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
    Then I see change password is disabled for production developers

 Scenario: I can navigate to reset password page after failed login attempts
    When I hit the Admin URL
    And I select "inBloom" from the dropdown and click go
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "Iforgotmyusername" "Iforgotmypassword" for the "Simple" login page
    Then I check for message  "Invalid User Name or password"
    Then I click the "forgotPassword" link
    Then I am redirected to the Forgot Password page

