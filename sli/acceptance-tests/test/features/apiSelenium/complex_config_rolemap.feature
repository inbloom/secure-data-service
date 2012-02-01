@wip
Feature: Complex Configurable Role Mapping
As an SLI Administrator, I should be able to map a specific realm's Roles to one of the SLI Default Roles.
I should not see mappings from one realm while looking at a different realm.

Scenario: Accessing the Role Mapping page as a Non-SLI Administrator

	Given I have an open web browser
	And I am authenticated to SLI IDP as user "leader" with pass "leader1234"
	When I navigate to the SLI Role Mapping Admin Page
	Then I should get a message that I am not authorized
	
Scenario: Mapping a realm's custom roles to Default SLI Roles

	Given I have an open web browser
	And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
	And I am from realm "SLI"
	When I navigate to the SLI Role Mapping Admin Page
	And I map the default SLI role "Educator" to the custom role "teacher" 
	Then I see that "Teacher" is now mapped to the "Educator" role
	And The user "teacher" who is a "Teacher" can now log in to SLI as a "Educator" from my realm "SLI"

Scenario: Removing a realm's custom role mapping from a Default SLI Role

	Given I have an open web browser
	And I am authenticated to SLI IDP as user "demo" with pass "demo1234"
	And I am from realm "SLI"
	And a "Teacher" has been mapped to the "Educator" default role
	When I navigate to the SLI Role Mapping Admin Page
	And I remove the map from the default SLI role "Educator" to the custom role "teacher" 
	Then I see that "Teacher" is no longer mapped to the "Educator" role
	And The user "teacher" who is a "Teacher" can not access SLI as a "Educator" from my realm "SLI"

Scenario: Custom roles not visible across realms

	Given I have an open web browser
	And I am authenticated to SLI IDP as user "otheradmin" with pass "otheradmin1234"
	And I am from realm "State"
	And another realm's admin mapped "Teacher" to the "Educator" default SLI role in their realm
	When I navigate to the SLI Role Mapping Admin Page
	Then I should not see the mapping from the other realm
	And The user "teacher" who is a "Teacher" can not access SLI as a "Educator" from my realm "State"
