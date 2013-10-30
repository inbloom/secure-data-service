Feature: Application Enablement and Authorization by Education Organization
	As a developer I want to be able to enable my application for specific states and education organizations.
	As an admin I want to be able to approve the applications that developers enabled for specific education organizations.

Background:
	Given I have an open web browser

	Scenario: Create new bulk extract application (set up data)
        Given I am a valid SLI Developer "admintest-developer@slidev.org" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
        Then I am redirected to the Application Registration Tool page
        And I have clicked to the button New
        And I am redirected to a new application page
        When I entered the name "Aboukir" into the field titled "Name"
        And I check Bulk Extract
        And I have entered data into the other required fields except for the shared secret and the app id which are read-only
        And I click on the button Submit
        Then I am redirected to the Application Registration Tool page
        And the application "Aboukir" is listed in the table on the top
        When I click on the row of application named "Aboukir" in the table
        Then the client ID and shared secret fields are Pending
        And the Registration Status field is Pending

    Scenario: SLC Operator accepts bulk extract application registration request (set up data)
        Given I am a valid SLC Operator "slcoperator-email@slidev.org" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcoperator-email@slidev.org" "slcoperator-email1234" for the "Simple" login page
        Then I am redirected to the Application Approval Tool page
        And I see all the applications registered on SLI
        And I see all the applications pending registration
        And the pending apps are on top
        When I click on 'Approve' next to application "Aboukir"
        Then application "Aboukir" is registered
        And the 'Approve' button is disabled for application "Aboukir"

    Scenario: Developer registers bulk extract application (set up data)
        Given I am a valid SLI Developer "slcdeveloper" from the "SLI" hosted directory
        When I hit the Application Registration Tool URL
        And I was redirected to the "Simple" IDP Login page
        And I submit the credentials "slcdeveloper" "slcdeveloper1234" for the "Simple" login page
        Then I am redirected to the Application Registration Tool page
        And I see the list of (only) my applications
        And I clicked on the button Edit for the application "Aboukir"
        When I enable the educationalOrganization "Illinois State Board of Education" in tenant "Midgar"
        And I click on Save
        Then "Aboukir" is enabled for "200" education organizations
        
    Scenario: SEA Admin Approves application
         When I hit the Admin Application Authorization Tool
         And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
         And I see an application "Aboukir" in the table
         And in Status it says "Not Approved"
         And the sli securityEvent collection is empty

    ##########################################################################################
    #those edOrgs not enabled by the developer are grayed out and non-selectable
    ##########################################################################################
        And I click on the "Edit Authorizations" button next to it
        And I expand all nodes
      	 And I authorize the educationalOrganization "Illinois State Board of Education"
      	 And I click Update
      	 Then there are "200" edOrgs for the "Aboukir" application in the applicationAuthorization collection for the "Midgar" tenant
         And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
         And there are "200" educationalOrganizations in the targetEdOrgList
         And I see an application "Aboukir" in the table
         And in Status it says "200 EdOrg(s)"
         Given the sli securityEvent collection is empty
         When I click on the "Edit Authorizations" button next to it
         And I de-authorize the educationalOrganization "Illinois State Board of Education"
         And I click Update
         Then there are "0" edOrgs for the "Aboukir" application in the applicationAuthorization collection for the "Midgar" tenant
         And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
         And there are "200" educationalOrganizations in the targetEdOrgList
         And I see an application "Aboukir" in the table
         And in Status it says "Not Approved"
  
         #SEA only
         Then I click on the "Edit Authorizations" button next to it
         And the sli securityEvent collection is empty
         And I deselect hierarchical mode
         And I expand all nodes
         And I authorize the educationalOrganization "Illinois State Board of Education"
         And I click Update
         Then there are "1" edOrgs for the "Aboukir" application in the applicationAuthorization collection for the "Midgar" tenant
         Then The following edOrgs are authorized for the application "Aboukir" in tenant "Midgar"
  			|edorgs|
   	        |Illinois State Board of Education |
         And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
         And there are "1" educationalOrganizations in the targetEdOrgList
         And I see an application "Aboukir" in the table
         And in Status it says "1 EdOrg(s)"
         Given the sli securityEvent collection is empty
         When I click on the "Edit Authorizations" button next to it
         And I deselect hierarchical mode
         And I de-authorize the educationalOrganization "Illinois State Board of Education"
         And I click Update
         Then there are "0" edOrgs for the "Aboukir" application in the applicationAuthorization collection for the "Midgar" tenant
         And I check to find if record is in sli db collection:
             | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
             | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
         And there are "1" educationalOrganizations in the targetEdOrgList
         And I see an application "Aboukir" in the table
         And in Status it says "Not Approved"

    Scenario: LEA Admin Approves application
          When I hit the Admin Application Authorization Tool
          And I submit the credentials "daybreakadmin" "daybreakadmin1234" for the "Simple" login page
          And I see an application "Aboukir" in the table
          And in Status it says "Not Approved"
          And the sli securityEvent collection is empty

          #All edOrgs from LEA downwards
          And I click on the "Edit Authorizations" button next to it
          And I expand all nodes
          And I authorize the educationalOrganization "Daybreak School District 4529"
          And I click Update
          Then there are "45" edOrgs for the "Aboukir" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
            | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
            | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
          And there are "45" educationalOrganizations in the targetEdOrgList
          And I see an application "Aboukir" in the table
          And in Status it says "45 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I de-authorize the educationalOrganization "Daybreak School District 4529"
          And I click Update
          Then there are "0" edOrgs for the "Aboukir" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
              | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
              | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
          And there are "45" educationalOrganizations in the targetEdOrgList
          And I see an application "Aboukir" in the table
          And in Status it says "Not Approved"

          #LEA only
          Then I click on the "Edit Authorizations" button next to it
          And the sli securityEvent collection is empty
          And I deselect hierarchical mode
          And I expand all nodes
          And I authorize the educationalOrganization "Daybreak School District 4529"
          And I click Update
          Then there are "1" edOrgs for the "Aboukir" application in the applicationAuthorization collection for the "Midgar" tenant
          Then The following edOrgs are authorized for the application "Aboukir" in tenant "Midgar"
             |edorgs|
             |Daybreak School District 4529 |
          And I check to find if record is in sli db collection:
             | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
             | securityEvent       | 1                   | body.logMessage       | Application granted access to EdOrg data! |
          And there are "1" educationalOrganizations in the targetEdOrgList
          And I see an application "Aboukir" in the table
          And in Status it says "1 EdOrg(s)"
          Given the sli securityEvent collection is empty
          When I click on the "Edit Authorizations" button next to it
          And I deselect hierarchical mode
          And I de-authorize the educationalOrganization "Daybreak School District 4529"
          And I click Update
          Then there are "0" edOrgs for the "Aboukir" application in the applicationAuthorization collection for the "Midgar" tenant
          And I check to find if record is in sli db collection:
             | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
             | securityEvent       | 1                   | body.logMessage       | EdOrg data access has been revoked!       |
          And there are "1" educationalOrganizations in the targetEdOrgList
          And I see an application "Aboukir" in the table
          And in Status it says "Not Approved"
