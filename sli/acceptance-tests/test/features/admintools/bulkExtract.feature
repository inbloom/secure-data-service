@US5481
Feature: Admininstrating role-based access to bulking
    
    Scenario: Make sure it works
        Given I have an open web browser
        When I navigate to the Custom Role Mapping Page
        And I was redirected to the "Simple" IDP Login page
        When I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
        Then I have navigated to my Custom Role Mapping Page
        Then I should not see right "BULK_EXTRACT" on any existing role        
        When I click on the Add Group button
        And I type the name "Balrogs" in the Group name textbox
        When I add the right "BULK_EXTRACT" to the group "Balrogs"   
        And I add the role "Balrog" to the group "Balrogs"
        And I hit the save button
        And the group "Balrogs" contains the "right" rights "BULK_EXTRACT"
        
        # Now test generation and use of the endpoint
        Given I used the long lived session token generator script to create a token for user "manthony" with role "Balrog" in tenant "Midgar"  for realm "IL-Sunset" that will expire in "300" seconds with client_id "AT1k3PdHzX" 
        Then I should see that my role is "Balrog"
        When I navigate to GET "/v1/sections"
        And I should receive a return code of 403
        When I navigate to GET "/bulk/extract?sample=false"
        When the return code is 404 I ensure there is no bulkExtractFiles entry for Midgar
        When the return code is 503 I ensure there is a bulkExtractFiles entry for Midgar
        When the return code is 200 I get expected tar downloaded
        Then I check the http response headers

  @wip
  Scenario: Creating a new application for bulk extract
    Given I have an open web browser
    Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
    When I hit the Application Registration Tool URL
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
    Then I am redirected to the Application Registration Tool page
    And I have clicked to the button New
    And I am redirected to a new application page
    When I entered the name "Bulk Extract App" into the field titled "Name"
    And I click on "isBulkExtract"
    And I have entered data into the other required fields except for the shared secret and the app id which are read-only
    And I click on the button Submit
    Then I am redirected to the Application Registration Tool page
    And the application "Bulk Extract App" is listed in the table on the top
    And the client ID and shared secret fields are Pending
    And the Registration Status field is Pending
    And a notification email is sent to "slcoperator-email@slidev.org"
    And I have enabled "isBulkExtract"
  
  @wip
  Scenario: Operator can see that apps are choosing bulk extract
    Given I have an open web browser
    Given I am a valid SLC Operator "slcoperator-email@slidev.org" from the "SLI" hosted directory
    When I hit the Application Registration Tool URL
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
    Then I am redirected to the Application Approval Tool page
    And the pending apps are on top
    And application "Bulk Extract App" is pending approval
    When I click on the row of application named "Bulk Extract App" in the table
    Then I have enabled "isBulkExtract"
    When I click on 'Approve' next to application "Bulk Extract App"
    Then application "Bulk Extract App" is registered
    And the 'Approve' button is disabled for application "Bulk Extract App"
    And a notification email is sent to "developer-email@slidev.org"
  
  @wip  
  Scenario: App Developer is able to enable Bulk Extract for districts
    Given I have an open web browser
    Given I am a valid SLI Developer "developer-email@slidev.org" from the "SLI" hosted directory
    When I hit the Application Registration Tool URL
    	And I was redirected to the "Simple" IDP Login page
    	And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
    Then I am redirected to the Application Registration Tool page
    	And I see the list of (only) my applications
    	And I clicked on the button Edit for the application "Bulk Extract App"
      Then I can see the on-boarded states
    When I select a state
      Then I see all of the Districts
      Then I check the Districts
    When I click on Save
    Then the "Bulk Extract App" is enabled for Districts
  
  @wip
  Scenario: District administrator is alerted to applications that want bulk extract
    Given I have an open web browser
    Given I am an authenticated District Super Administrator for "Sunset School District"
  	And I am logged into the Application Authorization Tool
  	And I see an application "Bulk Extract App" in the table
  	And I see that it has an alert about bulk extract
  	And in Status it says "Not Approved"
  	And I click on the "Approve" button next to it
  	And I am asked 'Do you really want this application to access the district's data'
  	When I click on Ok
  	Then the application is authorized to use data of "Sunset School District"
  	And the app "Bulk Extract App" Status becomes "Approved"
  	And it is colored "green"
  	And the Approve button next to it is disabled
  	And the Deny button next to it is enabled
