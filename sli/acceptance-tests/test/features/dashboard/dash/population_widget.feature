Feature: Population Widget <US435>

@wip
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

Scenario: Selecting classes on LOS (live)
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "cgray" "cgray1234"
When I look in the ed org drop-down
Then I see these values in the drop-down: "Sunset School District 4526;Daybreak School District 4529;Illinois State Board of Education"

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
#User based views - we have multiple views
Then I should have a dropdown selector named "viewSelector"
And I should have a selectable view named "IL_3-8_ELA"
And I should have a selectable view named "IL_9-12"
#User based views - views have different headers
When I select view "IL_3-8_ELA"
Then I should see a table heading "ISAT Writing (highest)"
 #Then I see a list of 26 students
#Client filter tests
And I should have a dropdown selector named "studentFilterSelector"
And I should have multiple filters available
When I select filter "English Language Learner"
Then I should see a student named "Arsenio Durham"
And I should see a student named "Kimberley Pennington"
#When I select filter "Section 504"
#Then I should see a student named "Madeline Hinton"
When I select view "IL_9-12"
#Then I should see a table heading "Reading Test Scores (Highest)"
#And I should see a table heading "Writing Test Scores (Highest)"
#And I should see a table heading "AP Eng. Exam Scores (Highest)"


When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "Writing about Government"
And I select section "Sec 923"
Then I should only see one view named "IL_9-12"
#Then I see a list of 2 students



