Feature: User sees information in databrowser for sunset and daybreak

#Before running this scenario, the regular sandbox data should be imported
Scenario: An Educator in Daybreak and Sunset sees the Daybreak data and the Sunset data
    Given I have an open web browser
    And I navigated to the Data Browser Home URL
    And I choose realm "Illinois Sunset School District 4526" in the drop-down list
    And I click on the realm page Go button
    And I was redirected to the SLI IDP Login page
    When I submit the credentials "rrogers" "rrogers1234" for the "OpenAM" login page
    And I should be redirected to the Data Browser home page
    And I should navigate to "/entities/teachers"
    Then I should see that there are "4" teachers 
    And I should get the IDs for "Daybreak and Sunset"
    
    
    
  