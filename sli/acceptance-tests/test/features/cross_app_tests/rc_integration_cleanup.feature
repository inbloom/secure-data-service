@rc
Feature:  RC Integration CleanUp Tests

Background:
Given I have an open web browser

Scenario: App developer deletes installed app
When I navigate to the Portal home page
When I selected the realm "Shared Learning Infrastructure"
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "developer-test@slidev.org" "test1234" for the "Simple" login page     
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