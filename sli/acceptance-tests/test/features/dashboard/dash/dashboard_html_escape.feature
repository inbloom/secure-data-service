Feature: Dashboard HTML Escape Validation

As a secrutiy officer, I want the output strings to be HTML escaped.

Background:
Given I have an open web browser

@RALLY_US200
Scenario: Student Name Contains Special Character "<" and ">"

Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
Then I should be redirected to the Dashboard landing page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
When I click on student "Raf<>l Kirk"
Then I view its student profile