Feature: Upload View Configurations

As a admin, I'm able to upload view configuration

Background:
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go

@integration @RALLY_US2276
Scenario: Invalid User Login
When I login as "linda.kim" "linda.kim1234"
When I enter the Configuration Area
Then I see an error 

@integration @RALLY_US2276 @RALLY_US200
Scenario: Upload invalid config file
When I login as "jstevenson" "jstevenson1234"
When I enter the Configuration Area
Then I am authorized to the Configuration Area
And I paste Invalid json config into the text box
And click Save
Then I should be shown a failure message
And I reset custom config
And click Save
Then I should be shown a success message
And I logout
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Then I see a list of 28 students
Then I should have a dropdown selector named "viewSelect"
And I should have a selectable view named "Default View"

@integration @RALLY_US2276 @RALLY_US200
Scenario: Upload valid config file
When I login as "jstevenson" "jstevenson1234"
When I enter the Configuration Area
Then I am authorized to the Configuration Area
And I paste Valid json config into the text box
And click Save
Then I should be shown a success message
And I logout
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "linda.kim" "linda.kim1234"
When I select ed org "Daybreak School District 4529"
When I select school "East Daybreak Junior High"
When I select course "8th Grade English"
When I select section "8th Grade English - Sec 6"
Then I see a list of 28 students
Then I should have a dropdown selector named "viewSelect"
And I should have a selectable view named "Middle School ELA View"
Then I should see a table heading "StateTest Reading (highest ever)"
Then I should see a table heading "StateTest Writing (most recent)"
Then I should see a table heading "Final Grades"
And I click on student "Matt Sollars"
And there are "4" Tabs
And Tab has a title named "Middle School Overview"
And I logout
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "manthony" "manthony1234"
When I select ed org "Sunset School District 4526"
When I select school "Sunset Central High School"
When I select course "A.P. Calculus"
When I select section "A.P. Calculus Sec 201"
Then I should only see one view named "Default View"

@integration @RALLY_US2276
Scenario:  Non-District IT admin upload
When I login as "rrogers" "rrogers1234"
When I enter the Configuration Area
Then I am unauthorized to the Configuration Area
And I logout
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "akopel" "akopel1234"
When I enter the Configuration Area
Then I am unauthorized to the Configuration Area
