@RALLY_US187
@RALLY_US185
@RALLY_US103
Feature: Application Authorization Tool
As an SEA, I want to be able to allow specific applications access to my data

	Scenario: SEA Admin logs in to the authorization tool
	
	Given I have an open web browser
	When I hit the Admin Application Authorization Tool
	And I was redirected to the "Simple" IDP Login page
	When I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
	Then I am redirected to the Admin Application Authorization Tool
	And I see a label in the middle "Illinois State Board of Education"
	And I see the list of all available apps on SLI
	And the authorized apps for my district are colored green
	And the unauthorized are colored red
	And are sorted by 'Status'
	And I see the Name, Version, Vendor and Status of the apps

Scenario: Non SLI-hosted valid user tries to access the Application Authorization Tool
	Given I have an open web browser
	And the sli securityEvent collection is empty
	When I hit the Admin Application Authorization Tool
     And I submit the credentials "administrator" "administrator1234" for the "Simple" login page
     Then the api should generate a 403 error
      And I should see a count of "2" in the security event collection
      And I check to find if record is in sli db collection:
       | collectionName  | expectedRecordCount | searchParameter         | searchValue                                                                                              | searchType |
       | securityEvent   | 1                   | body.appId              | UNKNOWN                                                                                                  | string     |
       | securityEvent   | 1                   | body.userEdOrg          | fakeab32-b493-999b-a6f3-sliedorg1234                                                                     | string     |
       | securityEvent   | 1                   | body.className          | org.slc.sli.api.resources.security.SamlFederationResource                                                | string     |
       | securityEvent   | 1                   | body.logMessage         | Access Denied:Invalid user.  No valid role mappings exist for the roles specified in the SAML Assertion. | string     |
      And "1" security event with field "body.actionUri" matching "http.*/api/rest/saml/sso/post" should be in the sli db

Scenario: SEA Admin Approves bulk extract application
	
	Given I have an open web browser
    When I hit the Admin Application Authorization Tool
     And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
     And I see an application "SDK Sample" in the table
     #denying app to clean out incorrect fixture data
     And in Status it says "Approved"
     And I click on the "Deny" button next to it
     And I am asked 'Do you really want deny access to this application of the district's data'
     And I click on Ok
     And in Status it says "Not Approved"
     And the sli securityEvent collection is empty
	 And I click on the "Approve" button next to it
    Then I am asked 'Do you really want this application to access the district's data'
	When I click on Ok
	Then the application is authorized to use data of "Illinois State Board of Education"
	 #11 edorgs related to SEA, but only 4 direct children
     And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 38                  | body.logMessage       | Application granted access to EdOrg data! |
     #TODO - check edOrgs directly
     And the app "SDK Sample" Status becomes "Approved"
     And it is colored "green"
     And the Approve button next to it is disabled
     And the Deny button next to it is enabled

Scenario: SEA Admin Denies bulk extract application (dependant on above scenario)

     Given I have an open web browser
      And the sli securityEvent collection is empty
     When I hit the Admin Application Authorization Tool
      And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
      And I see an application "SDK Sample" in the table
      And in Status it says "Approved"
      And the sli securityEvent collection is empty
     When I click on the "Deny" button next to it
     Then I am asked 'Do you really want deny access to this application of the district's data'
     When I click on Ok
     Then the application is denied to use data of "Illinois State Board of Education"
      And the app "SDK Sample" Status becomes "Not Approved"
      And it is colored "red"
      And the Approve button next to it is enabled
      And the Deny button next to it is disabled
      #11 edorgs related to SEA, but only 4 direct children
      And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
       | securityEvent       | 38                  | body.logMessage       | EdOrg data access has been revoked!   |
      #TODO - check edOrgs directly
      And the app "SDK Sample" Status becomes "Not Approved"
      And it is colored "red"
      And the Approve button next to it is enabled
      And the Deny button next to it is disabled

Scenario: SEA Admin Approves non-bulk extract application

	Given I have an open web browser
    When I hit the Admin Application Authorization Tool
     And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
     And I see an application "Testing App" in the table
     And in Status it says "Not Approved"
     And the sli securityEvent collection is empty
	 And I click on the "Approve" button next to it
    Then I am asked 'Do you really want this application to access the district's data'
	When I click on Ok
	Then the application is authorized to use data of "Illinois State Board of Education"
	 #11 edorgs related to SEA, but only 4 direct children
     And I check to find if record is in sli db collection:
      | collectionName      | expectedRecordCount | searchParameter       | searchValue                               |
      | securityEvent       | 200                 | body.logMessage       | Application granted access to EdOrg data! |
     #TODO - check edOrgs directly
     And the app "Testing App" Status becomes "Approved"
     And it is colored "green"
     And the Approve button next to it is disabled
     And the Deny button next to it is enabled

Scenario: SEA Admin Denies non-bulk extract application (dependant on above scenario)

     Given I have an open web browser
      And the sli securityEvent collection is empty
     When I hit the Admin Application Authorization Tool
      And I submit the credentials "iladmin" "iladmin1234" for the "Simple" login page
      And I see an application "Testing App" in the table
      And in Status it says "Approved"
      And the sli securityEvent collection is empty
     When I click on the "Deny" button next to it
     Then I am asked 'Do you really want deny access to this application of the district's data'
     When I click on Ok
     Then the application is denied to use data of "Illinois State Board of Education"
      And the app "Testing App" Status becomes "Not Approved"
      And it is colored "red"
      And the Approve button next to it is enabled
      And the Deny button next to it is disabled
      #11 edorgs related to SEA, but only 4 direct children
      And I check to find if record is in sli db collection:
       | collectionName      | expectedRecordCount | searchParameter       | searchValue                           |
       | securityEvent       | 200                 | body.logMessage       | EdOrg data access has been revoked!   |
      #TODO - check edOrgs directly
      And the app "SDK Sample" Status becomes "Not Approved"
      And it is colored "red"
      And the Approve button next to it is enabled
      And the Deny button next to it is disabled
