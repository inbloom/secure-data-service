Feature: Student Profile View - Pages and Panels

As a teacher in a school district, I want to click on a student and be directed to their profile page that contains pages and panels

Background:
Given I have an open web browser

Scenario: View a student's information and check tabs
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
And there are "5" Tabs
And in Tab ID "6", there is "1" Panels
And in Tab ID "2", there is "1" Panels
And in Tab ID "3", there is "2" Panels
And in Tab ID "4", there is "1" Panels 
And in Tab ID "5", there is "0" Panels
And Tab has a title named "Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
And Tab has a title named "Advanced Academics"
# Temporary disable specific tab index tests
#And Tab "1" is titled "Overview"
#And Tab "2" is titled "Attendance and Discipline"
#And Tab "3" is titled "Assessments"
#And Tab "4" is titled "Grades and Credits"
#And Tab "5" is titled "Advanced Academics"

Scenario: View a student's info and check tabs in live mode
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Realm" and click go
When I login as "linda.kim" "linda.kim1234"
And I wait for "2" seconds
When I click on the Dashboard page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
And I click on student "Kimberley Pennington"
And I view its student profile
And there are "5" Tabs
And in Tab ID "6", there is "1" Panels
And in Tab ID "2", there is "1" Panels
And in Tab ID "3", there is "2" Panels
And in Tab ID "4", there is "1" Panels 
And in Tab ID "5", there is "0" Panels
And Tab has a title named "Overview"
And Tab has a title named "Attendance and Discipline"
And Tab has a title named "Assessments"
And Tab has a title named "Grades and Credits"
And Tab has a title named "Advanced Academics"
# Temp disable specific tab index tests
#And Tab "1" is titled "Overview"
#And Tab "2" is titled "Attendance and Discipline"
#And Tab "3" is titled "Assessments"
#And Tab "4" is titled "Grades and Credits"
#And Tab "5" is titled "Advanced Academics"
