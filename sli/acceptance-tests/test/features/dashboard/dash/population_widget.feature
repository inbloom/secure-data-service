Feature: Population Widget <US435>

@RALLY_US200
Scenario: Selecting classes on LOS
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "cgray" "cgray1234"
When I look in the ed org drop-down
Then I see these values in the drop-down: "Daybreak School District 4529;Sunset School District 4526"

When I select ed org "Sunset School District 4526"
When I look in the school drop-down
Then I only see "Sunset Central High School"

When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "Daybreak Central High"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I look in the course drop-down
Then I see these values in the drop-down: "Writing about Government;American Literature"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
Then I see these values in the drop-down: "Sec 145"
 And I select <section> "Sec 145"
Then I should have a dropdown selector named "viewSelect"
Then I see a list of 26 students
#Client filter tests
And I should have a dropdown selector named "filterSelect"
And I should have multiple filters available
When I select filter "English Language Learner"
Then I should see a student named "Arsenio Durham"
And I should see a student named "Kimberley Pennington"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "Writing about Government"
And I select section "Sec 923"
Then I see a list of 2 students

