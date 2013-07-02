@US684
Feature: OAuth flow for assessment application
As an app developer, I want to be able to authenticate to the API through OAuth so I can make API calls to an assessment app

Scenario: Obtain App Authentication Token through Device flow

Given the testing device app key has been created
	And I have an open web browser
  And I import the odin-local-setup application and realm data
	
  When I navigate to the API authorization endpoint with my client ID
	Then I should be redirected to the realm choosing page
	When I select "IL-DAYBREAK" drop the dropdown and click go
	And I was redirected to the "Simple" IDP Login page
    And I submit the credentials "cgray" "cgray1234" for the "Simple" login page
    Then I should receive a json response containing my authorization code
    When I navigate to the API token endpoint with my client ID, secret, authorization code, and redirect URI
    Then I should receive a json response containing my authorization token
    And I should be able to use the token to make valid API calls
