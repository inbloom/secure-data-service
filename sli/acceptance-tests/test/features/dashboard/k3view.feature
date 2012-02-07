#@wip

Feature: K-3 student list view

As a SEA/LEA user, I want to see the K-4 student list view
on SLI, so I could see elementary school students results

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
Then the fuel gauge label for the assessment "DIBELS_NEXT.perfLevel" and student "111111111" is "@B"

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

