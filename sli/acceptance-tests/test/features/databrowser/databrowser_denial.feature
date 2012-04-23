@wip  
  Feature: Authentication to SLI through various IDP types

	Scenario: Authenticated user (Educator) tries to access a resource through DB within a district that denied Data Browser
  
  # It is very critical that we expand this Gherkin properly when we will implement the non-Educator context mapping!
      Given I have an open web browser
      And I navigated to the Data Browser Home URL
      And I choose realm "Illinois Sunset School District 4526" in the drop-down list
      And I click on the realm page Go button
      And I was redirected to the "OpenAM" IDP Login page 
      When I submit the credentials "ejane" "ejane1234" for the "OpenAM" login page
     Then I get message that I am not authorized