Feature: Student Profile View - Display and Hide Pages Based on Grades

As a teacher in a school district, when I view a student's profile, a page appears based on that grade-range specified in configuration.

Background:
Given I have an open web browser

Scenario:  Page is hidden for student with no ELL
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"
When I click on the Dashboard page
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "M. Jones - 8th Grade English - Sec 6"
And I wait for "2" seconds
And I click on student "Nicholas Burks"
And I view its student profile
And there are "5" Tabs
And the lozenges count is "0"

Scenario:  Page is displayed for student with ELL
Given the server is in "test" mode
When I navigate to the Dashboard home page
Given I am authenticated to SLI as "lkim" password "lkim"
When I click on the Dashboard page
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "M. Jones - 8th Grade English - Sec 6"
And I wait for "2" seconds
And I click on student "Arsenio Durham"
And I view its student profile
And there are "6" Tabs
And Tab has a title named "ELL"
And the lozenges count is "1"
And the lozenges include "ELL"