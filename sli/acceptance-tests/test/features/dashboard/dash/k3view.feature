Feature: K-3 student list view

As a SEA/LEA user, I want to see the K-4 student list view
on SLI, so I could see elementary school students results

@wip
Scenario: Check assessment result

Given I have an open web browser
Given the server is in "test" mode
And I am authenticated to SLI as "rbraverman" password "rbraverman"
When I access "/studentlist"
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I select course "Reading Group"
And I select section "Reading Group - Grey"
And I wait for "2" seconds
Then the fuel gauge label for the assessment "DIBELS_NEXT.Mastery level" and student "111111111" is "2"

Scenario: Check student name
Given I have an open web browser
Given the server is in "test" mode
And I am authenticated to SLI as "rbraverman" password "rbraverman"
When I access "/studentlist"
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I select course "Reading Group"
And I select section "Reading Group - Grey"
And I wait for "2" seconds
Then I see a list of 1 students
And the list includes: "Student Fake"

Scenario: Check K-3 Student Name in Live
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "rbraverman" "rbraverman1234"
And I wait for "2" seconds
When I click on the Dashboard page
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
And I wait for "25" seconds
Then I see a list of 20 students
#And the list includes: "Mi-Ha Tran"
