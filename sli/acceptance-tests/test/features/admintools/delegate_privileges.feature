@wip
Feature: Delegating control from a district administrator to a state administrator

Scenario: District Super Administrator delegates app approval permission to state
	Given I have an open web browser
    And I get redirected to the IDP login page
    And I authenticate with username "developer" and password "developer1234"
    And I hit the Delegation Page
    And I am shown the Delegation configuration for my district
    And Application Authorization is un-checked
    When I check the Application Authorization
    And click Save
    And I log out 
    And I am authenticated to SLI IDP as user "" with pass "1234"
    Then I am able to view the applications authorizations page as a state administrator
  