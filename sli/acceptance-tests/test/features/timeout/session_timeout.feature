@wip
@US157
Feature: Configurable Session Timeout
I want to validate that sessions get timed out after the Idle time passes with no calls to the API

Scenario: Validate session timeout

Given the API timeout is set to "300" seconds
And I have an open web browser
And I navigated to the Data Browser Home URL
And I was redirected to the Realm page
And I choose realm "Illinois Sunset School District 4526" in the drop-down list
And I click on the realm page Go button
And I was redirected to the "Simple" IDP Login page
When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
Then I should be redirected to the Data Browser home page
When I click on the "Home" link
And I wait less than the timeout
When I click on the "Home" link
Then I should be redirected to the Data Browser home page
When I wait for longer than the timeout
When I click on the "Home" link
And I was redirected to the Realm page