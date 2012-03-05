Feature: Click on student and expand profile

As a teacher in a school district, I want to click on a student and be directed to their profile page.

Scenario: Navigate to student list and click on student.

Given I have an open web browser
Given the server is in "test" mode
When I navigate to the Dashboard home page
And I am authenticated to SLI as "lkim" password "lkim"
When I click on the Dashboard page
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "M. Jones - 8th Grade English - Sec 6"
And I wait for "1" seconds
When I click on student, their name shows up on student profile
