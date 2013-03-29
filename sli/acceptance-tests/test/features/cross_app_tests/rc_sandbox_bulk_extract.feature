@rc
@sandbox
Feature:  RC Integration Tests

Scenario: Operator triggers a bulk extract
   When the operator triggers a bulk extract for tenant "<SANDBOX_TENANT>"

Scenario: Get a session token for the bulk-extract command and add the BULK_EXTRACT right to IT Admins
	#Get App Auth info
	Given I have an open web browser
	When I navigate to the Portal home page
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the "Simple" login page    
	Then I should be on Portal home page
	Then I should see Admin link
	And I click on Admin
	Then I should be on the admin page
	And under System Tools, I click on "Register Application"
	And I switch to the iframe
	Then I am redirected to the Application Registration Tool page
	And I clicked on the button Edit for the application "NotTheAppYoureLookingFor"
    Then I get the client ID and shared secret for the app

    #Add Bulk Extract role to IT Admin
    And I click on Admin
	And under System Tools, I click on "Create Custom Roles"
	And I edit the group "IT Administrator"
	When I add the right "BULK_EXTRACT" to the group "IT Administrator"
	And I hit the save button
	Then I am no longer in edit mode
	And the group "IT Administrator" contains the "right" rights "Bulk IT Administrator"

	#Get a session token for an IT admin
    Then I authorize a session with the app
    And I was redirected to the "Simple" IDP Login page
    When I submit the developer credentials "<DEVELOPER_SB_EMAIL>" "<DEVELOPER_SB_EMAIL_PASS>" for the impersonation login page
    Then I should be redirected to the impersonation page
    And I should see that I "<DEVELOPER_SB_EMAIL>" am logged in
    And I want to select "jstevenson" from the "SmallDatasetUsers" in automatic mode
    And I capture the authorization and start a session

    #Get bulk extract tar file
    Then I request and download a bulk extract file
    And I validate the bulk extract file is correct