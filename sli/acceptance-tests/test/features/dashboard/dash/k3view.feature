Feature: K-3 student list view

As a SEA/LEA user, I want to see the K-4 student list view
on SLI, so I could see elementary school students results

@wip
Scenario: Check assessment result (live)
#TODO when fuel gauge works
Given I have an open web browser
Given the server is in "live" mod
And I am authenticated to SLI as "rbraverman" password "rbraverman"
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I select course "Reading Group"
And I select section "Reading Group - Grey"
And I wait for "2" seconds
Then the fuel gauge label for the assessment "READ2_NEXT.Mastery level" and student "111111111" is "2"

 @RALLY_US200 @RALLY_US147 @RALLY_US198
Scenario: Check K-3 Student Name in Live
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "rbraverman" "rbraverman1234"
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I should only see one view named "Default View"
Then I see a list of 20 students
And the list includes: "Mi-Ha Tran"
And I click on student "Lauretta Seip"
And I view its student profile
#Display Elementary School Tab
And their grade is "1"
And Tab has a title named "Elementary School Overview"

@wip @integration
Scenario: Check K-3 Student Name in sds
Given I have an open web browser
Given the server is in "live" mode
When I navigate to the Dashboard home page
When I select "Sunset School District 4526" and click go
When I login as "rbraverman" "rbraverman1234"
When I select ed org "Daybreak School District 4529"
When I select school "South Daybreak Elementary"
And I select course "1st Grade Homeroom"
And I select section "Mrs. Braverman's Homeroom #38"
Then I should only see one view named "IL_K-3"
Then I see a list of 25 students
And the list includes: "Mi-Ha Tran"
And I click on student "Lauretta Seip"
And I view its student profile
#Display Elementary School Tab
And their grade is "1"
And Tab has a title named "Elementary School Overview"
