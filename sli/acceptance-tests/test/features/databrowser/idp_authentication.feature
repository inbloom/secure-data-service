@RALLY_US2212
Feature: Authentication to SLI through various IDP types
As a developer, I want to have tests that tell me if I break OpenAM, ADFS or other IDP authentication with SLI

Background: Use HTTPS
	Given My API is running on HTTPS
	And I have an open web browser
	And I navigated to the Data Browser Home URL

Scenario: User authenticating using OpenAM
Given I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "OpenAM" IDP Login page
When I submit the credentials "jdoe" "jdoe1234" for the "OpenAM" login page
Then I should be redirected to the Data Browser home page
	
Scenario: User authenticating using ADFS

Given I was redirected to the Realm page
And I choose realm "Illinois Daybreak School District 4529" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "ADFS" IDP Login page
When I submit the credentials "linda.kim" "linda.kim1234" for the "ADFS" login page
Then I should be redirected to the Data Browser home page
