@RALLY_US215
Feature: User sees information in databrowser for certain districts

	Background:
  Given that databrowser has been authorized for all ed orgs

Scenario: An SEA Administrator in IL cannot see any teachers in Daybreak and the Sunset data
    Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
    And I should be redirected to the Data Browser home page
    And I should navigate to "/entities/teachers"
    Then I should see that there are no teachers 

Scenario: An Educator in Daybreak and Sunset sees the Daybreak and Sunset data
    Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
    And I should be redirected to the Data Browser home page
    And I should navigate to "/entities/teachers"
    Then I should see that there are "3" teachers 
    And I should get the IDs for "Daybreak and Sunset"

@DE1709
Scenario: An Educator in Daybreak and Sunset sees the Daybreak data but not the Sunset data
    Given I remove the application authorizations in daybreak
    And I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
    And I should be redirected to the Data Browser home page
    And I should navigate to "/entities/teachers"
    Then I should see that there are "2" teachers 
    And I should get the IDs for "Sunset only"

Scenario: Put data back
  Then I put back the application authorizations in daybreak
