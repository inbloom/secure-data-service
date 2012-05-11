Feature: Dashboard Sad Path Tests

Test Sad Paths and Display Error Messages

Background:
Given I have an open web browser
Given the server is in "live" mode

Scenario: User has NO data

When I navigate to the Dashboard home page
Then I should be redirected to the Realm page
When I select "Illinois Sunset School District 4526" and click go
When I login as "cgray" "cgray1234"
Then I am informed that "No data is available for you to view."

