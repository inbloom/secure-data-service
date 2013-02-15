@RALLY_US215
@derp
Feature: User sees information in databrowser for certain districts


	Background:
  Given that databrowser has been authorized for all ed orgs

Scenario: An Educator is denied access because they are not an administrator
    Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "linda.kim" "linda.kim1234" for the "Simple" login page
     Then I get message that I am not authorized

Scenario: A user who is a Educator and an IT Admin is denied access because the Educator role is not an admin role
	Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "mabernathy" "mabernathy1234" for the "Simple" login page
     Then I get message that I am not authorized

Scenario: The Educator role is given the Admin flag so an Educator gets access
	Given I change the isAdminRole flag for role "Educator" to in the realm "Daybreak" to be "true"
    And I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
    Then I should be redirected to the Data Browser home page
    When I navigate to see the teachers in the school "Daybreak Central High"
    Then I should see that there are "2" teachers 
    When I navigate to see the teachers in the school "Sunset Central High"
    Then I should see that there are "2" teachers 

@DE2510
@wip
Scenario: An IT Admin in Daybreak and Sunset sees the Daybreak data but not the Sunset data
    Given I remove the application authorizations in sunset
    And I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
    And I should be redirected to the Data Browser home page
    When I navigate to see the teachers in the school "Daybreak Central High"
    Then I should see that there are "2" teachers 
    When I navigate to see the teachers in the school "Sunset Central High"
    Then I see a "You do not have access to view this." alert box

Scenario: Put data back
  Given that databrowser has been authorized for all ed orgs
  And I change the isAdminRole flag for role "Educator" to in the realm "Daybreak" to be "false"