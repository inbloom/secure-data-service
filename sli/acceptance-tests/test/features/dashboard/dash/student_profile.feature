Feature: Click on student and expand profile

As a teacher in a school district, I want to click on a student and be directed to their profile page.

Background:
Given I have an open web browser
Given the server is in "test" mode
When I navigate to the Dashboard home page

Scenario: View a student's information 
Given I am authenticated to SLI as "lkim" password "lkim"
When I click on the Dashboard page
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "M. Jones - 8th Grade English - Sec 6"
And I wait for "2" seconds
And I click on student "Jolene Ashley"
And I view its student profile
And their name shown in profile is "Jolene Colt Ashley"
And their id shown in proflie is "943715230"
And their grade is "Eighth grade"
And the teacher is "lkim"
And the lozenges include "ELL"