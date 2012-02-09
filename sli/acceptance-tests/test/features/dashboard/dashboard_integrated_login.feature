Feature: Dashboard User Login Using IDP

As a teacher in a school district, I want to use the SLI IDP Login to authenticate on SLI, and I can see specific students retrieved from the API.

Scenario: Authenticate against IDP and navigate to studentlist page.

Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "New York Realm " and click go
When I login as "mario.sanchez" "mario.sanchez1234"
Then I should be redirected to the app selection page
When I click on the Dashboard page
When I select school "P.S. 5"
When I select course "8th Grade English"
When I select section "Period 2"
Then The students who have an ELL lozenge exist in the API
