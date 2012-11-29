@RALLY_US4835
@rc
@sandbox
Feature:  RC Integration CleanUp Tests

Background:
  Given I have an open web browser
  And I am running in Sandbox mode

Scenario: App developer deletes installed app
  When I navigate to the Portal home page
  Then I will be redirected to realm selector web page
  When I click on the "Admin" realm in "Sandbox"
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page
  Then I should be on Portal home page
  Then I should see Admin link
  And I click on Admin
  Then I should be on the admin page
  And under System Tools, I click on "Application Registration"
  And I switch to the iframe
  Then I am redirected to the Application Registration Tool page
  Then I have clicked on the button 'Deny' for the application named "NotTheAppYoureLookingFor"
  And I got warning message saying 'You are trying to remove this application from SLI. By doing so, you will prevent any active user to access it. Do you want to continue?'
  When I click 'Yes'
  And I switch to the iframe
  Then the application named "NotTheAppYoureLookingFor" is removed from the SLI
  Then I have clicked on the button 'Deny' for the application named "Schlemiel"
  And I got warning message saying 'You are trying to remove this application from SLI. By doing so, you will prevent any active user to access it. Do you want to continue?'
  When I click 'Yes'
  And I switch to the iframe
  Then the application named "Schlemiel" is removed from the SLI

Scenario: slcoperator deletes SEA,LEA
  When I navigate to the user account management page
  And I see the realm selector I authenticate to "Shared Learning Collaborative"
  Then I am redirected to "Simple" login page
  When I submit the credentials "slcoperator" "slcoperator1234" for the "Simple" login page
  Then I delete the user "RCTest Developer"
  Then I delete the user "RCTestDev PartTwo"
