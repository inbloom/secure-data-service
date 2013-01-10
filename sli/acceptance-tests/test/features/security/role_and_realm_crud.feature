@smoke @RALLY_US176 @RALLY_US174
Feature: Custom Role Mapping functions and Realm Listing functions
As an administrator tool application, I should have access to API calls to perform CRUD operations to allow custom role mapping
As any SLI application, I can access an API resource that only returns a list of realms, even while unauthenticated

Scenario: Read a list of realms

  Given I am logged in using "sunsetrealmadmin" "sunsetrealmadmin1234" to realm "SLI"
  When I GET a list of realms
  Then I should receive a return code of 200
  And I should see a list of valid realm objects
  And I should only see the realm "IL-Sunset"

Scenario: Read an existing realm

Given I am logged in using "sunsetrealmadmin" "sunsetrealmadmin1234" to realm "SLI"
When I GET a specific realm "IL-Sunset"
Then I should receive a return code of 200
And I should see a valid object returned

Scenario: Update an existing realm

	Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
	When I PUT to change the realm "Fake Realm" to change field "name" to "Endless"
	Then I should receive a return code of 204

Scenario: Delete an existing realm

	Given I am logged in using "anotherfakerealmadmin" "anotherfakerealmadmin1234" to realm "SLI"
	When I DELETE the realm "Another Fake Realm"
	Then I should receive a return code of 204
	
Scenario: Create a new realm

  Given I am logged in using "anotherfakerealmadmin" "anotherfakerealmadmin1234" to realm "SLI"
  When I POST a new realm
  Then I should receive a return code of 201
     And I should receive a new ID for my new realm


Scenario: Deny creation of a new custom role doc when one already exists for this realm/tenant
  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I POST a new custom role document with realm "Fake Realm"
  Then I should receive a return code of 400

Scenario: List custom role docs only ever returns one
  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I GET my custom role doc
  Then I should receive a return code of 200
  And I should see one custom roles document for the realm "Fake Realm"

Scenario: Delete a custom role doc
Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I DELETE my custom role doc
  Then I should receive a return code of 204

Scenario: Create a custom role doc
  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I POST a new custom role document with realm "Fake Realm"
    Then I should receive a return code of 201
     And I should receive a new ID for my new custom role doc
     
Scenario: Create another new realm

  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I POST another new realm
  Then I should receive a return code of 201
     And I should receive a new ID for my new realm

@wip
Scenario: Create a custom role doc for my second realm
  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I POST a new custom role document with for my new realm
    Then I should receive a return code of 201
     And I should receive a new ID for my new custom role doc

		
Scenario: Deny the same role being listed in two different groups

  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I add a role "Foo" in group "Educator"
  Then I should receive a return code of 204
  When I add a role "Foo" in group "Leader"
  Then I should receive a return code of 400


Scenario: Deny the same role being listed twice in one group
  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I add a role "Bar" in group "Educator"
  Then I should receive a return code of 204
  When I add a role "Bar" in group "Educator"
  Then I should receive a return code of 400

	
Scenario: Deny a right being listed twice in one group
  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I add a right "WRITE_GENERAL" in group "Educator"
  Then I should receive a return code of 204
  When I add a right "WRITE_GENERAL" in group "Educator"
  Then I should receive a return code of 400


Scenario: Deny creating a new role with a realm I do not have access to
  Given I am logged in using "fakerealmadmin" "fakerealmadmin1234" to realm "SLI"
  When I POST a new custom role document with realm "IL-Sunset"
  Then I should receive a return code of 403
  
  @sandbox
  Scenario: Sandbox developer creating a custom role doc
  Given I am logged in using "anothersandboxdeveloper" "anothersandboxdeveloper1234" to realm "SLI" 
  When I POST a new custom role document with realm "Sandbox"
  Then I should receive a return code of 201

  @sandbox
  Scenario: Sandbox developer creating a duplicate custom role doc
  Given I am logged in using "anothersandboxdeveloper" "anothersandboxdeveloper" to realm "SLI" 
  When I POST a new custom role document with realm "Sandbox"
  Then I should receive a return code of 400

@sandbox
Scenario: Delete a sandbox custom role doc
  Given I am logged in using "sandboxdeveloper" "sandboxdeveloper1234" to realm "SLI" 
  When I PUT a new group "Foo" with role "Foo" and right "READ_GENERAL"
  Then I should receive a return code of 204

@sandbox
Scenario: Sandbox developer confirming that his data was not affected by delete
 Given I am logged in using "anothersandboxdeveloper" "anothersandboxdeveloper1234" to realm "SLI" 
When I GET my custom role doc
Then I should see that my custom role document is the default with realm "Sandbox"
