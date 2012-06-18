@RALLY_US215
Feature: User sees information in databrowser for certain districts

Scenario: An Educator in Daybreak and Sunset sees the Daybreak data and the Sunset data
    Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
    And I should be redirected to the Data Browser home page
    And I should navigate to "/entities/teachers"
    Then I should see that there are "4" teachers 
    And I should get the IDs for "Daybreak and Sunset"


Scenario: An Educator in Daybreak and Sunset sees the Daybreak data but not the Sunset data
    Given I remove the application authorizations in sunset
    And I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the "Simple" IDP Login page
    When I submit the credentials "rrogers" "rrogers1234" for the "Simple" login page
    And I should be redirected to the Data Browser home page
    And I should navigate to "/entities/teachers"
    Then I should see that there are "3" teachers 
    And I should get the IDs for "Daybreak only"

Scenario: Put data back
  Then I put back the application authorizations in sunset