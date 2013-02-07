@RALLY_US2492
Feature: As an SLI application, I want to be able to read securityEvent entities
SEA Admins should be able to see all securityEvents related to their SEA and having securityEvent.role as SEA Administrator
LEA Admins should be able to see all securityEvents related to their LEA and having securityEvent.role as LEA Administrator
SLI Operators should be able to see all securityEvents
Non Admins should not be able to see any securityEvents
 
Scenario: Read all entities as SEA Admin
    Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    And format "application/vnd.slc+json"
    And parameter "limit" is "0"
    When I navigate to GET "<ENTITY URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "4" entities
    And each entity's "entityType" should be "<ENTITY TYPE>"
    And each entity's "targetEdOrg" should be "IL"
    And each entity's "roles" should contain "SEA Administrator"

Scenario: Read all entities as LEA Admin
    Given I am logged in using "sunsetadmin" "sunsetadmin1234" to realm "SLI"
    And format "application/vnd.slc+json"
    And parameter "limit" is "0"
    When I navigate to GET "<ENTITY URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "4" entities
    And each entity's "entityType" should be "<ENTITY TYPE>"
    And each entity's "targetEdOrg" should be "IL-SUNSET"
    And each entity's "roles" should contain "LEA Administrator"

Scenario: Read all entities as SLC Operator
    Given I am logged in using "operator" "operator1234" to realm "SLI"
    And format "application/vnd.slc+json"
    And parameter "limit" is "0"
    When I navigate to GET "<ENTITY URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "19" entities
    And each entity's "entityType" should be "<ENTITY TYPE>"

Scenario: Read all entities as NonAdmin User
    Given I am logged in using "linda.kim" "linda.kim1234" to realm "IL"
    And format "application/vnd.slc+json"
    And parameter "limit" is "0"
    When I navigate to GET "<ENTITY URI>"
    Then I should receive a return code of 403

Scenario: Setup admin delegation as LEA Admin(SecurityEventDelegation is disabled)
    Given I am logged in using "sunsetadmin" "sunsetadmin1234" to realm "SLI"
    When I POST a new admin delegation
    Then I should receive a return code of 201
    
Scenario: Enable delegation of SecurityEvents as LEA Admin
    Given I am logged in using "sunsetadmin" "sunsetadmin1234" to realm "SLI"
    And I have a valid admin delegation entity
    And I change "viewSecurityEventsEnabled" to true
    When I PUT to admin delegation
    Then I should receive a return code of 204


Scenario: Read delegation of SecurityEvents as SEA Admin
    Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    Then I should get my delegations
    And I should see that "viewSecurityEventsEnabled" is "true" for district "IL-SUNSET's ID"

Scenario: Read securityEvents as SEA Admin. 8 Events. Sunsetadmin(4) And Iladmin(4)
    Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    And format "application/vnd.slc+json"
    And parameter "limit" is "0"
    When I navigate to GET "<ENTITY URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "8" entities
    And each entity's "entityType" should be "<ENTITY TYPE>"
    Then each entity's "targetEdOrg" should be in the array "<IL_OR_IL_SUNSET>"
   
   Scenario: Enable delegation of SecurityEvents as Longwood LEA Admin 
    Given I am logged in using "longwoodadmin" "longwoodadmin1234" to realm "SLI"
    And I have a valid admin delegation entity for longwood
    And I change "viewSecurityEventsEnabled" to true
    When I PUT to admin delegation
    Then I should receive a return code of 201
    
    Scenario: Read three levels delegation of SecurityEvents as SEA Admin
    Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    Then I should get my delegations
    And I should see that "viewSecurityEventsEnabled" is "true" for district "IL-SUNSET's ID"
	And I should see that "viewSecurityEventsEnabled" is "true" for district "IL-LONGWOOD's ID"
	
	Scenario: Read securityEvents as SEA Admin. 11 Events. Sunsetadmin(4),  Iladmin(4) and Longwoodadmin(3)
    Given I am logged in using "iladmin" "iladmin1234" to realm "SLI"
    And format "application/vnd.slc+json"
    And parameter "limit" is "0"
    When I navigate to GET "<ENTITY URI>"
    Then I should receive a return code of 200
    And I should receive a collection of "11" entities
    And each entity's "entityType" should be "<ENTITY TYPE>"
    Then each entity's "targetEdOrg" should be in the array "<IL_OR_IL_SUNSET>"
   
