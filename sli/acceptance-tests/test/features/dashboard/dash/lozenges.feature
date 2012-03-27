Feature: Program Participation and Student Info on List (i.e., Lozenges) (US443)

As an educator, I want to see general student demographic data for 
each student, to inform instruction. 

Scenario: Check no lozenges
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
Then there is no lozenges for student "Student Fake" 

Scenario: Check program participation lozenges
Given I have an open web browser
Given the server is in "test" mode
And I am authenticated to SLI as "lkim" password "lkim"
When I access "/studentlist"
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
And I select course "7th Grade English"
And I select section "M. Jones - 7th Grade English - Sec. 3"
And I wait for "2" seconds
Then the lozenge for student "Bacon Burger" include "504"

Scenario: Check student attribute lozenges
Given I have an open web browser
Given the server is in "test" mode
And I am authenticated to SLI as "lkim" password "lkim"
When I access "/studentlist"
And I wait for "1" seconds
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
And I select course "7th Grade English"
And I select section "M. Jones - 7th Grade English - Sec. 3"
And I wait for "2" seconds
Then the lozenge for student "Salmon Burger" include "ELL"

Scenario:  Check no lozenges against live
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
And I wait for "2" seconds
When I click on the Dashboard page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "7th Grade English"
When I select section "7th Grade English - Sec. 3"
And I wait for "2" seconds
Then there is no lozenges for student "Student Fake" 

Scenario: Check student attribute lozenges against live
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
And I wait for "2" seconds
When I click on the Dashboard page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "7th Grade English"
When I select section "7th Grade English - Sec. 3"
And I wait for "2" seconds
Then the lozenge for student "Salmon Burger" include "FRE"

