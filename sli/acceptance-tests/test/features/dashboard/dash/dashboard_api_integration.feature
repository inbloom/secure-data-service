Feature: Dashboard User Login Using IDP

As a teacher in a school district, I want to use the SLI IDP Login to authenticate on SLI, and I can see specific students retrieved from the API.

@wip
Scenario: Authenticate against IDP and navigate to studentlist page
#bundled with a test in student_profile test
Given I have an open web browser
When I navigate to the Dashboard home page
And I wait for "2" seconds
When I select "Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
And the following students have "ELL" lozenges: "Matt Joseph Sollars;Odysseus Merrill;Hoyt Hicks;Brielle Klein;Patricia Harper"
And the following students have "FRE" lozenges: "Odysseus Merrill;Hoyt Hicks;Delilah Sims;Ursa Oconnor"

