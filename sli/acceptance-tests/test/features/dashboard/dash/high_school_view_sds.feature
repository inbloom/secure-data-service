Feature: high school college-ready student list view

As a SEA/LEA user, I want to see the high school student list view
on SLI, so I could see high school students results

@integration  @RALLY_US200
Scenario: Check table headers
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Illinois Sunset School District 4526" and click go
When I login as "cgray" "cgray1234"
When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
And I select user view "College Ready ELA View"
Then the table includes header "Reading Test Scores (Highest);Writing Test Scores (Highest);AP Eng. Exam Scores (Highest);Attendance"
