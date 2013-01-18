@smoke @RALLY_US134 @RALLY_US5156
Feature: Admin delegation CRUD

	Scenario: District administrator creating admin delegation
    Given I am logged in using "sunsetadmin" "sunsetadmin1234" to realm "SLI"
     When I POST a new admin delegation
     Then I should receive a return code of 201
     

	Scenario: State administrator without access being denied update to application authorization
      Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
      And the sli securityEvent collection is empty
      When I do not have access to app authorizations for district "IL-SUNSET"
      Then I should update app authorizations for district "IL-SUNSET"
      And I should receive a return code of 403
      And a security event matching "^Access Denied" should be in the sli db

  Scenario: District administrator updating admin delegation 	
    Given I am logged in using "sunsetadmin" "sunsetadmin1234" to realm "SLI"
    And I have a valid admin delegation entity
    And I change "appApprovalEnabled" to true
    When I PUT to admin delegation
    Then I should receive a return code of 204

	Scenario: State administrator with access updating application authorizations
     Given the sli securityEvent collection is empty
     And I am logged in using "iladmin" "iladmin1234" to realm "SLI"
     When I have access to app authorizations for district "IL-SUNSET"
     And I should save the old app authorizations for "IL-SUNSET"
     Then I should update app authorizations for district "IL-SUNSET" 
     And I should receive a return code of 204
     And a security event matching "^NOT ALLOWED" should be in the sli db

	Scenario: State administrator with access updating one application authorization
     And I am logged in using "iladmin" "iladmin1234" to realm "SLI"
     When I have access to app authorizations for district "IL-SUNSET"
     And I should save the old app authorizations for "IL-SUNSET"
     Then I should update one app authorization for district "IL-SUNSET"
     And I should receive a return code of 204
    
	Scenario: State administrator with access updating application authorizations again
     Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
     When I have access to app authorizations for district "IL-SUNSET"
     And I should save the old app authorizations for "IL-SUNSET"
     Then I should also update app authorizations for district "IL-SUNSET" 
     And I should receive a return code of 204
  
Scenario: Put back application authorizations
    Then I put back app authorizations
	
Scenario: State administrator seeing delegations they have access to
     Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
     When I have access to app authorizations for district "IL-SUNSET"
      Then I should get my delegations
      And I should see that "appApprovalEnabled" is "true" for district "IL-SUNSET's ID"
