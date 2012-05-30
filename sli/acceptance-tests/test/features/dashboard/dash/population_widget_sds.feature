Feature: Population Widget <US435>

@integration @RALLY_US200
Scenario: Selecting classes on LOS
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "cgray" "cgray1234"
When I look in the ed org drop-down
Then I see these values in the drop-down: "Daybreak School District 4529"

When I select ed org "Daybreak School District 4529"
When I look in the school drop-down
Then I only see "Daybreak Central High"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I look in the course drop-down
Then I see these values in the drop-down: "American Literature"

When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
Then I see these values in the drop-down: "Sec 145"
 And I select section "Sec 145"
#User based views - we have multiple views
Then I should have a dropdown selector named "viewSelect"
And I should have a selectable view named "College Ready ELA View"
#User based views - views have different headers
And I copy my current URL
When I select view "College Ready ELA View"
Then I should see a table heading "Reading Test Scores (Highest)"
Then I see a list of 25 students
#Client filter tests
And I should have a dropdown selector named "filterSelect"
And I should have multiple filters available
When I select filter "English Language Learner"
Then I should see a student named "Johnathan Zenz"
And I should see a student named "Kelvin Zahm"
And I logout
And I paste my copied URL
When I select "Illinois Sunset School District 4526" and click go
When I login as "cgray" "cgray1234"
Then I see a list of 25 students
#TODO make sure the selected values are correct

