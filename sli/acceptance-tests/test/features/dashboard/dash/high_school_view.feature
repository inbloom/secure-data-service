Feature: high school college-ready student list view

As a SEA/LEA user, I want to see the high school student list view
on SLI, so I could see high school students results

#test gets run in SDS set instead due to cgray seeting in UDS
@wip
Scenario: Check table headers
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
 And I was redirected to the "Simple" IDP Login page
 When I submit the credentials "cgray" "cgray1234" for the "Simple" login page
When I select ed org "Daybreak School District 4529"
When I select school "Daybreak Central High"
And I select course "American Literature"
And I select section "Sec 145"
And I select user view "College Ready ELA View"
Then the table includes header "Reading Test Scores (Highest);Writing Test Scores (Highest);AP Eng. Exam Scores (Highest);Attendance"
