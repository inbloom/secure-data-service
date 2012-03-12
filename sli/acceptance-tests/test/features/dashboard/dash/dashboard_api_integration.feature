Feature: Dashboard User Login Using IDP

As a teacher in a school district, I want to use the SLI IDP Login to authenticate on SLI, and I can see specific students retrieved from the API.

Scenario: Authenticate against IDP and navigate to studentlist page

Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Realm" and click go
When I login as "linda.kim" "linda.kim1234"
When I click on the Dashboard page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
And I wait for "10" seconds
Then The students who have an ELL lozenge exist in the API
