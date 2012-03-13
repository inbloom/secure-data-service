Feature: View pages and panels on student profile

As a teacher in a school district, I want to click on a student and be directed to their profile page that contains pages and panels

Background:
Given I have an open web browser

Scenario: View a male student's information and check tabs
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
And I click on student "Marvin Miller"
And I view its student profile
And there are "6" Tabs
And in Tab "1", there is "0" Panels
And in Tab "2", there is "1" Panels
And in Tab "3", there is "2" Panels
And in Tab "4", there is "1" Panels 
And in Tab "5", there is "0" Panels
And in Tab "6", there is "1" Panels
And Tab "1" is titled "Attendance and Discipline Grade Male"
And Tab "2" is titled "Attendance and Discipline"
And Tab "3" is titled "Assessments"
And Tab "4" is titled "Grades and Credits"
And Tab "5" is titled "Advanced Academics"
And Tab "6" is titled "Overview"