Feature:  Student Profile View

As a teacher in a school district, I want to click on a student and be directed to their profile page.

Background:
Given I have an open web browser

Scenario: View a student's profile
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
And I click on student "Jolene Ashley"
And I view its student profile
And their name shown in profile is "Jolene Colt Ashley (Ms Jolene Juggernaut Ashley IV)"
And their id shown in proflie is "943715230"
And their grade is "Eighth grade"
And the teacher is "Dr lkim"
And the class is "M. Jones - 8th Grade English - Sec 6"
And the lozenges count is "1"
And the lozenges include "ELL"

Scenario: View a student's profile without lozenges
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
And I click on student "Nomlanga Mccormick"
And I view its student profile
And their name shown in profile is "Nomlanga Deacon Mccormick"
And their id shown in proflie is "423037202"
And their grade is "Fourth grade"
And the teacher is "Dr lkim"
And the class is "M. Jones - 8th Grade English - Sec 6"
And the lozenges count is "0"

Scenario: View a student's profile in live mode
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
And I wait for "2" seconds
When I click on the Dashboard page
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
And I click on student "Kimberley Pennington"
And I view its student profile
And their name shown in profile is "Kimberley Yuli Pennington Jr"
And their id shown in proflie is "437680177"
And their grade is "Eighth grade"
And the teacher is "Mrs Linda Kim"
And the class is "8th Grade English - Sec 6"
And the lozenges count is "2"
And the lozenges include "FRE"
@wip
Scenario: View a student with other name
Given I am authenticated to SLI as "lkim" password "lkim"
When I click on the Dashboard page
And I wait for "1" seconds

	