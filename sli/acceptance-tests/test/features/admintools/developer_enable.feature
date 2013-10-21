Feature: Developer Enablement
	As a developer I want to be able to enable my application for specific states and districts.
	As an admin I want to be able to approve the applications that developers enabled.
	
Background:
	Given I have an open web browser

	Scenario: Create new application (set up data)
        Given I am a valid SLI Developer "admintest-developer@slidev.org" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
        Then I am redirected to the Application Registration Tool page
        And I have clicked to the button New
        And I am redirected to a new application page
        When I entered the name "Red Dwarf" into the field titled "Name"
        And I have entered data into the other required fields except for the shared secret and the app id which are read-only
        And I click on the button Submit
        Then I am redirected to the Application Registration Tool page
        And the application "Red Dwarf" is listed in the table on the top
        And the client ID and shared secret fields are Pending
        And the Registration Status field is Pending
        #And a notification email is sent to "slcoperator-email@slidev.org"

    Scenario: SLC Operator accepts application registration request (set up data)
    Given I am a valid SLC Operator "slcoperator-email@slidev.org" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
        Then I am redirected to the Application Approval Tool page
        And I see all the applications registered on SLI
        And I see all the applications pending registration
        And the pending apps are on top
        When I click on 'Approve' next to application "Red Dwarf"
        Then application "Red Dwarf" is registered
        And the 'Approve' button is disabled for application "Red Dwarf"
        #And a notification email is sent to "admintest-developer@slidev.org"


    Scenario: Application editing can handle large number of edOrgs for a bulk extract application
        Given the large list of edorgs is loaded
        Given I am a valid SLI Developer "slcdeveloper" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
        Then I am redirected to the Application Registration Tool page
        And I see the list of (only) my applications
        And I clicked on the button Edit for the application "Red Dwarf"
        When I check the Bulk Extract checkbox
        And I enable the educationalOrganization "Mega State" in tenant "Midgar"
        And I enable the educationalOrganization "New York State Education System" in tenant "Hyrule"
        And I click on Save
        # 8 for Ny and 2331 for Mega State
        Then "Red Dwarf" is enabled for "2339" education organizations
        And I clicked on the button Edit for the application "Red Dwarf"
        And I disable the educationalOrganization "Mega State" in tenant "Midgar"
        And I click on Save
        Then "Red Dwarf" is enabled for "8" education organizations
        #test non-bulk extract app
        And I clicked on the button Edit for the application "Red Dwarf"
        When I uncheck the Bulk Extract checkbox
        And I enable the educationalOrganization "Mega State" in tenant "Midgar"
        And I click on Save
        # confirm bulk extract specific app enable behavior is no longer there
        # 8 for Ny and 2331 for Mega State
        Then "Red Dwarf" is enabled for "2339" education organizations
        And I clicked on the button Edit for the application "Red Dwarf"
        And I disable the educationalOrganization "Mega State" in tenant "Midgar"
        And I click on Save
        Then "Red Dwarf" is enabled for "8" education organizations
        #Toggle bulk extract test
        And I clicked on the button Edit for the application "Red Dwarf"
        When I check the Bulk Extract checkbox
        And I click on Save
        Then "Red Dwarf" is enabled for "8" education organizations
        #Remove all SEAs test
        And I clicked on the button Edit for the application "Red Dwarf"
        And I disable the educationalOrganization "New York State Education System" in tenant "Hyrule"
        And I click on Save
        Then "Red Dwarf" is enabled for "0" education organizations
        Given I have replaced the edorg data

Scenario: App Developer or Vendor enabling application for a District
Given I am a valid SLI Developer "slcdeveloper" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
	And I was redirected to the "Simple" IDP Login page
	And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
	And I see the list of (only) my applications
	And I clicked on the button Edit for the application "Testing App"
#Already enabled
#When I select the state "Illinois State Board of Education"
And I click on Save
Then the "Testing App" is enabled for Districts

Scenario: District Admin authorizing application for their district
Given I log in as a valid SLI Operator "sunsetadmin" from the "SLI" hosted directory
When I hit the Admin Application Authorization Tool
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
	Then I am redirected to the Admin Application Authorization Tool
	Then I see the newly enabled application

Scenario: App Developer or Vendor disabling application for a District, part 2
Given I am a valid SLI Developer "slcdeveloper" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
	And I was redirected to the "Simple" IDP Login page
	And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
	And I see the list of (only) my applications
	And I clicked on the button Edit for the application "Testing App"
When I select the state "Illinois State Board of Education"
And I click on Save
  Then the "Testing App" is enabled for Districts

Scenario: District Admin no longers see apps disabled for their district 
Given I log in as a valid SLI Operator "sunsetadmin" from the "SLI" hosted directory
When I hit the Admin Application Authorization Tool
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
	Then I am redirected to the Admin Application Authorization Tool
	Then I don't see the newly disabled application

@sandbox @wip
Scenario: App Developer or Vendor disabling application for a District, part 3
Given I am a valid SLI Developer "developer" from the "SLI" hosted directory
When I hit the Application Registration Tool URL
	And I was redirected to the "Simple" IDP Login page
	And I submit the credentials "developer" "developer1234" for the "Simple" login page
Then I am redirected to the Application Registration Tool page
	And I see the list of (only) my applications
	And I clicked on the button Edit for the application "Testing App"
  Then I can see the on-boarded states
When I select the state "Illinois State Board of Education"
When I click on Save
Then the "Testing App" is enabled for Districts
Then I log out
Then I log in as a valid SLI Operator "sunsetadmin" from the "SLI" hosted directory
When I hit the Admin Application Authorization Tool
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
	Then I am redirected to the Admin Application Authorization Tool
	Then I see the newly enabled application is approved
