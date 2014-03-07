@no_ingestion_hooks

Feature: Application Registration
  As a super-admin
  In order to allow the onboarding of new applications to SLI
  I want to be able to create new application keys

#Background:
#  Given I have an open web browser
#    And my LDAP server has been setup and running

Scenario: A district-level administrator attempts to manage applications
  Given I am a valid district-level administrator
    And I have an open browser
   When I attempt to manage applications
   Then I should not be allowed to access the page

Scenario: An operator denies a pending application
  Given I have an open browser
    And a developer has registered a new application
    And I am a valid inBloom operator
    And I am managing my applications
    And I see the pending application
   When I deny the application
   Then I no longer see the application

Scenario: An operator denies an approved application
  Given I have an open browser
    And a developer has registered a new application
    And I am a valid inBloom operator
    And I am managing my applications
    And I see the pending application
    And I approve the application
    And I see the approved application
  When I deny the application
  Then I no longer see the application

Scenario: A developer cancels creation of a new application
  Given I am a valid inBloom developer
    And I have an open browser
    And I am managing my applications
    And I want to create a new application
   When I cancel out of the new application form
   Then I am managing my applications again

Scenario: A developer does not provide required information for an application
  Given I am a valid inBloom developer
    And I have an open browser
    And I am managing my applications
   When I submit an application for registration without inputting any information
   Then I should see validation errors for:
    | Name            |
    | Description     |
    | Version         |
    | Application url |
    | Redirect uri    |

Scenario: A developer does not need to provide application and redirect URLs for an installed application
  Given I am a valid inBloom developer
  And I have an open browser
  And I am managing my applications
  When I submit an application for registration marked as "Installed"
  Then I should not see validation errors for:
    | Application url |
    | Redirect uri    |

Scenario: A developer must provide correctly formatted URLs where needed
  Given I am a valid inBloom developer
    And I have an open browser
    And I am managing my applications
   When I submit an application for registration with improperly formatted URLs
   Then I should see validation errors for:
    | Administration url |
    | Image url          |
    | Application url    |
    | Redirect uri       |


#TODO: Clean up these stories
@sandbox
Scenario: App Developer logs-in to App Registration Tool in Sandbox (Vendor in Prod should see own apps respectively)
  Given I have an open web browser
    And my LDAP server has been setup and running
	When I hit the Application Registration Tool URL
	And I was redirected to the "Simple" IDP Login page
	And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
	Then I am redirected to the Application Registration Tool page
    Then I see the list of my registered applications only

@sandbox 
Scenario: Different App developer in same tenant should also see my apps
  Given I have an open web browser
  And my LDAP server has been setup and running
    Given there is a "Application Developer" with tenancy "developer-email@slidev.org" and in "STANDARD-SEA"
    Then I can navigate to app registration page with that user
	Then I am redirected to the Application Registration Tool page
	Then I see the list of registered applications as well


@sandbox 
Scenario: App Developer registers an application in App Registration Tool in Sandbox
  Given I have an open web browser
  And my LDAP server has been setup and running
	When I hit the Application Registration Tool URL
		And I was redirected to the "Simple" IDP Login page
		And I submit the credentials "developer-email@slidev.org" "test1234" for the "Simple" login page
	Then I am redirected to the Application Registration Tool page
		And I have clicked to the button New
		And I am redirected to a new application page
	When I entered the name "NewApp" into the field titled "Name"
		And I have entered data into the other required fields except for the shared secret and the app id which are read-only
	When I click on the button Submit
	Then the application is registered
		And I can see the client ID and shared secret
		And the Registration Status field is Registered

@sandbox
Scenario: The other app developer in my tenancy can also modify and delete my apps
  Given I have an open web browser
  And my LDAP server has been setup and running
    Given there is a "Application Developer" with tenancy "developer-email@slidev.org" and in "STANDARD-SEA"
    Then I can navigate to app registration page with that user
	    And I am redirected to the Application Registration Tool page
    Then I clicked on the button Edit for the application "NewApp"
        Then every field except the shared secret and the app ID became editable
        And I can update the version to "100" 
        Then I clicked Save
        Then I am redirected to the Application Registration Tool page
    And I can delete "NewApp"
