@wip
Feature: Delegating control from a district administrator to a state administrator

Scenario: District Super Administrator delegates app approval permission to state
	Given I have an open web browser
	When I hit the delegation url
    And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "sunsetadmin" "sunsetadmin1234" for the "Simple" login page
    And I am redirected to the delegation page for my district
    And Application Authorization is un-checked
    And I check the Application Authorization
    And click Save
    And I log out 
    And I am authenticated to SLI IDP as user "" with pass "1234"
    Then I am able to view the applications authorizations page as a state administrator
  