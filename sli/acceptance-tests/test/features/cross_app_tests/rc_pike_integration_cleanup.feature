@RALLY_US4835
@rc
Feature:  RC Integration CleanUp Tests

Background:
  Given I have an open web browser
  
Scenario: App developer deletes installed bulk extract app
  When I navigate to the Portal home page
  When I see the realm selector I authenticate to the developer realm
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page
  Then I should be on Portal home page
  Then I should see Admin link
  And I click on Admin
  Then the portal should be on the admin page
  And under System Tools, I click on "Register Application"
  And I switch to the iframe
  Then I am redirected to the Application Registration Tool page
  Then I have clicked on the button 'Delete' for the application named "BulkExtractApp2"
  And I got warning message saying 'You are trying to remove this application from inBloom. By doing so, you will prevent any active user to access it. Do you want to continue?'
  When I click 'Yes'
  And I switch to the iframe
  Then the application named "BulkExtractApp2" is removed from the SLI

Scenario: App developer deletes installed non bulk extract app
  When I navigate to the Portal home page
  When I see the realm selector I authenticate to the developer realm
  And I was redirected to the "Simple" IDP Login page
  When I submit the credentials "<DEVELOPER_EMAIL>" "<DEVELOPER_EMAIL_PASS>" for the "Simple" login page
  Then I should be on Portal home page
  Then I should see Admin link
  And I click on Admin
  Then the portal should be on the admin page
  And under System Tools, I click on "Register Application"
  And I switch to the iframe
  Then I am redirected to the Application Registration Tool page
  Then I have clicked on the button 'Delete' for the application named "NotABulkExtractApp"
  And I got warning message saying 'You are trying to remove this application from inBloom. By doing so, you will prevent any active user to access it. Do you want to continue?'
  When I click 'Yes'
  And I switch to the iframe
  Then the application named "NotABulkExtractApp" is removed from the SLI