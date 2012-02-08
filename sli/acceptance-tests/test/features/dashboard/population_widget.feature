Feature: Population Widget <US435>

Scenario: Selecting classes on LOS

Given the server is in "test" mode
Given I have an open web browser
And I am authenticated to SLI as "cgray" password "cgray"
When I look in the ed org drop-down
Then I see these values in the drop-down: "Daybreak School District 4529;Sunset School District 4526;Illinois State Board of Education"

When I select ed org "Sunset School District 4526"
When I look in the school drop-down
Then I only see "Sunset Central High School"

When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "Daybreak Central High"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I look in the course drop-down
Then I see these values in the drop-down: "Club/Team;Senior Homeroom;Writing about Government;Honors American Literature;American Literature"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
Then I see these values in the drop-down: "Sec 142;Sec 143;Sec 144;Sec 145"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 144"
And I wait for "2" seconds
Then I see a list of 24 students

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "Writing about Government"
Then I see these values in the section drop-down: "Sec 923"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
And I wait for "2" seconds
Then I see a list of 28 students
And the list includes: "Johnny Patel;Carmen Ortiz"
