@wip
Feature: User clicks the email verification link after creating an account

Background: None

Scenario: Checking email inbox
  Given I check my email inbox as user "devldapuser" "Y;Gtf@w{"
  Then I should find an email sent from "Email Admin" with subject "Email verification"
  And the email should contain a link to verify my account

Scenario: Valid link
  Given I have an open web browser
  When I navigate to "<VALID VERIFICATION LINK>"
  Then I should see the text "Registration successful"

Scenario: Invalid link
  Given I have an open web browser
  When I navigate to "<INVALID VERIFICATION LINK>"
  Then I should see the text "Invalid something something"

Scenario: Account already verified
  Given I have an open web browser
  When I navigate to "<VALID VERIFICATION LINK>"
  Then I should see the text "Account is already verified"