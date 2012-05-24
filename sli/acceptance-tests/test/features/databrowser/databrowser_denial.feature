@RALLY_US215
  Feature: User being denied databrowser access 

  # These tests rely on fixture data to set which apps can be used or not.
  @wip
	Scenario: Authenticated user (Educator) tries to access a resource through DB within a district that denied Data Browser 
  #This scenario can be run with the regular sandbox data
      Given I have an open web browser
      And I navigated to the Data Browser Home URL
      And I choose realm "Illinois Sunset School District 4526" in the drop-down list
      And I click on the realm page Go button
    And I was redirected to the SLI IDP Login page
    When I enter "ejane" in the username text field  
    And I enter "ejane1234" in the password text field
  And I click the IDP page Go button

     Then I get message that I am not authorized
     
     
     Scenario: Educator denied access to DB when it is approved by the district but not by the app
     #This scenario requires importing the application_denial_fixture first
      Given I have an open web browser
      And I navigated to the Data Browser Home URL
            And I choose realm "Illinois Sunset School District 4526" in the drop-down list
      And I click on the realm page Go button
    And I was redirected to the SLI IDP Login page
    When I enter "cgray" in the username text field  
    And I enter "cgray1234" in the password text field
And I click the IDP page Go button

     Then I get message that I am not authorized
