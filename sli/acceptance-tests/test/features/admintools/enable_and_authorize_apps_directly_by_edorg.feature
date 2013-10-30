
Feature: Application Enablement and Authorization by Education Organization
	As a developer I want to be able to enable my application for specific states and education organizations.
	As an admin I want to be able to approve the applications that developers enabled for specific education organizations.

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
        When I entered the name "Royal Oak" into the field titled "Name"
        And I have entered data into the other required fields except for the shared secret and the app id which are read-only
        And I click on the button Submit
        Then I am redirected to the Application Registration Tool page
        And the application "Royal Oak" is listed in the table on the top
        And the client ID and shared secret fields are Pending
        And the Registration Status field is Pending


    Scenario: SLC Operator accepts application registration request (set up data)
        Given I am a valid SLC Operator "slcoperator-email@slidev.org" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
        Then I am redirected to the Application Approval Tool page
        And I see all the applications registered on SLI
        And I see all the applications pending registration
        And the pending apps are on top
        When I click on 'Approve' next to application "Royal Oak"
        Then application "Royal Oak" is registered
        And the 'Approve' button is disabled for application "Royal Oak"

    Scenario: Developer registers application (set up data)
        Given I am a valid SLI Developer "slcdeveloper" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
        Then I am redirected to the Application Registration Tool page
        And I see the list of (only) my applications
        And a "In Progress" button is displayed for application "Royal Oak"
        And I clicked on the button Edit for the application "Royal Oak"
        And I expand all nodes
        And I enable the educationalOrganization "Illinois State Board of Education" in tenant "Midgar"
        And I enable the educationalOrganization "Dusk High School" in tenant "Hyrule"
        And I click on Save
        Then "Royal Oak" is enabled for "201" education organizations

        #DE2983 - test cancel button
        And I clicked on the button Edit for the application "Royal Oak"
        And I click Cancel on the application enable page
        And a "Edit" button is displayed for application "Royal Oak"
        Then "Royal Oak" is enabled for "201" education organizations

    @RALLY_DE2981
    Scenario: NY SEA Admin Approves application
        When I hit the Admin Application Authorization Tool
         And I submit the credentials "nyadmin" "nyadmin1234" for the "Simple" login page
         And I see an application "Royal Oak" in the table
         And in Status it says "Not Approved"
         And the sli securityEvent collection is empty

        # clean up app state for DE2981
    Scenario: Developer disables application
        Given I am a valid SLI Developer "slcdeveloper" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
        Then I am redirected to the Application Registration Tool page
        And I clicked on the button Edit for the application "Royal Oak"
        And I expand all nodes
        And I disable the educationalOrganization "Dusk High School" in tenant "Hyrule"
        And I click on Save
        And a "Edit" button is displayed for application "Royal Oak"
        Then "Royal Oak" is enabled for "200" education organizations

    Scenario: SEA Admin Approves application
        When I hit the Admin Application Authorization Tool
         And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
         And I see an application "Royal Oak" in the table
         And in Status it says "Not Approved"
         And the sli securityEvent collection is empty

    ##########################################################################################
    #those edOrgs not enabled by the developer are grayed out and non-selectable
    ##########################################################################################
      And I click on the "Edit Authorizations" button next to it
      And I expand all nodes
      And those edOrgs enabled by the developer should be selectable for application "Royal Oak" in tenant "Midgar"
      And the following edOrgs not enabled by the developer are non-selectable for application "Royal Oak" in tenant "Midgar"
        |edorgs|
        |Algebra Alternative |
    ##########################################################################################
    #All edOrgs from SEA downwards
    ##########################################################################################
    	 And I authorize the educationalOrganization "Illinois State Board of Education"
    	 And I click Update
    	Then there are "200" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
         And I check to find if record is in sli db collection:
          | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
          | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
          | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
         And there are "200" educationalOrganizations in the targetEdOrgList
         And I see an application "Royal Oak" in the table
         And in Status it says "200 EdOrg(s)"
         #cancel button check
         When I click on the "Edit Authorizations" button next to it
         And I click Cancel on the application authorization page
         Then I see an application "Royal Oak" in the table
         And in Status it says "200 EdOrg(s)"
         And there are "200" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
        Given the sli securityEvent collection is empty
        When I click on the "Edit Authorizations" button next to it
         And I de-authorize the educationalOrganization "Illinois State Board of Education"
         And I click Update
        Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
         And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
         And there are "200" educationalOrganizations in the targetEdOrgList
         And I see an application "Royal Oak" in the table
         And in Status it says "Not Approved"

    ##########################################################################################
    #SEA only
    ##########################################################################################
         Then I click on the "Edit Authorizations" button next to it
         And the sli securityEvent collection is empty
         And I deselect hierarchical mode
         And I expand all nodes
         And I authorize the educationalOrganization "Illinois State Board of Education"
         And I click Update
        Then there are "1" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
        Then The following edOrgs are authorized for the application "Royal Oak" in tenant "Midgar"
			|edorgs|
 	        |Illinois State Board of Education |
 	     #And only below is present in the application authorization edOrgs array for the application "Royal Oak" in tenant "Midgar"
            #| edOrg                             | user    | realm edOrg                          |
            #| Illinois State Board of Education | iladmin | fakeab32-b493-999b-a6f3-sliedorg1234 |
         And I check to find if record is in sli db collection:
          | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
          | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
          | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          | securityEvent       | 1                   | body.targetEdOrgList  | IL                                        |
         And there are "1" educationalOrganizations in the targetEdOrgList
         And I see an application "Royal Oak" in the table
         And in Status it says "1 EdOrg(s)"
        Given the sli securityEvent collection is empty
        When I click on the "Edit Authorizations" button next to it
         And I deselect hierarchical mode
         And I de-authorize the educationalOrganization "Illinois State Board of Education"
         And I click Update
        Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
         And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
         And there are "1" educationalOrganizations in the targetEdOrgList
         And I see an application "Royal Oak" in the table
         And in Status it says "Not Approved"

    ##########################################################################################
    #Authorise Multiple Parents
    ##########################################################################################
          Then I click on the "Edit Authorizations" button next to it
          And the sli securityEvent collection is empty
          And I deselect hierarchical mode
          And I expand all nodes
          And I see "1" checkbox for "Many-Parents"
          And I see "20" occurrences of "see Many-Parents"
          And I authorize the educationalOrganization "Many-Parents"
          And I click Update
         Then there are "1" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
         Then The following edOrgs are authorized for the application "Royal Oak" in tenant "Midgar"
            |edorgs|
            |Many-Parents |
          And I check to find if record is in sli db collection:
           | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
           | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
           | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "1" educationalOrganizations in the targetEdOrgList
          And I see an application "Royal Oak" in the table
          And in Status it says "1 EdOrg(s)"
         Given the sli securityEvent collection is empty
         When I click on the "Edit Authorizations" button next to it
          And I deselect hierarchical mode
          And I expand all nodes
          And I de-authorize the educationalOrganization "Many-Parents"
          And I click Update
         Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
             | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
             | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
             | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "1" educationalOrganizations in the targetEdOrgList
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"

    ##########################################################################################
    #non-SEA edorg only
    ##########################################################################################
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"
          Then I click on the "Edit Authorizations" button next to it
          And the sli securityEvent collection is empty
          And I deselect hierarchical mode
          And I authorize the educationalOrganization "Daybreak School District 4529"
          And I click Update
          Then The following edOrgs are authorized for the application "Royal Oak" in tenant "Midgar"
            |edorgs|
            |Daybreak School District 4529 |
          Then there are "1" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "1" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
          And I see an application "Royal Oak" in the table
          And in Status it says "1 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I deselect hierarchical mode
          And I de-authorize the educationalOrganization "Daybreak School District 4529"
          And I click Update
          Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "1" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"

    ##########################################################################################
    #A single branch beginning at a direct child of the SEA, but not including the SEA
    ##########################################################################################
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"
          Given the sli securityEvent collection is empty
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I authorize the educationalOrganization "Sunset School District 4526"
          And I click Update
          Then there are "3" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "3" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
          And I see an application "Royal Oak" in the table
          And in Status it says "3 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I de-authorize the educationalOrganization "Sunset School District 4526"
          And I click Update
          Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "3" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"

    ##########################################################################################
    #A single branch beginning at a grandchild of the SEA, but not including the SEA
    ##########################################################################################
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"
          Given the sli securityEvent collection is empty
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I authorize the educationalOrganization "LEA Tier 2"
          And I click Update
          Then there are "32" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "32" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
          And I see an application "Royal Oak" in the table
          And in Status it says "32 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I de-authorize the educationalOrganization "LEA Tier 2"
          And I click Update
          Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "32" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"

    ##########################################################################################
    #Authorize a single branch ending with nodes on the 6th tier
    ##########################################################################################
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"
          Given the sli securityEvent collection is empty
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I authorize the educationalOrganization "LEA-TIER-A-1"
          And I click Update
          Then there are "40" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "40" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
          And I see an application "Royal Oak" in the table
          And in Status it says "40 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I de-authorize the educationalOrganization "LEA-TIER-A-1"
          And I click Update
          Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "40" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"

    ##########################################################################################
    #Authorize only the single leaf node 5 levels below SEA edOrg
    ##########################################################################################
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"
          Given the sli securityEvent collection is empty
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I authorize the educationalOrganization "Tier-5A-School-1"
          And I click Update
          Then there are "1" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "1" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
          And I see an application "Royal Oak" in the table
          And in Status it says "1 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I de-authorize the educationalOrganization "Tier-5A-School-1"
          And I click Update
          Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "1" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"

    ##########################################################################################
    #Combine above in one action
    ##########################################################################################
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"
          Given the sli securityEvent collection is empty
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I deselect hierarchical mode
          And I authorize the educationalOrganization "Illinois State Board of Education"
          And I authorize the educationalOrganization "Daybreak School District 4529"
          And I select hierarchical mode
          And I authorize the educationalOrganization "Sunset School District 4526"
          And I authorize the educationalOrganization "LEA Tier 2"
          And I authorize the educationalOrganization "LEA-TIER-A-1"
          And I click Update
          Then there are "77" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "77" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
          And I see an application "Royal Oak" in the table
          And in Status it says "77 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I deselect hierarchical mode
          And I authorize the educationalOrganization "Illinois State Board of Education"
          And I authorize the educationalOrganization "Daybreak School District 4529"
          And I deselect hierarchical mode
          And I authorize the educationalOrganization "Sunset School District 4526"
          And I authorize the educationalOrganization "LEA Tier 2"
          And I authorize the educationalOrganization "LEA-TIER-A-1"
          And I click Update
          Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "77" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"

    ##########################################################################################
    #Unselect an edorg in the middle of the tree with children, Un-check hierarchical view and select the edorg again
    ##########################################################################################
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"
          Given the sli securityEvent collection is empty
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I authorize the educationalOrganization "LEA Tier 4"
          And I click Update
          Then there are "16" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "16" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
          And I see an application "Royal Oak" in the table
          And in Status it says "16 EdOrg(s)"
          Given the sli securityEvent collection is empty
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I de-authorize the educationalOrganization "LEA Tier 5"
          And I click Update
          Then there are "8" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "8" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"
          And I see an application "Royal Oak" in the table
          And in Status it says "8 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I deselect hierarchical mode
          And I authorize the educationalOrganization "LEA Tier 5"
          And I click Update
          Then there are "9" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "1" educationalOrganizations in the targetEdOrgList of securityEvent "Application granted access to EdOrg data!"
          And I see an application "Royal Oak" in the table
          And in Status it says "9 EdOrg(s)"
          Given the sli securityEvent collection is empty
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I de-authorize the educationalOrganization "LEA Tier 4"
          And I click Update
          Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
            | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
          And there are "9" educationalOrganizations in the targetEdOrgList of securityEvent "EdOrg data access has been revoked!"
          And I see an application "Royal Oak" in the table
          And in Status it says "Not Approved"


  Scenario: LEA Admin Approves application

    When I hit the Admin Application Authorization Tool
    And I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"
    And the sli securityEvent collection is empty
  ############################################################
  ######All edOrgs from LEA downwards 
  ############################################################
    And I click on the "Edit Authorizations" button next to it
    And I authorize the educationalOrganization "Daybreak School District 4529"   
    And I click Update
    Then there are "45" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "45" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "45 EdOrg(s)"
    Given the sli securityEvent collection is empty
    When I click on the "Edit Authorizations" button next to it
    And I de-authorize the educationalOrganization "Daybreak School District 4529"
    And I click Update
    Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "45" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"
    

  ############################################################
  ######LEA only
  ############################################################

    Then I click on the "Edit Authorizations" button next to it
    And the sli securityEvent collection is empty
    And I deselect hierarchical mode
    And I expand all nodes
    And I authorize the educationalOrganization "Daybreak School District 4529"
    And I click Update
    Then there are "1" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    Then The following edOrgs are authorized for the application "Royal Oak" in tenant "Midgar"
      |edorgs|
      |Daybreak School District 4529 |
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "1" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "1 EdOrg(s)"
    Given the sli securityEvent collection is empty
    When I click on the "Edit Authorizations" button next to it
    And I deselect hierarchical mode
    And I de-authorize the educationalOrganization "Daybreak School District 4529"
    And I click Update
    Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "1" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"


  ##########################################################################################
  ######Authorize a single LEA edOrg that is not the edOrg that user log into
  ##########################################################################################
    And the sli securityEvent collection is empty
    And I click on the "Edit Authorizations" button next to it
    And I deselect hierarchical mode
    And I expand all nodes
    And I authorize the educationalOrganization "LEA Tier 4"
    And I click Update
    Then there are "1" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "1" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "1 EdOrg(s)"
    Given the sli securityEvent collection is empty
    When I click on the "Edit Authorizations" button next to it
    And I expand all nodes
    And I de-authorize the educationalOrganization "LEA Tier 4"
    And I click Update
    Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "1" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"



  ##########################################################################################
  ######A single branch beginning at a direct child of the LEA, but not including the LEA
  ##########################################################################################
    And the sli securityEvent collection is empty
    And I click on the "Edit Authorizations" button next to it
    And I expand all nodes
    And I authorize the educationalOrganization "LEA Tier 2"
    And I click Update
    Then there are "32" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "32" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "32 EdOrg(s)"
    Given the sli securityEvent collection is empty
    When I click on the "Edit Authorizations" button next to it
    And I expand all nodes

    And I de-authorize the educationalOrganization "LEA Tier 2"
    And I click Update
    Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "32" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"

  ##########################################################################################
  ######  A single branch beginning at a grandchild of the LEA, but not including the LEA
  ##########################################################################################
    Given the sli securityEvent collection is empty
    And I click on the "Edit Authorizations" button next to it
    And I expand all nodes
    And I authorize the educationalOrganization "LEA Tier 3"
    And I click Update
    Then there are "24" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
    And there are "24" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "24 EdOrg(s)"
    Given the sli securityEvent collection is empty
    When I click on the "Edit Authorizations" button next to it
    And I expand all nodes
    And I de-authorize the educationalOrganization "LEA Tier 3"
    And I click Update
    Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "24" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"

  #############################################################################################
  ######  Authorize a single branch ending with nodes on the 5th tier
  #############################################################################################

    Given the sli securityEvent collection is empty
    And I click on the "Edit Authorizations" button next to it
    And I expand all nodes
    And I authorize the educationalOrganization "LEA Tier 5"
    And I click Update
    Then there are "8" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "8" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "8 EdOrg(s)"
    Given the sli securityEvent collection is empty
    When I click on the "Edit Authorizations" button next to it
    And I expand all nodes
    And I de-authorize the educationalOrganization "LEA Tier 5"
    And I click Update
    Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "8" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"


  #############################################################################################
  ######  Authorize a single leaf nodes on the 5th tier
  #############################################################################################

    Given the sli securityEvent collection is empty
    And I click on the "Edit Authorizations" button next to it
    And I expand all nodes
    And I authorize the educationalOrganization "Tier-5-School-1"
    And I click Update
    Then there are "1" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "1" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "1 EdOrg(s)"
    Given the sli securityEvent collection is empty
    When I click on the "Edit Authorizations" button next to it
    And I expand all nodes
    And I de-authorize the educationalOrganization "Tier-5-School-1"
    And I click Update
    Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "1" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"


  ############################################################
  ######combination of only LEA and a branch of LEA
  ############################################################
    Given the sli securityEvent collection is empty
    Then I click on the "Edit Authorizations" button next to it
    And the sli securityEvent collection is empty
    And I expand all nodes
    And I authorize the educationalOrganization "LEA Tier 2"
    And I deselect hierarchical mode
    And I authorize the educationalOrganization "Daybreak School District 4529"
    And I click Update
    Then there are "33" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "33" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "33 EdOrg(s)"
    Given the sli securityEvent collection is empty
    When I click on the "Edit Authorizations" button next to it
    And I de-authorize the educationalOrganization "LEA Tier 2"
    And I deselect hierarchical mode
    And I de-authorize the educationalOrganization "Daybreak School District 4529"
    And I click Update
    Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
    And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
      | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
    And there are "33" educationalOrganizations in the targetEdOrgList
    And I see an application "Royal Oak" in the table
    And in Status it says "Not Approved"



    Scenario: LEA Admin Approves Many Parents EdOrg

        When I hit the Admin Application Authorization Tool
        And I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
        And I see an application "Royal Oak" in the table
        And in Status it says "Not Approved"
        And the sli securityEvent collection is empty
        And I click on the "Edit Authorizations" button next to it
        And I expand all nodes
        And I see "1" checkbox for "Many-Parents"
        Then I see "4" occurrences of "see Many-Parents"
        And I authorize the educationalOrganization "Many-Parents"
        And I click Update
        Then there are "1" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
        Then The following edOrgs are authorized for the application "Royal Oak" in tenant "Midgar"
          |edorgs|
          |Many-Parents |
        And I check to find if record is in sli db collection:
          | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
          | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
          | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
        And there are "1" educationalOrganizations in the targetEdOrgList
        And I see an application "Royal Oak" in the table
        And in Status it says "1 EdOrg(s)"
        Given the sli securityEvent collection is empty
        When I click on the "Edit Authorizations" button next to it
        And I expand all nodes
        And I de-authorize the educationalOrganization "Many-Parents"
        And I click Update
        Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant
        And I check to find if record is in sli db collection:
          | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
          | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
          | securityEvent       | 1                   | body.userEdOrg        | fakeab32-b493-999b-a6f3-sliedorg1234      |
        And there are "1" educationalOrganizations in the targetEdOrgList
        And I see an application "Royal Oak" in the table
        And in Status it says "Not Approved"

    @wip
    Scenario: Verify last authorized information - SEA admin authorizes SEA only

        When I hit the Admin Application Authorization Tool
         And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
        Then I see an application "Royal Oak" in the table
         And in Status it says "Not Approved"
        Then I click on the "Edit Authorizations" button next to it
         And I deselect hierarchical mode
         And I expand all nodes
         And I authorize the educationalOrganization "Illinois State Board of Education"
         And I click Update
        Then only below is present in the application authorization edOrgs array for the application "Royal Oak" in tenant "Midgar"
            | edOrg                             | user    | realm edOrg                          |
            | Illinois State Board of Education | iladmin | fakeab32-b493-999b-a6f3-sliedorg1234 |

    @wip
    Scenario: Verify last authorized information - LEA admin authorizes LEA only (dependant on above scenario)

        When I hit the Admin Application Authorization Tool
         And I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
        Then I see an application "Royal Oak" in the table
         And in Status it says "Not Approved"
        Then I click on the "Edit Authorizations" button next to it
         And I deselect hierarchical mode
         And I expand all nodes
         And I authorize the educationalOrganization "Daybreak School District 4529"
         And I click Update
        Then only below is present in the application authorization edOrgs array for the application "Royal Oak" in tenant "Midgar"
            | edOrg                             | user          | realm edOrg                          |
            | Illinois State Board of Education | iladmin       | fakeab32-b493-999b-a6f3-sliedorg1234 |
            | Daybreak School District 4529     | daybreakadmin | fakeab32-b493-999b-a6f3-sliedorg1234 |

    @wip
    Scenario: Verify last authorized information - SEA admin de-authorizes LEA and adds school (depends on above)

        When I hit the Admin Application Authorization Tool
         And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
        Then I see an application "Royal Oak" in the table
         And in Status it says "2 EdOrg(s)"
        Then I click on the "Edit Authorizations" button next to it
         And I deselect hierarchical mode
         And I expand all nodes
         And I de-authorize the educationalOrganization "Daybreak School District 4529"
         And I authorize the educationalOrganization "Many-Parents"
         And I click Update
        Then only below is present in the application authorization edOrgs array for the application "Royal Oak" in tenant "Midgar"
            | edOrg                             | user          | realm edOrg                          |
            | Illinois State Board of Education | iladmin       | fakeab32-b493-999b-a6f3-sliedorg1234 |
            | Many-Parents                      | iladmin       | fakeab32-b493-999b-a6f3-sliedorg1234 |
        Then I see an application "Royal Oak" in the table
         And in Status it says "2 EdOrg(s)"
        Then I click on the "Edit Authorizations" button next to it
         And I deselect hierarchical mode
         And I expand all nodes
         And I de-authorize the educationalOrganization "Illinois State Board of Education"
         And I de-authorize the educationalOrganization "Many-Parents"
         And I click Update
        Then there are "0" edOrgs for the "Royal Oak" application in the applicationAuthorization collection for the "Midgar" tenant

        #Timestamps should be checked manually




     
          

